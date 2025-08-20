package com.phantoms.phantomsbackend.common.utils.PIS;

import com.google.common.collect.Lists;
//import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * company 重庆庆云石油工程技术有限责任公司
 * FileName DateUtils
 * Package com.health.safety.environment.management.logic.util
 * Description
 * author 李美杰
 * create 2019-01-18 19:33
 * version V1.0
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class.getSimpleName());

    private static final String object = "MM/dd/yyyy";

    private static final String COMMON_EXPRESSION = "yyyy-MM-dd hh:mm:ss";

    /**
    * @methodname dateStrToTemstap
    * @Description {将字符串转成时间戳}
    * @author 李美杰
    * @create 2019/1/18 19:36
    * @param dateStr
    * @throws {如果有异常说明请填写}
    * @return long
    */
    public static long dateStrToTemstap(String dateStr) {
        long result = 0l;
        SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = formate.parse(dateStr);
            result= date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("时间转换失败",e);
        }
        return result;
    }

    /**
     * 格式化时间为字符串
     * @param dateTime
     * @param expression
     * @return
     * @throws Exception
     */
    public static String parseStr(Long dateTime, String expression) throws Exception{
        if(dateTime != null){
            SimpleDateFormat sdf = StringUtils.isBlank(expression) ? new SimpleDateFormat(COMMON_EXPRESSION) : new SimpleDateFormat(expression);
            return sdf.format(new Date(dateTime));
        }
        return null;
    }

    /**
     * 格式化时间为字符串
     * @param dateTime
     * @param expression
     * @return
     * @throws Exception
     */
    public static Date parseStr(String dateTime, String expression){
        if(StringUtils.isNotEmpty(dateTime)){
            SimpleDateFormat sdf = StringUtils.isBlank(expression) ? new SimpleDateFormat(COMMON_EXPRESSION) : new SimpleDateFormat(expression);
            try {
                return sdf.parse(dateTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static String convertDateToTimestamp(Date date, String expression) {
        return String.valueOf(date.getTime()); // 获取时间戳
    }

    public static Date convertTimestampToDate(String dateTime, String expression) {
        if (StringUtils.isNotEmpty(dateTime)) {
            // 判断是否为数字，如果是，则说明是一个时间戳
            if (StringUtils.isNumeric(dateTime)) {
                long timestamp = Long.parseLong(dateTime); // 将字符串转换为 long 类型
                return new Date(timestamp); // 直接返回 Date 对象
            } else {
                // 处理普通日期字符串
                SimpleDateFormat sdf = StringUtils.isBlank(expression)
                        ? new SimpleDateFormat(COMMON_EXPRESSION)
                        : new SimpleDateFormat(expression);
                try {
                    return sdf.parse(dateTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * 获得只有年月的字符串
     * @param time
     * @return
     */
    public static String formatOnlyYearAndMonth(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String formatTime = sdf.format(time);
        return formatTime;
    }
    public static String formatDate(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatTime = sdf.format(time);
        return formatTime;
    }

    public static String formatDateForYear(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String formatTime = sdf.format(time);
        return formatTime;
    }

    public static String formatDate2(Date time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTime = sdf.format(time);
        return formatTime;
    }
    /**
     * 获得日期的数值
     * @param time
     * @return
     */
    public static long formatDateToLong(Date time){
        return time.getTime();
    }

    /**
     * 获得传入时间本月开始时间和本月结束时间
     * 返回值第一个元素为月开始时间    第二个元素为月结束时间
     * @param date
     */
    public static ArrayList<Date> getCurrentMonthStartTimeAndEndTime(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //设置为1号,当前日期既为本月第一天
        c.set(Calendar.DAY_OF_MONTH, 1);
        //将小时至0
        c.set(Calendar.HOUR_OF_DAY, 0);
        //将分钟至0
        c.set(Calendar.MINUTE, 0);
        //将秒至0
        c.set(Calendar.SECOND,0);
        //将毫秒至0
        c.set(Calendar.MILLISECOND, 0);
        // 获取本月第一天的时间戳
        Date firstDay = new Date(c.getTimeInMillis());

        //设置为当月最后一天
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        //将小时至23
        c.set(Calendar.HOUR_OF_DAY, 23);
        //将分钟至59
        c.set(Calendar.MINUTE, 59);
        //将秒至59
        c.set(Calendar.SECOND,59);
        //将毫秒至999
        c.set(Calendar.MILLISECOND, 999);
        // 获取本月最后一天的时间戳
        Date lastDay = new Date(c.getTimeInMillis());
        ArrayList<Date> dates = Lists.newArrayList(firstDay, lastDay);
        return dates;

    }


    /**
     * 两日期相差多少天
     * @param startDate
     * @param endDate
     * @return
     */
    public static double betweenDay(Date startDate, Date endDate){
        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
    }
    public static double betweenDayStr(String startDate, String endDate){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        double ret = 0;
        try {
            Date startDate1 = sdf.parse(startDate);
            Date endDate1 = sdf.parse(endDate);
            ret = (endDate1.getTime() - startDate1.getTime()) / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 传入的日期格式应为 yyyy-MM-dd
     * @param tmpDate
     * @return
     * @throws Throwable
     */
    public static String dayForWeek( Date tmpDate){
        Calendar cal = Calendar.getInstance();
        String[] weekDays = { "7", "1", "2", "3", "4", "5", "6" };
        cal.setTime(tmpDate);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }


    public static boolean isSameDate(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setFirstDayOfWeek(Calendar.MONDAY);//西方周日为一周的第一天，咱得将周一设为一周第一天
        cal2.setFirstDayOfWeek(Calendar.MONDAY);
        cal1.setTime(d1);
        cal2.setTime(d2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (subYear == 0)// subYear==0,说明是同一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) //subYear==1,说明cal比cal2大一年;java的一月用"0"标识，那么12月用"11"
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11)//subYear==-1,说明cal比cal2小一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }


    public static boolean isSameDate(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setFirstDayOfWeek(Calendar.MONDAY);//西方周日为一周的第一天，咱得将周一设为一周第一天
        cal2.setFirstDayOfWeek(Calendar.MONDAY);
        cal1.setTime(d1);
        cal2.setTime(d2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (subYear == 0)// subYear==0,说明是同一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == 1 && cal2.get(Calendar.MONTH) == 11) //subYear==1,说明cal比cal2大一年;java的一月用"0"标识，那么12月用"11"
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        else if (subYear == -1 && cal1.get(Calendar.MONTH) == 11)//subYear==-1,说明cal比cal2小一年
        {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    /**
     * 获取上周周一（第一天是周一）
     *
     * @return
     */
    public static String getLastWeekFirstDay() {
        Calendar cal = Calendar.getInstance();
        // 将每周第一天设为星期一，默认是星期天
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.DATE, -1 * 7);
        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String firstDay = format.format(cal.getTime());
        return firstDay;
    }

    /**
     * 获取上周周日（第一天是周一）
     * @return
     */
    public static String getLastWeekLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);//将每周第一天设为星期一，默认是星期天
        cal.add(Calendar.DATE, -1 * 7);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String lastDay = format.format(cal.getTime());
        return lastDay;
    }

    // 获取本周的开始时间
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    // 获取本周的结束时间
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    // 获取本月的开始时间
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    // 获取本月的结束时间
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    // 获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    /**
     * 当前季度的开始时间
     *
     * @return
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth >= 1 && currentMonth <= 3)
            c.set(Calendar.MONTH, 0);
        else if (currentMonth >= 4 && currentMonth <= 6)
            c.set(Calendar.MONTH, 3);
        else if (currentMonth >= 7 && currentMonth <= 9)
            c.set(Calendar.MONTH, 6);
        else if (currentMonth >= 10 && currentMonth <= 12)
            c.set(Calendar.MONTH, 9);
        c.set(Calendar.DATE, 1);
        return getDayStartTime(c.getTime());
    }

    /**
     * 当前季度的结束时间
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return getDayStartTime(cal.getTime());

    }


    // 获取本年的开始时间
    public static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        return getDayStartTime(cal.getTime());
    }

    // 获取本年的结束时间
    public static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }

    // 获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    // 获取某个日期的开始时间
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    // 获取某个日期的结束时间
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 将给定的 Date 对象设置为当天的 00:00:00 或 23:59:59
     *
     * @param date 要处理的 Date 对象
     * @param startOrEnd 0 表示 00:00:00，1 表示 23:59:59
     * @return 返回设置时间后的 Date 对象
     */
    public static Date setTimeOfDay(Date date, Integer startOrEnd) {
        if (date == null) {
            return null;
        }

        // 将 Date 转为 LocalDate
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime dateTime;

        // 根据参数设置时间
        if (startOrEnd == 0) {
            dateTime = localDate.atStartOfDay();  // 设置为 00:00:00
        } else if (startOrEnd == 1) {
            dateTime = localDate.atTime(LocalTime.MAX);  // 设置为 23:59:59
        } else {
            throw new IllegalArgumentException("参数无效，startOrEnd 必须是 0 或 1");
        }

        // 转换回 Date 对象并返回
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {

        System.out.println("上周开始时间======"+getLastWeekFirstDay());
        System.out.println("上周结束时间======"+getLastWeekLastDay());
        System.out.println("本周开始时间======"+getBeginDayOfWeek());
        System.out.println("本周结束时间======"+getEndDayOfWeek());
        System.out.println("本月开始时间======"+getBeginDayOfMonth());
        System.out.println("本月结束时间======"+getEndDayOfMonth());
        System.out.println("本季度开始时间======"+getCurrentQuarterStartTime());
        System.out.println("本季度结束时间======"+getCurrentQuarterEndTime());
        System.out.println("本年开始时间======"+getBeginDayOfYear());
        System.out.println("本年结束时间======"+getEndDayOfYear());

    }

}
