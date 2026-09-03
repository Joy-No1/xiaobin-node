# 好感度记账APP - 后端服务

> 情侣好感度记录与社交APP的后端微服务系统

## 📋 项目简介

这是一款专为情侣设计的**好感度记录与社交APP**后端服务。两个人建立情侣关系后，各自在对方心中有100分初始好感度，可以通过日常互动给对方打分，同时支持广场社区、点赞评论、互关聊天等社交功能。

### 核心功能

| 功能模块 | 描述 |
|---------|------|
| 💑 **情侣关系** | 建立/确认情侣关系，支持解除 |
| ⭐ **好感度打分** | 自定义加减分项目，给对方打分（不低于0，不设上限） |
| 📝 **分数记录** | 完整的打分历史记录，清晰透明 |
| 🏘️ **广场社区** | 发布文字+图片动态，支持点赞和评论 |
| 👥 **关注系统** | 关注/取关用户，自动检测互关状态 |
| 💬 **实时聊天** | 基于Netty的WebSocket实时通信，仅互关用户可聊 |

---

## 🏗️ 技术架构

### 技术栈

| 层面 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Spring Boot | 4.1.0 |
| 微服务治理 | Spring Cloud + Alibaba | 2025.0.0 / 2024.0.1.0 |
| 注册/配置中心 | Nacos | 2.x |
| API网关 | Spring Cloud Gateway | - |
| ORM | MyBatis-Plus | 3.5.10 |
| 关系型数据库 | MySQL | 8.0+ |
| 文档数据库 | MongoDB | 5.0+ |
| 缓存 | Redis | 6.0+ |
| 对象存储 | MinIO | - |
| 实时通信 | Netty WebSocket | 4.1.118 |
| 服务间调用 | OpenFeign | - |
| 认证授权 | JWT (jjwt) | 0.12.6 |
| API文档 | SpringDoc (Swagger) | 2.8.0 |
| 工具库 | HuTool | 5.8.34 |

### 微服务架构图

```
                    ┌─────────────┐
                    │   客户端APP   │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │   Gateway   │  ← 统一入口，JWT认证，路由 /api/**、/region/**
                    │   :8080     │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │ xiaobin-api  │  ← 聚合服务 :8090：统一封装 Result，Feign 转发
                    └──────┬──────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
    ┌─────▼─────┐   ┌─────▼─────┐   ┌─────▼─────┐
    │   User    │   │ Relation  │   │ Community │
    │  Service  │   │  Service  │   │  Service  │
    │  :8081    │   │  :8082    │   │  :8083    │
    └─────┬─────┘   └─────┬─────┘   └─────┬─────┘
          │                │                │
          │         ┌──────┴──────┐         │
          │         │    Chat     │         │
          │         │  Service    │         │
          │         │ REST:8084   │         │
          │         │  WS:9090    │         │
          │         └──────┬──────┘         │
          │                │                │
    ┌─────┴────────────────┴────────────────┴─────┐
    │              中间件 & 存储                       │
    │  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐    │
    │  │ MySQL │  │MongoDB│  │Redis │  │MinIO │    │
    │  └──────┘  └──────┘  └──────┘  └──────┘    │
    │  ┌──────────────────┐                       │
    │  │      Nacos       │   注册中心 + 配置中心    │
    │  └──────────────────┘                       │
    └─────────────────────────────────────────────┘
```

> **路径约定**：对外统一入口走 `网关(:8080) /api/v1/**` → `xiaobin-api(:8090)` 聚合转发。
> 各业务服务（user/chat/community/relationship）的 Controller 不再带 `/api`、`/v1` 前缀（如 `/users`、`/chat`），仅作为 Feign 内部调用目标；
> `/api` 与 `/v1` 只保留在 `xiaobin-api` 的对外 Controller 上。返回封装（`Result`）统一在 `xiaobin-api` 完成，网关不再包装。

---

## 📦 项目结构

