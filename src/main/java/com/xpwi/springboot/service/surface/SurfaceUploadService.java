package com.xpwi.springboot.service.surface;

import com.xpwi.springboot.model.JsonResult;
import org.springframework.web.multipart.MultipartFile;

public interface SurfaceUploadService {
    public JsonResult  uploadFile(String file_id, MultipartFile file, String file_type, String form_id);
}
