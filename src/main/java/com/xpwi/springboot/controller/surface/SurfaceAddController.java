package com.xpwi.springboot.controller.surface;

import com.xpwi.springboot.model.JsonResult;

import com.xpwi.springboot.model.PlatMaintain;

import com.xpwi.springboot.model.User;
import com.xpwi.springboot.service.BaseService;

import com.xpwi.springboot.model.PlatMessage;
import com.xpwi.springboot.service.UserService;
import com.xpwi.springboot.service.surface.SurfaceService;

import com.xpwi.springboot.service.utils.GetFormIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(value = "desc of class")
@RestController
@CrossOrigin
@Transactional
@RequestMapping("surfaceAdd")
public class SurfaceAddController {


    @Autowired
    private SurfaceService surfaceService;

    @Autowired
    private GetFormIdService getFormIdService;

    @Autowired
    private UserService userService;

    @Autowired
    private  BaseService baseService;

    @ApiOperation(value = "flow_base_user表条件查询人员信息", notes = "")
    @PostMapping("/userList")
    public JsonResult findUser(String propertyName, String input, String symbol) {
        return this.userService.findUserAndRank(propertyName, input, symbol);
    }

    @ApiOperation(value = "图面新增保存", notes = "")
    @PostMapping("/surfaceFormList")
    @Transactional
    public JsonResult surfaceAddMain(PlatMaintain platMaintain,
                                     @ApiParam(value = "登陆用户id", required = true) @RequestParam String user_id,
                                     @ApiParam(value = "操作标识", required = true) @RequestParam String countType,
                                     @ApiParam(value = "行信息", required = true) @RequestBody PlatMessage[] platMessages_arr
    ) {

        JsonResult jsonResult = this.surfaceService.surfaceAddMain(user_id, platMaintain, countType);
        JsonResult jsonResult1 = new JsonResult();
        if (jsonResult.getStatus().equals("200")) {
            String form_idd = jsonResult.getData().get(0).toString();
            if (platMessages_arr.length > 0) {
                jsonResult1 = this.surfaceService.surfaceRowSave(platMessages_arr, form_idd, countType);
            } else {
                return new JsonResult("200", "表單信息保存成功,可以繼續保存行信息！", jsonResult.getData());
            }
        } else {
            return new JsonResult("500", "保存失败！");
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

    @ApiOperation(value = "页面初始化查询登陆user", notes = "")
    @PostMapping("/userLogin")
    public JsonResult findLoginUser(@ApiParam(value = "登陆用户id", required = true) @RequestParam String user_id) {
        try {
            User user = this.userService.getUsersByUserid(user_id);
            List list = new ArrayList();
            if (user != null) {
                list.add(user);
                return new JsonResult(list);
            } else {
                return new JsonResult("400", "沒有該用戶信息");
            }

        } catch (Exception e) {
            return new JsonResult("500", "資料未維護");
        }
    }

    @ApiOperation(value = "页面初始化查询图面信息", notes = "")
    @PostMapping("/surfaceInitialization1")
    public JsonResult surfaceInitialization1(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        try{
            return this.surfaceService.surfaceInitialization(form_id);
        }catch(Exception e){
            return new JsonResult("500","数据初始化失败");
        }
    }
    @ApiOperation(value = "页面初始化查询行信息", notes = "")
    @PostMapping("/surfaceInitialization2")
    public JsonResult surfaceInitialization2(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        try{
            return this.surfaceService.surfaceInitialization2(form_id);
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","数据初始化失败");
        }
    }
    @ApiOperation(value = "页面初始化单据履历", notes = "")
    @PostMapping("/initFormDocument")
    public JsonResult formDocument(@ApiParam(value = "表单id", required = true) @RequestParam String form_id) {
        System.out.println(form_id+"+++++++++++++++");
        try{
            return this.baseService.initFormDocument(form_id);
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","单据履历初始化失败");
        }
    }
}