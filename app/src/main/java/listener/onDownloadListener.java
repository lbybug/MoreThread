package listener;

/**
 * Created by LLB on 2018/9/2.
 */

public interface onDownloadListener {

    void onStart(int which);  //第几个线程开始下载

    void onProgress(int which,int progress); //第几个线程的下载进度

    void onFinish(int which); //第几个线程下载完成

    void onFailed(int which); //第几个线程下载失败

    void onLost(int which,long dataLength); // 数据丢失

    void onAllProgress(int progress); //总进度

}
