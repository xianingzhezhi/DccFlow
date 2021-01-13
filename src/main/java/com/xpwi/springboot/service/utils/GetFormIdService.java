package com.xpwi.springboot.service.utils;

import com.xpwi.springboot.model.JsonResult;

public interface GetFormIdService {

    //生成图面文件系统id,生成文件系统id
     JsonResult getSurfaceFormId(String file_table, String file_sys);

}
