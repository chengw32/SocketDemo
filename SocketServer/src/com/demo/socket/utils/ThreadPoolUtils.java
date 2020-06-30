package com.demo.socket.utils;

import java.util.concurrent.*;

/**
 * 线程池，用于管理线程
 *
 * @author chenguowu
 * @date 2020/6/18 6:52 PM
 */
public class ThreadPoolUtils {
    /**
     * 线程池对象
     */
    private static final ExecutorService mExecutorService;

    static {
        ThreadFactory namedThreadFactory = Thread::new;
        mExecutorService = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static void execute(Runnable runnable) {
        mExecutorService.execute(runnable);
    }
}
