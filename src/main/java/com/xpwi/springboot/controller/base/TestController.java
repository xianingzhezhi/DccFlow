package com.xpwi.springboot.controller.base;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.iso.FlowMaintainIsofilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TestController {

    @Autowired
    private FlowMaintainIsofilesService flowMaintainIsofilesService;

    //KS-ISO  O19040004   0,1,,2,3,
    @PostMapping("/test")
    public String  test() {
        JsonResult jsonResult= this.flowMaintainIsofilesService.selectByMaintainIsofiles2("O19040004","MB-0006","1");
        return  jsonResult.getData().get(0).toString();
    }
}
