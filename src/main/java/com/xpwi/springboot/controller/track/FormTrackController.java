package com.xpwi.springboot.controller.track;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.track.FormTrackingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "desc of class",tags = "表单追踪")
@RestController
@CrossOrigin
@Transactional
@RequestMapping("formTracking")
public class FormTrackController {
    @Autowired
    private FormTrackingService formTrackingService;
    @PostMapping("checkType")
    public JsonResult formTracking(@ApiParam(value = "类型" , required=true ) @RequestParam String type,
                                   @ApiParam(value = "表单号" , required=false ) @RequestParam String form_id,
                                   @ApiParam(value = "用户id" , required=true ) @RequestParam String user_id,
                                   @ApiParam(value = "表单状态" , required=false ) @RequestParam String state){
        System.out.println("type======"+type);
        System.out.println("form_id======"+form_id);
        System.out.println("user_id======"+user_id);
        System.out.println("state======"+state);
        JsonResult result=null;
        if(type.equals("0")){//草稿箱
            System.out.println("======"+type);
            result=this.formTrackingService.drafts(user_id,form_id,state);
        }
        return result;
    }
}
