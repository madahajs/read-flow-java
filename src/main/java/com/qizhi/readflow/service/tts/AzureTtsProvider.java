package com.qizhi.readflow.service.tts;

import com.microsoft.cognitiveservices.speech.*;
import com.qizhi.readflow.config.TtsConfig;
import com.qizhi.readflow.dto.TtsRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Azure AI Speech TTS 提供商实现
 * 支持长文本自动分段合成
 */
@Slf4j
public class AzureTtsProvider implements TtsProvider {

    private final TtsConfig.AzureConfig config;

    // 每个分段的最大字符数（保守估计，避免超过10分钟限制）
    // 中文平均语速约 200 字/分钟，10分钟约 2000 字
    // 设置为 1500 字作为安全阈值
    private static final int MAX_CHUNK_SIZE = 1500;

    public AzureTtsProvider(TtsConfig.AzureConfig config) {
        this.config = config;
        if (config.getSubscriptionKey() == null || config.getRegion() == null) {
            log.warn("Azure TTS 配置不完整，subscriptionKey 或 region 为空");
        }
    }

    @Override
    public TtsResult synthesize(TtsRequest request) throws Exception {
        if (config.getSubscriptionKey() == null || config.getRegion() == null) {
            throw new IllegalStateException("Azure TTS 配置不完整，请检查 subscriptionKey 和 region");
        }

        // 打印请求参数
        log.info("TTS 请求参数: voiceId={}, speed={}, format={}, textLength={}",
                request.getVoiceId(), request.getSpeed(), request.getFormat(), request.getText().length());

        // 清洗文本：去除不必要的换行符，修复断句
        String text = cleanText(request.getText());
        log.info("文本清洗完成，清洗后长度: {}", text.length());

        // 更新 request 中的文本，以便后续方法使用清洗后的版本
        request.setText(text);

        // 如果文本较短，直接合成
        if (text.length() <= MAX_CHUNK_SIZE) {
            return synthesizeSingle(request);
        }

        // 长文本分段合成
        log.info("文本长度 {} 超过阈值 {}，进行分段合成", text.length(), MAX_CHUNK_SIZE);
        return synthesizeChunked(request);
    }

