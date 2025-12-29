# Read Flow 后端接口文档

本文档定义了 Read Flow 应用所需的后端 API 接口。

## 基础信息
- **Base URL**: `/api/v1`
- **Content-Type**: `application/json` (除非特别说明)

---

## 1. OCR (文字识别) 服务

### 上传文档提取文字
将用户上传的图片或文档转换为可编辑的文本。

- **Endpoint**: `POST /ocr/extract`
- **Content-Type**: `multipart/form-data`

#### 请求参数 (FormData)
| 参数名 | 类型 | 必选 | 描述 |
|---|---|---|---|
| `file` | File | 是 | 支持 .jpg, .png, .pdf, .docx 格式的文件 |

#### 响应示例 (Success)
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "text": "这里是识别出来的文字内容...",
    "detectedLanguage": "zh-CN"
  }
}
```

#### 响应示例 (Error)
```json
{
  "code": 400,
  "message": "Unsupported file format"
}
```

---

## 2. TTS (语音合成) 服务

### 文本转语音
将文本转换为语音音频，并返回字幕时间戳。

- **Endpoint**: `POST /tts/generate`

#### 请求参数 (JSON)
| 参数名 | 类型 | 必选 | 描述 |
|---|---|---|---|
| `text` | string | 是 | 需要转换的文本内容 |
| `voiceId` | string | 是 | 语音角色 ID (如 "zh-CN-XiaoxiaoNeural") |
| `speed` | number | 否 | 语速，默认 1.0 (范围 0.5 - 2.0) |
| [format](file:///Users/masixin/Desktop/xiangmu/read-flow/src/components/AudioPlayer.vue#43-49) | string | 否 | 音频格式，默认 "mp3" |

#### 响应示例 (Success)
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "audioUrl": "https://cdn.example.com/audio/123456.mp3",
    "duration": 15.5,
    "subtitles": [
      {
        "id": 1,
        "startTime": 0.0,
        "endTime": 2.5,
        "text": "这是一段测试文本"
      },
      {
        "id": 2,
        "startTime": 2.5,
        "endTime": 5.0,
        "text": "用于展示字幕同步功能"
      }
    ]
  }
}
```

---

## 3. 语音配置服务

### 获取可用语音列表
获取系统支持的所有语音角色列表。

- **Endpoint**: `GET /voices`

#### 响应示例 (Success)
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "zh-CN-XiaoxiaoNeural",
      "name": "晓晓",
      "region": "CN",
      "language": "zh-CN",
      "gender": "Female",
      "avatarUrl": "https://api.dicebear.com/7.x/avataaars/svg?seed=Xiaoxiao",
      "tags": ["温暖", "新闻"]
    },
    {
      "id": "en-US-JennyNeural",
      "name": "Jenny",
      "region": "US",
      "language": "en-US",
      "gender": "Female",
      "avatarUrl": "https://api.dicebear.com/7.x/avataaars/svg?seed=Jenny",
      "tags": ["Assistant", "Chat"]
    }
  ]
}
```

---

## 4. 错误码定义
| 状态码 | 描述 |
|---|---|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 / 需要登录 |
| 413 | 文件体积过大 |
| 500 | 服务器内部错误 |

---

## 5. 用户系统

### 用户注册
注册新用户。

- **Endpoint**: `POST /auth/register`

#### 请求参数
| 参数名 | 类型 | 必选 | 描述 |
|---|---|---|---|
| `email` | string | 是 | 用户邮箱 |
| `password` | string | 是 | 密码 (长度 >= 8) |
| `username` | string | 否 | 用户名 |

### 用户登录
用户登录获取 token。

- **Endpoint**: `POST /auth/login`

#### 请求参数
| 参数名 | 类型 | 必选 | 描述 |
|---|---|---|---|
| `email` | string | 是 | 用户邮箱 |
| `password` | string | 是 | 密码 |

#### 响应示例
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": "u_123456",
      "email": "user@example.com",
      "username": "ReadFlowUser"
    }
  }
}
```

### 获取用户信息
获取当前登录用户的个人资料。

- **Endpoint**: `GET /user/profile`
- **Headers**: `Authorization: Bearer <token>`

---

## 6. 高级功能服务

### 获取转换历史
分页获取用户的历史转换记录。

- **Endpoint**: `GET /history`
- **Headers**: `Authorization: Bearer <token>`

#### 请求参数 (Query)
| 参数名 | 类型 | 必选 | 描述 |
|---|---|---|---|
| `page` | number | 否 | 页码，默认 1 |
| `pageSize` | number | 否 | 每页数量，默认 20 |

#### 响应示例
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": "h_1001",
        "title": "Document_01.pdf",
        "type": "upload",
        "createdAt": "2023-10-27T10:00:00Z",
        "status": "completed",
        "audioDuration": 120
      }
    ],
    "total": 50
  }
}
```

### 删除历史记录
删除指定的转换记录。

- **Endpoint**: `DELETE /history/{id}`
- **Headers**: `Authorization: Bearer <token>`
