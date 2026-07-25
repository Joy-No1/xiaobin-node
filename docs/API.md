# 好感度记账APP - 接口文档

> Base URL: `http://localhost:8080/api/v1`
> 认证方式: `Authorization: Bearer {token}`

---

## 通用约定

### 统一响应格式
```json
{
  "code": 200,       // 200成功 400参数错误 401未登录 500服务器错误
  "message": "success",
  "data": { },
  "timestamp": 1750615800000
}
```

### 分页响应格式
```json
{
  "code": 200,
  "data": {
    "page": 1, "size": 10, "total": 100, "pages": 10,
    "records": []
  }
}
```

---

## 一、用户模块

### 注册 `POST /api/v1/auth/register` ⚡无需登录

**请求：**
```json
{
  "phone": "13800138000",
  "password": "123456",
  "nickname": "小斌"
}
```

**响应：** `data` 为用户对象

---

### 登录 `POST /api/v1/auth/login` ⚡无需登录

**请求：**
```json
{
  "account": "13800138000",
  "password": "123456"
}
```

**响应：**
```json
{
  "token": "eyJhbGciOi...",
  "user": { "id": 1, "phone": "138...", "nickname": "小斌", "avatarUrl": "" }
}
```

---

### 我的信息 `GET /api/v1/users/me`

**响应：** `data` 为用户对象

### 编辑资料 `PUT /api/v1/users/me`

**请求：** `{ "nickname": "新昵称", "bio": "简介", "gender": "MALE" }`

### 查看他人主页 `GET /api/v1/users/{id}`

### 按手机号查找 `GET /api/v1/users/search?phone=138xxxx`

> 用于发起关系前查找对方。返回用户信息，不存在返回 404。

---

## 二、关系模块

### 发起关系 `POST /api/v1/relationships?targetUserId=2`

> 服务端校验：双方都不能已有确认的关系；不能和自己建立关系

### 确认关系 `PUT /api/v1/relationships/{id}/confirm`

### 解除关系 `DELETE /api/v1/relationships/{id}`

### 我的关系 `GET /api/v1/relationships/me`

**响应：**
```json
{ "id": 1, "user1Id": 1, "user2Id": 2, "status": "CONFIRMED" }
```

---

### 查看好感度 `GET /api/v1/relationships/{id}/scores`

**响应：**
```json
{
  "relationshipId": 1, "scorerId": 1, "targetId": 2, "currentScore": 110
}
```

---

### 打分 `POST /api/v1/relationships/{id}/scores?scoreItemId=1&reason=`

**响应：**
```json
{
  "scorerId": 1, "targetId": 2, "scoreChange": 10,
  "scoreBefore": 100, "scoreAfter": 110, "reason": "做了好吃的饭"
}
```

---

### 自定义加减分项

**创建：** `POST /api/v1/relationships/{id}/score-items`
```json
{ "itemName": "做了好吃的饭", "scoreValue": 10, "type": "ADD", "icon": "🍳" }
```

**列表：** `GET /api/v1/relationships/{id}/score-items`

**修改：** `PUT /api/v1/relationships/{id}/score-items/{itemId}`

**删除：** `DELETE /api/v1/relationships/{id}/score-items/{itemId}`

---

### 打分历史 `GET /api/v1/relationships/{id}/score-records?page=1&size=20`

---

## 三、社区模块

### 发帖 `POST /api/v1/posts`

`Form-Data: content, images[], location`

### 帖子列表 `GET /api/v1/posts?page=1&size=10`

### 帖子详情 `GET /api/v1/posts/{id}`

### 删除帖子 `DELETE /api/v1/posts/{id}`

### 点赞 `POST /api/v1/posts/{id}/likes`

### 取消点赞 `DELETE /api/v1/posts/{id}/likes`

### 评论 `POST /api/v1/posts/{id}/comments`
```
?content=说得好&replyToUserId=2&parentCommentId=xxx
```

### 评论列表 `GET /api/v1/posts/{id}/comments?page=1&size=20`

### 删除评论 `DELETE /api/v1/comments/{id}`

### 关注 `POST /api/v1/users/{id}/follow`

### 取关 `DELETE /api/v1/users/{id}/follow`

### 上传文件 `POST /api/v1/files/upload` (Form-Data: file)

### 批量上传 `POST /api/v1/files/upload-batch` (Form-Data: files)

---

## 四、聊天模块

### 会话列表 `GET /api/v1/chat/conversations`

### 消息历史 `GET /api/v1/chat/conversations/{id}/messages?page=1&size=20`

**响应记录倒序，前端需要反转显示**

### 标记已读 `PUT /api/v1/chat/conversations/{id}/read`

---

### WebSocket 实时聊天

> 连接: `ws://localhost:9090/ws`（非网关端口）

**① 认证：**
```json
{ "type": "AUTH", "token": "eyJhbGciOi..." }
```
← `{ "type": "AUTH_SUCCESS", "userId": "1" }`

**② 发送消息：**
```json
{ "type": "MESSAGE", "receiverId": 2, "content": "你好！", "messageType": "TEXT" }
```
← `{ "type": "MESSAGE_SENT", "messageId": "abc", "createdAt": "..." }`

**③ 收到消息（对方发的）：**
```json
{ "type": "NEW_MESSAGE", "messageId": "abc", "senderId": 2, "content": "你好！", "createdAt": "..." }
```

**④ 心跳：**
```json
{ "type": "PING" }
```
← `{ "type": "PONG" }`

**限制：** 仅互关用户可聊天，互关检测由服务端校验

---

## 五、用户对象结构

```json
{
  "id": 1,
  "username": "user_13800138000",
  "phone": "13800138000",
  "nickname": "小斌",
  "avatarUrl": "http://127.0.0.1:9000/xiaobin/images/abc.jpg",
  "gender": "MALE",
  "bio": "这个人很懒什么都没写",
  "status": "ACTIVE"
}
```

> ⚠️ `password` 字段永远不返回
