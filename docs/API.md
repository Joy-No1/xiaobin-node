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
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOi...",
    "user": {
      "id": 1,
      "username": "user_13800138000",
      "phone": "13800138000",
      "email": null,
      "nickname": "小斌",
      "avatarUrl": "",
      "gender": "MALE",
      "bio": "这个人很懒什么都没写",
      "status": "ACTIVE",
      "birthday": null,
      "company": null,
      "school": null,
      "height": null,
      "weight": null,
      "education": null,
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "createdAt": "2026-01-01T00:00:00",
      "updatedAt": "2026-01-01T00:00:00"
    }
  }
}
```

---

### 我的信息 `GET /api/v1/users/me`

**响应：** `data` 为扁平化用户对象（含地址信息 province/city/district），格式见 [用户对象结构](#六用户对象结构)

### 编辑资料 `PUT /api/v1/users/me`

**请求：**
```json
{
  "user": { "nickname": "新昵称", "bio": "简介", "gender": "MALE" },
  "location": { "province": "广东省", "city": "深圳市", "district": "南山区" }
}
```

### 查看他人主页 `GET /api/v1/users/{id}`

**响应：** `data` 为用户主页对象（扁平化用户信息 + 当前登录用户对该用户的关注状态）

```json
{
  "id": 2,
  "username": "user_13900139000",
  "phone": "13900139000",
  "email": null,
  "nickname": "小红",
  "avatarUrl": "http://127.0.0.1:9000/xiaobin/images/abc.jpg",
  "gender": "FEMALE",
  "bio": "热爱生活",
  "status": "ACTIVE",
  "birthday": "2001-05-20",
  "company": null,
  "school": null,
  "height": 165.0,
  "weight": 50.0,
  "education": null,
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "createdAt": "2026-01-01T00:00:00",
  "updatedAt": "2026-01-01T00:00:00",
  "isFollowing": true,
  "isFollowedBy": false,
  "followStatus": "FOLLOWING"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `isFollowing` | Boolean | 当前登录用户是否已关注该用户 |
| `isFollowedBy` | Boolean | 该用户是否已关注当前登录用户 |
| `followStatus` | String | `NONE`互不关注 / `FOLLOWING`我关注了对方 / `FOLLOWER`对方关注了我 / `MUTUAL`互相关注 |

> 查看自己的主页时 `isFollowing=false, followStatus=NONE`。

### 按手机号查找 `GET /api/v1/users/search?phone=138xxxx`

> 用于发起关系前查找对方。返回扁平化用户对象，不存在返回 404。

### 批量获取用户 `POST /api/v1/users/batch`

> ⚠️ 已从 `GET ?ids=` 改为 `POST` + JSON 数组请求体。根据用户ID批量获取用户信息，返回用户对象数组。

**请求体（JSON 数组）：**
```json
[1, 2, 3]
```

**响应：** `data` 为用户对象数组。

### 行政区查询 `GET /region` / `GET /region/children?parentCode=`

> ⚡无需登录。用于注册/编辑资料时选择省市区。
> - `GET /region` — 获取所有省份
> - `GET /region/children?parentCode=xxx` — 获取指定编码的子级行政区（无parentCode返回省份）

---

## 二、字典模块

> 通用字典表，位于 `xiaobin_user` 库（`sys_dict_type` 字典类型表 / `sys_dict_item` 字典项表），支持按类型编码查询字典项。
> 以后新增其他字典（学历、职业等）时，直接往 `sys_dict_item` 插入数据即可，无需改代码。
> ⚡无需登录

### 查询字典项 `GET /api/v1/dict/items?typeCode=RELATION_TYPE` ⭐新增

> 传 `typeCode` 时只返回该类型下 `ACTIVE` 的字典项（按 `sortOrder` 升序）；不传 `typeCode` 时返回全部字典项（含禁用，用于后台管理）。

**响应：** `data` 为字典项数组：

```json
[
  {
    "id": 1,
    "typeCode": "RELATION_TYPE",
    "itemCode": "COUPLE",
    "itemName": "情侣",
    "description": "恋爱关系",
    "sortOrder": 1,
    "status": "ACTIVE",
    "createdAt": "2026-08-08T00:00:00",
    "updatedAt": "2026-08-08T00:00:00"
  }
]
```

> 前端发起关系时用此接口填充「关系类型」下拉框，返回的 `itemCode` 作为 `POST /api/v1/relationships?relationType=` 的参数值。

**预置关系类型（typeCode=RELATION_TYPE）：**

| itemCode | itemName | 说明 |
|----------|----------|------|
| `COUPLE` | 情侣 | 恋爱关系 |
| `BESTIE` | 闺蜜 | 女性密友 |
| `BUDDY` | 死党 | 铁杆兄弟 |
| `BRO` | 基友 | 男性密友 |
| `SOULMATE` | 知己 | 灵魂伴侣 |
| `FAMILY` | 家人 | 亲情关系 |
| `COLLEAGUE` | 同事 | 职场关系 |

**预置聊天消息类型（typeCode=MESSAGE_TYPE）：**

| itemCode | itemName | 说明 |
|----------|----------|------|
| `TEXT` | 文字 | 文字消息 |
| `IMAGE` | 图片 | 图片消息（content 为图片URL，前端显示缩略图、点击放大） |
| `VOICE` | 语音 | 语音消息（content 为语音URL，需传 `duration` 时长秒数，前端显示播放按钮+时长） |
| `EMOJI` | 表情 | 表情消息（content 为表情，前端大字号显示） |

### 字典类型管理

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/dict/types` | 查询所有字典类型 |
| `GET` | `/api/v1/dict/types/{id}` | 根据ID查询字典类型 |
| `POST` | `/api/v1/dict/types` | 新增字典类型（`typeCode` 需唯一） |
| `PUT` | `/api/v1/dict/types/{id}` | 修改字典类型（`typeCode` 不可与其他记录冲突） |
| `DELETE` | `/api/v1/dict/types/{id}` | 删除字典类型 |

**新增字典类型请求体：**
```json
{
  "typeCode": "EDUCATION_TYPE",
  "typeName": "学历类型",
  "description": "学历分类",
  "sortOrder": 1,
  "status": "ACTIVE"
}
```

### 字典项管理

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/v1/dict/items/{id}` | 根据ID查询字典项 |
| `POST` | `/api/v1/dict/items` | 新增字典项（`typeCode` 必须已存在，同类型下 `itemCode` 唯一） |
| `PUT` | `/api/v1/dict/items/{id}` | 修改字典项 |
| `DELETE` | `/api/v1/dict/items/{id}` | 删除字典项 |

**新增字典项请求体：**
```json
{
  "typeCode": "RELATION_TYPE",
  "itemCode": "PEN_PAL",
  "itemName": "笔友",
  "description": "书信往来朋友",
  "sortOrder": 8,
  "status": "ACTIVE"
}
```

---

## 三、关系模块

### 发起关系 `POST /api/v1/relationships?targetUserId=2&relationType=COUPLE`

> `relationType` 可选，默认 `COUPLE`（情侣）。取值来自 [查询字典项](#二字典模块) 中 `GET /api/v1/dict/items?typeCode=RELATION_TYPE` 返回的 `itemCode`（如 `BESTIE` 闺蜜、`BUDDY` 死党）。
> 服务端校验：双方都不能已有确认的关系；不能和自己建立关系

### 确认关系 `PUT /api/v1/relationships/{id}/confirm`

### 解除关系 `DELETE /api/v1/relationships/{id}`

### 我的关系 `GET /api/v1/relationships/me`

**响应：** `data` 为当前用户所有已确认的关系数组（支持情侣、闺蜜等多种关系并存）：

```json
[
  {
    "id": 1,
    "initiatorId": 1,
    "receiverId": 2,
    "status": "CONFIRMED",
    "relationType": "COUPLE",
    "createdAt": "2026-01-01T00:00:00",
    "confirmedAt": "2026-01-01T00:00:00",
    "dissolvedAt": null
  }
]
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

## 四、社区模块

### 发帖 `POST /api/v1/posts`

`Form-Data: content, images[], location`

### 帖子列表 `GET /api/v1/posts?page=1&size=10`

**响应：** `data` 为分页对象，`records` 为 PostVO 数组：

```json
{
  "page": 1, "size": 10, "total": 100, "pages": 10,
  "records": [
    {
      "id": "64f1a2b3c4d5e6f7a8b9c0d1",
      "userId": 1,
      "user": { "id": 1, "nickname": "小斌", "avatarUrl": "...", "..." : "..." },
      "content": "今天天气真好！",
      "images": ["http://..."],
      "location": "深圳",
      "likeCount": 10,
      "commentCount": 3,
      "isEdited": false,
      "status": "ACTIVE",
      "isLiked": true,
      "likers": [
        { "id": 1, "nickname": "小斌", "avatarUrl": "..." },
        { "id": 2, "nickname": "小红", "avatarUrl": "..." }
      ],
      "recentComments": [
        {
          "id": "comment-id",
          "postId": "64f1a2b3...",
          "userId": 2,
          "user": { "id": 2, "nickname": "小红", "avatarUrl": "..." },
          "content": "说得对！",
          "replyToUserId": null,
          "replyToUser": null,
          "parentCommentId": null,
          "createdAt": "2026-01-01T12:00:00"
        }
      ],
      "createdAt": "2026-01-01T10:00:00",
      "updatedAt": "2026-01-01T10:00:00"
    }
  ]
}
```

> `recentComments` 最多返回最新的 5 条评论。

### 帖子详情 `GET /api/v1/posts/{id}`

**响应格式同帖子列表中的单条帖子**，包含完整的点赞人列表和最新评论。

### 编辑帖子 `PUT /api/v1/posts/{id}` ⭐新增

`Form-Data: content, images[], location`

> 仅作者可编辑。编辑后 `isEdited` 自动变为 `true`，前端可用于展示"已编辑"标识。
> 参数均为可选，只更新传入的字段。

**响应：** 返回更新后的 Post 对象，`isEdited` 为 `true`。

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

> 关注成功后，被关注用户会收到通知（WebSocket实时推送 + 通知列表）

### 取关 `DELETE /api/v1/users/{id}/follow`

### 查询关注状态 `GET /api/v1/users/{id}/follow-status`

**响应：**
```json
{
  "code": 200,
  "data": {
    "isFollowing": true,
    "isFollowedBy": false,
    "followStatus": "FOLLOWING"
  }
}
```

### 上传文件 `POST /api/v1/files/upload` (Form-Data: file)

### 批量上传 `POST /api/v1/files/upload-batch` (Form-Data: files)

---

### 通知列表 `GET /api/v1/notifications?page=1&size=20`

**响应：**
```json
{
  "page": 1, "size": 20, "total": 5, "pages": 1,
  "records": [
    {
      "id": "64f1a2b3c4d5e6f7a8b9c0d1",
      "userId": 2,
      "type": "FOLLOW",
      "fromUserId": 1,
      "fromUser": { "id": 1, "nickname": "小斌", "avatarUrl": "..." },
      "content": "小斌 关注了你",
      "isRead": false,
      "createdAt": "2026-01-01T12:00:00"
    }
  ]
}
```

### 标记已读 `PUT /api/v1/notifications/{id}/read`

### 未读通知数 `GET /api/v1/notifications/unread-count`

**响应：** `{ "code": 200, "data": 3 }`

---

### 我的关注 `GET /api/v1/me/following`

**响应：** `data` 为关注列表数组：

```json
[
  {
    "user": { "id": 2, "nickname": "小红", "avatarUrl": "..." },
    "isMutual": true,
    "followedAt": "2026-01-01T12:00:00"
  }
]
```

| 字段 | 说明 |
|------|------|
| `user` | 被关注用户信息（UserVO） |
| `isMutual` | 是否互相关注 |
| `followedAt` | 关注时间 |

### 我的点赞 `GET /api/v1/me/likes`

**响应：** `data` 为我点赞过的帖子列表：

```json
[
  {
    "postId": "64f1a2b3...",
    "postUser": { "id": 1, "nickname": "小斌", "avatarUrl": "..." },
    "content": "帖子内容",
    "images": ["http://..."],
    "likeCount": 10,
    "commentCount": 3,
    "isEdited": false,
    "postCreatedAt": "2026-01-01T10:00:00",
    "likedAt": "2026-01-02T10:00:00"
  }
]
```

> 已删除的帖子会被过滤掉。

### 我的评论 `GET /api/v1/me/comments`

**响应：** `data` 为我发表过的评论列表：

```json
[
  {
    "id": "comment-id",
    "postId": "64f1a2b3...",
    "postUser": { "id": 1, "nickname": "小斌", "avatarUrl": "..." },
    "postContent": "帖子内容摘要",
    "content": "评论内容",
    "createdAt": "2026-01-01T12:00:00"
  }
]
```

---

## 五、聊天模块

> 会话采用三表设计：`conversation`（会话本体）+ `conversation_member`（每个用户独立的未读/置顶/免打扰/已读进度）+ `message`（消息，不存 receiverId/isRead，已读进度在 `conversation_member.last_read_message_id`）。

### 会话列表 `GET /api/v1/chat/conversations`

> 返回当前用户所有会话，已组装好「我在会话中的状态」和「私聊对方用户信息」，前端无需再判断哪个是我/对方。
> 排序：置顶优先，其次最后消息时间倒序。

**响应：**
```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "type": "PRIVATE",
      "lastMessageId": 10,
      "lastMessage": "你好！",
      "lastMessageTime": "2026-01-01T12:00:00",
      "createdAt": "2026-01-01T00:00:00",
      "updatedAt": "2026-01-01T12:00:00",
      "lastReadMessageId": 10,
      "unreadCount": 0,
      "isPinned": 0,
      "isMuted": 0,
      "otherUser": { "id": 2, "nickname": "小斌", "avatarUrl": "..." }
    }
  ]
}
```

### 创建会话 `POST /api/v1/chat/conversations?targetUserId=2`

> ⚠️ **解决"互关但无聊天记录"的问题**：互关后点击"发消息"，前端先调用此接口创建/获取会话（幂等），再进入聊天页。
> 仅互关用户可创建，若已存在则返回已有会话。

**响应：** 返回 `ConversationDTO`（结构同会话列表的单条，`otherUser` 为目标用户）。

### 消息历史 `GET /api/v1/chat/conversations/{id}/messages?page=1&size=20`

**响应记录倒序，前端需要反转显示**

> 消息对象：`id`、`conversationId`、`senderId`、`content`、`messageType`、`duration`（语音时长，其他类型为 null）、`createdAt`。
> 不再返回 `receiverId`/`isRead`：接收方由会话成员推断，已读状态由会话列表的 `unreadCount`/`lastReadMessageId` 表达。

### 发送消息 `POST /api/v1/chat/conversations/{id}/messages?content=你好&messageType=TEXT&duration=`

> 在指定会话发送消息，接收方由会话成员自动推断（无需传 receiverId）。仅互关用户可发送。
> `messageType` 可选：`TEXT` 文字 / `IMAGE` 图片 / `VOICE` 语音 / `EMOJI` 表情，默认 `TEXT`。
> `duration` 仅语音消息需要，单位秒，如 `duration=3`。
> 也会更新会话的 `lastMessageId`、`lastMessage` 和 `lastMessageTime`（图片/语音消息预览显示为 `[图片]`/`[语音]`）。

**响应：** 返回 `ChatMessageDTO`（id、conversationId、senderId、content、messageType、duration、createdAt）

**发送示例：**
```bash
# 文字
POST /api/v1/chat/conversations/1/messages?content=你好！
# 图片（content 为图片URL）
POST /api/v1/chat/conversations/1/messages?content=https://xxx/a.jpg&messageType=IMAGE
# 语音（content 为语音URL）
POST /api/v1/chat/conversations/1/messages?content=https://xxx/a.m4a&messageType=VOICE&duration=3
# 表情
POST /api/v1/chat/conversations/1/messages?content=%F0%9F%98%8D&messageType=EMOJI
```

### 标记已读 `PUT /api/v1/chat/conversations/{id}/read`

> 将该会话的 `lastReadMessageId` 推进到当前最大消息ID，并把 `unreadCount` 清零。

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
{ "type": "MESSAGE", "receiverId": 2, "content": "https://xxx/a.m4a", "messageType": "VOICE", "duration": 3 }
```
← `{ "type": "MESSAGE_SENT", "messageId": "abc", "createdAt": "..." }`

