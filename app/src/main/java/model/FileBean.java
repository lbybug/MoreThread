package model;

/**
 * Created by LLB on 2018/9/2.
 */

public class FileBean {

    private final String fileName;
    private final long fileLength;

    private FileBean(Builder builder) {
        fileName = builder.fileName;
        fileLength = builder.fileLength;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public static final class Builder {
        private String fileName;
        private long fileLength;

        public Builder() {
        }

        public Builder fileName(String val) {
            fileName = val;
            return this;
        }

        public Builder fileLength(long val) {
            fileLength = val;
            return this;
        }

        public FileBean build() {
            return new FileBean(this);
        }
    }
}
