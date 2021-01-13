package com.xpwi.springboot.test;

import com.xpwi.springboot.dao.FormTrackingDao;
import org.springframework.beans.factory.annotation.Autowired;
public class Test {
    @Autowired
    private FormTrackingDao formTrackingDao;
   // @org.junit.jupiter.api.Test
    public void selectAll(){
        String[] tableName={"dcc_plat_maintain"};
        for (String s : tableName) {
            System.out.println(s);
        }
    }
}
