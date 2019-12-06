package com.example.demo.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @ClassName ImageUtil
 * @Author lizhuoyuan
 * @Date 2019/12/5 13:41
 **/
public class ImageUtil {
    public static void main(String[] args) {
        File f = new File("D:\\1.jpg");
        File f1 = new File("D:\\2.jpg");
        try {
            InputStream is = new FileInputStream(f);
            OutputStream os = new FileOutputStream(f1);
            resizeImage(is, os, 500, 750, "jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * resizeImage:(等比例压缩图片文件大小)
     * is  文件输入流
     * os  字节数组输出流在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组缓冲区中
     * size 新图片宽度
     * format 新图片格式
     */
    public static void resizeImage(InputStream is, OutputStream os, int newWidth, int newHeight, String format) throws IOException {
        BufferedImage prevImage = ImageIO.read(is);
        double width = prevImage.getWidth();
        double height = prevImage.getHeight();
        //double percent = size/width;
        //int newWidth = (int)(width*percent);
        //int newHeight = (int)(height*percent);
        BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.createGraphics();
        graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
        ImageIO.write(image, format, os);
        os.flush();
        is.close();
        os.close();
    }
}