    /**
     * 单次合成（文本较短时使用）
     */
    private TtsResult synthesizeSingle(TtsRequest request) throws Exception {
        SpeechConfig speechConfig = createSpeechConfig(request);
        String voiceName = mapVoiceId(request.getVoiceId());

        try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, null)) {
            String ssml = buildSsml(request.getText(), voiceName, request.getSpeed());
            SpeechSynthesisResult result = synthesizer.SpeakSsml(ssml);

            if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                byte[] audioData = result.getAudioData();
                long durationTicks = result.getAudioDuration();
                double duration = durationTicks / 10_000_000.0;

                List<TtsResult.Subtitle> subtitles = generateSubtitles(request.getText(), duration);

                log.info("Azure TTS 合成成功，音频大小: {} bytes, 时长: {} 秒", audioData.length, duration);

                return TtsResult.builder()
                        .audioData(audioData)
                        .format(request.getFormat())
                        .duration(duration)
                        .subtitles(subtitles)
                        .build();

            } else if (result.getReason() == ResultReason.Canceled) {
                SpeechSynthesisCancellationDetails details = SpeechSynthesisCancellationDetails.fromResult(result);
                throw new RuntimeException("TTS 合成失败: " + details.getErrorDetails());
            } else {
                throw new RuntimeException("TTS 合成失败: " + result.getReason());
            }
        }
    }

    /**
     * 分段合成（长文本时使用）
     */
    private TtsResult synthesizeChunked(TtsRequest request) throws Exception {
        List<String> chunks = splitTextIntoChunks(request.getText());
        log.info("文本被分割为 {} 个片段", chunks.size());

        ByteArrayOutputStream audioStream = new ByteArrayOutputStream();
        List<TtsResult.Subtitle> allSubtitles = new ArrayList<>();
        double totalDuration = 0.0;
        int subtitleId = 1;

        SpeechConfig speechConfig = createSpeechConfig(request);
        String voiceName = mapVoiceId(request.getVoiceId());

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            log.debug("合成第 {}/{} 个片段，长度: {}", i + 1, chunks.size(), chunk.length());

            try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig, null)) {
                String ssml = buildSsml(chunk, voiceName, request.getSpeed());
                SpeechSynthesisResult result = synthesizer.SpeakSsml(ssml);

                if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    byte[] audioData = result.getAudioData();
                    long durationTicks = result.getAudioDuration();
                    double chunkDuration = durationTicks / 10_000_000.0;

                    // 合并音频数据
                    audioStream.write(audioData);

                    // 生成该片段的字幕，调整时间偏移
                    List<TtsResult.Subtitle> chunkSubtitles = generateSubtitles(chunk, chunkDuration);
                    for (TtsResult.Subtitle sub : chunkSubtitles) {
                        allSubtitles.add(TtsResult.Subtitle.builder()
                                .id(subtitleId++)
                                .startTime(sub.getStartTime() + totalDuration)
                                .endTime(sub.getEndTime() + totalDuration)
                                .text(sub.getText())
                                .build());
                    }

                    totalDuration += chunkDuration;
                    log.debug("片段 {} 合成完成，时长: {} 秒", i + 1, chunkDuration);

                } else if (result.getReason() == ResultReason.Canceled) {
                    SpeechSynthesisCancellationDetails details = SpeechSynthesisCancellationDetails.fromResult(result);
                    throw new RuntimeException("TTS 合成失败（片段 " + (i + 1) + "）: " + details.getErrorDetails());
                } else {
                    throw new RuntimeException("TTS 合成失败（片段 " + (i + 1) + "）: " + result.getReason());
                }
            }
        }

        log.info("分段合成完成，总片段: {}，总时长: {} 秒，总音频大小: {} bytes",
                chunks.size(), totalDuration, audioStream.size());

        return TtsResult.builder()
                .audioData(audioStream.toByteArray())
                .format(request.getFormat())
                .duration(totalDuration)
                .subtitles(allSubtitles)
                .build();
    }

    /**
     * 将长文本分割成多个片段
     * 优先按句子分割，保证语义完整性
     */
    private List<String> splitTextIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();

        // 按句子分割
        String[] sentences = text.split("(?<=[。！？.!?])");

        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            // 如果当前片段加上新句子不超过限制，就添加
            if (currentChunk.length() + sentence.length() <= MAX_CHUNK_SIZE) {
                currentChunk.append(sentence);
            } else {
                // 保存当前片段
                if (currentChunk.length() > 0) {
                    chunks.add(currentChunk.toString().trim());
                    currentChunk = new StringBuilder();
                }

                // 如果单个句子就超过限制，需要进一步分割
                if (sentence.length() > MAX_CHUNK_SIZE) {
                    chunks.addAll(splitLongSentence(sentence));
                } else {
                    currentChunk.append(sentence);
                }
            }
        }

        // 添加最后一个片段
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    /**
     * 分割超长句子（按逗号、分号等分割）
     */
    private List<String> splitLongSentence(String sentence) {
        List<String> parts = new ArrayList<>();
        String[] subSentences = sentence.split("(?<=[，,；;：:])");

        StringBuilder current = new StringBuilder();
        for (String sub : subSentences) {
            if (current.length() + sub.length() <= MAX_CHUNK_SIZE) {
                current.append(sub);
            } else {
                if (current.length() > 0) {
                    parts.add(current.toString().trim());
                    current = new StringBuilder();
                }
                // 如果还是太长，强制分割
                if (sub.length() > MAX_CHUNK_SIZE) {
                    for (int i = 0; i < sub.length(); i += MAX_CHUNK_SIZE) {
                        parts.add(sub.substring(i, Math.min(i + MAX_CHUNK_SIZE, sub.length())));
                    }
                } else {
                    current.append(sub);
                }
            }
        }

        if (current.length() > 0) {
            parts.add(current.toString().trim());
        }

        return parts;
    }

    /**
     * 创建 Speech 配置
     */
    private SpeechConfig createSpeechConfig(TtsRequest request) throws Exception {
        SpeechConfig speechConfig = SpeechConfig.fromSubscription(
                config.getSubscriptionKey(),
                config.getRegion());

        String voiceName = mapVoiceId(request.getVoiceId());
        speechConfig.setSpeechSynthesisVoiceName(voiceName);

        SpeechSynthesisOutputFormat outputFormat = getOutputFormat(request.getFormat());
        speechConfig.setSpeechSynthesisOutputFormat(outputFormat);

        return speechConfig;
    }

    @Override
    public String getProviderName() {
        return "Azure AI Speech";
    }

    /**
     * 映射 voiceId 到 Azure 语音名称
     */
    private String mapVoiceId(String voiceId) {
        switch (voiceId) {
            case "xiaoxiao":
            case "zh-CN-XiaoxiaoNeural":
                return "zh-CN-XiaoxiaoNeural";
            case "yunxi":
            case "zh-CN-YunxiNeural":
                return "zh-CN-YunxiNeural";
            case "yunyang":
            case "zh-CN-YunyangNeural":
                return "zh-CN-YunyangNeural";
            case "xiaoyi":
            case "zh-CN-XiaoyiNeural":
                return "zh-CN-XiaoyiNeural";
            case "yunjian":
            case "zh-CN-YunjianNeural":
                return "zh-CN-YunjianNeural";
            case "yunxia":
            case "zh-CN-YunxiaNeural":
                return "zh-CN-YunxiaNeural";
            case "xiaobei":
            case "zh-CN-XiaobeiNeural":
                return "zh-CN-XiaobeiNeural";
            case "xiaoni":
            case "zh-CN-XiaoniNeural":
                return "zh-CN-XiaoniNeural";
            case "xiaochen":
            case "zh-CN-XiaochenNeural":
                return "zh-CN-XiaochenNeural";
            case "jenny":
            case "en-US-JennyNeural":
                return "en-US-JennyNeural";
            case "guy":
            case "en-US-GuyNeural":
                return "en-US-GuyNeural";
            case "sonia":
            case "en-GB-SoniaNeural":
                return "en-GB-SoniaNeural";
            default:
                if (voiceId.contains("-") && voiceId.contains("Neural")) {
                    return voiceId;
                }
                return "zh-CN-XiaoxiaoNeural";
        }
    }

    /**
     * 获取输出格式
     */
    private SpeechSynthesisOutputFormat getOutputFormat(String format) {
        switch (format.toLowerCase()) {
            case "wav":
                return SpeechSynthesisOutputFormat.Riff24Khz16BitMonoPcm;
            case "ogg":
                return SpeechSynthesisOutputFormat.Ogg24Khz16BitMonoOpus;
            case "mp3":
            default:
                return SpeechSynthesisOutputFormat.Audio24Khz160KBitRateMonoMp3;
        }
    }

    /**
     * 构建 SSML
     * Azure SSML prosody rate 使用相对值：
     * - "default" 或 "medium" = 正常速度
     * - "+50%" = 快 50%
     * - "-50%" = 慢 50%
     * 或使用绝对值如 "slow", "fast", "x-slow", "x-fast"
     */
    private String buildSsml(String text, String voiceName, double speed) {
        // 将 speed 转换为相对百分比
        // speed=1.0 -> +0%, speed=0.5 -> -50%, speed=2.0 -> +100%
        int relativePercent = (int) ((speed - 1.0) * 100);
        String rate;
        if (relativePercent == 0) {
            rate = "default";
        } else if (relativePercent > 0) {
            rate = "+" + relativePercent + "%";
        } else {
            rate = relativePercent + "%";
        }

        log.info("SSML 参数: voiceName={}, speed={}, rate={}", voiceName, speed, rate);

        String escapedText = text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");

        return String.format(
                "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" xml:lang=\"zh-CN\">" +
                        "<voice name=\"%s\">" +
                        "<prosody rate=\"%s\">%s</prosody>" +
                        "</voice></speak>",
                voiceName, rate, escapedText);
    }

    /**
     * 生成字幕
     */
    private List<TtsResult.Subtitle> generateSubtitles(String text, double totalDuration) {
        List<TtsResult.Subtitle> subtitles = new ArrayList<>();
        String[] sentences = text.split("[。！？.!?]");

        int totalChars = 0;
        for (String sentence : sentences) {
            totalChars += sentence.trim().length();
        }

        if (totalChars == 0) {
            return subtitles;
        }

        double currentTime = 0.0;
        int id = 1;

        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                double ratio = (double) trimmed.length() / totalChars;
                double duration = totalDuration * ratio;

                subtitles.add(TtsResult.Subtitle.builder()
                        .id(id++)
                        .startTime(currentTime)
                        .endTime(currentTime + duration)
                        .text(trimmed)
                        .build());

                currentTime += duration;
            }
        }

        return subtitles;
    }

    /**
     * 清洗文本，去除 OCR 产生的多余换行符
     */
    private String cleanText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 1. 将所有换行符统一为 \n
        String normalized = text.replace("\r\n", "\n").replace("\r", "\n");

        // 2. 按行分割
        String[] lines = normalized.split("\n");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue; // 跳过空行
            }

            sb.append(line);

            // 检查当前行是否应该与下一行合并
            // 如果行尾不是句子结束标点，且不是最后一行，则连接下一行
            if (i < lines.length - 1) {
                if (!isSentenceEnd(line)) {
                    // 如果是中文，直接连接；如果是英文，加空格
                    if (isChinese(line)) {
                        // 中文直接连接
                    } else {
                        sb.append(" ");
                    }
                } else {
                    // 是句子结尾
                }
            }
        }

        return sb.toString();
    }

    private boolean isSentenceEnd(String line) {
        if (line.isEmpty())
            return false;
        char lastChar = line.charAt(line.length() - 1);
        return lastChar == '。' || lastChar == '！' || lastChar == '？' ||
                lastChar == '.' || lastChar == '!' || lastChar == '?' ||
                lastChar == ';' || lastChar == '；' || lastChar == '：' || lastChar == ':';
    }

    private boolean isChinese(String text) {
        for (char c : text.toCharArray()) {
            if (Character.isIdeographic(c)) {
                return true;
            }
        }
        return false;
    }
}
