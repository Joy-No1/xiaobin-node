package com.xml.xiaobinnode.common.constant;

/**
 * 公共常量
 */
public interface CommonConstants {

    /** 请求头 - Token */
    String HEADER_TOKEN = "Authorization";

    /** Token前缀 */
    String TOKEN_PREFIX = "Bearer ";

    /** Token有效期 (7天) */
    long TOKEN_EXPIRE_SECONDS = 7 * 24 * 60 * 60;

    /** 验证码有效期 (5分钟) */
    long VERIFY_CODE_EXPIRE_SECONDS = 5 * 60;

    /** 初始好感度分数 */
    int INITIAL_SCORE = 100;

    /** 最小好感度分数 */
    int MIN_SCORE = 0;

    /** Redis Key - Token */
    String REDIS_TOKEN_KEY = "token:";

    /** Redis Key - 验证码 */
    String REDIS_VERIFY_CODE_KEY = "verify_code:";

    /** Redis Key - 在线用户（单个） */
    String REDIS_ONLINE_KEY = "online:";

    /** Redis Key - 用户在线状态 im:online:{userId} */
    String REDIS_IM_ONLINE_KEY = "im:online:";

    /** Redis Key - 用户连接集合 im:connections:{userId} */
    String REDIS_IM_CONNECTIONS_KEY = "im:connections:";

    /** 在线状态 TTL（秒） - 35秒，客户端30秒心跳 */
    long ONLINE_STATUS_TTL_SECONDS = 35;

    /** Redis Pub/Sub Channel - 用户上线事件 */
    String REDIS_CHANNEL_USER_ONLINE = "user:online";

    /** Redis Pub/Sub Channel - 用户离线事件 */
    String REDIS_CHANNEL_USER_OFFLINE = "user:offline";

    /** Redis Key - 帖子点赞 */
    String REDIS_POST_LIKE_KEY = "post:like:";

    /** Redis Key - 限流 */
    String REDIS_RATE_LIMIT_KEY = "rate_limit:";

    /** Redis Key - 互关用户 */
    String REDIS_MUTUAL_FOLLOW_KEY = "mutual_follow:";

    /** Redis Pub/Sub Channel - 关注通知 */
    String REDIS_CHANNEL_FOLLOW_NOTIFICATION = "follow:notification";

    /** Redis Pub/Sub Channel - 聊天消息 */
    String REDIS_CHANNEL_CHAT_MESSAGE = "chat:message";
}
