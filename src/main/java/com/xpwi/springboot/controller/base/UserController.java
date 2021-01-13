package com.xpwi.springboot.controller.base;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "desc of class")
@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
    //h_ttp://10.57.94.57:8092/url/Base/UserBase.ashx

    @Autowired
    private UserService userService;

    @ApiOperation(value = "三级菜单信息", notes = "")
    @PostMapping(value="/getMenuByUserid")
    public JsonResult getMenuByUserid(
            @ApiParam(value = "用户名" , required=true ) @RequestParam String user_id){
        Map<String, Object> userByUserid = userService.getUserByUserid(user_id);
        List list = new ArrayList();
        list.add(userByUserid);
        JsonResult jsonResult = new JsonResult("200","获取用户信息成功",list);
        return jsonResult;
    }

    @ApiOperation(value = "用户信息", notes = "")
    @PostMapping(value="/getUserInfoByUserid")
    public JsonResult getUserInfoByUserid(
            @ApiParam(value = "用户名" , required=true ) @RequestParam String user_id){
        Map<String, Object> userByUserid = userService.getUserByUserid(user_id);
        List list = new ArrayList();
        list.add(userByUserid);
        JsonResult jsonResult = new JsonResult("200","获取用户信息成功",list);
        return jsonResult;
    }
}
