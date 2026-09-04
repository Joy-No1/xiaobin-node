# 小斌社交APP - 完整接口文档

> **更新时间**: 2026-09-03  
> **API基础地址**: `http://localhost:8090`  
> **认证方式**: `Authorization: Bearer {token}`

---

## 📡 WebSocket 实时通信

### 连接信息
- **地址**: `ws://localhost:9090/ws`
- **协议**: WebSocket
- **认证**: 连接后发送AUTH消息

### 连接流程
```javascript
// 1. 建立连接
const ws = new WebSocket('ws://localhost:9090/ws');

// 2. 连接成功后立即认证
ws.onopen = () => {
    ws.send(JSON.stringify({
        type: 'AUTH',
        token: 'your-jwt-token'
    }));
};

// 3. 每30秒发送心跳
setInterval(() => {
    ws.send(JSON.stringify({ type: 'PING' }));
}, 30000);
```

### 客户端发送消息

| 消息类型 | 必填字段 | 可选字段 | 说明 |
|---------|---------|---------|------|
| **AUTH** | `token` | - | 认证（连接后必须先认证） |
| **MESSAGE** | `receiverId`<br>`content`<br>`messageType` | `duration` | 发送聊天消息<br>messageType: TEXT/IMAGE/VOICE/EMOJI<br>语音需传duration（秒） |
| **PING** | - | - | 心跳（每30秒发送一次） |

**示例**
```javascript
// 认证
ws.send(JSON.stringify({
    type: 'AUTH',
    token: 'eyJhbGci...'
}));

// 发送文本消息
ws.send(JSON.stringify({
    type: 'MESSAGE',
    receiverId: 123,
    content: '你好',
    messageType: 'TEXT'
}));

// 发送图片
ws.send(JSON.stringify({
    type: 'MESSAGE',
    receiverId: 123,
    content: 'https://example.com/image.jpg',
    messageType: 'IMAGE'
}));

// 发送语音
ws.send(JSON.stringify({
    type: 'MESSAGE',
    receiverId: 123,
    content: 'https://example.com/voice.mp3',
    messageType: 'VOICE',
    duration: 15
}));

// 心跳
ws.send(JSON.stringify({ type: 'PING' }));
```

### 服务端推送消息

| 消息类型 | 字段 | 触发时机 |
|---------|------|---------|
| **AUTH_SUCCESS** | `userId` | 认证成功 |
| **MESSAGE_SENT** | `messageId`<br>`conversationId`<br>`createdAt` | 消息发送成功（给发送者） |
| **NEW_MESSAGE** | `messageId`<br>`conversationId`<br>`senderId`<br>`content`<br>`messageType`<br>`duration`<br>`createdAt` | 收到新消息（给接收者） |
| **CONVERSATION_UPDATED** | `conversationId`<br>`lastMessageId`<br>`lastMessage`<br>`lastMessageTime`<br>`unreadCount` | 会话更新（发送/接收消息后） |
| **NEW_FOLLOWER** | `notificationId`<br>`fromUserId`<br>`fromUser`<br>`content`<br>`createdAt` | 被关注通知 |
| **PONG** | - | 心跳响应 |
| **ERROR** | `message` | 发生错误 |

**接收消息示例**
```javascript
ws.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    
    switch (msg.type) {
        case 'AUTH_SUCCESS':
            console.log('认证成功:', msg.userId);
            break;
            
        case 'MESSAGE_SENT':
            // 消息已发送
            console.log('消息已发送:', msg.messageId);
            break;
            
        case 'NEW_MESSAGE':
            // 收到新消息
            console.log('收到新消息:', msg);
            break;
            
        case 'CONVERSATION_UPDATED':
            // 会话更新
            console.log('会话更新:', msg);
            break;
            
        case 'NEW_FOLLOWER':
            // 被关注
            console.log('新关注者:', msg.fromUser);
            break;
            
        case 'ERROR':
            console.error('错误:', msg.message);
            break;
    }
};
```

---

## 🔐 一、认证模块

### 1.1 注册
```http
POST /api/v1/auth/register
Content-Type: application/json
```

**请求体**
```json
{
    "phone": "13800138000",
    "password": "123456",
    "nickname": "小斌"
}
```

