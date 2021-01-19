package com.jzg.clue.api.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author mush
 * @time 2017/6/8 17:07
 * @describe 时间处理工具类
 */
public class DateUtils {
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
    public static SimpleDateFormat format2 = new SimpleDateFormat("yyyy.MM.dd");
    public static SimpleDateFormat format3 = new SimpleDateFormat("yyyy.MM");
    public static SimpleDateFormat format4 = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat format5 = new SimpleDateFormat("yyyy/MM");

    /**
     * 处理接口参数传值过来的日期为--》****年**月
     */
    public static String getRegDate(String regDate) {
        String strRegDate = "";
        Date date = new Date();
        int flag = 0;
        try {
            try {
                date = format.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format1.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format2.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format3.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format4.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            try {
                date = format5.parse(regDate);
            } catch (Exception ex) {
                flag = flag + 1;
            }
            if (flag < 6) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                strRegDate = cal.get(Calendar.YEAR) + "年" + (cal.get(Calendar.MONTH) + 1) + "月";
            } else {
                strRegDate = regDate;
            }
        } catch (Exception e) {
            strRegDate = regDate;
        }
        return strRegDate;
    }
}
