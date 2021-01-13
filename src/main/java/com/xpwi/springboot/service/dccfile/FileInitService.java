package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: DccFlow
 * @description: 文件初始化
 * @author: Cool
 * @create: 2020-12-25 11:20
 **/
public interface FileInitService {
    public JsonResult FileInitialization(String form_id);

    public JsonResult FileInitialization2(String form_id);
}