**响应**
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": 1,
        "username": "user_13800138000",
        "nickname": "小斌",
        "phone": "13800138000"
    }
}
```

### 1.2 登录
```http
POST /api/v1/auth/login
Content-Type: application/json
```

**请求体**
```json
{
    "account": "13800138000",
    "password": "123456"
}
```

**响应**
```json
{
    "code": 200,
    "data": {
        "token": "eyJhbGciOi...",
        "user": {
            "id": 1,
            "username": "user_13800138000",
            "phone": "13800138000",
            "nickname": "小斌",
            "avatarUrl": "",
            "gender": "MALE",
            "bio": "这个人很懒什么都没写",
            "birthday": null,
            "province": "广东省",
            "city": "深圳市",
            "district": "南山区"
        }
    }
}
```

---

## 👤 二、用户模块

### 2.1 获取我的信息
```http
GET /api/v1/users/me
```

**响应**: 返回当前用户完整信息

### 2.2 编辑我的资料
```http
PUT /api/v1/users/me
Content-Type: application/json
```

**请求体**
```json
{
    "nickname": "新昵称",
    "avatarUrl": "https://...",
    "gender": "MALE",
    "bio": "个人简介",
    "birthday": "1990-01-01",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区"
}
```

### 2.3 查看其他用户信息
```http
GET /api/v1/users/{userId}
```

### 2.4 搜索用户
```http
GET /api/v1/users/search?keyword={关键词}&page=1&size=20
```

### 2.5 批量获取用户信息
```http
POST /api/v1/users/batch
Content-Type: application/json

[123, 456, 789]
```

**响应**: 返回用户列表

---

## 💬 三、聊天模块

### 3.1 获取会话列表
```http
GET /api/v1/chat/conversations
```

**响应**
```json
{
    "code": 200,
    "data": [
        {
            "id": 1,
            "type": "PRIVATE",
            "lastMessageId": 100,
            "lastMessage": "你好",
            "lastMessageTime": "2024-01-01T10:00:00",
            "unreadCount": 3,
            "isPinned": 0,
            "isMuted": 0,
            "otherUser": {
                "id": 123,
                "nickname": "张三",
                "avatarUrl": "https://..."
            }
        }
    ]
}
```

### 3.2 创建/获取会话
```http
POST /api/v1/chat/conversations?targetUserId={对方用户ID}
```

**说明**: 幂等操作，已存在则返回已有会话

### 3.3 获取聊天历史
```http
GET /api/v1/chat/conversations/{conversationId}/messages?page=1&size=20
```

**响应**
```json
{
    "code": 200,
    "data": {
        "page": 1,
        "size": 20,
        "total": 100,
        "records": [
            {
                "id": 100,
                "conversationId": 1,
                "senderId": 123,
                "content": "你好",
                "messageType": "TEXT",
                "duration": null,
                "createdAt": "2024-01-01T10:00:00"
            }
        ]
    }
}
```

### 3.4 发送消息（HTTP方式）
```http
POST /api/v1/chat/conversations/{conversationId}/messages
    ?content={消息内容}
    &messageType={TEXT|IMAGE|VOICE|EMOJI}
    &duration={语音时长，语音消息必填}
```

**说明**: 
- 推荐使用WebSocket发送，实时性更好
- HTTP方式也会通过WebSocket推送给在线接收者

### 3.5 标记已读
```http
PUT /api/v1/chat/conversations/{conversationId}/read
```

---

## 📊 四、在线状态模块

### 4.1 检查单个用户在线状态
```http
GET /api/v1/online/check/{userId}
```

**响应**
```json
{
    "code": 200,
    "data": {
        "userId": 123,
        "online": true
    }
}
```

### 4.2 批量检查在线状态
```http
POST /api/v1/online/batch-check
Content-Type: application/json