```
xiaobin-node/
├── pom.xml                          # 父POM（聚合项目，依赖管理）
├── README.md                        # 本文档
├── docs/
│   └── init.sql                     # MySQL数据库初始化脚本
│
├── xiaobin-common/                  # 公共模块（库）
│   └── src/main/java/.../common/
│       ├── annotation/NoAuth.java   # 无需认证注解
│       ├── config/
│       │   ├── JwtConfig.java       # JWT配置
│       │   ├── MyBatisPlusConfig.java # MyBatis-Plus配置
│       │   ├── RedisConfig.java     # Redis配置
│       │   ├── SwaggerConfig.java   # Swagger文档配置
│       │   ├── UserContextInterceptor.java # 用户上下文拦截器
│       │   └── WebMvcConfig.java    # Web MVC配置
│       ├── constant/CommonConstants.java # 公共常量
│       ├── dto/
│       │   ├── PageResult.java      # 分页响应体
│       │   └── Result.java          # 统一响应体
│       ├── exception/
│       │   ├── BusinessException.java # 业务异常
│       │   └── GlobalExceptionHandler.java # 全局异常处理
│       └── util/
│           ├── JwtUtils.java        # JWT工具类
│           └── UserContext.java     # 用户上下文（ThreadLocal）
│
├── xiaobin-api/                     # 聚合API服务 (:8090)
│   └── src/main/java/.../
│       ├── api/ApiApplication.java  # 启动类
│       ├── api/controller/          # 对外聚合 Controller（/api/v1/**，统一封装 Result）
│       │   ├── AuthAggController.java
│       │   ├── UserAggController.java
│       │   ├── ChatAggController.java
│       │   ├── RelationshipAggController.java
│       │   ├── CommunityAggController.java
│       │   ├── DictAggController.java
│       │   ├── RegionAggController.java
│       │   └── FileAggController.java
│       └── api/feign/               # 各业务服务的 Feign 客户端
│           ├── auth/AuthFeignClient.java
│           ├── user/UserFeignClient.java
│           ├── chat/ChatFeignClient.java
│           ├── relationship/RelationshipFeignClient.java
│           ├── community/CommunityFeignClient.java
│           ├── dict/DictFeignClient.java
│           └── region/RegionFeignClient.java
│
├── xiaobin-gateway/                 # API网关服务 (:8080) 只做 JWT认证+路由
│   └── src/main/java/.../gateway/
│       ├── GatewayApplication.java
│       ├── config/GatewayConfig.java # 路由（/api/**、/region/** → lb://xiaobin-api）+ CORS
│       └── filter/AuthFilter.java    # JWT认证过滤器
│
├── xiaobin-user/                    # 用户服务 (:8081)
│   └── src/main/java/.../user/
│       ├── UserApplication.java
│       ├── controller/
│       │   ├── AuthController.java    # 注册/登录接口
│       │   └── UserController.java    # 个人信息接口
│       ├── dto/
│       │   ├── LoginRequest.java
│       │   └── RegisterRequest.java
│       ├── entity/User.java
│       ├── mapper/UserMapper.java
│       └── service/UserService.java + impl
│
├── xiaobin-relationship/            # 关系服务 (:8082)
│   └── src/main/java/.../relationship/
│       ├── RelationshipApplication.java
│       ├── controller/RelationshipController.java
│       ├── entity/
│       │   ├── Relationship.java      # 关系实体
│       │   ├── Score.java             # 好感度分数实体
│       │   ├── ScoreItem.java         # 自定义加减分项
│       │   └── ScoreRecord.java       # 打分记录
│       ├── mapper/ (×4)
│       └── service/RelationshipService.java + impl
│
├── xiaobin-community/               # 社区服务 (:8083)
│   └── src/main/java/.../community/
│       ├── CommunityApplication.java
│       ├── config/MinioConfig.java
│       ├── controller/CommunityController.java
│       ├── document/                  # MongoDB文档
│       │   ├── Post.java
│       │   ├── Comment.java
│       │   └── PostLike.java
│       ├── entity/Follow.java         # MySQL关注实体
│       ├── mapper/FollowMapper.java
│       ├── repository/ (×3)           # MongoDB Repository
│       └── service/CommunityService.java + impl
│
└── xiaobin-chat/                    # 聊天服务 (:8084 REST, :9090 WS)
    └── src/main/java/.../chat/
        ├── ChatApplication.java
        ├── controller/ChatController.java
        ├── entity/
        │   ├── ChatMessage.java
        │   ├── Conversation.java
        │   └── ConversationMember.java
        ├── mapper/ (×3)
        ├── service/ChatService.java + impl
        └── websocket/
            ├── ChatWebSocketHandler.java  # 消息处理
            └── NettyWebSocketServer.java  # Netty服务器
```

