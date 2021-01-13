package com.xpwi.springboot.service.surface;


import com.xpwi.springboot.model.JsonResult;
import springfox.documentation.spring.web.json.Json;

public interface PlatProcessService {


    JsonResult getPlatProcessAll(String form_id, String create_user_id,String department_id);

    JsonResult serchTwoProcess(String twoProcessInput);

    JsonResult insertSetProcess(String form_id,String one, String two, String three, String four);

    JsonResult addSerProcess(String form_id);
}
