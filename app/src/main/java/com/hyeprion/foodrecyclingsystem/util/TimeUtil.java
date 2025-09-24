package com.hyeprion.foodrecyclingsystem.util;

import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class TimeUtil {
    public static final String TIME_FORMAT_STYLE_MONTH = "yyyy.MM";
    public static final String TIME_FORMAT_STYLE_DAY = "yyyy.MM.dd";
    public static final String TIME_FORMAT_STYLE_YMD = "yyyy/MM/dd HH:mm";
    public static final String Time_FORMAT_YEAD = "yyyy年MM月dd日 HH:mm";
    public static final String Time_POSITION = "yyyy/MM/dd";
    public static final String TIME_FORMAT_STYLE_MD = "MM月dd日 HH:mm";
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    // private final static SimpleDateFormat dateFormater = new
    // SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // private final static SimpleDateFormat dateFormater2 = new
    // SimpleDateFormat("yyyy-MM-dd");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater4 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 将字符串转为日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        Date d = new Date();
        d.setTime(Long.parseLong(sdate) * 1000);
        return d;
    }

    /**
     * 友好方式显示日期
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        Log.i("toDate", time.toString());
        return friendly_time1(time);
    }

    /**
     * 以友好的方式显示时间
     *
     * @param time
     * @return
     */
    public static String friendly_time1(Date time) {

        if (time == null) {
            return "Unknown";
        }

        Date serviceTime = new Date();
        if (serviceTime == null) {
            return "Unknown1";
        }
        String ftime = "";

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(serviceTime.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((serviceTime.getTime() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (serviceTime.getTime() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = serviceTime.getTime() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((serviceTime.getTime() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (serviceTime.getTime() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = (days - 1) + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 获取当天零点时间戳
     *
     * @return
     */
    public static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    /**
     * 获取当月第一天零点时间戳
     *
     * @return
     */
    public static long getWeeOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }


    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isThisMonth(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater3.get().format(today);
            String timeDate = dateFormater3.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断是否为今年，必须固定格式如下 ：2014-01-01
     *
     * @param time 如：2014-01-01
     * @return
     */
    public static boolean isThisYear(String time) {
        if (time.length() != 10) {
            return false;
        } else {
            Date today = new Date();
            String nowDate = dateFormater2.get().format(today);
            if (nowDate.substring(0, 4).equals(time.substring(0, 4))) {
                return true;
            } else {
                return false;
            }
        }

    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }


    /**
     * 格式化显示时间
     * @param dateStr  yyyy-MM-dd HH:mm:ss
     * @return
     */
//    public static String formatDate(String dateStr) {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Date date;
//        try {
//            date = sdf.parse(dateStr);
//        } catch (ParseException e) {
//            date = new Date();
//            e.printStackTrace();
//        }
//        String needStr = sdf.format(date);
//        return needStr;
//    }

    /**
     * @param dateStr
     * @param format
     * @return
     */
    public static Date parseDate(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化时间
     *
     * @param time
     * @param format
     * @return
     */
    public static String formatTime(long time, String format) {
        if (time == 0 || TextUtils.isEmpty(format)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format.toString());
        Date date = new Date(time);
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * 格式化时间
     *
     * @return
     */
    public static String formatTime(String strTime) {
        long time = Long.parseLong(strTime);
        if (time == 0 || TextUtils.isEmpty(TIME_FORMAT_STYLE_YMD)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_STYLE_YMD.toString());
        Date date = new Date(time * 1000);
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * 格式化时间
     *
     * @return
     */
    public static String formatTime(String strTime, String format) {
        long time = Long.parseLong(strTime);
        if (time == 0 || TextUtils.isEmpty(format)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format.toString());
        Date date = new Date(time * 1000);
        String dateStr = sdf.format(date);
        return dateStr;
    }


    /*时间戳转换成字符*/
    static SimpleDateFormat sf;


    /**
     * 获取当前月份字符串
     *
     * @return String 月份字符串
     */
    public static String getDateMonthToString() {
        long time = System.currentTimeMillis();
        Date d = new Date(time);
//        sf = new SimpleDateFormat("yyyy年MM月dd日");
        sf = new SimpleDateFormat(TIME_FORMAT_STYLE_MONTH);

        return sf.format(d);
    }

    /**
     * 获取当前日期字符串
     *
     * @return long 时间戳
     */
    public static long getDate() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前日期字符串
     *
     * @return String 日期字符串
     */
    public static String getDateDayToString() {
        long time = System.currentTimeMillis();
        Date d = new Date(time);
//        sf = new SimpleDateFormat("yyyy年MM月dd日");
        sf = new SimpleDateFormat(TIME_FORMAT_STYLE_DAY);

        return sf.format(d);
    }

    public static String getDateMsToString() {
        long time = System.currentTimeMillis();
        Date d = new Date(time);
//        sf = new SimpleDateFormat("yyyy年MM月dd日");
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        return sf.format(d);
    }

    public static String getDateSToString() {
        long time = System.currentTimeMillis();
        Date d = new Date(time);
//        sf = new SimpleDateFormat("yyyy年MM月dd日");
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sf.format(d);
    }


    /**
     * time1早于time2，返回true
     *
     * @param time1
     * @return
     */
    public static boolean compareTime(String time1) {
        if (time1 == null || time1.equals("")) {
            return false;
        }

        try {
            long time = System.currentTimeMillis();
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date a = sf.parse(sf.format(time));
            Date b = sf.parse(time1);
            LogUtils.e("当前时间：" + a.toString());
            LogUtils.e("预计到达时间：" + b.toString());
            if (a.before(b))  // 如果当前时间早于预计到达时间，不迟到
                return false;
            else // 迟到
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 如果当前时间比传入时间大于（晚）一小时，返回false，在一小时之内，返回true
     *
     * @param startTime
     * @return
     */
    public static boolean compareTime2(String startTime) {

        try {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long time = System.currentTimeMillis();
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = sf.parse(sf.format(time));
            Date start = sf.parse(startTime);
            LogUtils.e("当前时间：" + now.toString());
            LogUtils.e("上次时间：" + start.toString());

            // 获得两个时间的毫秒时间差异
            long diff = now.getTime() - start.getTime();
            // 计算差多少小时
            long hour = diff / nh;
            if (hour >= 1) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 如果当前时间比传入时间大于（晚）10分钟，返回false，在10分钟之内，返回true
     *
     * @param startTime
     * @return
     */
    public static boolean orderOvertime10m(String startTime) {

        try {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            long time = System.currentTimeMillis();
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = sf.parse(sf.format(time));
            Date start = sf.parse(startTime);
            LogUtils.e("当前时间：" + now.toString());
            LogUtils.e("上次时间：" + start.toString());

            // 获得两个时间的毫秒时间差异
            long diff = now.getTime() - start.getTime();
            // 计算差多少小时
            long minute = diff / nm;
            if (minute >= 10) {
                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * time1早于time2，返回true
     *
     * @param time1
     * @return
     */
    public static boolean compareTime2(Date time1) {

        try {
            long time = System.currentTimeMillis();
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date a = sf.parse(sf.format(time));
            Date b = sf.parse(sf.format(time1));
            if (a.before(b))  // 如果当前时间早于预计到达时间，不迟到
                return false;
            else // 迟到
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * time1早于time2，返回true
     *
     * @param startTime
     * @return
     */
    public static boolean compareTime3(String startTime) {

        try {
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long time = System.currentTimeMillis();
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date now = sf.parse(sf.format(time));
            Date start = sf.parse(startTime);
            LogUtils.e("当前时间：" + now.toString());
            LogUtils.e("上次时间：" + start.toString());

            if (now.before(start))  // 如果当前时间早于预计到达时间，不迟到
                return false;
            else // 迟到
                return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 当前时间等于凌晨5：00，返回true
     *
     * @return boolean
     */
    public static boolean compareTime4() {
        long time = System.currentTimeMillis();
        sf = new SimpleDateFormat("HH:mm:ss");//设置日期格式

        Date now = null;
        Date fiveTime = null;
        try {
            now = sf.parse(sf.format(time));

            fiveTime = sf.parse("05:00:00");
            long result = now.getTime() - fiveTime.getTime();
//            now - fiveTime
            if (Math.abs(result) < 60) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getDateAdd1ToString() {
        long time = System.currentTimeMillis();
        Date d = new Date(time + (60 * 60 * 1000));
//        sf = new SimpleDateFormat("yyyy年MM月dd日");
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        return sf.format(d);
    }

    public static Timestamp getDatetime() {
        Timestamp timestamp = null;
        try {
            long time = System.currentTimeMillis();
            sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = sf.parse(sf.format(time));
            timestamp = new Timestamp(date.getTime());
            return timestamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;


    }

    /**
     * 获取指定分钟后的时间
     *
     * @param afterMin 指定的分钟
     * @return
     */
    public static Date getAfterXMin(String afterMin) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());//获取当前时间
        calendar.add(Calendar.MINUTE, Integer.parseInt(afterMin));// 指定分钟后
        return calendar.getTime();
    }

    /**
     * 判断当前时间是否在传入参数时间之前，之前返回true，之后返回false
     *
     * @param afterDate 指定分钟后的时间
     * @return
     */
    public static boolean nowTimeIsAfter(Date afterDate) {
        Date date2 = new Date();//获取当前时间
        return date2.getTime() < afterDate.getTime();
    }

   /* public static void a() {
        Date date = new Date();//获取当前时间
        Date date2 = new Date();//获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 20);// 20分钟前
        date = calendar.getTime();
        //获取到完整的时间
        String minute = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(calendar.getTime());
        LogUtils.e("time:---" + minute);
        LogUtils.e("time:---2  " + date.getTime());
        LogUtils.e("time:---3   " + date2.getTime() + "\t\t\t" + (date2.getTime() < date.getTime()));
    }*/


    public static String formatTimeMonth(String strTime) {
        long time = Long.parseLong(strTime);
        if (time == 0 || TextUtils.isEmpty(TIME_FORMAT_STYLE_MD)) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_STYLE_MD.toString());
        Date date = new Date(time * 1000);
        String dateStr = sdf.format(date);
        return dateStr;
    }

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, 1);
        cal.roll(Calendar.DATE, -1);
        int maxDate = cal.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 将字符串转为日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate2(String sdate) {

        try {
            if (sdate.contains("/")) {
                return dateFormater2.get().parse(sdate);
            } else {
                return dateFormater4.get().parse(sdate);
            }
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取当前年月日
     *
     * @return
     */
    public static String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);
        return dateNowStr;
    }

    /**
     * 获取当前年月日
     *
     * @return
     */
    public static long getCurrentLongTime() {
        return System.currentTimeMillis() / 1000;
    }


    /**
     * 判断时间是否在时间段内
     *
     * @param nowTime   当前时间
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return true 在  false 不在
     */
    private static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        if (nowTime == null || beginTime == null || endTime == null) {
            return false;
        }
        return nowTime.getTime() >= beginTime.getTime() && nowTime.getTime() <= endTime.getTime();

    }


}
