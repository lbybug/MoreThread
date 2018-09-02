package model;

/**
 * Created by LLB on 2018/9/2.
 */

public class DownloadBean {

    private final String downloadUrl;
    private final String downloadName;

    private DownloadBean(Builder builder) {
        downloadUrl = builder.downloadUrl;
        downloadName = builder.downloadName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public static final class Builder {
        private String downloadUrl;
        private String downloadName;

        public Builder() {
        }

        public Builder downloadUrl(String val) {
            downloadUrl = val;
            return this;
        }

        public Builder downloadName(String val) {
            downloadName = val;
            return this;
        }

        public DownloadBean build() {
            return new DownloadBean(this);
        }
    }
}
