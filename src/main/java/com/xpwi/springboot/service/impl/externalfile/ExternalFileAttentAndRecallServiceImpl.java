package com.xpwi.springboot.service.impl.externalfile;

import com.xpwi.springboot.model.DccFilerevMaintain;
import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.service.externalfile.ExternalFileAttentAndRecallService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class ExternalFileAttentAndRecallServiceImpl implements ExternalFileAttentAndRecallService {
    public JsonResult fileAttachment(String form_id, MultipartFile file, String file_type, String file_id) {
        try {
           return null;
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","程序异常！上传失败");
        }
    }

    public JsonResult fileRecall(String form_id, String user_id) {
        return null;
    }
}
