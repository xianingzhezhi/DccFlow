package com.xpwi.springboot.controller.file;

import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.BaseService;
import com.xpwi.springboot.service.dccfile.AddFileService;
import com.xpwi.springboot.service.dccfile.FileInitService;
import com.xpwi.springboot.service.iso.DccFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: DccFlow
 * @description: 文件保存及初始化
 * @author: Cool
 * @create: 2020-12-24 10:34
 **/

@Api(value = "desc of class",tags = "文件保存及初始化")
@RestController
@CrossOrigin
@RequestMapping("file")
public class FileAddController {
    @Autowired
    private AddFileService addFileService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private FileInitService fileInitService;


    @ApiOperation(value = "文件保存", notes = "")
    @PostMapping("/doFileSave")
    public JsonResult doFileSave(DccFileMaintain dccFileMaintain,
                                 @ApiParam(value = "登陆用户id", required = true) @RequestParam String user_id,
                                 @ApiParam(value = "操作标识", required = false) @RequestParam String countType,
                                 @ApiParam(value = "行信息", required = true) @RequestBody DccFileRowMessage[] dccFileRowMessages_arr){
        System.out.println(dccFileMaintain);
        JsonResult jsonResult1=new JsonResult();
        JsonResult jsonResult=this.addFileService.addFile(user_id,dccFileMaintain);
        if (jsonResult.getStatus().equals("200")){
            String form_id=jsonResult.getData().get(0).toString();
            System.out.println(form_id);
            if (dccFileRowMessages_arr.length > 0) {
                jsonResult1 = this.addFileService.fileRowSave(dccFileRowMessages_arr, form_id, countType);
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
        return this.fileInitService.FileInitialization(form_id);
    }

    @ApiOperation(value = "页面初始化查询行信息", notes = "")
    @PostMapping("/fileInitialization2")
    public JsonResult fileInitialization2(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
       return this.fileInitService.FileInitialization2(form_id);
    }

}
