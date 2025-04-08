package com.phantoms.phantomsbackend.common.utils.PIS;

import cn.afterturn.easypoi.word.WordExportUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ClassPathResource;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

public class FileUtil {
    /**
     * 根据模板导出Word
     * @param response HTTP响应对象
     * @param map 替换的数据
     * @param modelFileName 模板文件名
     * @param outFileName 输出文件名（不需要包含.docx扩展名）
     */
    public static void exportWordByModel(HttpServletResponse response, Map<String, Object> map,
                                         String modelFileName, String outFileName) {
        try {
            String templatePath = "template/" + modelFileName;
            ClassPathResource resource = new ClassPathResource(templatePath);

            if (!resource.exists()) {
                throw new IllegalArgumentException("模板文件不存在: " + templatePath);
            }

            File tempFile = File.createTempFile("template", ".docx");
            try (InputStream inputStream = resource.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            XWPFDocument word = WordExportUtil.exportWord07(tempFile.getAbsolutePath(), map);

            response.reset();
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/msexcel");
            response.setContentType("text/html; charset=UTF-8");
            response.setContentType("application/octet-stream");
            String encodedFileName = URLEncoder.encode(outFileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ".docx");

            word.write(response.getOutputStream());

            tempFile.deleteOnExit();
        } catch (Exception e) {
            throw new RuntimeException("导出Word文档失败: " + e.getMessage(), e);
        }
    }

}