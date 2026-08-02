/*
 * 定时任务公共模块
 *
 * 封装 XXL-Job 核心依赖，提供:
 * - XXL-Job 官方注解和 API (@XxlJob, XxlJobSpringExecutor)
 * - 共享的 Handler 基类 (BaseJobHandler)
 * - Feign 客户端自动注入支持
 *
 * 子模块:
 * - xiaobin-job-executor → 执行器，注册到 Admin 执行定时任务
 * - xiaobin-job-admin   → 调度中心，提供任务管理 Web UI
 */
