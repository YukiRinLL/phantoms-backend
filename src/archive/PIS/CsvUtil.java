package com.phantoms.phantomsbackend.common.utils.PIS;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CsvUtil {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String convertToCsv(List<String[]> dataList) {
        StringWriter stringWriter = new StringWriter();
        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(dataList.get(0)))) {
            for (int i = 1; i < dataList.size(); i++) {
                csvPrinter.printRecord((Object[]) dataList.get(i));
            }
            return stringWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] objectArrayToStringArray(Object[] objects) {
        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            Object obj = objects[i];
            if (obj instanceof Date) {
                strings[i] = dateFormat.format((Date) obj);
            } else if (obj != null) {
                strings[i] = obj.toString();
            } else {
                strings[i] = ""; // Provide an empty string for null values
            }
        }
        return strings;
    }
}