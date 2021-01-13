package com.xpwi.springboot.controller.file;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.dccfile.DccFileContractService;
import com.xpwi.springboot.service.dccfile.DccFileLoseContractService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
文件遗失会签
 */
@RestController
@Api(value = "desc of class",tags = "文件遗失会签")
@CrossOrigin
@RequestMapping("DccFileLoseComtract")
public class DccFileLoseComtract {

    @Autowired
    private DccFileLoseContractService dccFileLoseContractService;

    @ApiOperation(value = "根据Lose表单号获取流程信息", notes = "")
    @PostMapping(value="/getProcess")
    public JsonResult getProcess(@ApiParam(value = "type (eg:dcc)" , required=true ) @RequestParam String type,
                                 @ApiParam(value = "form_id" , required=true ) @RequestParam String form_id,
                                 @ApiParam(value = "user_id" , required=true ) @RequestParam String user_id,
                                 @ApiParam(value = "department_id",required=true)@RequestParam String department_id){
        System.out.println("接收到的参数-------------------------------"+type+form_id+user_id+department_id);

        JsonResult process =  dccFileLoseContractService.getProcess(type,form_id,user_id,department_id);
        return process;
    }

    @ApiOperation(value = "保存Lose签核流程",notes = "")
    @PostMapping("/insertProcess")
    public JsonResult insertProcess(@ApiParam(value = "form_id",required = true) @RequestParam String form_id,
                                    @ApiParam(value = "one",required = true) @RequestParam String one,
                                    @ApiParam(value = "two",required = true) @RequestParam String two,
                                    @ApiParam(value = "three",required = true) @RequestParam String three,
                                    @ApiParam(value = "four",required = true) @RequestParam String four,
                                    @ApiParam(value = "five",required = true) @RequestParam String five) {
        JsonResult jsonResult = dccFileLoseContractService.insertSetDccContract(form_id,one,two,three,four,five);
        return jsonResult;
    }
    @ApiOperation(value = "文件遗失加签",notes = "")
    @PostMapping("/fileLoseProcess")
    public JsonResult fileLoseProcess(@ApiParam(value = "form_id",required = true) @RequestParam String form_id
                                  ) {
        JsonResult jsonResult = dccFileLoseContractService.addFileLose(form_id);
        return jsonResult;
    }

}
