package com.phantoms.phantomsbackend.common.excel;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.WriteWorkbook;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;

import com.phantoms.phantomsbackend.common.utils.TH.ExcelUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * excel数据导出监听抽象类(采用模版模式, 支持百万数据处理方式)
 * @author 头秃程序员
 */
@Slf4j
public abstract class AbstractExcelExport<T>  {
    private Integer rowHeight;
    private Integer contentHeight;
   private Integer columnWidth;

   private Map<Integer,Integer> assignWidthMap;

   private Integer size=0;
   private CellWriteHandler    cellWriteHandler;
   private MergeHandler mergeHandler;

   private HorizontalCellStyleStrategy horizontalCellStyleStrategy;

    public void setHorizontalCellStyleStrategy(HorizontalCellStyleStrategy horizontalCellStyleStrategy) {
        this.horizontalCellStyleStrategy = horizontalCellStyleStrategy;
    }

    public void setMergeHandler(MergeHandler mergeHandler) {
        this.mergeHandler = mergeHandler;
    }

    /**
     * 按照模板进行导出。有需要排除的字段
     *
     */
    public  void  exportTemplateHasExclude( String fileName, List<String> head, List<T> dataList) {
        //获取当前请求的resopne对象
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            EasyExcel.write(outputStream,this.getClass().getGenericSuperclass().getClass()).excludeColumnFiledNames(head).sheet(fileName.split("\\.")[0])
                    .doWrite(dataList);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }


