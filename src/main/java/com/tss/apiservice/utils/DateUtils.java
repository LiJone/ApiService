package com.tss.apiservice.utils;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * @author 壮Jone
 */
public class DateUtils {

    public static Boolean beforeTime(String thisTime, String otherTime) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 =formatter.parse(thisTime);
        Date d2 =formatter.parse(otherTime);
        return d2.before(d1);
    }

    public static Boolean afterTime(String thisTime, String otherTime) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 =formatter.parse(thisTime);
        Date d2 =formatter.parse(otherTime);
        return d2.after(d1);
    }
}
