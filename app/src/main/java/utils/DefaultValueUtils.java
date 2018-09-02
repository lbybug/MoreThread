package utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by LLB on 2018/9/2.
 */

public class DefaultValueUtils {

    public static final int THREAD_NUM = 5;

    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator + "MoreThread";

    public static final String DOWNLOAD_URL = "http://gainscha.cn/AppOTA/GprinterFont/";

}
