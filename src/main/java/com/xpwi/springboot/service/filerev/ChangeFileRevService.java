package com.xpwi.springboot.service.filerev;

import com.xpwi.springboot.model.JsonResult;

public interface ChangeFileRevService {
    JsonResult fileRevReject(String form_id, String user_id, String rejectIdea);

    //初始化
    public JsonResult FileInitialization(String form_id);

    public JsonResult FileInitialization2(String form_id);
}
