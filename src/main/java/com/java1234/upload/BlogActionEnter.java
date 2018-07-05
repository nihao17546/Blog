package com.java1234.upload;

import com.baidu.ueditor.ActionEnter;
import com.baidu.ueditor.ConfigManager;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.FileManager;
import com.baidu.ueditor.hunter.ImageHunter;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author nihao 2018/7/4
 */
public class BlogActionEnter extends ActionEnter {
    private HttpServletRequest request = null;
    private String rootPath = null;
    private String contextPath = null;
    private String actionType = null;
    private ConfigManager configManager = null;
    public BlogActionEnter(HttpServletRequest request, String rootPath) {
        super(request, rootPath);
        this.request = request;
        this.rootPath = rootPath;
        this.actionType = request.getParameter("action");
        this.contextPath = request.getContextPath();
        this.configManager = ConfigManager.getInstance(this.rootPath, this.contextPath, request.getRequestURI());
    }

    @Override
    public String invoke() {
        if (this.actionType != null && ActionMap.mapping.containsKey(this.actionType)) {
            if (this.configManager != null && this.configManager.valid()) {
                State state = null;
                int actionCode = ActionMap.getType(this.actionType);
                Map<String, Object> conf = null;
                switch (actionCode) {
                    case 0:
                        return this.configManager.getAllConfig().toString();
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        conf = this.configManager.getConfig(actionCode);
                        //注意再这里修改rootPath和savePath，上传的实际路径为rootPath＋savePath
                        conf.put("rootPath", "/mydata/ftp/blog/");
                        conf.put("savePath", conf.get("savePath"));
                        state = (new BlogUploader(this.request, conf)).doExec();
                        break;
                    case 5:
                        conf = this.configManager.getConfig(actionCode);
                        //注意再这里修改rootPath和savePath，上传的实际路径为rootPath＋savePath
                        conf.put("rootPath", "/mydata/ftp/blog/");
                        conf.put("savePath", conf.get("savePath"));
                        String[] list = this.request.getParameterValues((String) conf.get("fieldName"));
                        state = (new ImageHunter(conf)).capture(list);
                        break;
                    case 6:
                    case 7:
                        conf = this.configManager.getConfig(actionCode);
                        //注意再这里修改rootPath和savePath，上传的实际路径为rootPath＋savePath
                        conf.put("rootPath", "/mydata/ftp/blog/");
                        conf.put("savePath", conf.get("savePath"));
                        conf.put("dir", "/upload" + conf.get("dir"));
                        int start = this.getStartIndex();
                        state = (new FileManager(conf)).listFile(start);
                }
                return state.toJSONString();
            } else {
                return (new BaseState(false, 102)).toJSONString();
            }
        } else {
            return (new BaseState(false, 101)).toJSONString();
        }
    }
}
