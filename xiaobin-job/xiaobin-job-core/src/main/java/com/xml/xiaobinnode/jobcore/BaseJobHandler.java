package com.xml.xiaobinnode.jobcore;

import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务 Handler 基类
 * <p>
 * 提供统一的:
 * <ul>
 *   <li>任务执行日志（自动记录开始/结束/耗时/异常）</li>
 *   <li>XXL-Job Admin UI 日志输出</li>
 *   <li>分片参数获取</li>
 * </ul>
 *
 * <pre>
 * 使用示例:
 * {@code
 * @Component
 * public class MyHandler extends BaseJobHandler {
 *
 *     @XxlJob("myTask")
 *     public void execute() {
 *         run("任务名称", () -> {
 *             // 业务逻辑
 *             logToAdmin("处理了 {} 条数据", count);
 *         });
 *     }
 * }
 * }
 * </pre>
 */
@Slf4j
public abstract class BaseJobHandler {

    /**
     * 执行任务并统一处理日志和异常
     *
     * @param taskName 任务名称（用于日志）
     * @param action   业务逻辑
     */
    protected void run(String taskName, Runnable action) {
        long start = System.currentTimeMillis();
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        log.info("[{}] 开始执行, 分片: {}/{}", taskName, shardIndex, shardTotal);
        XxlJobHelper.log("[{}] 开始执行", taskName);

        try {
            action.run();
            long cost = System.currentTimeMillis() - start;
            log.info("[{}] 执行成功, 耗时: {}ms", taskName, cost);
            XxlJobHelper.log("[{}] 执行成功, 耗时: {}ms", taskName, cost);
            XxlJobHelper.handleSuccess();
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("[{}] 执行失败, 耗时: {}ms", taskName, cost, e);
            XxlJobHelper.log("[{}] 执行失败: {}", taskName, e.getMessage());
            XxlJobHelper.handleFail(e.getMessage());
            throw e;
        }
    }

    /**
     * 输出日志到 XXL-Job Admin 调度日志面板
     */
    protected void logToAdmin(String format, Object... args) {
        XxlJobHelper.log(format, args);
    }

    /**
     * 获取分片参数中的当前分片索引 (从0开始)
     */
    protected int getShardIndex() {
        return XxlJobHelper.getShardIndex();
    }

    /**
     * 获取分片参数中的总分片数
     */
    protected int getShardTotal() {
        return XxlJobHelper.getShardTotal();
    }

    /**
     * 从任务参数中获取指定 key 的值
     * <p>
     * 任务参数格式: {@code --key1=value1 --key2=value2} 或 {@code key1=value1&key2=value2}
     *
     * @param key 参数名
     * @return 参数值，不存在则返回 null
     */
    protected String getParam(String key) {
        String raw = XxlJobHelper.getJobParam();
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        // 支持 --key=value 和 key=value 两种格式
        for (String part : raw.split("\\s+")) {
            String kv = part.startsWith("--") ? part.substring(2) : part;
            int eq = kv.indexOf('=');
            if (eq > 0 && kv.substring(0, eq).equals(key)) {
                return kv.substring(eq + 1);
            }
        }
        return null;
    }

}
