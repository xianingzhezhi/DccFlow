package com.xpwi.springboot.service.impl.surface;

import com.xpwi.springboot.dao.*;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.EmailService;
import com.xpwi.springboot.service.surface.SurfaceToSignedService;
import com.xpwi.springboot.utils.DateUntil;
import com.xpwi.springboot.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: workspace_svn
 * @description: 圖面簽核送核實現類
 * @author: PengFei_Ge
 * @create: 2020-12-11 14:15
 **/
@Service
public class SurfaceToSignedServiceImpl implements SurfaceToSignedService {

    @Autowired
    private FlowBaseSignprocessDao flowBaseSignprocessDao;

    @Autowired
    private FlowBaseFileinfoDao flowBaseFileinfoDao;

    @Autowired
    private PlatMessageDao platMessageDao;

    @Autowired
    private FlowBaseUserDao flowBaseUserDao;

    @Autowired
    private PlatMaintainDao platMaintainDao;

    @Autowired
    private FlowHisSignoffDao flowHisSignoffDao;

    @Autowired
    private FlowBaseSignoffDao flowBaseSignoffDao;

    @Autowired
    private FlowAddpeopleDao flowAddpeopleDao;

    @Autowired
    private BakUserDao bakUserDao;

    @Autowired
    private PlatDataDao platDataDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private  DbSessionDao dbSessionDao;

    @Autowired
    private FlowAgentDao flowAgentDao;

