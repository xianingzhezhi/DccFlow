package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: idea_server
 * @description: 文件review 送核
 * @author: PengFei_Ge
 * @create: 2020-12-28 09:02
 **/
public interface FileReviewToSignedService {
    JsonResult readyToSigned(String formId, String userId, String signedIdea);

    JsonResult signedClick(String formId, String userId);
}
