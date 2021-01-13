package com.xpwi.springboot.controller.surface;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PlatMaintain;
import com.xpwi.springboot.service.surface.SurfaceToSignedService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @program: workspace_svn
 * @description: 圖面送核
 * @author: PengFei_Ge
 * @create: 2020-12-11 13:25
 **/
@Api(value = "desc of class",tags = "圖面送核")
@RestController
@CrossOrigin
@RequestMapping("surfaceToSigned")
public class SurfaceToSignedController {

    @Autowired
    SurfaceToSignedService surfaceToSignedService;

    @ApiOperation(value = "图面送核", notes = "")
    @PostMapping("/toSigned")
    public JsonResult toSigned(@ApiParam(value = "表單號" , required=true ) @RequestParam String formId,
                                     @ApiParam(value = "工號信息" , required=true ) @RequestParam String userId,
                                     @ApiParam(value = "新增/變更/作廢" , required=true ) @RequestParam String init_param1,
                                     @ApiParam(value = "簽核意見" , required=true ) @RequestParam String signedIdea) {
        switch (init_param1){
            case  "1" :
                init_param1 = "新增";
               break;
            case  "2" :
                init_param1 = "變更";
                break;
            case  "3" :
                init_param1 = "作廢";
                break;
        }
        JsonResult jsonResult = surfaceToSignedService.readyToSigned(formId,userId,init_param1,signedIdea);
        return  jsonResult;
    }

    @ApiOperation(value = "送核確定", notes = "")
    @PostMapping("/signedClick")
    public JsonResult signedClick(@ApiParam(value = "表單號" , required=true ) @RequestParam String formId,
                                     @ApiParam(value = "工號信息" , required=true ) @RequestParam String userId) {
        JsonResult jsonResult = surfaceToSignedService.signedClick(formId,userId);
        return  jsonResult;
    }
}
