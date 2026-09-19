package com.xml.xiaobinnode.utils;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {


    /**
     * 核心线程数：根据 CPU 核数自动计算（IO 密集型场景）
     */
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 2;

    /**
     * 非核心线程空闲存活时间（秒）
     */
    private static final long KEEP_ALIVE = 60L;

    /**
     * 任务队列容量
     */
    private static final int QUEUE_CAPACITY = 500;

    /**
     * 单例线程池实例
     */
    private static volatile ThreadPoolExecutor executor;

    public static ThreadPoolExecutor getExecutor() {
        if (executor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(
                            CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }
        return executor;
    }
}
