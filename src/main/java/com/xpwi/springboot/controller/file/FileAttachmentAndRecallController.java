package com.xpwi.springboot.controller.file;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.dccfile.FileAttachmentAndRecallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(value = "desc of class",tags = "文件附件和抽单")
@CrossOrigin
@RequestMapping("FileAttachmentAndRecall")
public class FileAttachmentAndRecallController {
    @Autowired
    private FileAttachmentAndRecallService fileAttachmentAndRecallService;

    @ApiOperation(value = "附件上传", notes = "")
    @PostMapping(value="/fileAttachment")
    public JsonResult fileAttachment(@ApiParam(value = "表单号" , required=true ) @RequestParam String form_id,
                                     @ApiParam(value = "文件" , required=true ) @RequestParam("file") MultipartFile file,
                                     @ApiParam(value = "文件类型" , required=true ) @RequestParam String file_type,
                                   @ApiParam(value = "文件ID" , required=true ) @RequestParam String file_id){
        return this.fileAttachmentAndRecallService.fileAttachment(form_id,file,file_type,file_id);
    }
    @ApiOperation(value = "抽单", notes = "")
    @PostMapping(value="/fileRecall")
    public JsonResult fileRecall(@ApiParam(value = "表单号" , required=true ) @RequestParam String form_id,
                                   @ApiParam(value = "工號" , required=true ) @RequestParam String user_id){
        return this.fileAttachmentAndRecallService.fileRecall(form_id,user_id);
    }
}
