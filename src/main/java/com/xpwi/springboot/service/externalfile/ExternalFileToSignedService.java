package com.xpwi.springboot.service.externalfile;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: idea_server
 * @description: 外來文件送核接口
 * @author: PengFei_Ge
 * @create: 2020-12-28 13:22
 **/
public interface ExternalFileToSignedService {
    JsonResult readyToSigned(String formId, String userId, String signedIdea,String version);

    JsonResult signedClick(String formId, String userId);
}