**③ 收到消息（对方发的）：**
```json
{ "type": "NEW_MESSAGE", "messageId": "abc", "senderId": 2, "content": "你好！", "messageType": "TEXT", "duration": null, "createdAt": "..." }
```

**④ 心跳：**
```json
{ "type": "PING" }
```
← `{ "type": "PONG" }`

**⑤ 关注通知（被关注时收到）：**
```json
{
  "type": "NEW_FOLLOWER",
  "notificationId": "64f1a2b3...",
  "fromUserId": 1,
  "fromUser": { "id": 1, "nickname": "小斌", "avatarUrl": "..." },
  "content": "小斌 关注了你",
  "createdAt": "2026-01-01T12:00:00"
}
```

> 用户被关注时，服务端通过 WebSocket 实时推送 `NEW_FOLLOWER` 消息，前端可据此弹出提示并更新未读通知角标。未在线用户上线后可通过 `GET /api/v1/notifications` 拉取。

**限制：** 仅互关用户可聊天，互关检测由服务端校验

---

## 六、用户对象结构

> 所有返回用户信息的接口均使用以下扁平化结构，不再使用 `{ user: {...}, location: {...} }` 的嵌套格式。

```json
{
  "id": 1,
  "username": "user_13800138000",
  "phone": "13800138000",
  "email": null,
  "nickname": "小斌",
  "avatarUrl": "http://127.0.0.1:9000/xiaobin/images/abc.jpg",
  "gender": "MALE",
  "bio": "这个人很懒什么都没写",
  "status": "ACTIVE",
  "birthday": "2000-01-01",
  "company": "某某公司",
  "school": "某某大学",
  "height": 175.0,
  "weight": 65.0,
  "education": "BACHELOR",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "createdAt": "2026-01-01T00:00:00",
  "updatedAt": "2026-01-01T00:00:00"
}
```

