package com.xpwi.springboot.controller.track;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.track.SignedStatusService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @program: idea_server
 * @description: 獲取待簽核列表
 * @author: PengFei_Ge
 * @create: 2020-12-23 16:56
 **/
@Api(value = "desc of class",tags = "獲取待簽核列表")
@RestController
@CrossOrigin
@Transactional
@RequestMapping("signedStatus")
public class SignedStatusController {

    @Autowired
    private SignedStatusService signedStatusService;

    @ApiOperation(value = "已送核", notes = "")
    @PostMapping("getWriterList")
    public JsonResult getWriterList(@ApiParam(value = "用戶工號信息" , required=true ) @RequestParam String userId){
        JsonResult jsonResult = signedStatusService.getWriterList(userId);
        return jsonResult;
    }

    @ApiOperation(value = "待簽核", notes = "")
    @PostMapping("getSignedList")
    public JsonResult getSignedList(@ApiParam(value = "用戶工號信息" , required=true ) @RequestParam String userId){
        JsonResult jsonResult = signedStatusService.getSignedList(userId);
        return jsonResult;
    }

    @ApiOperation(value = "已結案", notes = "")
    @PostMapping("getClosedList")
    public JsonResult getClosedList(@ApiParam(value = "用戶工號信息" , required=true ) @RequestParam String userId){
        JsonResult jsonResult = signedStatusService.getClosedList(userId);
        return jsonResult;
    }
}
