package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.JsonResult;
import springfox.documentation.spring.web.json.Json;

//文件遗失会签
public interface DccFileLoseContractService {
    //获取流程信息，初始化流程
    public JsonResult getProcess( String type, String form_id,String user_id,String department_id);
    //存储流程信息
    public JsonResult insertSetDccContract(String form_id, String one, String two, String three, String four, String five);

    //加签
    public JsonResult addFileLose(String form_id);

}
