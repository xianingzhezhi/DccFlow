package com.xpwi.springboot.service.track;

import com.xpwi.springboot.model.JsonResult;

/**
 * @program: idea_server
 * @description: 簽核list
 * @author: PengFei_Ge
 * @create: 2020-12-23 17:02
 **/
public interface SignedStatusService {
    /**
    * @Description: 獲取送簽LIST
    * @Param: [userId]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2021/1/6
    */
    JsonResult getWriterList(String userId);
    /**
    * @Description: 獲取待簽核列表
    * @Param: [userId]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2021/1/6
    */
    JsonResult getSignedList(String userId);
    /**
    * @Description: 獲取結案List
    * @Param: [userId]
    * @return: com.xpwi.springboot.model.JsonResult
    * @Author: PengFei_Ge
    * @Date: 2021/1/6
    */
    JsonResult getClosedList(String userId);
}
