package com.xpwi.springboot.service.dccfile;

import com.xpwi.springboot.model.JsonResult;

//文件会签服务层
public interface DccFileContractService {

    public JsonResult getProcess( String type, String form_id,String file_id,String user_id,String department_id);
    public JsonResult serchfour(String fourInput);
    public JsonResult insertSetDccContract(String form_id, String one, String two, String three, String four, String five, String six);
    //查询了状态
    JsonResult selectStage(String form_id, String user_id);

    JsonResult AddSignOff(String form_id);
}
