package com.java1234.upload;

import com.baidu.ueditor.define.State;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author nihao 2018/7/4
 */
public class BlogUploader {
    private HttpServletRequest request = null;
    private Map<String, Object> conf = null;

    public BlogUploader(HttpServletRequest request, Map<String, Object> conf) {
        this.request = request;
        this.conf = conf;
    }

    public final State doExec() {
        String filedName = (String)this.conf.get("fieldName");
        State state = null;
        if ("true".equals(this.conf.get("isBase64"))) {
            state = BlogBase64Uploader.save(this.request.getParameter(filedName), this.conf);
        } else {
            state = BlogBinaryUploader.save(this.request, this.conf);
        }

        return state;
    }
}
