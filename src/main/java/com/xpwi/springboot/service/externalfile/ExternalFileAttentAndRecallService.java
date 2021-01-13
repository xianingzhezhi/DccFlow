package com.xpwi.springboot.service.externalfile;

import com.xpwi.springboot.model.JsonResult;
import org.springframework.web.multipart.MultipartFile;

public interface ExternalFileAttentAndRecallService {
    JsonResult fileAttachment(String form_id, MultipartFile file, String file_type, String file_id);

    JsonResult fileRecall(String form_id, String user_id);
}
