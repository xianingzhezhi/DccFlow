package com.xpwi.springboot.service.externalfile;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: idea_server
 * @description: 外來文件分批上傳
 * @author: PengFei_Ge
 * @create: 2020-12-29 11:43
 **/
public interface ExternalFileBatchToSignedService {

    JsonResult readyToSigned(String formId, String userId, String signedIdea, String version);

    JsonResult signedClick(String formId, String userId);
}
