-- Read Flow 测试数据

-- 用户数据 (密码均为: 123456 的BCrypt加密)
INSERT INTO `rf_user` (`id`, `email`, `password`, `username`, `avatar_url`, `status`) VALUES
(1, 'admin@readflow.com', '$2a$10$N.zmdr9k7uONQZyUvvKQ0OjnZ3p7l6kZJvqVzvV5ZVl3UBvK.YXRO', 'Admin', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Admin', 1),
(2, 'test@example.com', '$2a$10$N.zmdr9k7uONQZyUvvKQ0OjnZ3p7l6kZJvqVzvV5ZVl3UBvK.YXRO', 'TestUser', 'https://api.dicebear.com/7.x/avataaars/svg?seed=TestUser', 1),
(3, 'demo@readflow.com', '$2a$10$N.zmdr9k7uONQZyUvvKQ0OjnZ3p7l6kZJvqVzvV5ZVl3UBvK.YXRO', 'DemoUser', 'https://api.dicebear.com/7.x/avataaars/svg?seed=DemoUser', 1);

-- 语音配置数据
INSERT INTO `rf_voice` (`id`, `name`, `region`, `language`, `gender`, `avatar_url`, `tags`, `sort_order`) VALUES
('zh-CN-XiaoxiaoNeural', '晓晓', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaoxiao', '["温暖", "新闻"]', 1),
('zh-CN-YunxiNeural', '云希', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunxi', '["阳光", "解说"]', 2),
('zh-CN-XiaoyiNeural', '晓伊', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaoyi', '["亲切", "客服"]', 3),
('zh-CN-YunjianNeural', '云健', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunjian', '["沉稳", "纪录片"]', 4),
('zh-CN-XiaochenNeural', '晓辰', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaochen', '["童声", "可爱"]', 5),
('en-US-JennyNeural', 'Jenny', 'US', 'en-US', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jenny', '["Assistant", "Chat"]', 6),
('en-US-GuyNeural', 'Guy', 'US', 'en-US', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Guy', '["Newscast", "Formal"]', 7),
('en-GB-SoniaNeural', 'Sonia', 'GB', 'en-GB', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Sonia', '["British", "Friendly"]', 8);

-- 转换历史数据
INSERT INTO `rf_conversion_history` (`id`, `user_id`, `title`, `type`, `original_text`, `voice_id`, `audio_url`, `audio_duration`, `status`, `created_at`) VALUES
(1, 2, 'Document_01.pdf', 'upload', '这是一段从PDF文档中提取的测试文本内容，用于验证OCR功能是否正常工作。', 'zh-CN-XiaoxiaoNeural', 'https://cdn.example.com/audio/doc_01.mp3', 120, 'completed', '2023-10-27 10:00:00'),
(2, 2, '新闻稿件.docx', 'upload', '今日头条新闻：科技公司发布最新产品，引发市场广泛关注。', 'zh-CN-YunxiNeural', 'https://cdn.example.com/audio/news_01.mp3', 85, 'completed', '2023-10-26 15:30:00'),
(3, 2, '手动输入文本', 'text', '这是用户手动输入的一段文本，可以直接转换为语音。', 'zh-CN-XiaoyiNeural', 'https://cdn.example.com/audio/text_01.mp3', 45, 'completed', '2023-10-25 09:15:00'),
(4, 2, 'English_Article.pdf', 'upload', 'This is a sample English text extracted from a PDF document for testing purposes.', 'en-US-JennyNeural', 'https://cdn.example.com/audio/eng_01.mp3', 60, 'completed', '2023-10-24 14:20:00'),
(5, 2, '正在处理的文档.png', 'upload', '这是一张图片中识别出的文字内容。', 'zh-CN-XiaoxiaoNeural', NULL, 0, 'processing', '2023-10-28 11:00:00');
