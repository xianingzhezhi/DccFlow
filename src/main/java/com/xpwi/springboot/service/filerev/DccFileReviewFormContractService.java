package com.xpwi.springboot.service.filerev;

import com.xpwi.springboot.model.JsonResult;
//ReviewFrom会签
public interface DccFileReviewFormContractService {

    //根据表单信息获取表单信息，初始化表单
    public JsonResult getProcess(String type, String form_id,String file_id,String user_id, String department_id);
   //查询第五关人员
    public JsonResult serchfive(String fiveInput);
    //添加会签流程
    public JsonResult insertSetDccReviewFromContract(String form_id, String one, String two, String three, String four, String five, String six, String seven);
}
