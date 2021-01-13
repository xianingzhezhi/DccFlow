package com.xpwi.springboot.service.surface;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PlatMaintain;
import com.xpwi.springboot.model.PlatMessage;
import org.springframework.web.bind.annotation.RequestBody;

public interface SurfaceService {

    JsonResult initSurfacePage(String form_id, String user_id, String init_status ,String init_param1 , String login_userId);

    //保存主表信息
    public JsonResult  surfaceAddMain(String user_id, PlatMaintain platMaintain,String countType);

    //保存行信息
    public JsonResult surfaceRowSave(@RequestBody PlatMessage[] platMessages_arr,String form_id,String countType);

    //变更保存行信息
    public JsonResult surfaceChangeRowSave(@RequestBody PlatMessage[] platMessages_arr,String form_id);

    //变更查询表单信息
    public JsonResult surfaceInitialization(String form_id);

    //变更查询行信息
    public JsonResult surfaceInitialization2(String form_id);
}
