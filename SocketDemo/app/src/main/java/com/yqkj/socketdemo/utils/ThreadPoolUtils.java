package com.yqkj.socketdemo.utils;


import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池对象，用于执行线程
 *
 * @author chenguowu
 * @date 2020/6/18 6:59 PM
 */
public class ThreadPoolUtils {

    /**
     * 线程池对象
     */
    private static ExecutorService M_FIXED_THREAD_EXECUTOR;

    static {
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@Nullable Runnable r) {
                return new Thread(r);
            }
        };


        M_FIXED_THREAD_EXECUTOR = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    }

    public static void executor(Runnable runnable) {
        M_FIXED_THREAD_EXECUTOR.execute(runnable);
    }


}
