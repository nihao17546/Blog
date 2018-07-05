package com.java1234.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.StorageManager;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;

/**
 * @author nihao 2018/7/4
 */
public class BlogBase64Uploader {
    public BlogBase64Uploader() {
    }

    public static State save(String content, Map<String, Object> conf) {
        byte[] data = decode(content);
        long maxSize = (Long)conf.get("maxSize");
        if (!validSize(data, maxSize)) {
            return new BaseState(false, 1);
        } else {
            String suffix = FileType.getSuffix("JPG");
            String savePath = PathFormat.parse((String)conf.get("savePath"), (String)conf.get("filename"));
            savePath = savePath + suffix;
            String physicalPath = (String)conf.get("rootPath") + savePath;
            State storageState = StorageManager.saveBinaryFile(data, physicalPath);
            if (storageState.isSuccess()) {
                storageState.putInfo("url", PathFormat.format(savePath));
                storageState.putInfo("type", suffix);
                storageState.putInfo("original", "");
            }

            return storageState;
        }
    }

    private static byte[] decode(String content) {
        return Base64.decodeBase64(content);
    }

    private static boolean validSize(byte[] data, long length) {
        return (long)data.length <= length;
    }

}
