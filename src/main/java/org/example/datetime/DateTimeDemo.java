package org.example.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeDemo {
    public static void main(String[] args) {
        test();
    }
    public static void test(){
        LocalDate d = LocalDate.now(); // 当前日期
        LocalTime t = LocalTime.now(); // 当前时间
        LocalDateTime dt = LocalDateTime.now(); // 当前日期和时间
        System.out.println(d); // 严格按照ISO 8601格式打印
        System.out.println(t);
        System.out.println(dt);

        LocalDate localDate = dt.toLocalDate(); // 转换到当前日期
        LocalTime localTime = dt.toLocalTime(); // 转换到当前时间

        LocalDate localDate1 = localDate.of(2023, 1, 1);
        LocalTime localTime1 = localTime.of(0, 0, 0);
        LocalDateTime dateTime = LocalDateTime.of(2023,1,1,0,0,1);
        LocalDateTime dateTime1 = LocalDateTime.of(localDate1, localTime1);

        //注意ISO 8601规定的日期和时间分隔符是T
        LocalDateTime localDateTime = LocalDateTime.parse("2019-11-19T15:16:17");
        LocalDate localDate2 = LocalDate.parse("2019-11-19");
        LocalTime localTime2 = LocalTime.parse("15:16:17");

        //如果要自定义输出的格式，或者要把一个非ISO 8601格式的字符串解析成LocalDateTime，可以使用新的DateTimeFormatter：
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(dtf.format(LocalDateTime.now()));
        // 用自定义格式解析:
        LocalDateTime dt2 = LocalDateTime.parse("2019-11-30 15:16:17", dtf);
        System.out.println(dt2);
    }
    public static void test2(){
        //LocalDateTime提供了对日期和时间进行加减的非常简单的链式调用：
        LocalDateTime dt = LocalDateTime.now();

        LocalDateTime localDateTime = dt.plusDays(3)  //加3天
                .minusHours(1)    //减1小时
                .minusMonths(1);   //减1个月
        //注意到调整月份时，会相应地调整日期，即把2019-10-31的月份调整为9时，日期也自动变为30

        localDateTime.withYear(2022)   //调整年
                .withMonth(12)      //调整月
                .withDayOfMonth(1)  //调整日
                .withHour(1)        //调整时
                .withMinute(23)     //调整分
                .withSecond(0);     //调整秒

        //本月第一天0点0刻
        LocalDateTime localDateTime1 = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        //使用isBefore和 isAfter 比较两个日期
        boolean before = localDateTime.isBefore(localDateTime1);
        boolean after = localDateTime.isAfter(localDateTime1);
    }

}
