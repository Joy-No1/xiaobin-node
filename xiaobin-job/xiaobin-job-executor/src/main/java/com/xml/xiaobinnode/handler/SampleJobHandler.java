package com.xml.xiaobinnode.handler;

import com.xml.xiaobinnode.jobcore.BaseJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

/**
 * 定时任务示例 —— 通过 Feign 调用业务服务
 *
 * 使用方式：
 * 1. 在 xiaobin-api 模块定义 Feign 接口
 * 2. 在此注入 Feign 客户端，调用业务服务
 *
 * 示例（需要先在 xiaobin-api 定义接口）：
 *
 *   @Autowired
 *   private UserFeignClient userFeignClient;
 *
 *   @XxlJob("syncExpiredRelationships")
 *   public void syncExpiredRelationships() {
 *       run("同步过期关系", () -> {
 *           userFeignClient.syncExpired();
 *       });
 *   }
 */
@Component
public class SampleJobHandler extends BaseJobHandler {

    /**
     * 健康检查任务 —— 验证 XXL-Job 接入正常
     * Cron: 每分钟执行一次
     */
    @XxlJob("healthCheckHandler")
    public void healthCheck() {
        run("健康检查", () -> {
            logToAdmin("XXL-Job 心跳正常, 分片: {}/{}", getShardIndex(), getShardTotal());
        });
    }
}
