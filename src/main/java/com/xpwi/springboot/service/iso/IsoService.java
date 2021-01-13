package com.xpwi.springboot.service.iso;

import com.xpwi.springboot.model.FlowOpRowmessage;
import com.xpwi.springboot.model.IsoForm;
import com.xpwi.springboot.model.JsonResult;

/**
 * @program: springboot
 * @description: ISO 文件管理后台
 * @author: PengFei_Ge
 * @create: 2020-11-20 11:39
 **/
public interface IsoService {
    
    /**
    * @Description: 获取系统编号和文件编号
    * @Param: [table_name, salt]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/11/23
    */
    JsonResult getNewSysid(String table_name, String salt);

    /**
    * @Description: DCC签核 新增签核表单
    * @Param: [isoForm]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/11/23
     * @param isoForm
    */
    JsonResult save(IsoForm isoForm,String user_id,String initStatus);
    /**
    * @Description: 页面初始化  1新增 2修改 3作廢 4查看/送簽
    * @Param: [formId, user_id, init_status]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/12/4
    */
    JsonResult initIsoPage(String form_id, String user_id, String init_status ,String init_param1 , String login_userId);

    int updateIsoFrom(IsoForm isoForm);
    /**
    * @Description: 行信息保存
    * @Param: [flowOpRowmessage]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2020/12/7
     * @param flowOpRowmessage_arr
     */
    JsonResult rowSave(FlowOpRowmessage[] flowOpRowmessage_arr);

    /**
     * 顯示評論，單據履歷表
     * @param form_id
     * @return
     */
    JsonResult initFormDocument(String form_id);

    JsonResult initIsoRowsInfo(String form_id);
}
