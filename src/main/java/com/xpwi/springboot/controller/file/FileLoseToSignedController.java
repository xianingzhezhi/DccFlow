package com.xpwi.springboot.controller.file;

import com.xpwi.springboot.model.DccLoseMaintain;
import com.xpwi.springboot.model.DccLoseRowmessage;
import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.dccfile.FileLoseToSignedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: idea_server
 * @description: 文件模塊   文件遺失切結書
 * @author: PengFei_Ge
 * @create: 2020-12-27 13:49
 **/
@RestController
@Api(value = "desc of class",tags = "文件遺失切結書送核")
@CrossOrigin
@RequestMapping("FileLoseToSigned")
public class FileLoseToSignedController {

    @Autowired
    private FileLoseToSignedService fileLoseToSignedService;

    @ApiOperation(value = "準備送核", notes = "")
    @PostMapping(value="/readyToSigned")
    public JsonResult readyToSigned(@ApiParam(value = "表单号" , required=true ) @RequestParam String formId,
                                    @ApiParam(value = "工號" , required=true ) @RequestParam String userId,
                                    @ApiParam(value = "簽核意見" , required=true ) @RequestParam String signedIdea){
        JsonResult jsonResult = fileLoseToSignedService.readyToSigned(formId,userId,signedIdea);
        return jsonResult;
    }

    @ApiOperation(value = "點擊確定送簽", notes = "")
    @PostMapping(value="/signedClick")
    public JsonResult signedClick(@ApiParam(value = "表单号" , required=true ) @RequestParam String formId,
                                  @ApiParam(value = "工號" , required=true ) @RequestParam String userId){
        JsonResult jsonResult = fileLoseToSignedService.signedClick(formId,userId);
        return jsonResult;
    }
}
