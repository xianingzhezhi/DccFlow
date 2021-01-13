package com.xpwi.springboot.controller.surface;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.BaseService;
import com.xpwi.springboot.service.surface.SurfaceRecallDocService;
import com.xpwi.springboot.service.surface.SurfaceUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@Api(value = "desc of class",tags = "圖面附件和抽單")
@RestController
@CrossOrigin
@Transactional
@RequestMapping("surfaceUploadAndrecallDoc")
public class SurfaceUploadAndRecallController {
    @Autowired
    private SurfaceUploadService surfaceUploadService;
    @Autowired
    private SurfaceRecallDocService SurfaceRecallDocService;
    @ApiOperation(value = "图面上传附件", notes = "")
    @PostMapping(value="/uploadFile")
    public JsonResult uploadFile(@ApiParam(value = "文件" , required=true ) @RequestParam("file") MultipartFile file,
                                 @ApiParam(value = "文件编号" , required=false ) @RequestParam String file_id,
                                 @ApiParam(value = "系统信息" , required=false ) @RequestParam String file_type,
                                 @ApiParam(value = "文件系统编号",required = false) @RequestParam String form_id){
        JsonResult rs= this.surfaceUploadService.uploadFile(file_id,file,file_type,form_id);
        return rs;
    }
    @ApiOperation(value = "图面抽单", notes = "")
    @PostMapping(value="/recallDoc")
    public JsonResult RecallDoc( @ApiParam(value = "用户id",required = false) @RequestParam String user_id,
                                 @ApiParam(value = "表单号",required = false) @RequestParam String form_id){
        return this.SurfaceRecallDocService.recallDoc(user_id,form_id);
    }

}
