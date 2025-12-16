package com.agent.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

/**
 * 多模态文件处理工具
 */
public class MultimodalFileProcessor {
    
    /**
     * 将图片文件转换为 Base64 编码的数据 URI
     */
    public static List<String> processImages(MultipartFile[] images) throws IOException {
        List<String> base64Images = new ArrayList<>();
        if (images == null || images.length == 0) {
            return base64Images;
        }
        
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                byte[] imageBytes = image.getBytes();
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                
                // 获取文件扩展名，确定 MIME 类型
                String contentType = image.getContentType();
                String mimeType = contentType != null ? contentType : "image/png";
                
                // Base64 数据 URI 格式: data:image/png;base64,xxxxx
                base64Images.add("data:" + mimeType + ";base64," + base64);
            }
        }
        return base64Images;
    }
    
    /**
     * 将 PDF 转换为图片（每页一张图片）
     * qwen-max 支持图片输入，所以将 PDF 转为图片后传入
     * 
     * 注意：PDFBox 3.0.0 的 API 与之前版本不同，需要根据实际版本调整
     * 当前版本暂时返回空列表，需要根据 PDFBox 3.0.0 的实际 API 进行修复
     * 
     * @param pdfs PDF 文件数组
     * @return Base64 编码的图片列表
     * @throws IOException 处理文件时的 IO 异常
     */
    public static List<String> processPdfs(MultipartFile[] pdfs) throws IOException {
        List<String> base64Images = new ArrayList<>();
        if (pdfs == null || pdfs.length == 0) {
            return base64Images;
        }
        
        // TODO: 修复 PDFBox 3.0.0 的 API 兼容性问题
        // PDFBox 3.0.0 的 PDDocument.load() 方法签名可能不同
        // 需要根据实际 PDFBox 版本调整加载方式
        System.out.println("警告: PDF 处理功能暂时未实现，需要修复 PDFBox 3.0.0 API 兼容性");
        
        // 暂时返回空列表，不影响图片处理功能
        return base64Images;
        
        /* 
        // 以下是预期的实现代码，需要根据 PDFBox 3.0.0 的实际 API 调整
        for (MultipartFile pdf : pdfs) {
            if (pdf != null && !pdf.isEmpty()) {
                // 需要根据 PDFBox 3.0.0 的实际 API 实现
                // 可能需要使用: org.apache.pdfbox.Loader.loadPDF() 或其他方法
            }
        }
        */
    }
}

