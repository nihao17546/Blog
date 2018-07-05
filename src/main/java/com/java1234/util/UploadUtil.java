package com.java1234.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nihao 2018/7/4
 */
public class UploadUtil {
    private static File tempFile = new File("/temp");
    public static ServletFileUpload upload = null;
    static {
        if(!tempFile.exists()){
            tempFile.mkdirs();
        }
        // 内存缓冲区大小：2M，临时文件目录：/temp
        DiskFileItemFactory factory = new DiskFileItemFactory(2 * 1024 * 1024, tempFile);
        upload = new ServletFileUpload(factory);
        upload.setSizeMax(20 * 1024 * 1024);// 设置所有上传数据的最大值：20M
        upload.setHeaderEncoding("UTF-8");
    }

    public static List<FileItem> getFile(HttpServletRequest request){
        List<FileItem> list = new ArrayList<>();
        try {
            List<FileItem> itemList = upload.parseRequest(request);
            for(FileItem item : itemList){
                if(!item.isFormField()){
                    list.add(item);
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        return list;
    }
}