[123, 456, 789]
```

**响应**
```json
{
    "code": 200,
    "data": {
        "123": true,
        "456": false,
        "789": true
    }
}
```

### 4.3 获取在线用户数量
```http
GET /api/v1/online/count
```

**响应**
```json
{
    "code": 200,
    "data": {
        "count": 1523
    }
}
```

### 4.4 查看用户连接（多端登录）
```http
GET /api/v1/online/connections/{userId}
```

**响应**
```json
{
    "code": 200,
    "data": {
        "userId": 123,
        "connections": ["channel-id-1", "channel-id-2"]
    }
}
```

---

## 📱 五、社区模块

### 5.1 发布动态
```http
POST /api/v1/posts
Content-Type: application/json
```

**请求体**
```json
{
    "content": "动态内容",
    "images": ["https://...", "https://..."],
    "visibility": "PUBLIC"
}
```

### 5.2 动态列表（推荐/关注）
```http
GET /api/v1/posts?type={RECOMMEND|FOLLOWING}&page=1&size=20
```

**响应**
```json
{
    "code": 200,
    "data": {
        "page": 1,
        "size": 20,
        "total": 100,
        "records": [
            {
                "id": 1,
                "userId": 123,
                "content": "动态内容",
                "images": ["https://..."],
                "likeCount": 10,
                "commentCount": 5,
                "isLiked": false,
                "user": {
                    "id": 123,
                    "nickname": "张三",
                    "avatarUrl": "https://..."
                },
                "createdAt": "2024-01-01T10:00:00"
            }
        ]
    }
}
```

### 5.3 动态详情
```http
GET /api/v1/posts/{postId}
```

### 5.4 修改动态
```http
PUT /api/v1/posts/{postId}
Content-Type: application/json
```

### 5.5 删除动态
```http
DELETE /api/v1/posts/{postId}
```

### 5.6 点赞动态
```http
POST /api/v1/posts/{postId}/likes
```

### 5.7 取消点赞
```http
DELETE /api/v1/posts/{postId}/likes
```

### 5.8 评论动态
```http
POST /api/v1/posts/{postId}/comments
Content-Type: application/json
```

**请求体**
```json
{
    "content": "评论内容"
}
```

### 5.9 获取评论列表
```http
GET /api/v1/posts/{postId}/comments?page=1&size=20
```

### 5.10 删除评论
```http
DELETE /api/v1/comments/{commentId}
```

---

## 👥 六、关注模块

### 6.1 关注用户
```http
POST /api/v1/users/{userId}/follow
```

### 6.2 取消关注
```http
DELETE /api/v1/users/{userId}/follow
```

### 6.3 查询关注状态
```http
GET /api/v1/users/{userId}/follow-status
```

**响应**
```json
{
    "code": 200,
    "data": {
        "isFollowing": true,
        "isFollowedBy": false,
        "isMutualFollow": false
    }
}
```

### 6.4 我的关注列表
```http
GET /api/v1/me/following?page=1&size=20
```

---

## 🔔 七、通知模块

### 7.1 获取通知列表
```http
GET /api/v1/notifications?page=1&size=20
```

**响应**
```json
{
    "code": 200,
    "data": {
        "records": [
            {
                "id": 1,
                "type": "LIKE",
                "fromUserId": 123,
                "fromUser": {
                    "id": 123,
                    "nickname": "张三",
                    "avatarUrl": "https://..."
                },
                "content": "赞了你的动态",
                "relatedId": 456,
                "isRead": false,
                "createdAt": "2024-01-01T10:00:00"
            }
        ]
    }
}
```

### 7.2 未读通知数量
```http
GET /api/v1/notifications/unread-count
```

**响应**
```json
{
    "code": 200,
    "data": {
        "count": 5
    }
}
```

### 7.3 标记通知已读
```http
PUT /api/v1/notifications/{notificationId}/read
```

---

## 💝 八、关系模块（好感度）

### 8.1 发起关系
```http
POST /api/v1/relationships?targetUserId={对方ID}&relationType={COUPLE}
```

**relationType**: COUPLE（情侣）| FRIEND（朋友）| FAMILY（家人）

### 8.2 接收到的关系请求
```http
GET /api/v1/relationships/received
```

### 8.3 发送出去的关系请求
```http
GET /api/v1/relationships/sent
```

### 8.4 确认关系
```http
PUT /api/v1/relationships/{relationshipId}/confirm
```

### 8.5 解除关系
```http
DELETE /api/v1/relationships/{relationshipId}
```

### 8.6 我的关系列表
```http
GET /api/v1/relationships/me
```

**响应**
```json
{
    "code": 200,
    "data": [
        {
            "id": 1,
            "relationType": "COUPLE",
            "status": "CONFIRMED",
            "otherUser": {
                "id": 123,
                "nickname": "张三",
                "avatarUrl": "https://..."
            },
            "score": {
                "currentScore": 95,
                "initialScore": 100
            },
            "createdAt": "2024-01-01T00:00:00"
        }
    ]
}
```

### 8.7 查看好感度
```http
GET /api/v1/relationships/{relationshipId}/scores
```

**响应**
```json
{
    "code": 200,
    "data": {
        "currentScore": 95,
        "initialScore": 100,
        "changedScore": -5
    }
}
```

### 8.8 打分
```http
POST /api/v1/relationships/{relationshipId}/scores
    ?scoreItemId={加减分项ID}
    &reason={原因，可选}
