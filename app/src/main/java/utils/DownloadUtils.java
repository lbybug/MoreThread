package utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import listener.onDownloadListener;
import model.FileBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static utils.DefaultValueUtils.THREAD_NUM;

/**
 * Created by LLB on 2018/9/2.
 */

public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    public static DownloadUtils downloadUtils;

    public ThreadPoolUtils threadPoolUtils;

    public OkHttpClient client;

    public long allLength = 0;

    public DownloadUtils(){
        if (client  == null) {
            client = new OkHttpClient();
            client.newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10,TimeUnit.SECONDS).writeTimeout(10,TimeUnit.SECONDS).build();
        }
        if (threadPoolUtils == null) {
            threadPoolUtils = ThreadPoolUtils.getInstance();
        }
    }

    public static DownloadUtils getInstance() {
        if (downloadUtils == null) {
            downloadUtils = new DownloadUtils();
        }
        return downloadUtils;
    }

    public void download(final String url, final onDownloadListener listener){
        Request request = new Request.Builder().url(url).get().tag(this).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: 获取失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    long contentLength = response.body().contentLength();
                    FileBean bean = new FileBean.Builder().fileLength(contentLength).fileName(url.substring(url.lastIndexOf("/"))).build();
                    allLength = 0;
                    prepareDownload(url,bean,listener);
                }
            }
        });
    }

    private void prepareDownload(String url, FileBean bean, onDownloadListener listener) { //根据线程数，分配每段下载大小
        if (url == null || bean == null || listener == null) {
            return;
        }
        createNewFile(bean);
        long downloadLength = bean.getFileLength();
        long everyLength = downloadLength / THREAD_NUM;
        for (int i = 0 ; i < THREAD_NUM ; i++) {
            long startLength = everyLength * i;
            long endLength = everyLength * (i + 1);
            if (i == 0){
            }else if (i == THREAD_NUM - 1){
                startLength += 1;
                endLength += downloadLength % THREAD_NUM;
            }else {
                startLength += 1;
            }
            startDownload(url,startLength,endLength,bean,(i+1),listener);
        }
    }

    private void startDownload(final String url, final long startLength, final long endLength, final FileBean bean, final int whichThread, final onDownloadListener listener) {
        threadPoolUtils.addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().get().url(url).tag(this).addHeader("RANGE","bytes="+startLength+"-"+endLength).build();
                    Response response = client.newCall(request).execute();
                    listener.onStart(whichThread);
                    if (response != null && response.isSuccessful()) {
                        InputStream is = response.body().byteStream();
                        RandomAccessFile r = new RandomAccessFile(new File(DefaultValueUtils.ROOT_PATH + bean.getFileName()),"rw");
                        r.seek(startLength);
                        byte bytes[] = new byte[1024];
                        int total = 0;
                        int len;
                        while ((len = is.read(bytes)) != -1){
                            r.write(bytes,0,len);
                            total += len;
                            int progress = (int)(((total * 100)) / (endLength - startLength));
                            listener.onProgress(whichThread,progress);
                            allLength+=len;
                            int allProgress = (int)(((allLength * 100)) / bean.getFileLength());
                            listener.onAllProgress(allProgress);
                        }
                        if (total >= bean.getFileLength()){
                            listener.onFinish(whichThread);
                        }else {
                            listener.onLost(whichThread,(total-(endLength - startLength)));
                        }
                        is.close();
                        r.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    listener.onFailed(whichThread);
                }
            }
        });

    }

    private void createNewFile(FileBean bean) {
        try {
            File file = new File(DefaultValueUtils.ROOT_PATH + bean.getFileName());
            if (!file.exists()){
                file.createNewFile();
            }
            RandomAccessFile r = new RandomAccessFile(file,"rw");
            r.setLength(bean.getFileLength());  //根据获取到的文件大小，设置创建的文件大小
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // 创建一个新的文件

    public void clean(){
        if (client != null) {
            client = null;
        }
        if (downloadUtils != null) {
            downloadUtils = null;
        }
        if (threadPoolUtils != null) {
            threadPoolUtils.shutDown();
        }
    }

}