    @Override
    public JsonResult readyToSigned(String formId, String userId,String initType,String signed_idea) {

        String txt_idea = "同意 " +signed_idea;
        //String txtreason = update_reason;
        String next_user_id = "" ;
        String next_user_name = "" ;
        String next_department_id = "" ;
        String next_gate = "";
        String next_email = "";


        if(StringUtils.checkStrIsNull(formId)|| StringUtils.checkStrIsNull(userId)){
            return new JsonResult("400","請求數據異常!");
        }
        if("作廢".equals(initType)){
            if (signedProcessCheck(formId)) {
                return new JsonResult("400","未勾選會簽人,操作失敗！");
            }
        }else{
            if (!fileInfoCheck(formId)||!fileInfoCheck(formId)) {
                return new JsonResult("400","未上傳附件或未勾選會簽人,操作失敗！");
            }
        }
        List<PlatMessage> platMessage_list = platMessageDao.selectByPrimaryKey(formId);
        if(platMessage_list.size()==0){
            return new JsonResult("400","行信息無資料,不可送簽！");}
        FlowBaseUser userInfo = flowBaseUserDao.selectByPrimaryKey(new FlowBaseUserKey(userId, "dcc"));
        PlatMaintain platMaintain = platMaintainDao.selectAllByFormId(formId);
        if(platMaintain.getFormState().equals("0")){
            FlowHisSignoff flowHisSignoff = flowHisSignoffDao.selectAllByFormIdAndStage(new FlowHisSignoff(formId, "10"));
            if(flowHisSignoff!= null){
                FlowBaseSignoff flowBaseSignoffs = flowBaseSignoffDao.selectByFormIdAndState(new FlowBaseSignoff(formId, "10"));
                return new JsonResult("400",
                        "您的工號為" + userId + "此單據" + formId + "流程操作人應為" + flowBaseSignoffs.getUserId()+"_" + flowBaseSignoffs.getUserName() + ",操作失敗");
            }
            //刪除可能異常的數據  ,保證程序運行
            flowBaseSignoffDao.deleteByFormIdAndState(formId,"10");
            //sql = "delete from flow_system.flow_base_signoff where form_id = '"+form_id+"' and state != '10'" ;
            //sign_offDao.deleteSign_off(sql);

            List<FlowBaseSignprocess> processes_list = flowBaseSignprocessDao.selectAllByFormId(formId);

            int state = 10;  // 判斷為 第一次送簽  ,且已刪除大於10 的 數據 ,故 state 可為 10
            String sysDate = DateUntil.df.format(new Date()); // 獲取當前時間  yyyyMMddHHmm
            for( int i = 0 ; i<processes_list.size() ; i++ ){
                state += 10;
                FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(formId,state+"",userInfo.getDepartmentId(),userId,userInfo.getUserName(),"N","",sysDate);
                flowBaseSignoffDao.insertSelective(flowBaseSignoff);
            }
        }else if (platMaintain.getFormState().equals("2") ){
            return new JsonResult("400","此單據已結案");
        }else if (platMaintain.getFormState().equals("3")){
            return new JsonResult("400","此單據已过期30天作废");
        }
        // 获取加签人
        //sql = "select * from flow_system.flow_addpeople ba  where flag  = 'N' and form_id = '"+form_id+"'";
        //List<AddPeople> add_list = addPeopleDao.findAll("sql");
        List<FlowAddpeople> add_list = flowAddpeopleDao.selectAllByFormIdAndFlag(new FlowAddpeople(formId, "N"));
        //獲取當前操作人
        //尋找当前簽核人員
        Map<String, Object> now_signed = flowBaseSignoffDao.selectAllByFormIdAndFlagOrderByStatelimit1(formId);
        if(now_signed == null){
            return new JsonResult("400","表單已結案,不可再送簽!");
        }else {
            next_gate = now_signed.get("state").toString();
            next_user_id = now_signed.get("user_id").toString();
            next_user_name = now_signed.get("user_name").toString();
            next_department_id = now_signed.get("dept_id").toString();
            next_email = now_signed.get("email").toString();
            if(!userId.equals(next_user_id) && !userId.equals(platMaintain.getOperatorId()) ){  // 判断送签人是否为 保存人员
                return new JsonResult("400",
                        "您的工號為" + userId + "此單據" + formId + "流程操作人應為" + next_user_id+"_" + next_user_name + ",操作失敗");
            }else {
                if(next_gate!= "10"){
                    if(!userId.equals(next_user_id)){
                        return new JsonResult("400",
                                "您的工號為" + userId + "此單據" + formId + "流程操作人應為" + next_user_id+"_" + next_user_name + ",操作失敗");
                    }
                }
                //判斷是否有加簽信息
                if(add_list.size()!=0){  // 此处 无效作用  只做逻辑判断
                    BakUser bakUser = bakUserDao.selectAllByUserId(add_list.get(0).getUserId());
                    if(bakUser == null || bakUser.getEmail() == "" ){
                        return new JsonResult("400","加签人员信息异常,强联系IT!");
                    } else
                    {
                        next_gate = "";
                        next_user_id = bakUser.getUserId();
                        next_user_name = bakUser.getUserName();
                        next_department_id = bakUser.getDepartmentId();
                        next_email = bakUser.getEmail();
                    }
                    flowAddpeopleDao.updateFlagByFormIdAndUserId(formId, add_list.get(0).getUserId());
                }else {
                    //尋找下關簽核人員並發送mail
                    Map<String, Object> next_signed = flowBaseSignoffDao.selectNextAllByFormIdAndFlagOrderByStatelimit1(formId, next_gate);
                    if(next_signed.size()==0){  // "簽核完成" ,為查找到下關謙和人員信息
                        //將資料移至表中
                        Plat_Insert(formId,initType,platMaintain.getDepartmentName(),platMaintain.getCreateUserId(),platMaintain.getDrawType() ,platMaintain.getDrawOther());

                        //txt_idea 簽核意見
                        //  修改主表 狀態為結案
                        platMaintainDao.updateFormStateByFormId(formId);
                        // 修改簽核執行表 (原)flow_sign_off   現(flow_base_signoff)
                        FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(formId,next_gate,null,null,null,"Y",txt_idea, DateUntil.df.format(new Date()));
                        flowBaseSignoffDao.updateSignoff(flowBaseSignoff);

                        // 结案通知
                        String message="<font size=2pt face=新明細體>表單編號:"+formId ;
                        message=message+"<br/>申請人員:" + platMaintain.getCreateUser();
                        message=message+"<br/>申請部門:" + platMaintain.getDepartmentName() ;
                        message=message+"<br/>簽核意見:[結案]" ;

                        String sys_time = DateUntil.df.format(new Date()).toString();
                        message=message+"<br/>收件時間:" + sys_time;

                        Email email = new Email();
                        email.setIs_mail("0");
                        //email.setMail("Xinming_Chen@wistron.com");
                        email.setMessage(message);
                        email.setMail(next_email);
                        email.setUrl(StringUtils.url_ui + "/#/flow/dcc/ios/newfile?u=4&form_id="+formId);
                        email.setTheme("你有一封新的DCC<iso>文件待簽核");
                        email.setCreate_user(next_user_name);
                        emailService.insertEmailReject(email);
                        return new JsonResult("200","送签成功，已结案！");

                    }else{
                        next_email = next_signed.get("email").toString();
                        next_gate = next_signed.get("state").toString();
                        next_user_id = next_signed.get("user_id").toString();
                        next_user_name = next_signed.get("user_name").toString();
                        next_department_id = next_signed.get("dept_id").toString();
                    }

                }
                // 查找是否由代理人
                String format = DateUntil.df.format(new Date());
                List<Map<String, Object>> agent_list = flowAgentDao.selectAllByUserIdAndFlagLeftJsonBakUser(userId, format);
                /*sql = "select a.ddepartment_id ,a.duser_id ,a.user_name,a.ent09 ,b.email from flow_system.flow_agent a\n" +
                        "left join flow_system.flow_dsd_user b on a.duser_id  = b.user_id \n" +
                        "where a.user_id ='' and a.flag = 'Y' and  ''  > a.start_time  and  '' < a.end_time ";*/
                //List<Map<String, Object>> agent_list = utilsDao.findAll(sql);
                if (agent_list.size()>0 ){
                    if(agent_list.get(0).get("ent09").toString().equals("ALL")
                            || agent_list.get(0).get("ent09").toString().equals(formId.substring(0,6))){
                        next_user_id = agent_list.get(0).get("duser_id").toString();
                        next_user_name = agent_list.get(0).get("user_name").toString();
                        next_department_id = agent_list.get(0).get("ddepartment_id").toString();
                        next_email = agent_list.get(0).get("email").toString();

                        flowBaseSignoffDao.updateSignoff(new FlowBaseSignoff(formId,next_gate,next_department_id,next_user_id,next_user_name));
                    }
                }


                /*ViewState["str_next_gate"] = str_next_gate;
                ViewState["str_next_dep"] = str_next_dep;
                ViewState["str_next_id"] = str_next_id;
                ViewState["str_next_name"] = str_next_name;
                ViewState["str_next_mail"] = str_next_mail;
                ViewState["str_form_id"] = str_form_id;*/

                setDbSession(formId + "_" + userId + "_" + "next_gate",next_gate);
                setDbSession(formId + "_" + userId + "_" + "txt_idea",txt_idea);
                setDbSession(formId + "_" + userId + "_" + "next_user_id",next_user_id);
                setDbSession(formId + "_" + userId + "_" + "next_user_name",next_user_name);
                setDbSession(formId + "_" + userId + "_" + "next_department_id",next_department_id);
                setDbSession(formId + "_" + userId + "_" + "next_email",next_email);
                //setDbSession(formId + "_" + userId + "_" + "txt_version",txt_version);

                if(platMaintain.getFormState().equals("0")){
                    return new JsonResult("200",
                            "確定送簽嗎？下一關單位" + next_department_id + "下一關人員" + next_user_id+"_" + next_user_name + "");
                }else {  // 重复是为了提示是否签核
                    return new JsonResult("200",
                            "確定送簽嗎？下一關單位" + next_department_id + "下一關人員" + next_user_id+"_" + next_user_name + "");
                }
            }
        }
    }

