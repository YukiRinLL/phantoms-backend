package com.phantoms.phantomsbackend.common.utils.PIS;


import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;


import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

public class FileUtils {

    /*
     * Java文件操作 获取文件扩展名
     *
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    /*
     * Java文件操作 获取不带扩展名的文件名
     *
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    public static String getPath(String fileName){
        String path= Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
        if (path!=null&&path.startsWith("/")){
            return path.substring(1);
        }
        return path;
    }

    public static  void exportExcel(HttpServletResponse response, List<?>data, Class fillDataClass, String fileName) throws IOException {
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/x-download;charset=utf-8");
        //如果下载的是中文文件名，则会乱码，需要使用url编码
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 配置文件下载
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        // 下载文件能正常显示中文
        response.setHeader("Content-Disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
        EasyExcel.write(response.getOutputStream(), fillDataClass).relativeHeadRowIndex(1). registerWriteHandler(new MonthSheetWriteHandler()).sheet("data").doWrite(data);
    }

    public static  void exportExcel(HttpServletResponse response, List<?>data, String file, String fileName,int mergeRowIndex,int[] mergeColumnIndex) throws IOException {
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/x-download;charset=utf-8");
        //如果下载的是中文文件名，则会乱码，需要使用url编码
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 配置文件下载
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        // 下载文件能正常显示中文
        response.setHeader("Content-Disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
        String tempFile="template/"+file;
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(tempFile);
        EasyExcel.write(response.getOutputStream()).withTemplate(inputStream).sheet().registerWriteHandler(new ExcelFillCellMergeStrategy(mergeRowIndex,mergeColumnIndex)).doFill(data);
    }

    public static void exportExcel(HttpServletResponse response, List<?> data, String file, String fileName) throws IOException {
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileNameURL = URLEncoder.encode(fileName, "UTF-8");
        response.setContentType("application/x-download;charset=utf-8");
        //如果下载的是中文文件名，则会乱码，需要使用url编码
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        // 配置文件下载
        response.setCharacterEncoding("utf-8");
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        // 下载文件能正常显示中文
        response.setHeader("Content-Disposition", "attachment;filename=" + fileNameURL + ";" + "filename*=utf-8''" + fileNameURL);
        String tempFile = "template/" + file;
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(tempFile);
        EasyExcel.write(response.getOutputStream()).withTemplate(inputStream).sheet().doFill(data);
    }

    /**
     *  ava读取文本文件内容
     * @param fileName
     * @return
     */
    public static String readFileContent(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }


    public static String download(String urlString, String filename,
                                  String savePath) throws Exception {
        // 构造URL
        URL url = new URL(urlString);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5 * 1000);
        // 输入流
        InputStream is = con.getInputStream();

        // 1K的数据缓冲
        byte[] bs = new byte[1024];
        // 读取到的数据长度
        int len;
        // 输出的文件流
        File sf = new File(savePath);
        if (!sf.exists()) {
            sf.mkdirs();
        }
        OutputStream os = new FileOutputStream(sf.getPath() + "\\" + filename);
        // 开始读取
        while ((len = is.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        // 完毕，关闭所有链接
        os.close();
        is.close();
        return savePath+filename;
    }

    //链接url下载图片
    private  String downloadPicture(String urlList,String imageName) {
        URL url = null;
        int imageNumber = 0;
        //String imageName =  "C:/Users/admin/Desktop/sign.png";
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            byte[] context=output.toByteArray();
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageName;
    }


}
