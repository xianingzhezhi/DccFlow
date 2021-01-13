package com.xpwi.springboot.service.surface;

import com.xpwi.springboot.model.JsonResult;

public interface SurfaceRejService {
    JsonResult setSurfaceRej(String form_id, String rejReason, String loginUser,int cancelType);

}
