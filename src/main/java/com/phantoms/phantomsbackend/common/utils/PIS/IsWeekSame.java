package com.phantoms.phantomsbackend.common.utils.PIS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * java中如何判断两个日期是否是同一周
 * @author Administrator
 * java中的Calendar是比较强的。
 * “2004-12-25”是星期六，也就是说它是2004年中第52周的星期六，
 * 那么“2004-12-26”到底是2004年的第几周哪，java中经测试取得的它的Week值是1，
 * 那么也就是说它被看作2005年的第一周了，这个处理是比较好的。
 * 可以用来判断“2004-12-26”和“2005-1-1”是同一周。
 */
public class IsWeekSame {

    public static boolean isSameDate(String date1, String date2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try
        {
            d1 = format.parse(date1);
            d2 = format.parse(date2);
        }
        catch (Exception e)
        {
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


    public static void main(String[] args)
    {
        boolean a = isSameDate("2016-12-25", "2017-1-1");
        if (a) {
            System.out.println("是同一周！");
        } else {
            System.out.println("不是同一周！");
        }
    }
}
