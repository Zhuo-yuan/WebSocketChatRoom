package com.example.demo.controller;

import com.example.demo.util.FileHandleUtil;
import com.example.demo.util.ImageUtil;
import org.springframework.http.HttpRequest;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * @ClassName UeditorController
 * @Author lizhuoyuan
 * @Date 2019/11/26 14:14
 **/
@RestController
@RequestMapping("/api/ueditor")
public class UeditorController {

    private String imageFileType = "png,jpg,jpeg,gif,bmp";


    private String localPath = "/static/upload/image";

    /**
     * 获取Ueditor的配置文件
     * @return
     */
    @ResponseBody
    @RequestMapping("/config")
    public String getConfig(HttpServletRequest request) {
        String action = request.getParameter("action");
        if (action.equals("uploadimage")) {
            MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
            MultipartFile multipartFile = multipartHttpServletRequest.getFile("upfile");
            return upload(multipartFile);
        }else{
            return "{\n" +
                    "        \"imageActionName\": \"uploadimage\",\n" +
                    "            \"imageFieldName\": \"upfile\",\n" +
                    "            \"imageMaxSize\": 2048000,\n" +
                    "            \"imageAllowFiles\": [\".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\"],\n" +
                    "        \"imageCompressEnable\": true,\n" +
                    "            \"imageCompressBorder\": 1600,\n" +
                    "            \"imageInsertAlign\": \"none\",\n" +
                    "            \"imageUrlPrefix\": \"../\",\n" +
                    "            \"imagePathFormat\": \"/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}\",\n" +
                    "\n" +
                    "        /* 上传文件配置 */\n" +
                    "        \"fileActionName\": \"uploadfile\",\n" +
                    "            \"fileFieldName\": \"file\",\n" +
                    "            \"filePathFormat\": \"/ueditor/jsp/upload/file/{yyyy}{mm}{dd}/{time}{rand:6}\",\n" +
                    "            \"fileUrlPrefix\": \"\",\n" +
                    "            \"fileMaxSize\": 51200000,\n" +
                    "            \"fileAllowFiles\": [\n" +
                    "        \".png\", \".jpg\", \".jpeg\", \".gif\", \".bmp\",\n" +
                    "                \".flv\", \".swf\", \".mkv\", \".avi\", \".rm\", \".rmvb\", \".mpeg\", \".mpg\",\n" +
                    "                \".ogg\", \".ogv\", \".mov\", \".wmv\", \".mp4\", \".webm\", \".mp3\", \".wav\", \".mid\",\n" +
                    "                \".rar\", \".zip\", \".tar\", \".gz\", \".7z\", \".bz2\", \".cab\", \".iso\",\n" +
                    "                \".doc\", \".docx\", \".xls\", \".xlsx\", \".ppt\", \".pptx\", \".pdf\", \".txt\", \".md\", \".xml\"]\n" +
                    "    }";
        }
    }

    /**
     * Ueditor上传文件
     * 这里以上传图片为例，图片上传后，imgPath将存储图片的保存路径，返回到编辑器中做展示
     * @param mFile
     * @return
     */
    public String upload(MultipartFile mFile) {
        String result = "";
        PrintWriter out = null;
        String[] imageTypes= imageFileType.split(",");
        List<String> fileTypes = new ArrayList<String>();
        Collections.addAll(fileTypes, imageTypes);
        if(!mFile.isEmpty()) {
            // 这里写你的文件上传逻辑
            // String imgPath = fileUtil.uploadImg(file);
            String oldFileName = "";
            String newFileName = "";

            String filetype = mFile.getOriginalFilename().substring(mFile.getOriginalFilename().lastIndexOf(".")+1, mFile.getOriginalFilename().length());
            if(fileTypes.contains(filetype.toLowerCase())){
                if(mFile.getSize()<100*1024*1024){
                    oldFileName = mFile.getOriginalFilename();
                    String suffixName = oldFileName.substring(oldFileName.lastIndexOf("."));
                    newFileName = UUID.randomUUID() + suffixName;
                    try {
                        InputStream files = mFile.getInputStream();
                        FileHandleUtil.upload(files,"image/",newFileName);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("文件太大上传失败！");
                }
            }else{
                System.out.println("文件格式错误！");
            }
            result = "{\n" +
                    "    \"state\": \"SUCCESS\",\n" +
                    "    \"url\": \"" + localPath+"/"+newFileName + "\",\n" +
                    "    \"title\": \"" + oldFileName + "\",\n" +
                    "    \"original\": \"" + newFileName + "\"\n" +
                    "}";
        }
        return result;
    }

}
