package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.dao.DccLoseRowmessageDao;
import com.xpwi.springboot.model.*;
import org.springframework.web.bind.annotation.RequestBody;

public interface AddFileLoseService {
    JsonResult addFile(String user_id, DccLoseMaintain dccLoseMaintain);

    //保存行信息
    public JsonResult fileRowSave(@RequestBody DccLoseRowmessage[] dccLoseRowmessage_arr, String form_id, String countType);

    public JsonResult FileInitialization(String form_id);

    public JsonResult FileInitialization2(String form_id);
}
