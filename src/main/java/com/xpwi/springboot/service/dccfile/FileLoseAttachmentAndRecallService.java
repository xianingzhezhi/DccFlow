package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.JsonResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileLoseAttachmentAndRecallService {
    public JsonResult fileAttachment(String form_id, MultipartFile file, String file_type, String file_id);
    public JsonResult fileRecall(String form_id, String user_id);
}
