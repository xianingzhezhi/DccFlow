package com.xpwi.springboot.controller.base;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.EmailService;
import com.xpwi.springboot.service.UserService;
import com.xpwi.springboot.service.base.ChangeService;
import com.xpwi.springboot.service.iso.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: DccFlow
 * @description: 文件变更
 * @author: Cool
 * @create: 2021-01-04 14:43
 **/
@Api(value = "desc of class")
@RestController
@RequestMapping("change")
@CrossOrigin
public class ChangeController {

    @Autowired
    private ChangeService changeService;

    @Autowired
    private FlowHisDccFilesService flowHisDccFilesService;
    /**
     * 公共接口，查询文件   flow_his_dcc_files表
     *
     * @param fistId       文件前缀ISO/OSO
     * @param propertyName 数据库表属性
     * @param input        输入框内容，查询条件
     * @param symbol       符号:  例如<  <=  >  >=  =  like
     * @return
     */
    @ApiOperation(value = "flow_his_dcc_files表条件查询表单(变更，作废)", notes = "")
    @PostMapping("/isoFileList")
    public JsonResult selectHisDccFileList(String fistId, String propertyName, String input, String symbol) {
        return  this.flowHisDccFilesService.selectHisDccFileList(fistId,propertyName,input,symbol);
    }

    /**
     *
     * @param file_id
     * @param file_name
     * @param version
     * @param countType  2为作废   1为变更
     * @return
     */
    @ApiOperation(value = "作废/变更确定按钮", notes = "")
    @PostMapping("/isoChangeFile")
    public JsonResult isoChangeFile(String file_id, String file_name, String version,int countType) {
        return flowHisDccFilesService.selectHisDccFileListBy3(file_id,file_name,version,countType);
    }
    /**
     * 最终跳转到第一页，u=3
     * @param user_id
     * @param file_id
     * @param file_name
     * @param version
     * @param scrapReason
     * @param update_before
     * @param update_after
     * @param countType  变更1  ，作废2
     * @return
     */
    @ApiOperation(value = "作废/变更原因", notes = "")
    @PostMapping("/insertScrapReasonChange")
    @Transactional
    public JsonResult scrapReasonChange(String user_id, String file_id, String file_name, String version, String scrapReason, String update_before, String update_after,int countType) {
        return  changeService.scrapReasonChange(user_id,file_id,file_name,version,scrapReason,update_before,update_after,countType);
    }



}
