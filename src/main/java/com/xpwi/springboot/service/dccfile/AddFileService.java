package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.DccFileMaintain;
import com.xpwi.springboot.model.DccFileRowMessage;
import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PlatMessage;
import org.springframework.web.bind.annotation.RequestBody;

public interface AddFileService {
    JsonResult addFile(String user_id,DccFileMaintain dccFileMaintain);

    //保存行信息
    public JsonResult fileRowSave(@RequestBody DccFileRowMessage[] dccFileRowMessage_arr, String form_id, String countType);

}
