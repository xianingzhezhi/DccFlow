package com.xpwi.springboot.service.track;

import com.xpwi.springboot.model.JsonResult;

public interface FormTrackingService {
    //草稿箱
    public JsonResult drafts(String user_id,String form_id,String state);
}
