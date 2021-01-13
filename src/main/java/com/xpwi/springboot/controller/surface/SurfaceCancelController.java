package com.xpwi.springboot.controller.surface;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PlatData;
import com.xpwi.springboot.service.surface.SurfaceCancelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: idea_server
 * @description: 圖面作廢
 * @author: PengFei_Ge
 * @create: 2020-12-16 14:43
 **/
@Api(value = "desc of class",tags = "圖面作廢")
@RestController
@CrossOrigin
@RequestMapping("surfaceCancel")
public class SurfaceCancelController {

    @Autowired
    private SurfaceCancelService surfaceCancelService;

    @ApiOperation(value = "作廢列表獲取", notes = "")
    @PostMapping("/cancelList")
    public JsonResult getCancelList(@ApiParam(value = "" , required=true ) @RequestParam String modelId,
                                    @ApiParam(value = "" , required=true ) @RequestParam String drawId,
                                    @ApiParam(value = "圖面類別" , required=true ) @RequestParam String drawType,
                                    @ApiParam(value = "其它圖面" , required=true ) @RequestParam String drawOther){

        JsonResult jsonResult =  surfaceCancelService.getCancelList(modelId,drawId,drawType,drawOther);
        return jsonResult;
    }

    @ApiOperation(value = "作廢選中CHECK", notes = "")
    @PostMapping("/selectCheck")
    public JsonResult selectCheck(@RequestBody List<PlatData> PlatDataList){

        JsonResult jsonResult =  surfaceCancelService.selectCheck(PlatDataList);
        return jsonResult;
    }

    @ApiOperation(value = "確定作廢", notes = "")
    @PostMapping("/cancelClick")
    public JsonResult cancelClick(
                                @ApiParam(value = "圖面類別" , required=true ) @RequestParam String drawType,
                                @ApiParam(value = "其它圖面" , required=true ) @RequestParam String drawOther,
                                @ApiParam(value = "用戶信息" , required=true ) @RequestParam String userId,
                                @RequestBody List<PlatData> PlatDataList){
        JsonResult jsonResult =  surfaceCancelService.selectCheck(PlatDataList);
        System.out.println(jsonResult.getStatus()+"+++++++++++++");
        if (jsonResult.getStatus().equals("200")){
            System.out.println("1111111111111");
            JsonResult jsonResult1 =  surfaceCancelService.cancelClick(drawType,drawOther,userId,PlatDataList);
            if (jsonResult1.getStatus().equals("200")){
                return jsonResult1;
            }else {
                return  new JsonResult("500","數據庫連接失敗，請聯繫IT");
            }
        }else {
            return jsonResult;
        }
    }

}
