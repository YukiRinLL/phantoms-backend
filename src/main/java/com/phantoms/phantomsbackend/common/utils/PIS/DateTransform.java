package com.phantoms.phantomsbackend.common.utils.PIS;

import com.google.common.base.Strings;

import java.sql.Date;
import java.time.LocalDate;

public class DateTransform {

    public static Long stringToLong(String dateStr) {
        if (!Strings.isNullOrEmpty(dateStr)) {
            try {
                LocalDate localDate = LocalDate.parse(dateStr);
                Date date = Date.valueOf(localDate);
                return date.getTime();
            } catch (Exception e) {
                return null;
            }

        }
        return null;
    }
}