```

### 8.9 加减分项列表
```http
GET /api/v1/relationships/{relationshipId}/score-items
```

**响应**
```json
{
    "code": 200,
    "data": [
        {
            "id": 1,
            "name": "陪我逛街",
            "score": 5,
            "description": "陪我逛街加5分"
        },
        {
            "id": 2,
            "name": "忘记纪念日",
            "score": -10,
            "description": "忘记纪念日扣10分"
        }
    ]
}
```

### 8.10 创建加减分项
```http
POST /api/v1/relationships/{relationshipId}/score-items
Content-Type: application/json
```

**请求体**
```json
{
    "name": "陪我逛街",
    "score": 5,
    "description": "陪我逛街加5分"
}
```

### 8.11 修改加减分项
```http
PUT /api/v1/relationships/{relationshipId}/score-items/{itemId}
Content-Type: application/json
```

### 8.12 删除加减分项
```http
DELETE /api/v1/relationships/{relationshipId}/score-items/{itemId}
```

### 8.13 打分记录
```http
GET /api/v1/relationships/{relationshipId}/score-records?page=1&size=20
```

**响应**
```json
{
    "code": 200,
    "data": {
        "records": [
            {
                "id": 1,
                "scorerUserId": 123,
                "scoreItemName": "陪我逛街",
                "score": 5,
                "reason": "今天陪我逛了3小时",
                "createdAt": "2024-01-01T15:30:00"
            }
        ]
    }
}
```

---

## 🌏 九、地区模块

### 9.1 获取省份列表
```http
GET /region?level=1
```

### 9.2 获取下级地区
```http
GET /region/children?code={上级地区代码}
```

**示例**
```http
GET /region/children?code=440000  # 获取广东省的城市列表
```

---

## 📁 十、文件上传模块

### 10.1 单文件上传
```http
POST /api/v1/files/upload
Content-Type: multipart/form-data
```

**表单字段**: `file`

**响应**
```json
{
    "code": 200,
    "data": "https://example.com/uploads/xxx.jpg"
}
```

### 10.2 批量上传
```http
POST /api/v1/files/upload-batch
Content-Type: multipart/form-data
```

**表单字段**: `files`（多个文件）

**响应**
```json
{
    "code": 200,
    "data": [
        "https://example.com/uploads/1.jpg",
        "https://example.com/uploads/2.jpg"
    ]
}
```

---

## 📖 十一、我的内容

### 11.1 我关注的用户
```http
GET /api/v1/me/following?page=1&size=20
```

### 11.2 我点赞的动态
```http
GET /api/v1/me/likes?page=1&size=20
```

### 11.3 我的评论
```http
GET /api/v1/me/comments?page=1&size=20
```

---

## 📝 通用说明

### 统一响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": 1750615800000
}
```

**状态码**
- `200`: 成功
- `400`: 参数错误
- `401`: 未登录或token失效
- `403`: 无权限
- `500`: 服务器错误

### 分页响应格式
```json
{
    "code": 200,
    "data": {
        "page": 1,
        "size": 20,
        "total": 100,
        "pages": 5,
        "records": []
    }
}
```

### 消息类型
| 类型 | 说明 | content字段 | 其他字段 |
|-----|------|------------|---------|
| TEXT | 文本消息 | 文本内容 | - |
| IMAGE | 图片消息 | 图片URL | - |
| VOICE | 语音消息 | 语音文件URL | duration（秒） |
| EMOJI | 表情消息 | 表情符号 | - |

### 认证说明
- 除了注册和登录接口，其他接口都需要在请求头中携带token
- Header格式: `Authorization: Bearer {token}`
- Token从登录接口获取，有效期7天

### WebSocket心跳机制
- 客户端必须每30秒发送一次PING心跳
- 服务端35秒内未收到心跳，自动判定离线
- 支持多端登录（PC、手机同时在线）

---

## 🔗 相关链接

- **Swagger文档**: `http://localhost:8090/swagger-ui.html`
- **WebSocket测试工具**: 推荐使用 [WebSocket King](https://websocketking.com/)
- **API测试工具**: 推荐使用 Postman 或 Apifox

---

## ⚡ 快速开始示例

### JavaScript完整示例
```javascript
// 1. 登录
async function login() {
    const res = await fetch('http://localhost:8090/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            account: '13800138000',
            password: '123456'
        })
    });
    const data = await res.json();
    const token = data.data.token;
    localStorage.setItem('token', token);
    return token;
}

// 2. 建立WebSocket连接
const token = localStorage.getItem('token');
const ws = new WebSocket('ws://localhost:9090/ws');

ws.onopen = () => {
    // 认证
    ws.send(JSON.stringify({ type: 'AUTH', token }));
};

ws.onmessage = (event) => {
    const msg = JSON.parse(event.data);
    console.log('收到消息:', msg);
    
    if (msg.type === 'AUTH_SUCCESS') {
        // 启动心跳
        setInterval(() => {
            ws.send(JSON.stringify({ type: 'PING' }));
        }, 30000);
    }
};

// 3. 获取会话列表
async function getConversations() {
    const res = await fetch('http://localhost:8090/api/v1/chat/conversations', {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    const data = await res.json();
    return data.data;
}

// 4. 发送消息
function sendMessage(receiverId, content) {
    ws.send(JSON.stringify({
        type: 'MESSAGE',
        receiverId: receiverId,
        content: content,
        messageType: 'TEXT'
    }));
}

// 5. 查询在线状态
async function checkOnline(userIds) {
    const res = await fetch('http://localhost:8090/api/v1/online/batch-check', {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(userIds)
    });
    const data = await res.json();
    return data.data; // {123: true, 456: false}
}
```

---

**文档版本**: v2.0  
**最后更新**: 2026-09-03
