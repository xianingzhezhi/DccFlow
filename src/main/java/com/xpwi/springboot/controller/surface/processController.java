package com.xpwi.springboot.controller.surface;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.surface.PlatProcessService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("platProcessSign")
@CrossOrigin
public class processController {

    @Autowired
    PlatProcessService platProcessService;

    @ApiOperation(value = "图面签核流程设定",notes = "")
    @PostMapping("/setProcess")
    public JsonResult setProcess(@ApiParam(value = "文件系统编号",required = true) @RequestParam String form_id,
                                 @ApiParam(value = "登录人员工号",required = true) @RequestParam String create_user_id,
                                 @ApiParam(value = "申请人部门",required = true) @RequestParam String department_id
                                ){
        JsonResult platProcessAll = platProcessService.getPlatProcessAll(form_id, create_user_id, department_id);
        return platProcessAll;
    }

    @ApiOperation(value = "签核第二关查询",notes = "")
    @PostMapping("/searchTwoProcess")
    public JsonResult searchTwoProcess(@ApiParam(value = "twoProcessInput",required = true) @RequestParam String twoProcessInput){
        JsonResult jsonResult = platProcessService.serchTwoProcess(twoProcessInput);
       return jsonResult;
    }

    @ApiOperation(value = "保存签核流程设定数据",notes = "")
    @PostMapping("/insertProcess")
    public JsonResult insertProcess(@ApiParam(value = "文件系统编号",required = true) @RequestParam String form_id,
                                    @ApiParam(value = "第一关",required = true) @RequestParam String one,
                                    @ApiParam(value = "第二关",required = true) @RequestParam String two,
                                    @ApiParam(value = "第三关",required = true) @RequestParam String three,
                                    @ApiParam(value = "第四关",required = true) @RequestParam String four){
        JsonResult jsonResult = platProcessService.insertSetProcess(form_id, one, two, three, four);
        return jsonResult;
    }

    @ApiOperation(value = "加签初始化操作",notes = "")
    @PostMapping("/addProcess")
    public JsonResult addSetProcess(@ApiParam(value = "文件系统编号",required = true) @RequestParam String form_id){
        JsonResult jsonResult = platProcessService.addSerProcess(form_id);
        return  jsonResult;
    }
}
