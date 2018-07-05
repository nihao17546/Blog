package com.java1234.upload;

import com.alibaba.fastjson.JSON;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/**
 * Created by nihao on 18/1/26.
 */
public class QiNiuUpload {
    private static final Logger logger = LoggerFactory.getLogger(QiNiuUpload.class);

    public static String qiniuAccessKey = null;
    public static String qiniuSecretKey = null;

    public static Result upload(File file, String fileName, String lookBucket){
        if(qiniuAccessKey == null || qiniuAccessKey == null){
            throw new RuntimeException("七牛云密钥为空");
        }
        Result result = new Result();
        Configuration configuration = new Configuration();
        UploadManager uploadManager = new UploadManager(configuration);
        Auth auth = Auth.create(qiniuAccessKey, qiniuSecretKey);
        String token = auth.uploadToken(lookBucket);
        try {
            Response res = uploadManager.put(file, fileName, token);
            String bodyStr = res.bodyString();
            Map<String,Object> resultMap = JSON.parseObject(bodyStr);
            String k = (String) resultMap.get("key");
            result.setMsg(k);
            result.setRet(1);
        } catch (Exception e) {
            result.setRet(0);
            result.setMsg("抱歉服务异常,请联系管理员:nhweiwin(微信号)");
            logger.error("七牛上传文件错误", e);
        }
        return result;
    }

    public static class Result{
        private int ret;
        private String msg;

        public int getRet() {
            return ret;
        }

        public void setRet(int ret) {
            this.ret = ret;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

}
