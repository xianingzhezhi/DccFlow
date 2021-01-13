package com.xpwi.springboot.service.iso;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: workspace_svn
 * @description: 签核流程设定
 * @author: PengFei_Ge
 * @create: 2020-11-26 09:45
 **/
public interface IsoProcessService {

    /**
    * @Description: 获取签核流程
    * @Param: [type, form_id]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/11/26
    */
    JsonResult getProcess(String type, String form_id,String file_id,String user_id);
    
    /**
    * @Description: 设定签核流程
    * @Param: [form_id, one, two, three, four, five, six, seven, eight]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/11/26
    */
    JsonResult setProcess(String form_id, String one, String two, String three, String four, String five, String six, String seven, String eight);

    /**
    * @Description: 获取签核进度
    * @Param: [form_id]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/11/26
    */
    JsonResult getSignStatus(String form_id);
    /**
    * @Description: 根据表单号删除流程信息
    * @Param: [form_id]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/12/1
    */
    JsonResult delProcess(String form_id);

    JsonResult checkProcess(String form_id, String user_id);
}
