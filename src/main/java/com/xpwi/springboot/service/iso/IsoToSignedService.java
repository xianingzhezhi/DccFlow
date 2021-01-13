package com.xpwi.springboot.service.iso;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: workspace_svn
 * @description: 送核接口
 * @author: PengFei_Ge
 * @create: 2020-12-01 10:01
 **/
public interface IsoToSignedService {

    JsonResult sendToSigned(String form_id,String user_id,String signed_idea ,String update_reason,String txt_version, String init_param1);


    JsonResult signedClick(String form_id,String user_id);


}