> ⚠️ `password` 字段永远不返回。地址信息（province/city/district）已扁平化到用户对象中。

---

## 七、帖子对象结构（PostVO）

```json
{
  "id": "64f1a2b3c4d5e6f7a8b9c0d1",
  "userId": 1,
  "user": { /* 用户对象，发帖人信息 */ },
  "content": "帖子正文内容",
  "images": ["http://image1.jpg", "http://image2.jpg"],
  "location": "深圳",
  "likeCount": 10,
  "commentCount": 3,
  "isEdited": false,
  "status": "ACTIVE",
  "isLiked": true,
  "likers": [ /* 点赞人用户对象数组 */ ],
  "recentComments": [ /* CommentVO 数组，最新5条 */ ],
  "createdAt": "2026-01-01T10:00:00",
  "updatedAt": "2026-01-01T10:00:00"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `isEdited` | Boolean | ⭐ 是否被编辑过，`true` 时前端展示"已编辑"标识 |
| `isLiked` | Boolean | 当前登录用户是否已点赞 |
| `likers` | UserVO[] | 点赞人列表（含用户信息） |
| `recentComments` | CommentVO[] | 最新评论列表（含评论人信息，最多5条） |
| `user` | UserVO | 发帖人信息 |

---

## 八、评论对象结构（CommentVO）

```json
{
  "id": "comment-id",
  "postId": "64f1a2b3...",
  "userId": 2,
  "user": { /* 用户对象，评论人信息 */ },
  "content": "评论内容",
  "replyToUserId": 1,
  "replyToUser": { /* 用户对象，被回复人信息 */ },
  "parentCommentId": "parent-comment-id",
  "createdAt": "2026-01-01T12:00:00"
}
```

---

## 九、OpenFeign 远程调用接口

> `xiaobin-api` 同时承担两层职责：
> 1. **对外聚合**：`com.xml.xiaobinnode.api.controller` 下 `/api/v1/**` 对外接口，统一封装 `Result`；
> 2. **内部 Feign 契约**：`com.xml.xiaobinnode.api.feign.*` 下各业务服务的 Feign 客户端，路径**不带 `/api`、不带 `/v1`**（仅作为服务间调用目标）。
>
> 业务服务（user/chat/community/relationship）的 Controller 只暴露内部路径（如 `/users`、`/chat`）；需要认证的接口通过 `X-User-Id` 请求头传递当前用户ID。
> 公共 Feign DTO 已统一迁移至 `xiaobin-common` 的 `com.xml.xiaobinnode.common.dto`。

### 包结构

```
com.xml.xiaobinnode.api
├── controller/             # 聚合对外接口（/api/v1/**）
│   ├── AuthAggController.java
│   ├── UserAggController.java
│   ├── ChatAggController.java
│   ├── RelationshipAggController.java
│   ├── CommunityAggController.java
│   ├── DictAggController.java
│   ├── RegionAggController.java
│   └── FileAggController.java
└── feign/                  # 内部 Feign 客户端（路径无 /api、无 /v1）
    ├── auth/AuthFeignClient.java
    ├── user/UserFeignClient.java
    ├── relationship/RelationshipFeignClient.java
    ├── community/CommunityFeignClient.java
    ├── chat/ChatFeignClient.java
    ├── dict/DictFeignClient.java
    └── region/RegionFeignClient.java
```

### 8.1 用户服务 (`xiaobin-user`，内部路径前缀 `/users`)

| 方法 | 路径 | 说明 |
|------|------|------|
| `getCurrentUser` | `GET /users/me` | 获取当前用户信息，需传 `X-User-Id` 头 |
| `getUserById` | `GET /users/{id}` | 根据ID获取用户（返回 UserProfileVO：含关注状态） |
| `searchByPhone` | `GET /users/search?phone=` | 按手机号查找用户 |
| `getUsersByIds` | `POST /users/batch` | 批量获取用户（body 为 JSON 数组 `List<Long>`） |
| `updateCurrentUser` | `PUT /users/me` | 更新当前用户信息 |

### 8.2 关系服务 (`xiaobin-relationship`，内部路径前缀 `/relationships`)

| 方法 | 路径 | 说明 |
|------|------|------|
| `createRelationship` | `POST /relationships` | 发起关系请求（可选传 `relationType`，默认 COUPLE） |
| `getReceived` | `GET /relationships/received` | 收到的关系请求 |
| `getSent` | `GET /relationships/sent` | 发送的关系请求 |
| `confirmRelationship` | `PUT /relationships/{id}/confirm` | 确认关系 |
| `dissolveRelationship` | `DELETE /relationships/{id}` | 解除关系 |
| `getMyRelationship` | `GET /relationships/me` | 获取我的关系（返回 RelationshipVO，含对方用户） |
| `getPartnerInfo` | `GET /relationships/user/{userId}/partner` | 获取用户伴侣信息 |
| `getScores` | `GET /relationships/{id}/scores` | 查看好感度 |
| `score` | `POST /relationships/{id}/scores` | 打分 |
| `getScoreItems` | `GET /relationships/{id}/score-items` | 加减分项列表 |
| `createScoreItem` | `POST /relationships/{id}/score-items` | 创建加减分项 |
| `updateScoreItem` | `PUT /relationships/{id}/score-items/{itemId}` | 修改加减分项 |
| `deleteScoreItem` | `DELETE /relationships/{id}/score-items/{itemId}` | 删除加减分项 |
| `getScoreRecords` | `GET /relationships/{id}/score-records` | 打分记录（分页） |

### 8.3 社区服务 (`xiaobin-community`，内部路径无前缀)

| 方法 | 路径 | 说明 |
|------|------|------|
| `createPost` | `POST /posts` | 发帖 |
| `getPostList` | `GET /posts` | 帖子列表（分页，返回 PostDTO） |
| `getPost` | `GET /posts/{id}` | 帖子详情 |
| `editPost` | `PUT /posts/{id}` | 编辑帖子 |
| `deletePost` | `DELETE /posts/{id}` | 删除帖子 |
| `likePost` | `POST /posts/{id}/likes` | 点赞 |
| `unlikePost` | `DELETE /posts/{id}/likes` | 取消点赞 |
| `addComment` | `POST /posts/{id}/comments` | 发表评论 |
| `getComments` | `GET /posts/{id}/comments` | 评论列表（分页） |
| `deleteComment` | `DELETE /comments/{id}` | 删除评论 |
| `follow` | `POST /users/{id}/follow` | 关注用户 |
| `unfollow` | `DELETE /users/{id}/follow` | 取消关注 |
| `getFollowStatus` | `GET /users/{id}/follow-status` | 查询关注状态（返回 FollowStatusDTO） |
| `getNotifications` | `GET /notifications` | 通知列表（分页，返回 NotificationDTO） |
| `getUnreadCount` | `GET /notifications/unread-count` | 未读通知数 |
| `markNotificationRead` | `PUT /notifications/{id}/read` | 标记通知已读 |
| `getMyFollowing` | `GET /me/following` | 我的关注列表（返回 MyFollowDTO） |
| `getMyLikes` | `GET /me/likes` | 我的点赞帖子列表（返回 MyLikeDTO） |
| `getMyComments` | `GET /me/comments` | 我的评论列表（返回 MyCommentDTO） |
| `uploadFile` | `POST /files/upload` | 单文件上传（multipart，返回URL） |
| `uploadFiles` | `POST /files/upload-batch` | 批量上传（multipart，返回URL列表） |

### 8.4 聊天服务 (`xiaobin-chat`，内部路径前缀 `/chat`)

| 方法 | 路径 | 说明 |
|------|------|------|
| `getConversations` | `GET /chat/conversations` | 会话列表 |
| `createConversation` | `POST /chat/conversations` | 创建会话（幂等，仅互关） |
| `getMessages` | `GET /chat/conversations/{id}/messages` | 消息历史（分页，倒序） |
| `sendMessage` | `POST /chat/conversations/{id}/messages` | 发送消息（可选传 `messageType` 默认 TEXT，VOICE 需传 `duration`） |
| `markAsRead` | `PUT /chat/conversations/{id}/read` | 标记已读 |

### 使用示例

```java
// 注入 Feign 客户端
@Autowired
private UserFeignClient userFeignClient;

// 调用（Feign 返回裸数据；统一 Result 封装由 xiaobin-api 对外完成）
UserProfileVO user = userFeignClient.getUserById(1L);

// 批量查询
List<UserVO> batch = userFeignClient.getUsersByIds(List.of(1L, 2L, 3L));
```
