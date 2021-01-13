package com.xpwi.springboot.controller.filerev;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.filerev.ChangeFileRevService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: DccFlow
 * @description: 文件Rev   驳回
 * @author: Cool
 * @create: 2020-12-28 11:31
 **/

@Api(value = "desc of class",tags = "文件Rev变更（驳回）")
@RestController
@CrossOrigin
@RequestMapping("fileRevChange")
public class ChangeFileRevController {

    @Autowired
    private ChangeFileRevService changeFileRevService;

    @ApiOperation(value = "页面初始化查询文件Rev信息", notes = "")
    @PostMapping("/fileInitialization1")
    public JsonResult fileInitialization1(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        return this.changeFileRevService.FileInitialization(form_id);
    }

    @ApiOperation(value = "页面初始化查询Rev行信息", notes = "")
    @PostMapping("/fileInitialization2")
    public JsonResult fileInitialization2(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        return this.changeFileRevService.FileInitialization2(form_id);
    }





    @ApiOperation(value = "文件遗失切结书  驳回", notes = "")
    @PostMapping("/fileRevReject")
    public JsonResult fileRevReject(@ApiParam(value = "表单号" , required=true ) @RequestParam String form_id,
                                     @ApiParam(value = "工號" , required=true ) @RequestParam String user_id,
                                     @ApiParam(value = "簽核意見" , required=true ) @RequestParam String rejectIdea) {
        JsonResult jsonResult=this.changeFileRevService.fileRevReject(form_id,user_id,rejectIdea);
        return jsonResult;
    }

}
