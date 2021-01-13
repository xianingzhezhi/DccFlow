package com.xpwi.springboot.service.impl.surface;

import com.xpwi.springboot.dao.*;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.iso.IsoService;
import com.xpwi.springboot.service.surface.SurfaceService;
import com.xpwi.springboot.service.utils.GetFormIdService;
import com.xpwi.springboot.utils.DateUntil;
import com.xpwi.springboot.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class SurfaceServiceImpl implements SurfaceService {

    @Autowired
    private FlowBaseSignoffDao flowBaseSignoffDao;

    @Autowired
    private FlowHisSignoffDao flowHisSignoffDao;

    @Autowired
    private PlatMaintainDao platMaintainDao;

    @Autowired
    private PlatMessageDao platMessageDao;

    @Autowired
    private GetFormIdService getFormIdService;

    @Autowired
    private FlowBaseUserDao flowBaseUserDao;

    @Autowired
    private PlatDataDao platDataDao;
    @Autowired
    private BakUserDao bakUserDao;


    @Override
    public JsonResult initSurfacePage(String form_id, String user_id, String init_status, String init_param1, String login_userId) {
        String updateFlag = "N";
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> initMap = InitColumns();
        list.add(initMap);

        FlowBaseUser user_info = flowBaseUserDao.selectByPrimaryKey(new FlowBaseUserKey(login_userId, "dcc"));


        return null;
    }

    private Map<String, Object> InitColumns() {
        Map<String, Object> map = new LinkedHashMap();
        map.put("formId", null);                     //表单编号
        map.put("applyDate", null);                  //申請日期
        map.put("departmentId", null);               //部门编号
        map.put("departmentName", null);             //部门名称
        map.put("createUser", null);                 //创建人
        map.put("createUserId", null);               //创建人工号
        map.put("drawType", null);                   //圖面類別
        map.put("drawOther", null);                  //其它
        map.put("remark", null);                     //版次
        map.put("formType", null);                   //0新增，1变更，2作废
        map.put("formState", null);                  //0 开立，1送签中，2 结案，3作废
        map.put("operatorId", null);                 //操作人id
        return map;
    }

    @Override
    public JsonResult surfaceAddMain(String user_id, PlatMaintain platMaintain, String countType) {
        BakUser user = this.bakUserDao.selectAllByUserId(user_id);
        String form_id = platMaintain.getFormId();
        Boolean flag = false;
        List list = new ArrayList();
        if (user == null) {
            return new JsonResult("400", "當前用戶信息未維護!");
        }
        try {
            // 判断单号 是否为空
            if (StringUtils.checkStrIsNull(form_id)) {
                //设置操作人
                platMaintain.setOperatorId(user_id);
                // 获取系统单号
                JsonResult jsonResult2 = this.getFormIdService.getSurfaceFormId("dcc_plat_maintain", "KS-DWG");
                if (jsonResult2.getStatus().equalsIgnoreCase("200")) {
                    form_id = jsonResult2.getData().get(0).toString();
                    list.add(form_id);
                }

                //签核流程表插入一笔数据   ,创建人信息!
                FlowBaseSignoff flowBaseSignoff
                        = new FlowBaseSignoff(form_id, "10", platMaintain.getDepartmentId(),
                        platMaintain.getCreateUserId(), platMaintain.getCreateUser(),
                        "N", "新增申請", DateUntil.df.format(new Date()));
                flowBaseSignoffDao.insert(flowBaseSignoff);
                flag = true;
            } else {
                list.add(form_id);
                // 判断点保存按钮的是不是申请人  ,不是则 驳回
                FlowHisSignoff flowHisSignoff = flowHisSignoffDao.selectAllByFormIdAndStage(new FlowHisSignoff(form_id, "10"));
                if (flowHisSignoff != null) {
                    FlowBaseSignoff flowBaseSignoff = flowBaseSignoffDao.selectByFormIdAndState(new FlowBaseSignoff(form_id, "10"));
                    if (!flowBaseSignoff.getUserId().equals(user_id)) {
                        return new JsonResult("400", "駁回單據,應由申請者修改,操作失敗！");
                    }
                }
                List<PlatMaintain> platMaintainList = platMaintainDao.selectByPlatMaintain(new PlatMaintain(form_id));
                if (platMaintainList.size() > 0) {
                    System.out.println(platMaintainList.get(0));
                    System.out.println(platMaintainList.get(0).getFormState());
                    if (platMaintainList.get(0).getFormState().equals("1")) {
                        return new JsonResult("400", "此單據正在送核中，不能修改，操作失敗！");
                    }
                    if (platMaintainList.get(0).getFormState().equals("2")) {
                        return new JsonResult("400", "此單據已結案,不可操作！");
                    }
                    if (platMaintainList.get(0).getFormState().equals("3")) {
                        return new JsonResult("400", "此文件編號已填寫超過30天未送簽,已被佔用不可以操作！");
                    }
                }
                //设置操作人
                platMaintain.setOperatorId(platMaintainList.get(0).getOperatorId());
            }
            if (countType.equals("0")) {
                platMaintain.setFormType("0");
            }
            if (countType.equals("1")) {
                platMaintain.setFormType("1");
            }
            if (countType.equals("2")) {
                platMaintain.setFormType("2");
            }
            List<PlatMaintain> check_exit = platMaintainDao.selectByPlatMaintain(new PlatMaintain(form_id));
            PlatMaintain platMaintain1 = new PlatMaintain(form_id, new Date(), platMaintain.getCreateUserId(),
                    platMaintain.getCreateUser(), platMaintain.getDepartmentId(), platMaintain.getDepartmentName(),
                    platMaintain.getDrawType(), platMaintain.getDrawOther(), platMaintain.getRemark(),
                    platMaintain.getFormType(), "0", platMaintain.getOperatorId());

            if (check_exit.size() > 0) {
                this.platMaintainDao.update(platMaintain1);
            } else {
                this.platMaintainDao.insert(platMaintain1);
            }
            List<FlowBaseSignoff> flowBaseSignoffList = this.flowBaseSignoffDao.selectsByFormIdAndState(form_id, "10");
            FlowHisSignoff flowHisSignoff = this.flowHisSignoffDao.selectAllByFormId(form_id);
            if (flowHisSignoff == null) {//無駁回
                if (platMaintain.getCreateUserId() != flowBaseSignoffList.get(0).getUserId()) {
                    FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(platMaintain.getDepartmentId(),
                            platMaintain.getCreateUserId(), platMaintain.getCreateUser(), platMaintain.getFormId(), platMaintain.getFormState());
                    this.flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                }
            }
            return new JsonResult(list);
        } catch (Exception e) {
            e.printStackTrace();
            return new JsonResult("500", e.toString());
        }
    }

    @Override
    public JsonResult surfaceRowSave(PlatMessage[] platMessages_arr, String form_id,String countType) {

        PlatMessage platMessage = null;
        List list = new ArrayList<>();
        list.add(form_id);
        int insertcount = 0;
        //保存所有行先刪除以前舊資料重新做保存
        platMessageDao.deleteByPrimaryKey(platMessages_arr[0].getFormId());
        for (int i = 0; i < platMessages_arr.length; i++) {
            platMessage = platMessages_arr[i];
            if (StringUtils.checkStrIsNull(platMessage.getDrawId()) || StringUtils.checkStrIsNull(platMessage.getModelId())) {
                continue;
            }
            // 0  新增   1  变更
            if (countType.equals("0")){
                System.out.println("卡控++++++"+StringUtils.checkStrIsNull(platMessage.getDrawId()));
                if (!StringUtils.checkStrIsNull(platMessage.getDrawId())) {
                    PlatMessage platMessage1 = new PlatMessage();
                    platMessage1.setDrawId(platMessage.getDrawId());
                    platMessage1.setMaterialName(platMessage.getMaterialName());
                    platMessage1.setVersionId(platMessage.getVersionId());
                    List<PlatMessage> platMessageList = this.platMessageDao.selectsByPrimaryKey(platMessage1);
                    if (platMessageList.size() > 0) {
                        String str_err = "表單信息保存成功，但第" + (i + 1) + "行圖面編號重復,請檢查后重新保存!";
                        return new JsonResult("200", str_err, list);
                    }
                }
            }else if (countType.equals("1")){
                PlatData platData=new PlatData();
                platData.setPlatValid("Y");
                platData.setModelId(platMessage.getModelId());
                platData.setDrawId(platMessage.getDrawId());
                platData.setVersionId(platMessage.getVersionId());
                platData.setMaterialName(platMessage.getMaterialName());
                List<PlatData> platData1 = this.platDataDao.selectByPlatData(platData);
                if (platData1.size()<=0){
                    String message="第" + (i+1) + "行MODEL+DWG+REV未找到此版本資料!";
                    return  new JsonResult("400",message);
                }else{
                    platMessage.setMaterialName(platData1.get(0).getMaterialName());
                    platMessage.setMaterialId(platData1.get(0).getMaterialId());
                }
            }


            try {
                platMessage.setFormId(form_id);
                int count = this.platMessageDao.insert(platMessage);
            } catch (Exception e) {
                e.printStackTrace();
                return new JsonResult("500", "插入圖面信息失敗,請聯繫資訊！");
            }
        }
        return new JsonResult("200", "保存成功！");
    }

    //变更保存 行信息   已整合进行保存  ，，作为备用
    @Override
    public JsonResult surfaceChangeRowSave(PlatMessage[] platMessages_arr, String form_id) {
        PlatMessage platMessage = null;
        List list = new ArrayList<>();
        list.add(form_id);
        int insertcount = 0;
        //保存所有行先刪除以前舊資料重新做保存
        platMessageDao.deleteByPrimaryKey(platMessages_arr[0].getFormId());
        for (int i = 0; i < platMessages_arr.length; i++) {
            platMessage = platMessages_arr[i];
            if (StringUtils.checkStrIsNull(platMessage.getDrawId()) || StringUtils.checkStrIsNull(platMessage.getModelId())) {
                continue;
            }
            PlatData platData=new PlatData();
            platData.setPlatValid("Y");
            platData.setModelId(platMessage.getModelId());
            platData.setDrawId(platMessage.getDrawId());
            platData.setVersionId(platMessage.getVersionId());
            platData.setMaterialName(platMessage.getMaterialName());
            List<PlatData> platData1 = this.platDataDao.selectByPlatData(platData);
            if (platData1.size()<=0){
                String message="第" + (i+1) + "行MODEL+DWG+REV未找到此版本資料!";
                return  new JsonResult("400",message);
            }else{
                platMessage.setMaterialName(platData1.get(0).getMaterialName());
                platMessage.setMaterialId(platData1.get(0).getMaterialId());
            }

            try {
                platMessage.setFormId(form_id);
                int count = this.platMessageDao.insert(platMessage);
            } catch (Exception e) {
                e.printStackTrace();
                return new JsonResult("500", "插入圖面信息失敗,請聯繫資訊！");
            }
        }
        return new JsonResult("200", "保存成功！");
    }

    @Override
    public JsonResult surfaceInitialization(String form_id) {
        PlatMaintain platMaintain = this.platMaintainDao.selectAllByFormId(form_id);
        List list=new ArrayList();
        if (platMaintain!=null){
            list.add(platMaintain);
            return new JsonResult(list);
        }else{
            return new JsonResult("400","没有该表单信息");
        }
    }

    @Override
    public JsonResult surfaceInitialization2(String form_id) {
        List<PlatMessage> platMessageList = this.platMessageDao.selectByPrimaryKey(form_id);
        if (platMessageList.size()>0){
            return  new JsonResult(platMessageList);
        }else{
            return new JsonResult("400","没有该表单行信息");
        }
    }
}