---

## 🗄️ 数据存储设计

### MySQL（结构化数据）

| 表名 | 用途 | 关键字段 |
|------|------|---------|
| `user` | 用户信息 | id, phone, email, password, nickname, avatar_url |
| `relationship` | 情侣关系 | id, user1_id, user2_id, status(PENDING/CONFIRMED/DISSOLVED) |
| `score` | 好感度分数 | relationship_id, scorer_id, target_id, current_score(默认100) |
| `score_item` | 自定义加减分项 | user_id, item_name, score_value, type(ADD/SUBTRACT) |
| `score_record` | 打分记录 | scorer_id, target_id, score_change, reason, score_before/after |
| `follow` | 关注关系 | follower_id, followee_id, status(FOLLOWING/MUTUAL) |
| `conversation` | 聊天会话 | id, type, private_key(幂等键), last_message_id, last_message, last_message_time |
| `conversation_member` | 会话参与者 | conversation_id, user_id, last_read_message_id, unread_count, is_pinned, is_muted |
| `message` | 聊天消息 | conversation_id, sender_id, content, message_type, duration |

### MongoDB（半结构化数据）

| 集合 | 用途 | 文档结构 |
|------|------|---------|
| `posts` | 广场帖子 | userId, content, images[], location, likeCount, commentCount |
| `comments` | 评论 | postId, userId, content, replyToUserId, parentCommentId |
| `post_likes` | 点赞记录 | postId, userId |

### Redis（缓存）

| Key Pattern | 用途 | TTL |
|-------------|------|-----|
| `token:{userId}` | JWT Token 白名单 | 7天 |
| `verify_code:{phone}` | 短信验证码 | 5分钟 |
| `online:{userId}` | 用户在线状态 | 30分钟 |
| `post:like:{postId}` | 帖子点赞用户Set | 永久 |
| `mutual_follow:{userId}` | 互关用户Set | 永久 |
| `rate_limit:{ip}:{api}` | 限流计数 | 1分钟 |

---

## 🔌 API 接口文档

所有接口通过 Gateway 统一入口，启用Swagger后访问：
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

### 认证接口 `POST /api/v1/auth`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/auth/register` | 手机号注册 | ❌ |
| POST | `/api/v1/auth/login` | 账号密码登录 | ❌ |

**注册请求：**
```json
{
  "phone": "13800138000",
  "password": "123456",
  "nickname": "小斌"
}
```

**登录请求：**
```json
{
  "account": "13800138000",
  "password": "123456"
}
```

**登录响应：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "phone": "13800138000",
      "nickname": "小斌",
      "avatarUrl": "",
      "gender": null,
      "bio": null
    }
  }
}
```

### 用户接口 `GET/PUT /api/v1/users`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/users/me` | 获取当前用户信息 | ✅ |
| PUT | `/api/v1/users/me` | 更新个人信息 | ✅ |
| GET | `/api/v1/users/{id}` | 查看用户主页 | ✅ |

