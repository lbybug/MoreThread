package test.jb.pp.morethreaddownload;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import adapter.DownloadAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import model.DownloadBean;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import utils.DefaultValueUtils;
import utils.DownloadUtils;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Context context = MainActivity.this;

    public static boolean isReadWrite = false;

    public DownloadAdapter adapter;
    public DownloadUtils downloadUtils;

    public List<DownloadBean> list;

    @BindView(R.id.downloadList)
    ListView downloadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d(TAG, "onCreate: 获取读写内存权限");
        MainActivityPermissionsDispatcher.needReadOrWritePermissionWithCheck(this);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadUtils.clean();
    }

    private void initData() {
        list = new ArrayList<>();
        list.clear();
        downloadUtils = DownloadUtils.getInstance();

        String [] downloadUrl = context.getResources().getStringArray(R.array.array_download_url);
        String [] downloadName = context.getResources().getStringArray(R.array.array_download_name);

        for (int i = 0; i < downloadUrl.length; i++) {
            DownloadBean bean = new DownloadBean.Builder().downloadName(downloadName[i]).downloadUrl(DefaultValueUtils.DOWNLOAD_URL + downloadUrl[i]).build();
            list.add(bean);
        }
        adapter = new DownloadAdapter(list,context,downloadUtils);
        downloadList.setAdapter(adapter);
    }//初始化下载数据

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needReadOrWritePermission() {
        isReadWrite = true;
        File file = new File(DefaultValueUtils.ROOT_PATH);
        if (!file.exists()){
            file.mkdirs();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
    @OnShowRationale({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void showReadOrWritePermission(final PermissionRequest request) {request.proceed();
    }
    @OnPermissionDenied({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void deniedReadOrWritePermission() {
        isReadWrite = false;
        Toast.makeText(context, "未获取到权限", Toast.LENGTH_SHORT).show();
    }
    @OnNeverAskAgain({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void AskAgainReadOrWritePermission() {
        Toast.makeText(context, "请在设置中打开权限", Toast.LENGTH_SHORT).show();
    }
}
