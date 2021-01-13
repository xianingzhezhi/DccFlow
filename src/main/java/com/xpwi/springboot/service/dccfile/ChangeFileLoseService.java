package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.JsonResult;

public interface ChangeFileLoseService {
    JsonResult fileLoseReject(String form_id, String user_id, String rejectIdea);
}