    @Override
    public JsonResult signedClick(String formId, String userId) {
        List<DbSession> department_id_ession = getDbSession(formId + "_" + userId + "_" + "next_department_id");
        List<DbSession> user_id_session = getDbSession(formId + "_" + userId + "_" + "next_user_id");
        List<DbSession> user_name_session = getDbSession(formId + "_" + userId + "_" + "next_user_name");
        List<DbSession> next_gate_session = getDbSession(formId + "_" + userId + "_" + "next_gate");
        List<DbSession> txt_idea_session = getDbSession(formId + "_" + userId + "_" + "txt_idea");
        List<DbSession> next_email_session = getDbSession(formId + "_" + userId + "_" + "next_email");


        PlatMaintain platMaintain = platMaintainDao.selectAllByFormId(formId);
        // 發送郵件
        String message="<font size=2pt face=新明細體>表單編號:"+formId ;
        message=message+"<br/>申請人員:" + platMaintain.getCreateUser();
        message=message+"<br/>申請部門:" + platMaintain.getDepartmentName() ;
        if(txt_idea_session.size()>0 && txt_idea_session.get(0).getSession_value().equals("undifined"))
            message=message+"<br/>簽核意見:[同意]" ;
        else
            message=message+"<br/>簽核意見:[同意] : " + txt_idea_session.get(0).getSession_value() ;

        String sys_time = DateUntil.df.format(new Date()).toString();
        message=message+"<br/>收件時間:" + sys_time;

        Email email = new Email();
        email.setIs_mail("0");
        //email.setMail("Xinming_Chen@wistron.com");
        email.setMessage(message);
        email.setMail(next_email_session.get(0).getSession_value());
        email.setUrl(StringUtils.url_ui + "/#/flow/dcc/ios/newfile?u=4&form_id="+formId);
        email.setTheme("你有一封新的DCC<圖面申請>待簽核");
        email.setCreate_user(user_name_session.get(0).getSession_value());
        emailService.insertEmailReject(email);

        if (platMaintain.getFormState().equals("0")){
            platMaintainDao.updateFormStateByFormIdToTarget(formId,"1");
        }
        try {
            //更新簽核檔
            Map<String, Object> map = null;
            if(next_gate_session.get(0).getSession_value().length()!= 0){
                map = flowBaseSignoffDao.selectNextAllByFormIdAndFlagOrderByStatelimit1(formId,next_gate_session.get(0).getSession_value());
                if(map == null){
                    return  new JsonResult("400","更新簽核檔失敗,郵件已發送");
                }else{
                    FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(formId,next_gate_session.get(0).getSession_value());
                    flowBaseSignoff.setFlag("Y");
                    flowBaseSignoff.setSysTime(DateUntil.df.format(new Date()));
                    if(map.get("state").toString().equals("10")){
                        flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                    }else {
                        flowBaseSignoff.setMessage(txt_idea_session.get(0).getSession_value());
                        flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                    }
                }
            }else {
                map = flowBaseSignoffDao.selectAllByFormIdAndFlagOrderByStatelimit1(formId);
                if(map == null){
                    return  new JsonResult("400","更新簽核檔失敗,郵件已發送");
                }else{
                    FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(formId,next_gate_session.get(0).getSession_value());
                    flowBaseSignoff.setFlag("Y");
                    flowBaseSignoff.setSysTime(DateUntil.df.format(new Date()));
                    if(map.get("state").toString().equals("10")){
                        flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                    }else {
                        flowBaseSignoff.setMessage(txt_idea_session.get(0).getSession_value());
                        flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                    }
                    FlowBaseSignoff flowBaseSignoff1
                            = new FlowBaseSignoff(formId,map.get("state").toString(),department_id_ession.get(0).getSession_value()
                    ,user_id_session.get(0).getSession_value(),user_name_session.get(0).getSession_value());
                    flowBaseSignoff1.setFlag("N");
                    flowBaseSignoffDao.insertSelective(flowBaseSignoff1);
                    /*String sql = "insert into flow_system.flow_base_signoff  (form_id,state,dept_id,user_id,user_name,flag)\n" +
                            "values ('"+form_id+"','"+sign_off2.get(0).getState()+"'," +
                            "'"+department_id_ession.get(0).getSession_value()+"','"+user_id_session.get(0).getSession_value()+"'," +
                            "'"+user_name_session.get(0).getSession_value()+"','N' )" ;
                    sign_offDao.insertScrap(sql);*/

                }
            }
            return  new JsonResult("200","送核發送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return  new JsonResult("500",e.toString());
        } finally {
            delDbSession(formId);
        }

    }


    public  void delDbSession(String formId){
        String inSql = "delete from flow_system.flow_base_session where session_id like '"+formId+"%'";
        dbSessionDao.setDbSession(inSql);
    }
    /**
    * @Description: 簽核完畢 ,插入數據,簽核最後一次執行
    * @Param: [formId, initType, departmentName, createUserId, drawType, drawOther]
    * @return: void
    * @Author: PengFei_Ge
    * @Date: 2020/12/12
    */
    private void Plat_Insert(String formId, String initType, String departmentName, String createUserId, String drawType, String drawOther) {
        List<PlatMessage> platMessage_list = platMessageDao.selectByPrimaryKey(formId);
        for(int i = 0 ; i<platMessage_list.size() ;){
            PlatMessage platMessage = platMessage_list.get(i);
            if("新增".equals(initType)){
                PlatData platData
                        = new PlatData(platMessage.getModelId(),platMessage.getMaterialName(),platMessage.getMaterialId(),
                        platMessage.getDrawId(),platMessage.getVersionId(),platMessage.getShareInfo(),platMessage.getUpdateReason(),
                        "Y",departmentName,createUserId,drawType,drawOther,DateUntil.simpleDateFormat.format(new Date()));
                platDataDao.insertSelective(platData);
            }else if("變更".equals(initType)) {
                String material_up_id = platMessage.getMaterialUpId();
                if(material_up_id == null){
                    material_up_id = platMessage.getMaterialId();
                }
                String draw_up_id = platMessage.getDrawUpId();
                if(draw_up_id == null){
                    draw_up_id = platMessage.getDrawId();
                }
                String version_up_id = platMessage.getVersionUpId();
                if(version_up_id == null){
                    version_up_id = platMessage.getVersionId();
                }
                //使用新版本數據進行插入數據
                PlatData platData
                        = new PlatData(platMessage.getModelId(),platMessage.getMaterialName(),material_up_id,
                        draw_up_id,version_up_id,platMessage.getShareInfo(),platMessage.getUpdateReason(),
                        "Y",departmentName,createUserId,drawType,drawOther,DateUntil.simpleDateFormat.format(new Date()));
                platDataDao.insertSelective(platData);
                // 使用舊版本 數據進行update
                PlatData update_platData
                        = new PlatData(platMessage.getModelId(),platMessage.getMaterialName(),platMessage.getDrawId(),platMessage.getVersionId(),platMessage.getUpdateReason(),"");
                platDataDao.updatePlatValidAndUpdateReason(update_platData);
            }else {
                PlatData update_platData
                        = new PlatData(platMessage.getModelId(),platMessage.getMaterialName(),platMessage.getDrawId(),platMessage.getVersionId(),platMessage.getUpdateReason(),drawType);
                platDataDao.updatePlatValidAndUpdateReason(update_platData);
            }
        }

    }

    /**
    * @Description: 根據表單號檢查
    * @Param: [formId]
    * @return: java.lang.Boolean
    * @Author: PengFei_Ge
    * @Date: 2020/12/11
    */
    private Boolean fileInfoCheck(String formId) {
        List<FlowBaseFileinfo> flowBaseFileinfos = flowBaseFileinfoDao.selectAllByFormId(formId);
        if(flowBaseFileinfos.size()>0)
            return true;
        else
            return false;
    }

    /**
    * @Description: 根據表單號檢查單號流程信息是否設定
    * @Param: [formId]
    * @return: void
    * @Author: PengFei_Ge
    * @Date: 2020/12/11
    */
    private Boolean signedProcessCheck(String formId) {
        List<FlowBaseSignprocess> all = flowBaseSignprocessDao.findAll(new FlowBaseSignprocess(formId));
        if(all.size()>0)
            return true;
        else
            return false;
    }

    public  List<DbSession> getDbSession(String id){
        String sql = "select * from flow_system.flow_base_session  where session_id  ='"+id+"' order by create_date  desc ";
        List<DbSession> dbSession = dbSessionDao.getDbSession(sql);
        String deleteSql = "delete from flow_system.flow_base_session  where session_id  ='"+id+"' ";
        dbSessionDao.delDbSession(deleteSql);
        return dbSession;
    }

    public  void setDbSession(String id,String value){
        String inSql = "insert into flow_system.flow_base_session (session_id,session_value)\n" +
                "values ('"+id+"','"+value+"')";
        dbSessionDao.setDbSession(inSql);
    }
}
