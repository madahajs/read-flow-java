-- 添加更多 Azure 神经网络语音
-- 先删除旧数据（可选）
-- DELETE FROM rf_voice;

-- 中文（普通话）语音
INSERT INTO rf_voice (id, name, region, language, gender, avatar_url, tags, status, sort_order, created_at) VALUES
('zh-CN-XiaoxiaoNeural', '晓晓', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaoxiao', '["温暖", "新闻"]', 1, 1, NOW()),
('zh-CN-YunxiNeural', '云希', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunxi', '["阳光", "解说"]', 1, 2, NOW()),
('zh-CN-YunyangNeural', '云扬', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunyang', '["专业", "新闻"]', 1, 3, NOW()),
('zh-CN-XiaoyiNeural', '晓伊', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaoyi', '["亲切", "客服"]', 1, 4, NOW()),
('zh-CN-YunjianNeural', '云健', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunjian', '["沉稳", "纪录片"]', 1, 5, NOW()),
('zh-CN-XiaochenNeural', '晓辰', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaochen', '["轻松", "休闲"]', 1, 6, NOW()),
('zh-CN-XiaohanNeural', '晓涵', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaohan', '["多情感", "角色扮演"]', 1, 7, NOW()),
('zh-CN-XiaomoNeural', '晓墨', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaomo', '["角色扮演", "故事"]', 1, 8, NOW()),
('zh-CN-XiaoruiNeural', '晓睿', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaorui', '["成熟", "冷静"]', 1, 9, NOW()),
('zh-CN-XiaoshuangNeural', '晓双', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaoshuang', '["童声", "可爱"]', 1, 10, NOW()),
('zh-CN-XiaoxuanNeural', '晓萱', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaoxuan', '["多情感", "故事"]', 1, 11, NOW()),
('zh-CN-XiaozhenNeural', '晓甄', 'CN', 'zh-CN', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaozhen', '["多情感", "客服"]', 1, 12, NOW()),
('zh-CN-YunfengNeural', '云枫', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunfeng', '["运动解说", "激情"]', 1, 13, NOW()),
('zh-CN-YunhaoNeural', '云皓', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunhao', '["广告", "配音"]', 1, 14, NOW()),
('zh-CN-YunxiaNeural', '云夏', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunxia', '["童声", "男孩"]', 1, 15, NOW()),
('zh-CN-YunzeNeural', '云泽', 'CN', 'zh-CN', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Yunze', '["多情感", "故事"]', 1, 16, NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    tags = VALUES(tags),
    sort_order = VALUES(sort_order);

-- 英文（美国）语音
INSERT INTO rf_voice (id, name, region, language, gender, avatar_url, tags, status, sort_order, created_at) VALUES
('en-US-JennyNeural', 'Jenny', 'US', 'en-US', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jenny', '["通用", "助手"]', 1, 20, NOW()),
('en-US-GuyNeural', 'Guy', 'US', 'en-US', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Guy', '["新闻", "正式"]', 1, 21, NOW()),
('en-US-AriaNeural', 'Aria', 'US', 'en-US', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Aria', '["多情感", "故事"]', 1, 22, NOW()),
('en-US-DavisNeural', 'Davis', 'US', 'en-US', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Davis', '["多情感", "专业"]', 1, 23, NOW()),
('en-US-JasonNeural', 'Jason', 'US', 'en-US', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Jason', '["休闲", "对话"]', 1, 24, NOW()),
('en-US-SaraNeural', 'Sara', 'US', 'en-US', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Sara', '["友好", "对话"]', 1, 25, NOW()),
('en-US-TonyNeural', 'Tony', 'US', 'en-US', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Tony', '["休闲", "友好"]', 1, 26, NOW()),
('en-US-NancyNeural', 'Nancy', 'US', 'en-US', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Nancy', '["阅读", "温和"]', 1, 27, NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    tags = VALUES(tags),
    sort_order = VALUES(sort_order);

-- 英文（英国）语音
INSERT INTO rf_voice (id, name, region, language, gender, avatar_url, tags, status, sort_order, created_at) VALUES
('en-GB-SoniaNeural', 'Sonia', 'GB', 'en-GB', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Sonia', '["英式", "友好"]', 1, 30, NOW()),
('en-GB-RyanNeural', 'Ryan', 'GB', 'en-GB', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Ryan', '["英式", "专业"]', 1, 31, NOW()),
('en-GB-LibbyNeural', 'Libby', 'GB', 'en-GB', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Libby', '["英式", "阅读"]', 1, 32, NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    tags = VALUES(tags),
    sort_order = VALUES(sort_order);

-- 日语语音
INSERT INTO rf_voice (id, name, region, language, gender, avatar_url, tags, status, sort_order, created_at) VALUES
('ja-JP-NanamiNeural', 'Nanami', 'JP', 'ja-JP', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Nanami', '["日语", "温柔"]', 1, 40, NOW()),
('ja-JP-KeitaNeural', 'Keita', 'JP', 'ja-JP', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=Keita', '["日语", "专业"]', 1, 41, NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    tags = VALUES(tags),
    sort_order = VALUES(sort_order);

-- 韩语语音
INSERT INTO rf_voice (id, name, region, language, gender, avatar_url, tags, status, sort_order, created_at) VALUES
('ko-KR-SunHiNeural', 'SunHi', 'KR', 'ko-KR', 'Female', 'https://api.dicebear.com/7.x/avataaars/svg?seed=SunHi', '["韩语", "友好"]', 1, 50, NOW()),
('ko-KR-InJoonNeural', 'InJoon', 'KR', 'ko-KR', 'Male', 'https://api.dicebear.com/7.x/avataaars/svg?seed=InJoon', '["韩语", "专业"]', 1, 51, NOW())
ON DUPLICATE KEY UPDATE 
    name = VALUES(name),
    tags = VALUES(tags),
    sort_order = VALUES(sort_order);