### 关系接口 `POST/GET/PUT/DELETE /api/v1/relationships`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/relationships` | 发起关系请求 `?targetUserId=2` | ✅ |
| PUT | `/api/v1/relationships/{id}/confirm` | 确认关系 | ✅ |
| DELETE | `/api/v1/relationships/{id}` | 解除关系 | ✅ |
| GET | `/api/v1/relationships/me` | 获取我的当前关系 | ✅ |
| GET | `/api/v1/relationships/{id}/scores` | 查看好感度 | ✅ |
| POST | `/api/v1/relationships/{id}/scores` | 打分 `?scoreItemId=1&reason=` | ✅ |
| GET | `/api/v1/relationships/{id}/score-items` | 查看自定义加减分项 | ✅ |
| POST | `/api/v1/relationships/{id}/score-items` | 创建加减分项 | ✅ |
| PUT | `/api/v1/relationships/{id}/score-items/{itemId}` | 修改加减分项 | ✅ |
| DELETE | `/api/v1/relationships/{id}/score-items/{itemId}` | 删除加减分项 | ✅ |
| GET | `/api/v1/relationships/{id}/score-records` | 打分历史记录 | ✅ |

**打分业务规则：**
- 初始好感度：100分
- 加分无上限
- 扣分不低于0
- 不能给自己打分
- 必须使用自定义打分项目进行打分

**创建加减分项示例：**
```json
{
  "itemName": "做了一顿好吃的饭",
  "scoreValue": 10,
  "type": "ADD",
  "icon": "🍳"
}
```

### 社区接口 `POST/GET/DELETE /api/v1/posts`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/v1/posts` | 发帖 | ✅ |
| GET | `/api/v1/posts` | 广场帖子列表 `?page=1&size=10` | ✅ |
| GET | `/api/v1/posts/{id}` | 帖子详情 | ✅ |
| DELETE | `/api/v1/posts/{id}` | 删除帖子 | ✅ |
| POST | `/api/v1/posts/{id}/likes` | 点赞 | ✅ |
| DELETE | `/api/v1/posts/{id}/likes` | 取消点赞 | ✅ |
| POST | `/api/v1/posts/{id}/comments` | 评论 `?content=&replyToUserId=&parentCommentId=` | ✅ |
| DELETE | `/api/v1/comments/{id}` | 删除评论 | ✅ |
| GET | `/api/v1/posts/{id}/comments` | 评论列表 `?page=1&size=20` | ✅ |
| POST | `/api/v1/users/{id}/follow` | 关注用户 | ✅ |
| DELETE | `/api/v1/users/{id}/follow` | 取消关注 | ✅ |
| POST | `/api/v1/files/upload` | 上传文件（图片） | ✅ |
| POST | `/api/v1/files/upload-batch` | 批量上传 | ✅ |

### 聊天接口 `GET /api/v1/chat`

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/v1/chat/conversations` | 会话列表 | ✅ |
| GET | `/api/v1/chat/conversations/{id}/messages` | 消息历史 `?page=1&size=20` | ✅ |
| PUT | `/api/v1/chat/conversations/{id}/read` | 标记已读 | ✅ |
| DELETE | `/api/v1/chat/conversations/{id}` | 删除会话 | ✅ |

**WebSocket 连接：** `ws://localhost:9090/ws`

### WebSocket 消息协议

**1. 认证：**
```json
{"type": "AUTH", "token": "eyJhbGciOiJIUzI1NiJ9..."}
```

**2. 发送消息：**
```json
{
  "type": "MESSAGE",
  "receiverId": 2,
  "content": "你今天真好看！",
  "messageType": "TEXT"
}
```

**3. 收到新消息：**
```json
{
  "type": "NEW_MESSAGE",
  "messageId": "abc123",
  "senderId": 2,
  "content": "谢谢你~",
  "messageType": "TEXT",
  "createdAt": "2026-07-12T10:30:00"
}
```

