package com.xpwi.springboot.service.surface;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: workspace_svn
 * @description: 圖面簽核送核
 * @author: PengFei_Ge
 * @create: 2020-12-11 14:15
 **/
public interface SurfaceToSignedService {

    JsonResult readyToSigned(String formId, String userId,String init_param1,String signed_idea);

    JsonResult signedClick(String form_id,String user_id);
}
