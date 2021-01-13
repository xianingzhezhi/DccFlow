package com.xpwi.springboot.service.externalfile;

import com.xpwi.springboot.model.*;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: DccFlow
 * @description: 外来文件批量保存
 * @author: Cool
 * @create: 2020-12-28 15:09
 **/
public interface ExternalFileAddService {

    //新增使用
    JsonResult saveMain2(String user_id,String countType,FlowMaintainIsofiles flowMaintainIsofiles);
    //单个初始化
    JsonResult  FileInitialization(String  form_id);

    //行信息初始化
    JsonResult  FileInitialization2(String  form_id);

    //批量新增使用
    JsonResult saveMain(String user_id,String countType,FlowMaintainIsofiles flowMaintainIsofiles);

    // 批量新增使用    保存行信息
    public JsonResult externalFileRowSave(@RequestBody FlowExternalMessage[] flowExternalMessage_arr, String form_id, String countType);

}
