package com.phantoms.phantomsbackend.common.utils.PIS;

import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.phantoms.phantomsbackend.common.exception.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

/**
 * @author xiaohuang
 * @date 2024/7/14
 */
public class ExcelUtils {
    private final static Logger logger= LoggerFactory.getLogger(ExcelUtil.class);

    public static void exportMergeExcel(String fileName, Collection<?> excelList, Class<?> excel, int[] mergeColIndex, int mergeRowIndex) {
        try {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            // 设置表头样式
            WriteCellStyle headStyle = new WriteCellStyle();
            headStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            // 设置表格内容样式
            WriteCellStyle bodyStyle = new WriteCellStyle();
            bodyStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
            bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            // 拿到表格处理对象
            ExcelWriter writer = null;
            try {
                writer = EasyExcel.write(response.getOutputStream())
                       .needHead(true)
                       .excelType(ExcelTypeEnum.XLSX)
                       // 设置需要待合并的行和列。参数1：数值数组new int[]{1,2}，指定需要合并的列；参数2：数值，指定从第几行开始合并
                       .registerWriteHandler(new ExcelMergeCustomerCellHandler(mergeColIndex, mergeRowIndex))
                       .registerWriteHandler(new HorizontalCellStyleStrategy(headStyle, bodyStyle))
                       .build();
                // 设置表格sheet样式,并写入excel
                WriteSheet sheet = EasyExcel.writerSheet(fileName).head(excel).sheetNo(1).build();
                writer.write(excelList, sheet);
            } catch (IOException e) {
                throw new ServiceException(ServiceException.UNKNOWN_ERROR,"导出异常："+e.getMessage());
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
            writer.finish();
        } catch (IOException e) {
            throw new ServiceException(ServiceException.UNKNOWN_ERROR,"全局导出异常："+e.getMessage());
        }
    }

    public static void export(Collection<?> exportList, Class<?> excel, String fileName, String sheetName) {
        try {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
            try (OutputStream outputStream = response.getOutputStream()) {
                EasyExcel.write(outputStream, excel)
                        .sheet(sheetName)
                        .doWrite(exportList);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("导出文件时编码错误: {}", fileName, e);
        } catch (IOException e) {
            logger.error("导出文件时出现IO错误: {}", fileName, e);
        } catch (Exception e) {
            logger.error("导出文件时出现一般性错误: {}", fileName, e);
        }
    }

    public static HorizontalCellStyleStrategy formatExcel() {
        //表头格式设置
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
        headWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headWriteCellStyle.setTopBorderColor((short) 3892127);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 15);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short) 15);
        contentWriteFont.setFontName("宋体");
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 设置自动换行
        contentWriteCellStyle.setWrapped(false);
        // 设置垂直居中
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 设置水平居中
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    public static XSSFCellStyle createCellStyle(XSSFWorkbook workbook, short fontsize, boolean overbold, boolean center, boolean headColor,boolean border) {
        // TODO Auto-generated method stub
        XSSFCellStyle style = workbook.createCellStyle();
        if (border) {
            // 下边框
            style.setBorderBottom(BorderStyle.MEDIUM);
            // 左边框
            style.setBorderLeft(BorderStyle.MEDIUM);
            // 上边框
            style.setBorderTop(BorderStyle.MEDIUM);
            // 右边框
            style.setBorderRight(BorderStyle.MEDIUM);
        }
        //是否水平居中
        if(center) {
            //水平居中
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        //垂直居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        //创建字体
        Font font = workbook.createFont();
        font.setFontName("宋体");
        //是否加粗字体
        if(overbold)
            font.setBold(true);
        font.setFontHeightInPoints(fontsize);
        //加载字体
        style.setFont(font);
        //设置字体颜色
        //style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        //设置行背景颜色为浅蓝色
        if (headColor){
            style.setFillForegroundColor(new XSSFColor((IndexedColorMap) new java.awt.Color(130, 143, 169, 255)));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }
}
