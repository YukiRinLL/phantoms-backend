package com.phantoms.phantomsbackend.common.utils.TH;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Data
public class ExcelUtil {

    public static final int SAVE_DB_MAX_SIZE = 1000;  //存储插入数据的中间阈值  多少数据量提交一次
    /**
     * 进行启用禁用状态的判断  转Integer
     * @param status
     * @return
     */
    public static  Integer getStatus(String status){
        if (status==null|| status.trim().equals("")){
            return 0;
        }
        if (status.equals("启用")){
            return 1;
        } else  {
            return 0;
        }
    }

    /**
     * 进行对象空值的判断
     * @param obj
     * @return
     */
    public static boolean areAllFieldsNull(Object obj) {
        if (obj == null) {
            return true;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
    public static  String getFieldsIsNull(Integer column, Object o, Map<String,String> context){
        //方法的目的是拼凑可以校验字段是否为空，是空则拼错误信息。
        StringBuilder bu=new StringBuilder("");
        if (o == null) {
            return bu.toString();
        }
        Class<?> clazz = o.getClass();
        int i = context.size();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                if (i==0) break;
                field.setAccessible(true);
                Object value = field.get(o);
                String s = context.get(field.getName());
                if (s != null) {
                    // 判断是否为空
                    i--;
                    if (ObjectUtil.isEmpty(value)||"".equals(value)){
                        bu.append("第").append(column + 1).append("行有错误：").append(s).append("\n");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return bu.toString();
    }

    public static HorizontalCellStyleStrategy getHorizontalCellStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为白色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //边框
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        //自动换行
        headWriteCellStyle.setWrapped(true);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(true);
        headWriteFont.setFontName("宋体");
        headWriteFont.setBold(false);
        headWriteFont.setFontHeightInPoints((short)11);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setWrapped(true);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        WriteFont contentFont =  new WriteFont();
        contentFont.setColor(IndexedColors.BLACK.getIndex());
        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        contentWriteCellStyle.setWriteFont(contentFont);
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
//        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
//        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
//        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        //边框
//        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
//        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
//        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
//        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
//        //自动换行
//        contentWriteCellStyle.setWrapped(true);
//        //文字
//        WriteFont contentWriteFont = new WriteFont();
//        // 字体大小
//        contentWriteFont.setFontHeightInPoints((short)12);
//        contentWriteFont.setFontName("宋体");
//        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

    public static HorizontalCellStyleStrategy getKQStyleStrategy() {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为白色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //边框
        headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
        headWriteCellStyle.setBorderRight(BorderStyle.THIN);
        headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        //自动换行
        headWriteCellStyle.setWrapped(true);
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setBold(true);
        headWriteFont.setFontName("宋体");
        headWriteFont.setFontHeightInPoints((short)14);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        contentWriteCellStyle.setWrapped(true);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);

        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
//        // 背景绿色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
//        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
//        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//        //边框
//        contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
//        contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
//        contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
//        contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
//        //自动换行
//        contentWriteCellStyle.setWrapped(true);
//        //文字
//        WriteFont contentWriteFont = new WriteFont();
//        // 字体大小
//        contentWriteFont.setFontHeightInPoints((short)12);
//        contentWriteFont.setFontName("宋体");
//        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }
    public static boolean areAllFieldsNulls(Object obj) {
        if (obj == null) {
            return true;
        }


        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value != null) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
    public static String Bytes2HexString(Integer decimal) {
        String hexadecimal = Integer.toHexString(decimal).toUpperCase(); // 将10进制数字转换为16进制字符串
        int length = hexadecimal.length(); // 获取16进制字符串的长度
        if (length < 24) {
            // 如果长度小于24，则在前面补0
            for (int i = 0; i < 24 - length; i++) {
                hexadecimal =  hexadecimal+ "0";
            }
        } else if (length > 24) {
            // 如果长度大于24，则截取前24个字符
            hexadecimal = hexadecimal.substring(0, 24);
        }
        return hexadecimal;


    }



    /**
     * 写入到指定excel文件
     */
    public static <T> void write(String fileName, String sheetName, Class<T> head, List<T> datas){
        EasyExcel.write(fileName).head(head).sheet(sheetName).doWrite(datas);
    }

    /**
     * 写入到指定excel的sheet文件
     */
    public static <T> void write(OutputStream outputStream, String sheetName, Class<T> head, List<T> data){
        EasyExcel.write(outputStream).head(head).sheet(sheetName).doWrite(data);
    }

    /**
     * 写入到指定excel的sheet文件
     */
    public static <T> void write(OutputStream outputStream, Integer sheetNo, String sheetName, Class<T> head, List<T> data){
        EasyExcel.write(outputStream).head(head).sheet(sheetNo).sheetName(sheetName).doWrite(data);
    }

    /**
     * 写入到指定excel文件
     */
    public static <T> void write(String fileName, String sheetName, List<List<String>> head, List<T> datas){
        EasyExcel.write(fileName).head(head).sheet(sheetName).doWrite(datas);
    }

    /**
     * 写入到指定excel的sheet文件
     */
    public static <T> void write(OutputStream outputStream, String sheetName, List<List<String>> head, List<T> data){
        EasyExcel.write(outputStream).head(head).sheet(sheetName).doWrite(data);
    }

    /**
     * 写入到指定excel的sheet文件
     */
    public static <T> void write(OutputStream outputStream, Integer sheetNo, String sheetName, List<List<String>> head,
                                 List<T> data){
        EasyExcel.write(outputStream).head(head).sheet(sheetNo).sheetName(sheetName).doWrite(data);
    }

    public static Integer getExcutionSystemType(String executeSystemType) {
        if (executeSystemType==null|| executeSystemType.trim().equals("")){
            return 0;
        }
        if (executeSystemType.equals("内部")){
            return 1;
        } else  {
            return 0;
        }
    }
}
