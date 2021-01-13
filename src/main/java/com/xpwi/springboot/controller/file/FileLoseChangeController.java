package com.xpwi.springboot.controller.file;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.dccfile.ChangeFileLoseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * @program: DccFolw
 * @description: 文件变更（驳回）
 * @author: Cool
 * @create: 2020-12-27 10:35
 **/
@Api(value = "desc of class",tags = "文件变更（驳回）")
@RestController
@CrossOrigin
@RequestMapping("fileChange")
public class FileLoseChangeController {

    @Autowired
    private ChangeFileLoseService changeFileLoseService;

    @ApiOperation(value = "文件遗失切结书  驳回", notes = "")
    @PostMapping("/fileLoseReject")
    public JsonResult fileLoseReject(@ApiParam(value = "表单号" , required=true ) @RequestParam String form_id,
                                     @ApiParam(value = "工號" , required=true ) @RequestParam String user_id,
                                     @ApiParam(value = "簽核意見" , required=true ) @RequestParam String rejectIdea) {
        JsonResult jsonResult=this.changeFileLoseService.fileLoseReject(form_id,user_id,rejectIdea);
        return jsonResult;
    }


}