**4. 心跳：**
```json
{"type": "PING"}
{"type": "PONG"}
```

---

## 🚀 快速开始

### 前置条件

| 软件 | 版本要求 | 端口 |
|------|---------|------|
| JDK | 21+ | - |
| Maven | 3.8+ | - |
| MySQL | 8.0+ | 3306 |
| MongoDB | 5.0+ | 27017 |
| Redis | 6.0+ | 6379 |
| Nacos | 2.x | 8848 |
| MinIO | - | 9000 |

### 1. 启动中间件

```bash
# 启动 Redis
redis-server

# 启动 MongoDB
mongod --dbpath /data/db

# 启动 Nacos（单机模式）
sh nacos/bin/startup.sh -m standalone

# 启动 MinIO
minio server /data/minio
```

### 2. 初始化数据库

```bash
mysql -u root -p < docs/init.sql
```

### 3. 编译项目

```bash
cd xiaobin-node
mvn clean install -DskipTests
```

### 4. 启动服务（按顺序）

```bash
# 方式一：IDE中依次启动各模块的 Application 类

# 方式二：命令行启动
mvn -pl xiaobin-gateway spring-boot:run
mvn -pl xiaobin-user spring-boot:run
mvn -pl xiaobin-relationship spring-boot:run
mvn -pl xiaobin-community spring-boot:run
mvn -pl xiaobin-chat spring-boot:run
```

### 5. 验证

访问 Swagger 文档：http://localhost:8080/swagger-ui.html

---

## 📐 统一响应格式

所有接口返回统一格式：

```json
{
  "code": 200,
  "message": "success",
  "data": { },
  "timestamp": 1750615800000
}
```

**状态码：**
| Code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**分页响应：**
```json
{
  "code": 200,
  "data": {
    "page": 1,
    "size": 10,
    "total": 100,
    "pages": 10,
    "records": []
  }
}
```

---

## 🔐 认证流程

```
客户端                        Gateway                      业务服务
  │                              │                             │
  │  POST /api/v1/auth/login     │                             │
  │─────────────────────────────>│                             │
  │                              │  转发到 xiaobin-user         │
  │                              │────────────────────────────>│
  │                              │                             │
  │                              │    返回 JWT Token            │
  │                              │<────────────────────────────│
  │  返回 { token, user }        │                             │
  │<─────────────────────────────│                             │
  │                              │                             │
  │  GET /api/v1/users/me        │                             │
  │  Header: Bearer {token}      │                             │
  │─────────────────────────────>│                             │
  │                              │  验证JWT + Redis白名单       │
  │                              │  添加 X-User-Id Header      │
  │                              │────────────────────────────>│
  │                              │                             │
  │                              │    返回用户信息              │
  │                              │<────────────────────────────│
  │  返回用户信息                 │                             │
  │<─────────────────────────────│                             │
```

- Token 有效期：7天
- Token 存储在 Redis 中，支持主动失效
- Gateway 全局过滤器验证所有非白名单路径的 Token
- 下游服务通过 `X-User-Id` Header 获取当前用户ID

---

## 🔧 配置说明

### 关键配置项

| 配置项 | 位置 | 说明 |
|--------|------|------|
| `jwt.secret` | common / application.yml | JWT签名密钥 |
| `jwt.expire-seconds` | common / application.yml | Token过期时间（秒） |
| `netty.websocket.port` | chat / application.yml | Netty WebSocket端口 |
| `minio.endpoint` | community / application.yml | MinIO服务地址 |
| `minio.bucket` | community / application.yml | MinIO存储桶名称 |

### Nacos 配置

服务启动时会从 Nacos 拉取 `common-config.yaml` 共享配置，内容包括：
- 数据库连接信息
- Redis 连接信息
- MongoDB 连接信息
- 日志级别

本地开发时，`application.yml` 中已包含完整的本地配置，无需依赖 Nacos 即可启动。

---

## 📄 License

MIT
