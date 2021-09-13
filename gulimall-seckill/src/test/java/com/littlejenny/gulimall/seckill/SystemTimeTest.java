package com.littlejenny.gulimall.seckill;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SystemTimeTest {
    public static void main(String[] args) {

        DateFormat dateFo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long l = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        System.out.println(dateFo.format(calendar.getTime()));
        System.out.println(calendar.getTime());

        Date date = new Date();

        System.out.println(date);
    }
}
