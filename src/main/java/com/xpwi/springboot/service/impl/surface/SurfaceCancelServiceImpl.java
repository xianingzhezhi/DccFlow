package com.xpwi.springboot.service.impl.surface;

import com.xpwi.springboot.dao.*;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.surface.SurfaceCancelService;
import com.xpwi.springboot.service.utils.GetFormIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: idea_server
 * @description: 圖面作廢實現類
 * @author: PengFei_Ge
 * @create: 2020-12-16 15:04
 **/
@Service
public class SurfaceCancelServiceImpl implements SurfaceCancelService {

    @Autowired
    private PlatDataDao platDataDao;

    @Autowired
    private PlatMessageDao platMessageDao;

    @Autowired
    private PlatMaintainDao platMaintainDao;

    @Autowired
    private GetFormIdService getFormIdService;

    @Autowired
    private FlowBaseUserDao flowBaseUserDao;

    @Autowired
    private FlowBaseSignoffDao flowBaseSignoffDao;

    @Override
    public JsonResult getCancelList(String modelId,String drawId, String drawType, String drawOther) {
        PlatData platData = new PlatData(modelId,drawId,drawType,drawOther);
        if (!drawType.equals("D")) {
            drawOther=null;
        }
        List<PlatData> platData_list = platDataDao.selectAllByDrawTypeAndModelIdLikeAndDrawIdLikeAndDrawOtherLike(platData);
        return new JsonResult(platData_list);
    }

    @Override
    public JsonResult selectCheck(List<PlatData> PlatDataList) {
        String rtn = "所選第";

        for(int i = 0 ; i<PlatDataList.size();i++ ){
            Map<String, Object> map =
                    platMessageDao.selectAllLeftJoinMaintainByDrawId(PlatDataList.get(i).getModelId(),PlatDataList.get(i).getDrawId());
            if (map != null){
                rtn+= (i+",");
            }
        }
        if (rtn.length() == 3){
            return new JsonResult("200","當前選擇無誤!");
        }else {
            return new JsonResult("400",rtn+"信息作廢簽核中!");
        }
    }

    @Override
    public JsonResult cancelClick(String drawType, String drawOther,String userId, List<PlatData> PlatDataList) {
        List list= new ArrayList();
        try{
            // 獲取用戶信息
            Map<String, Object> map = flowBaseUserDao.selectByPrimaryKeyLeftJoindept(new FlowBaseUserKey(userId, "dcc"));
            // 獲取系統單號
            JsonResult formId_obj = getFormIdService.getSurfaceFormId("dcc_plat_maintain", "KS-DWG");
            String formId = formId_obj.getData().get(0).toString();
            // 插入主表信息   初始化信息
            PlatMaintain platMaintain = new PlatMaintain(formId,new Date(),map.get("user_id").toString()
                    ,map.get("user_name").toString(),map.get("department_id").toString(),map.get("department_name").toString()
                    ,drawType,drawOther,"","2","0",map.get("user_id").toString());
            platMaintainDao.insertSelective(platMaintain);
            // 將已選中資料插入表中   首先刪除異常信息
            platMessageDao.deleteByPrimaryKey(formId);
            list.add(formId);
            //開始保存行信息
            for(int i = 0 ; i<PlatDataList.size();i++ ){
                PlatMessage platMessage = new PlatMessage(formId,PlatDataList.get(i).getModelId(),PlatDataList.get(i).getDrawId()
                        ,PlatDataList.get(i).getVersionId(),PlatDataList.get(i).getShareInfo(),PlatDataList.get(i).getMaterialName()
                        ,PlatDataList.get(i).getMaterialId(),PlatDataList.get(i).getUpdateReason());
                platMessageDao.insertSelective(platMessage);
            }
            // 刪除并插入  簽核執行表
            flowBaseSignoffDao.deleteByFormIdAndEqualState(formId,"10");
            FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(formId,"10",
                    map.get("department_id").toString(),map.get("user_id").toString(),map.get("user_name").toString());
            flowBaseSignoffDao.insertSelective(flowBaseSignoff);
            return new JsonResult("200","跳轉頁面到主頁面",list);
        }catch(Exception e){
            e.printStackTrace();
            return  new JsonResult("500","數據庫連接失敗，請聯繫IT");
        }
    }
}
