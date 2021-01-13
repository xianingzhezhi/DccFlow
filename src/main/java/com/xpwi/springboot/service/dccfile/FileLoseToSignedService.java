package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.JsonResult;
import org.springframework.stereotype.Service;

/**
 * @program: idea_server
 * @description: 文件遺失切結書 送核
 * @author: PengFei_Ge
 * @create: 2020-12-27 13:55
 **/
@Service
public interface FileLoseToSignedService {

     JsonResult readyToSigned(String formId, String userId, String signedIdea);

     JsonResult signedClick(String formId, String userId);
}
