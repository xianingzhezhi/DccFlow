package com.xpwi.springboot;

import org.apache.catalina.LifecycleState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test01 {
    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");//设置日期格式
        System.out.println(df.format(new Date()).toString());// new Date()为获取当前系统时间
        System.out.println("\033[30;4m" + "我滴个颜什" + "\033[0m");//黑色
        System.out.println("\033[31;4m" + "我滴个颜什" + "\033[0m");//红色
        System.out.println("\033[32;4m" + "我滴个颜什" + "\033[0m");//绿色
        System.out.println("\033[33;4m" + "我滴个颜什" + "\033[0m");//黄色
        System.out.println("\033[34;4m" + "我滴个颜什" + "\033[0m");//蓝色
        System.out.println("\033[35;4m" + "我滴个颜什" + "\033[0m");//紫色
        System.out.println("\033[36;4m" + "我滴个颜什" + "\033[0m");//青色
        System.out.println("\033[37;4m" + "我滴个颜什" + "\033[0m");//灰色
    }
}
