package com.xpwi.springboot.controller.filerev;

import com.xpwi.springboot.model.DccFilerevMaintain;
import com.xpwi.springboot.model.DccFilerevRowmessage;
import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.filerev.DccFilerevAddService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(value = "desc of class",tags = "文件ReviewForm保存,附件,抽單")
@CrossOrigin
@RequestMapping("DccFilerevAdd")
public class DccFilerevAddController {
    @Autowired
    private DccFilerevAddService dccFilerevAddService;
    @ApiOperation(value = "保存", notes = "")
    @PostMapping("save")
    public JsonResult addFileRev(DccFilerevMaintain obj,
                                  @ApiParam(value = "登陆用户id", required = true) @RequestParam String user_id,
                                 @ApiParam(value = "操作标识符", required = false) @RequestParam String type,
                                 @ApiParam(value = "行信息", required = false) @RequestBody DccFilerevRowmessage[] rowmessages){
        JsonResult jsonResult = this.dccFilerevAddService.saveFileRev(user_id, obj, type);
        JsonResult result=null;
        if(jsonResult.getStatus().equalsIgnoreCase("200")){
            String formId=jsonResult.getData().get(0).toString();
            if(rowmessages.length>0){
                result=  this.dccFilerevAddService.saveRow(formId,type,rowmessages);
            }else{
                result= new JsonResult("200","表單信息保存成功,可以繼續保存行信息！",jsonResult.getData());
            }
        }
        return result;
    }
    @ApiOperation(value = "获取文件列表", notes = "")
    @PostMapping("getFiles")
    public  JsonResult selectFiles(@ApiParam(value = "文件标识符", required = false) @RequestParam String fileType,
                                   @ApiParam(value = "属性名称", required = true) @RequestParam String pro,
                                   @ApiParam(value = "比较运算符", required = true) @RequestParam String symbol,
                                   @ApiParam(value = "输入框内容", required = true) @RequestParam  String input){
        return this.dccFilerevAddService.getFiles(fileType,pro,symbol,input);
    }
    @ApiOperation(value = "附件上传", notes = "")
    @PostMapping(value="/attachment")
    public JsonResult fileAttachment(@ApiParam(value = "表单号" , required=true ) @RequestParam String form_id,
                                     @ApiParam(value = "文件" , required=true ) @RequestParam("file") MultipartFile file,
                                     @ApiParam(value = "文件类型" , required=true ) @RequestParam String file_type,
                                     @ApiParam(value = "文件ID" , required=true ) @RequestParam String file_id){
        return this.dccFilerevAddService.fileAttachment(form_id,file,file_type,file_id);
    }
    @ApiOperation(value = "抽单", notes = "")
    @PostMapping(value="/fileRecall")
    public JsonResult fileRecall(@ApiParam(value = "表单号" , required=true ) @RequestParam String form_id,
                                 @ApiParam(value = "工號" , required=true ) @RequestParam String user_id){
        return this.dccFilerevAddService.fileRecall(form_id,user_id);
    }
}
