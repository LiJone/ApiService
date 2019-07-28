package com.tss.apiservice.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * @author 壮Jone
 */
public class DateUtils {
    private static Boolean checkTime(String time) throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 =formatter.parse(time);
        return null;
    }
}
