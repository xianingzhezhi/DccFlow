package com.xpwi.springboot.controller.file;

import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.dccfile.AddFileLoseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: DccFlow
 * @description: 文件切结书  保存
 * @author: Cool
 * @create: 2020-12-25 15:09
 **/

@Api(value = "desc of class",tags = "文件切结书保存及初始化")
@RestController
@CrossOrigin
@RequestMapping("fileQJ")
public class FileLoseAddController {
    @Autowired
    private AddFileLoseService addFileLoseService;

    @ApiOperation(value = "文件保存", notes = "")
    @PostMapping("/doFileQJSave")
    public JsonResult doFileQJSave(DccLoseMaintain dccLoseMaintain,
                                   @ApiParam(value = "登陆用户id", required = true) @RequestParam String user_id,
                                   @ApiParam(value = "操作标识", required = false) @RequestParam String countType,
                                   @ApiParam(value = "行信息", required = true) @RequestBody DccLoseRowmessage[] dccLoseRowmessage_arr){
        System.out.println(dccLoseMaintain);
        JsonResult jsonResult1=new JsonResult();
        JsonResult jsonResult=this.addFileLoseService.addFile(user_id,dccLoseMaintain);
        if (jsonResult.getStatus().equals("200")){
            String form_id=jsonResult.getData().get(0).toString();
            System.out.println(form_id);
            if (dccLoseRowmessage_arr.length > 0) {
                jsonResult1 = this.addFileLoseService.fileRowSave(dccLoseRowmessage_arr, form_id, countType);
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

    @ApiOperation(value = "页面初始化查询文件信息", notes = "")
    @PostMapping("/fileInitialization1")
    public JsonResult fileInitialization1(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        return this.addFileLoseService.FileInitialization(form_id);
    }

    @ApiOperation(value = "页面初始化查询行信息", notes = "")
    @PostMapping("/fileInitialization2")
    public JsonResult fileInitialization2(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        return this.addFileLoseService.FileInitialization2(form_id);
    }
}
