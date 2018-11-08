package com.java1234.upload;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.StorageManager;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author nihao 2018/7/4
 */
public class BlogBinaryUploader {
    public static final String lookBucket = "activity";
    public static final String qiNiuUrlPrefix = "http://activity.appcnd.com/";
    private static File tempFile = null;
    static {
        tempFile = new File("/temp");
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
    }
    public BlogBinaryUploader() {
    }

    public static final State save(HttpServletRequest request, Map<String, Object> conf) {
        FileItemStream fileStream = null;
        boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;
        if (!ServletFileUpload.isMultipartContent(request)) {
            return new BaseState(false, 5);
        } else {
            ServletFileUpload upload = new ServletFileUpload(
                    new DiskFileItemFactory(2 * 1024 * 1024, tempFile));
            if (isAjaxUpload) {
                upload.setHeaderEncoding("UTF-8");
            }

            try {
                for(FileItemIterator iterator = upload.getItemIterator(request); iterator.hasNext(); fileStream = null) {
                    fileStream = iterator.next();
                    if (!fileStream.isFormField()) {
                        break;
                    }
                }

                if (fileStream == null) {
                    return new BaseState(false, 7);
                } else {
                    String savePath = (String)conf.get("savePath");
                    // 文件名（带后缀）
                    String originFileName = fileStream.getName();
                    // 文件类型
                    String suffix = FileType.getSuffixByFilename(originFileName);
                    // 文件名（不带后缀）
                    originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
                    savePath = savePath + suffix;
                    long maxSize = (Long)conf.get("maxSize");
                    if (!validType(suffix, (String[])conf.get("allowFiles"))) {
                        return new BaseState(false, 8);
                    } else {
                        savePath = PathFormat.parse(savePath, originFileName);
                        String physicalPath = (String)conf.get("rootPath") + savePath;
                        InputStream is = fileStream.openStream();
                        State storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
                        is.close();
                        if (storageState.isSuccess()) {
                            // 上传到七牛云
                            QiNiuUpload.Result result = QiNiuUpload.upload(new File(physicalPath),
                                    physicalPath.replaceAll("/", "_"), lookBucket);
                            if(result.getRet() == 1){
                                storageState.putInfo("url", qiNiuUrlPrefix + result.getMsg());
                                storageState.putInfo("type", suffix);
                                storageState.putInfo("original", originFileName + suffix);
                            }
                            else{
                                return new BaseState(false, 201);
                            }
//                            storageState.putInfo("url", PathFormat.format(savePath));
//                            storageState.putInfo("type", suffix);
//                            storageState.putInfo("original", originFileName + suffix);
                        }

                        return storageState;
                    }
                }
            } catch (FileUploadException var14) {
                return new BaseState(false, 6);
            } catch (IOException var15) {
                return new BaseState(false, 4);
            }
        }
    }

    private static boolean validType(String type, String[] allowTypes) {
        List<String> list = Arrays.asList(allowTypes);
        return list.contains(type);
    }
}
