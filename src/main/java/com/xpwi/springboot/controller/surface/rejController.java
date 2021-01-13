package com.xpwi.springboot.controller.surface;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.surface.SurfaceRejService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("surfaceRej")
@CrossOrigin
public class rejController {

    @Autowired
    SurfaceRejService surfaceRej;

    @ApiOperation(value = "图面驳回",notes = "")
    @PostMapping("/setSurfaceRej")
    public JsonResult setProcess(@ApiParam(value = "文件系统编号",required = true) @RequestParam String form_id,
                                 @ApiParam(value = "驳回理由",required = true) @RequestParam String rejReason,
                                 @ApiParam(value = "登录人",required = true) @RequestParam String loginUser
    ){
        JsonResult platProcessAll = surfaceRej.setSurfaceRej(form_id, rejReason, loginUser,1);
        return platProcessAll;
    }

}
