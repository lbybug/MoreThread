package utils;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LLB on 2018/9/2.
 */

public class ThreadPoolUtils {

    private static final String TAG = "ThreadPoolUtils";

    public static ThreadPoolUtils threadPoolUtils;

    public ThreadPoolExecutor threadPoolExecutor;

    public static final int CPU_AVAILABLE = Runtime.getRuntime().availableProcessors();

    public static final int CORE_POOL_SIZE = CPU_AVAILABLE + 1;

    public static final int MAX_POOl_SIZE = CPU_AVAILABLE * 2 + 1;

    public static final long AVAILABLE = 1L;

    private static final BlockingQueue<Runnable> queue =
            new LinkedBlockingQueue<>(MAX_POOl_SIZE);

    public ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable);
        }
    };


    public ThreadPoolUtils(){
        threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOl_SIZE,AVAILABLE, TimeUnit.SECONDS,queue,threadFactory);
    }


    public static ThreadPoolUtils getInstance() {
        if (threadPoolUtils == null) {
            threadPoolUtils = new ThreadPoolUtils();
        }
        return threadPoolUtils;
    }

    public void addTask(Runnable r){
        if (r != null) {
            if (threadPoolExecutor.getActiveCount() < MAX_POOl_SIZE){
                threadPoolExecutor.execute(r);
            }
            threadPoolExecutor.execute(r);
        }else {
            Log.d(TAG, "addTask: 传入参数为空");
        }
    }

    public void shutDown(){  //销毁界面时关闭，回收资源
        threadPoolExecutor.shutdown();
    }


}
