package com.xpwi.springboot.controller.base;

import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.BaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Api(value = "desc of class")
@RestController
@RequestMapping("Base")
@CrossOrigin
public class BaseController {

    @Autowired
    private BaseService baseService;

    @ApiOperation(value = "单个  获取下拉框信息公共方法", notes = "")
    @PostMapping(value="/getBaseSelect")
    public JsonResult getUserByUserid(
            @ApiParam(value = "签核系统类别" , required=true ) @RequestParam String type,
            @ApiParam(value = "签核页面信息" , required=true ) @RequestParam String page_num,
            @ApiParam(value = "下拉框ID" , required=true ) @RequestParam String select_num){
        List<BaseSelect> list = baseService.findAll(type,page_num,select_num);
        JsonResult jsonResult = new JsonResult("200","获取用户信息成功",list);
        return jsonResult;
    }

    @ApiOperation(value = "多个  获取下拉框信息公共方法", notes = "")
    @PostMapping(value="/getSelectMore")
    public JsonResult getSelectMore(
            @ApiParam(value = "签核系统类别" , required=true ) @RequestParam String type,
            @ApiParam(value = "签核页面信息" , required=true ) @RequestParam String page_num){
        JsonResult alls = baseService.findAlls(type, page_num);
        return alls;
    }

    @ApiOperation(value = "文件上传", notes = "")
    @PostMapping(value="/upfile")
    public JsonResult upfile(@ApiParam(value = "文件" , required=true ) @RequestParam("file") MultipartFile file,
                             @ApiParam(value = "文件编号" , required=false ) @RequestParam String file_id,
                             @ApiParam(value = "系统信息" , required=false ) @RequestParam String file_type,
                             @ApiParam(value = "文件系统编号",required = false) @RequestParam String form_id){

        List list = new ArrayList();
        JsonResult upfile = baseService.upfile(file_id, file, file_type,form_id);
        return upfile;
    }

    @ApiOperation(value = "文件下载", notes = "")
    @GetMapping(value="/downfile")
    public void downfile(HttpServletResponse response,
                         @ApiParam(value = "文件编号" , required=true ) @RequestParam String file_id,
                         @ApiParam(value = "系统信息" , required=true ) @RequestParam String file_type){
        List list = new ArrayList();
        List<BaseFile> downfile = baseService.downfile(file_id,file_type);
        byte[] file_byte = downfile.get(0).getFile_byte();
        String file_name = downfile.get(0).getFile_name();
        response.setContentType("application/octet-stream;charset=GBK");
        BufferedOutputStream output = null;
        try {
            output = new BufferedOutputStream(response.getOutputStream());
            String fileNameDown = new String(file_name.getBytes(), "GBK");
            //fileNameDown上面得到的文件名
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    fileNameDown);
            //byte[] result = null;
            output.write(file_byte);
            output.flush();
            response.flushBuffer();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @ApiOperation(value = "通过form_id查询附件信息", notes = "")
    @PostMapping(value="/fileInfoByFormId")
    public JsonResult getFileInfo(@ApiParam(value = "文件系统编号" , required=true ) @RequestParam String form_id){

        JsonResult fileInfo = baseService.getFileInfo(form_id);
        return fileInfo;
    }

}
