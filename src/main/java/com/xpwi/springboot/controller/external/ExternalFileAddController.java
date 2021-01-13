package com.xpwi.springboot.controller.external;

import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.externalfile.ExternalFileAddService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: DccFlow
 * @description: 外来文件 保存以及初始化
 * @author: Cool
 * @create: 2020-12-28 14:14
 **/
@Api(value = "desc of class",tags = "外来文件批量新增及初始化")
@RestController
@CrossOrigin
@RequestMapping("external")
public class ExternalFileAddController {
    @Autowired
    private ExternalFileAddService externalFileAddService;

    @ApiOperation(value = "外来文件单个保存", notes = "")
    @PostMapping("/doFileExternalSaveDone")
    public JsonResult doFileExternalSaveDone(FlowMaintainIsofiles flowMaintainIsofiles,
                                         @ApiParam(value = "登陆用户id", required = true) @RequestParam String user_id,
                                         @ApiParam(value = "操作标识", required = false) @RequestParam String countType){
        return  this.externalFileAddService.saveMain2(user_id,countType,flowMaintainIsofiles);
    }

    @ApiOperation(value = "页面初始化查询外来文件信息", notes = "")
    @PostMapping("/fileInitialization1")
    public JsonResult fileInitialization1(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        return this.externalFileAddService.FileInitialization(form_id);
    }

    @ApiOperation(value = "页面初始化查询外来文件信息", notes = "")
    @PostMapping("/fileInitialization2")
    public JsonResult fileInitialization2(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        return this.externalFileAddService.FileInitialization2(form_id);
    }

        @ApiOperation(value = "外来文件批量保存", notes = "")
    @PostMapping("/doFileExternalSave")
    public JsonResult doFileExternalSave(FlowMaintainIsofiles flowMaintainIsofiles,
                                         @ApiParam(value = "登陆用户id", required = true) @RequestParam String user_id,
                                         @ApiParam(value = "操作标识", required = false) @RequestParam String countType,
                                         @ApiParam(value = "行信息", required = true) @RequestBody FlowExternalMessage[] flowExternalMessage_arr){
        System.out.println(flowMaintainIsofiles+"222222222222222222222222222222");
        JsonResult jsonResult1=new JsonResult();
        JsonResult jsonResult=this.externalFileAddService.saveMain(user_id,countType,flowMaintainIsofiles);
        if (jsonResult.getStatus().equals("200")){
            String form_id=jsonResult.getData().get(0).toString();
            System.out.println(form_id);
            if (flowExternalMessage_arr.length > 0) {
                jsonResult1 = this.externalFileAddService.externalFileRowSave(flowExternalMessage_arr, form_id, countType);
            } else {
                return new JsonResult("200", "表單信息保存成功,可以繼續保存行信息！", jsonResult.getData());
            }
        }else {
            return new JsonResult("500","保存失败");
        }
        if (jsonResult1.getStatus().equals("200")) {
            if (jsonResult1.getMessage().equals("保存成功！")){
                return new JsonResult("200", "保存成功！", jsonResult.getData());
            }else {
                return jsonResult1;
            }

        } else {
            return jsonResult1;
        }
    }


}
