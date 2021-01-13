package com.xpwi.springboot;

import com.xpwi.springboot.dao.PlatMaintainDao;
import com.xpwi.springboot.model.BaseSelect;
import com.xpwi.springboot.model.PlatMaintain;
import com.xpwi.springboot.service.*;
import com.xpwi.springboot.service.iso.IsoProcessService;
import com.xpwi.springboot.service.iso.IsoService;
import com.xpwi.springboot.utils.ArrayUtils;
import com.xpwi.springboot.utils.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class SpringbootApplicationTests {

    @Autowired
    RedisService redisService;

    @Autowired
    IsoService isoService;

    @Autowired
    UtilsService utilsService;

    @Autowired
    IsoProcessService isoProcessService;

    @Autowired
    BaseService baseService;

    //@Autowired
//    FlowBaseFileinfoDao flowBaseFileinfoDao;

    @Test
    public void test6() {
        String xx = "楊竹青";
        String s = StringUtils.convertToSimplifiedChinese(xx);
        System.out.println(s);
        String s1 = StringUtils.convertToTraditionalChinese(s);
        System.out.println(s1);
    }

    @Test
    public void test5() {
        Map[] arr = new LinkedHashMap[3];
        Map<String ,Object> map = new LinkedHashMap<>();
        map.put("1","2");
        arr[0] = map;
        ArrayUtils.rejectNullArr(arr);
        System.out.println(arr);
    }

    @Test
    public void test4() {
        String  q = "*151561*";
        String replace = q.replace("*", "%");
        System.out.println(replace);
    }
    @Test
    public void test2(){
//        FlowBaseFileinfo flowBaseFileinfo = this.flowBaseFileinfoDao.selectByPrimaryKey("KS-ISO-G20200B002");
//        System.out.println(flowBaseFileinfo);

//        FlowBaseFileinfo form_id=new FlowBaseFileinfo("KS-ISO-G20200B002","1","2","3","4","5","6","7",new Date());
//
//        this.flowBaseFileinfoDao.insert(form_id);
    }

    @Test
    public void test1(){
        System.out.println("\033[32;4m" + "我滴个颜什" + "\033[0m");
        List<BaseSelect> all = baseService.findAll("indicate", "P01", "mo_type");
        System.out.println(all);
    }


    @Test
    public void getTest16() {
        System.out.println("\033[30;4m" + "我滴个颜什" + "\033[0m");     //黑色
        System.out.println("\033[31;4m" + "我滴个颜什" + "\033[0m");     //红色
        System.out.println("\033[32;4m" + "我滴个颜什" + "\033[0m");     //绿色
        System.out.println("\033[33;4m" + "我滴个颜什" + "\033[0m");     //黄色
        System.out.println("\033[34;4m" + "我滴个颜什" + "\033[0m");     //蓝色
        System.out.println("\033[35;4m" + "我滴个颜什" + "\033[0m");     //紫色
        System.out.println("\033[36;4m" + "我滴个颜什" + "\033[0m");     //青色
        System.out.println("\033[37;4m" + "我滴个颜什" + "\033[0m");     //灰色
    }
    /**
     * 插入字符串
     */
    /*@Test
    public void setString() {
        try{
            redisService.set("Signed", "springboot redis test",300);
        }catch (Exception E){
            System.out.println(E);
            0
        }

    }*/

    /*@Test
    public void RedisUtilsTest() {
        try{
            List list = new ArrayList();
            list.add("1");
            list.add("2");
            list.add("3");
            RedisUtis redisUtis = new RedisUtis();
            redisUtis.redis_insert(list,"Signed","DCC",300);
        }catch (Exception E){
            System.out.println(E);
        }

    }*/

    /**
     * 获取字符串
     */
    /*@Test
    public void getString() {
        *//*RedisUtis re = new RedisUtis();
        String drillCheck = re.redis_check("2020", "DrillCheck");*//*
        String result = redisService.get("Signed");
        System.out.println(result);
    }*/

}
