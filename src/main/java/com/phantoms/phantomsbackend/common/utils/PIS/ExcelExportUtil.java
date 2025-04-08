package com.phantoms.phantomsbackend.common.utils.PIS;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;

import com.phantoms.phantomsbackend.common.exception.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExcelExportUtil {

    public static void exportExcel(HttpServletResponse response, String fileName, List<Map<String, Object>> dataList, String[] headers) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据行
            int rowNum = 1;
            for (Map<String, Object> data : dataList) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (String header : headers) {
                    Cell cell = row.createCell(colNum++);
                    cell.setCellValue(String.valueOf(data.get(header)));
                }
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            // 写入Excel文件
            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to Excel", e);
        }
    }

    public static void exportExcel(HttpServletResponse response, Map<String, Object> map, String file, String fileName, int mergeRowIndex, int[] mergeColumnIndex) throws IOException {
        // 防止中文乱码
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);

        // 从 resources/template/ 目录加载模板文件
        String templatePath = "template/" + file;
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(templatePath);
        if (inputStream == null) {
            throw new FileNotFoundException("模板文件未找到：" + templatePath);
        }

        // 使用 EasyExcel 填充数据并写入响应
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).registerWriteHandler(new ExcelFillCellMergeStrategy(mergeRowIndex, mergeColumnIndex)).withTemplate(inputStream).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            // 构建填充配置，设置是否强制换行
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

            if (map != null) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    String key = entry.getKey();
                    if (value instanceof List) {
                        excelWriter.fill(new FillWrapper(entry.getKey(), (List<?>) value), fillConfig, writeSheet);
                    } else {
                        excelWriter.fill(Collections.singletonMap(key, value), writeSheet);
                    }
                }
            }
        }
    }

    public static void exportMergeExcel(HttpServletResponse response, String fileName, Collection<?> excelList, Class<?> excel, int[] mergeColIndex, int mergeRowIndex) {
        try {
            String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setCharacterEncoding("utf-8");
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
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
                throw new ServiceException(ServiceException.UNKNOWN_ERROR, "导出异常：" + e.getMessage());
            } finally {
                if (writer != null) {
                    writer.finish();
                }
            }
            writer.finish();
        } catch (IOException e) {
            throw new ServiceException(ServiceException.UNKNOWN_ERROR, "全局导出异常：" + e.getMessage());
        }
    }


    public static void createChart(OutputStream outputStream, List<Map<String, Object>> data) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("统计图");

        // 创建表头
        String[] headers = {"name", "问题数量", "完成度"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (Map<String, Object> entry : data) {
            String name = (String) entry.get("name");
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) entry.get("dataList");

            for (Map<String, Object> item : dataList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(name); // 项目部名称
                row.createCell(1).setCellValue(((Number) item.get("problemNum")).doubleValue()); // 问题数量
                //row.createCell(2).setCellValue(((Number) item.get("solveRate")).doubleValue()); // 完成度
            }
        }

        // 创建图表
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 2, 10, 20);

        XSSFChart chart = drawing.createChart(anchor);
        CTChart ctChart = chart.getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();

        // 创建柱状图
        CTBarChart barChart = ctPlotArea.addNewBarChart();
        barChart.addNewVaryColors().setVal(false);
        CTBarSer barSeries = barChart.addNewSer();
        CTSerTx barSerTx = barSeries.addNewTx();
        CTStrRef barStrRef = barSerTx.addNewStrRef();
        barStrRef.setF("Sheet1!$B$1");
        barSeries.addNewIdx().setVal(0);

        CTAxDataSource catAx = barSeries.addNewCat();
        CTStrRef catStrRef = catAx.addNewStrRef();
        catStrRef.setF("Sheet1!$A$2:$A$" + (data.size() + 1));

        CTNumDataSource valAx = barSeries.addNewVal();
        CTNumRef valStrRef = valAx.addNewNumRef();
        valStrRef.setF("Sheet1!$B$2:$B$" + (data.size() + 1));

        // 创建折线图
        CTLineChart lineChart = ctPlotArea.addNewLineChart();
        CTLineSer lineSeries = lineChart.addNewSer();
        CTSerTx lineSerTx = lineSeries.addNewTx();
        CTStrRef lineStrRef = lineSerTx.addNewStrRef();
        lineStrRef.setF("Sheet1!$C$1");
        lineSeries.addNewIdx().setVal(1);

        CTAxDataSource catAxLine = lineSeries.addNewCat();
        CTStrRef catStrRefLine = catAxLine.addNewStrRef();
        catStrRefLine.setF("Sheet1!$A$2:$A$" + (data.size() + 1));

        CTNumDataSource valAxLine = lineSeries.addNewVal();
        CTNumRef valStrRefLine = valAxLine.addNewNumRef();
        valStrRefLine.setF("Sheet1!$C$2:$C$" + (data.size() + 1));

        // 设置坐标轴
        CTValAx valAx1 = ctPlotArea.addNewValAx();
        valAx1.addNewAxId().setVal(123456);
        valAx1.addNewScaling().addNewOrientation().setVal(STOrientation.MIN_MAX);
        valAx1.addNewAxPos().setVal(STAxPos.L);
        valAx1.addNewCrossAx().setVal(123457);
        valAx1.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        CTValAx valAx2 = ctPlotArea.addNewValAx();
        valAx2.addNewAxId().setVal(123458);
        valAx2.addNewScaling().addNewOrientation().setVal(STOrientation.MIN_MAX);
        valAx2.addNewAxPos().setVal(STAxPos.R);
        valAx2.addNewCrossAx().setVal(123457);
        valAx2.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        CTCatAx catAx1 = ctPlotArea.addNewCatAx();
        catAx1.addNewAxId().setVal(123457);
        catAx1.addNewScaling().addNewOrientation().setVal(STOrientation.MIN_MAX);
        catAx1.addNewAxPos().setVal(STAxPos.B);
        catAx1.addNewCrossAx().setVal(123456);
        catAx1.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);

        // 设置图表标题
        CTTitle title = ctChart.addNewTitle();
        CTStrRef titleStrRef = title.addNewTx().addNewStrRef();
        titleStrRef.setF("Sheet1!$A$1");

        // 写入输出流
        workbook.write(outputStream);
        workbook.close();
    }

}