    /**
     * 按照模板进行导出。没有排除的字段
     *
     */
    public  void  exportTemplateNoExclude( String fileName, List<T> dataList) {
        //获取当前请求的resopne对象
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            EasyExcel.write(outputStream,this.getClass().getGenericSuperclass().getClass()).sheet(fileName.split("\\.")[0])
                    .doWrite(dataList);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * 导出小数据量(百万以下)
     */
    protected void exportSmallData(HttpServletResponse response, String fileName, List<List<String>> head, List<T> data){
        OutputStream outputStream = null;
        try {
            long startTime = System.currentTimeMillis();
            outputStream = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String sheetName = fileName;
            fileName = URLEncoder.encode(fileName+ IdUtil.objectId(), "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName
                    + ExcelTypeEnum.XLSX.getValue());
            ExcelUtil.write(outputStream, sheetName, head, data);
            log.info("导出所用时间:{}", (System.currentTimeMillis() - startTime) / 1000 + "秒");
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }

    /**
     * 导出大数据量(百万级别)
     */
    protected void exportWithBigData(HttpServletResponse response, String fileName, List<String> head,
                                  Map<String, Object> queryCondition,Class<?> e) {
        // 总的记录数
        Integer totalCount = dataTotalCount(queryCondition);
        // 每一个Sheet存放的数据
        Integer sheetDataRows = eachSheetTotalCount();
        // 每次写入的数据量20w
        Integer writeDataRows = eachTimesWriteSheetTotalCount();
        if(totalCount < sheetDataRows){
            sheetDataRows = totalCount;
        }
        if(sheetDataRows < writeDataRows){
            writeDataRows = sheetDataRows;
        }
        doExport(response, fileName, head, queryCondition, totalCount, sheetDataRows, writeDataRows,e);
    }
    protected void exportDataCustomer(HttpServletResponse response, String fileName, List<List<String>> head,
                                     Map<String, Object> queryCondition) {
        // 总的记录数
        Integer totalCount = dataTotalCount(queryCondition);
        // 每一个Sheet存放的数据
        Integer sheetDataRows = eachSheetTotalCount();
        // 每次写入的数据量20w
        Integer writeDataRows = eachTimesWriteSheetTotalCount();
        if(totalCount < sheetDataRows){
            sheetDataRows = totalCount;
        }
        if(sheetDataRows < writeDataRows){
            writeDataRows = sheetDataRows;
        }doExport(response, fileName, head, queryCondition, totalCount, sheetDataRows, writeDataRows);
    }

    /**
     * 计算导出数据的总数
     */
    protected abstract Integer dataTotalCount(Map<String, Object> queryCondition);

    /**
     * 每一个sheet存放的数据总数
     */
    protected abstract Integer eachSheetTotalCount();

    /**
     * 每次写入sheet的总数
     */
    protected abstract Integer eachTimesWriteSheetTotalCount();

    protected abstract void buildDataList(List<T> resultList, Map<String, Object> queryCondition,
                                          Integer pageNo, Integer pageSize);

    protected abstract void buildDataListInit(List<List<String>> resultList, Map<String, Object> queryCondition,
                                          Integer pageNo, Integer pageSize);

    private void doExport(HttpServletResponse response, String fileName, List<String> head,
                          Map<String, Object> queryCondition, Integer totalCount, Integer sheetDataRows,
                          Integer writeDataRows,Class<?> C){
        OutputStream outputStream = null;
        try {
            long startTime = System.currentTimeMillis();
                outputStream = response.getOutputStream();
//            WriteCellStyle style=new WriteCellStyle();
//            WriteWorkbook writeWorkbook = new WriteWorkbook();
//            writeWorkbook.setOutputStream(outputStream);
//            writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
//            ExcelWriter writer = new ExcelWriter(writeWorkbook);
//            WriteTable table = new WriteTable();
//            SimpleColumnWidthStyleStrategy y = new SimpleColumnWidthStyleStrategy(20);
//            SimpleRowHeightStyleStrategy k = new SimpleRowHeightStyleStrategy((short) 50, (short) 25);
//            List<WriteHandler> writeHandlers=new ArrayList<>();
//            Collections.addAll(writeHandlers,y,k);
//            table.setCustomWriteHandlerList(writeHandlers);
//            table.setHead(head);


            // 计算需要的Sheet数量
            int sheetNum = totalCount % sheetDataRows == 0 ? (totalCount / sheetDataRows) : (totalCount / sheetDataRows + 1);
            // 计算一般情况下每一个Sheet需要写入的次数(一般情况不包含最后一个sheet,因为最后一个sheet不确定会写入多少条数据)
            int oneSheetWriteCount = totalCount > sheetDataRows ? sheetDataRows / writeDataRows :
                    totalCount % writeDataRows > 0 ? totalCount / writeDataRows + 1 : totalCount / writeDataRows;
            // 计算最后一个sheet需要写入的次数
            int lastSheetWriteCount = totalCount % sheetDataRows == 0 ? oneSheetWriteCount :
                    (totalCount % sheetDataRows % writeDataRows == 0 ? (totalCount / sheetDataRows / writeDataRows) :
                            (totalCount / sheetDataRows / writeDataRows + 1));
            // 分批查询分次写入
            List<T> dataList=new ArrayList<>();
            for (int i = 0; i < sheetNum; i++) {
                // 创建Sheet
                WriteSheet sheet = new WriteSheet();
                sheet.setSheetNo(i);
                sheet.setSheetName(sheetNum == 1 ? fileName : fileName + i);
                // 循环写入次数: j的自增条件是当不是最后一个Sheet的时候写入次数为正常的每个Sheet写入的次数,如果是最后一个就需要使用计算的次数lastSheetWriteCount
                for (int j = 0; j < (i != sheetNum - 1 || i==0 ? oneSheetWriteCount : lastSheetWriteCount); j++) {
                    // 集合复用,便于GC清理
                    dataList.clear();
                    buildDataList(dataList, queryCondition, j + 1 + oneSheetWriteCount * i, writeDataRows);
                    // 写数据
//                    writer.write(dataList, sheet, table);

                    EasyExcel.write(outputStream,C).excludeColumnFiledNames(head).sheet(fileName.split("\\.")[0])
                            .doWrite(dataList);

//                    SimpleColumnWidthStyleStrategy simpleColumnWidthStyleStrategy = new SimpleColumnWidthStyleStrategy(22);
//                    EasyExcel.write(outputStream)
//                            // 这里放入动态头
//                            .head(head)
//                            // java以点分割要转义符
//                            .sheet(fileName.split("\\.")[0])
//                            // 注册策略
//                            .registerWriteHandler(simpleColumnWidthStyleStrategy)// 简单的列宽策略，列宽20
//
//                            .registerWriteHandler(new SimpleRowHeightStyleStrategy((short)50,(short)25)) // 简单的行高策略：头行高，内容行高
//                            .doWrite(dataList);

                }
            }
            response.setHeader("Content-Disposition", "attachment;filename="
//                    + new String((fileName+ IdUtil.objectId()).getBytes("gb2312"),
                    + new String((fileName).getBytes("gb2312"), "ISO-8859-1") + ".xlsx");
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
//            writer.finish();
            outputStream.flush();
            log.info("导出所用时间:{}", (System.currentTimeMillis() - startTime) / 1000 + "秒");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }

    public void setCellWriteHandler(CellWriteHandler cellWriteHandler) {
        this.cellWriteHandler = cellWriteHandler;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setRowAndColumn(Integer headWidth, Integer contentHeight, Integer columnWidth){
        this.rowHeight=headWidth;
        this.contentHeight=contentHeight;
        this.columnWidth=columnWidth;
    }

    public void setAssignWidthMap(Map<Integer, Integer> assignWidthMap) {
        this.assignWidthMap = assignWidthMap;
    }

    private void doExport(HttpServletResponse response, String fileName, List<List<String>> head,
                          Map<String, Object> queryCondition, Integer totalCount, Integer sheetDataRows,
                          Integer writeDataRows){
        OutputStream outputStream = null;
        try {
            long startTime = System.currentTimeMillis();
            outputStream = response.getOutputStream();
            WriteWorkbook writeWorkbook = new WriteWorkbook();
            writeWorkbook.setOutputStream(outputStream);
            writeWorkbook.setExcelType(ExcelTypeEnum.XLSX);
            ExcelWriter writer = new ExcelWriter(writeWorkbook);
            WriteTable table = new WriteTable();
            table.setUseDefaultStyle(false);
            table.setHead(head);
            //设置列宽
            HeadWidthHandler headWidthHandler = new HeadWidthHandler(columnWidth);
            if (!ObjectUtil.isEmpty(assignWidthMap)) headWidthHandler.setColumnWidthMap(assignWidthMap);
            table.getCustomWriteHandlerList().add(headWidthHandler);
            //设置行高、内容行高
            table.getCustomWriteHandlerList().add(new RowHeightWriteHandler(rowHeight,contentHeight));
            if (cellWriteHandler != null)             table.getCustomWriteHandlerList().add(cellWriteHandler);
            if (mergeHandler!=null) table.getCustomWriteHandlerList().add(mergeHandler);
            if (horizontalCellStyleStrategy!=null) table.getCustomWriteHandlerList().add(horizontalCellStyleStrategy);

            // 计算需要的Sheet数量
            int sheetNum = totalCount % sheetDataRows == 0 ? (totalCount / sheetDataRows) : (totalCount / sheetDataRows + 1);
            // 计算一般情况下每一个Sheet需要写入的次数(一般情况不包含最后一个sheet,因为最后一个sheet不确定会写入多少条数据)
            int oneSheetWriteCount = totalCount > sheetDataRows ? sheetDataRows / writeDataRows :
                    totalCount % writeDataRows > 0 ? totalCount / writeDataRows + 1 : totalCount / writeDataRows;
            // 计算最后一个sheet需要写入的次数
            int lastSheetWriteCount = totalCount % sheetDataRows == 0 ? oneSheetWriteCount :
                    (totalCount % sheetDataRows % writeDataRows == 0 ? (totalCount / sheetDataRows / writeDataRows) :
                            (totalCount / sheetDataRows / writeDataRows + 1));
            // 分批查询分次写入
            List<List<String>> dataList = new ArrayList<>();
            for (int i = 0; i < sheetNum; i++) {
                // 创建Sheet
                WriteSheet sheet = new WriteSheet();

                sheet.setSheetName(sheetNum == 1 ? fileName : fileName + i);
                // 循环写入次数: j的自增条件是当不是最后一个Sheet的时候写入次数为正常的每个Sheet写入的次数,如果是最后一个就需要使用计算的次数lastSheetWriteCount
                for (int j = 0; j < (i != sheetNum - 1 || i==0 ? oneSheetWriteCount : lastSheetWriteCount); j++) {
                    // 集合复用,便于GC清理
                    dataList.clear();
                    buildDataListInit(dataList, queryCondition, j + 1 + oneSheetWriteCount * i, writeDataRows);
                    // 写数据
                    writer.write(dataList, sheet, table);
                }
            }
            response.setHeader("Content-Disposition", "attachment;filename="
                    + new String((fileName+ IdUtil.objectId()).getBytes("gb2312"),
                    "ISO-8859-1") + ".xlsx");
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            writer.finish();
            outputStream.flush();
            log.info("导出所用时间:{}", (System.currentTimeMillis() - startTime) / 1000 + "秒");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }
}
