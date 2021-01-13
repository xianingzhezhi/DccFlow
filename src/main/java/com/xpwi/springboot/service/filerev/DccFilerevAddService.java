package com.xpwi.springboot.service.filerev;

import com.xpwi.springboot.model.DccFilerevMaintain;
import com.xpwi.springboot.model.DccFilerevRowmessage;
import com.xpwi.springboot.model.JsonResult;
import org.springframework.web.multipart.MultipartFile;

public interface DccFilerevAddService {
    public JsonResult saveFileRev(String user_id, DccFilerevMaintain obj, String type);
    public JsonResult saveRow(String formId,String type,DccFilerevRowmessage[] rowmessages);
    JsonResult getFiles(String fileType, String pro, String symbol, String input);

    JsonResult fileAttachment(String form_id, MultipartFile file, String file_type, String file_id);

    JsonResult fileRecall(String form_id, String user_id);
}
