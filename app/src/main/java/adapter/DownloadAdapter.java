package adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import listener.onDownloadListener;
import model.DownloadBean;
import test.jb.pp.morethreaddownload.MainActivity;
import test.jb.pp.morethreaddownload.R;
import utils.DownloadUtils;

/**
 * Created by LLB on 2018/9/2.
 */

public class DownloadAdapter extends BaseAdapter{

    private static final String TAG = "DownloadAdapter";

    public List<DownloadBean> list;

    public Context context;

    public DownloadUtils utils;

    public DownloadAdapter(List<DownloadBean> list, Context context, DownloadUtils utils){
        this.list = list;
        this.context = context;
        this.utils = utils;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.item_download,null);

        TextView downloadName = view.findViewById(R.id.downloadName);
        final TextView downloadProgress = view.findViewById(R.id.downloadProgress);
        final ProgressBar downloadBar = view.findViewById(R.id.downloadBar);
        final Button downloadButton = view.findViewById(R.id.downloadButton);

        downloadName.setText(list.get(i).getDownloadName());

        downloadBar.setMax(100);
        downloadBar.setProgress(0);
        downloadProgress.setText("0%");
        downloadBar.setVisibility(View.GONE);
        downloadProgress.setVisibility(View.GONE);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MainActivity.isReadWrite){
                    Toast.makeText(context, "请先开启读写权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                downloadBar.setVisibility(View.VISIBLE);
                downloadProgress.setVisibility(View.VISIBLE);
                utils.download(list.get(i).getDownloadUrl(), new onDownloadListener() {
                    @Override
                    public void onStart(int which) {
//                        downloadButton.setText("下载中");
                    }

                    @Override
                    public void onProgress(int which,int progress) {
                        Log.d(TAG, "onProgress: 第"+which+"个线程下载进度："+progress+"%");
                    }

                    @Override
                    public void onFinish(int which) {
                        downloadButton.setText("下载完成");
                    }

                    @Override
                    public void onFailed(int which) {
                        downloadButton.setText("下载失败");
                    }

                    @Override
                    public void onLost(int which, long dataLength) {
                        Toast.makeText(context, "下载失败，丢失"+dataLength+"个数据", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAllProgress(int progress) {
//                        downloadProgress.setText(progress+"%");
//                        downloadBar.setProgress(progress);
                        Log.d(TAG, "onAllProgress: 总进度为："+progress+"%");
                    }
                });
            }
        });
        return view;
    }

}
