package com.xpwi.springboot.service.pqc;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PqcHisList;
import com.xpwi.springboot.model.PqcHisListDetail;

public interface PqcSignOffService {
    //查看所有表单(条件查询)
    JsonResult selectPqcAfterForm(PqcHisList pqcHisList);

    ////PD、PIC待回复查询（联查）
    JsonResult selectPqcForm(PqcHisList pqcHisList);



    //查看单个表单详细信息
    JsonResult selectPqcFormDetail(String listNo);

    //根据表单号查询（排除掉已回复的）
    JsonResult selectByListNoAndSuggetIsNull(String  listNo,String picUser);

    //签核表单
    JsonResult updatePqcAfterForm(PqcHisList pqcHisList);

    //组长修改(送核)
    JsonResult updateQcForm(PqcHisListDetail pqcHisListDetail,String countType);


    //改善对策回复
    JsonResult updatePqcAfterForm(PqcHisListDetail pqcHisListDetail);

}
