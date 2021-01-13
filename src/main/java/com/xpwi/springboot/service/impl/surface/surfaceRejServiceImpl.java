package com.xpwi.springboot.service.impl.surface;

import com.xpwi.springboot.dao.BakUserDao;
import com.xpwi.springboot.dao.FlowBaseSignoffDao;
import com.xpwi.springboot.dao.FlowHisSignoffDao;
import com.xpwi.springboot.dao.PlatMaintainDao;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.EmailService;
import com.xpwi.springboot.service.surface.SurfaceRejService;
import com.xpwi.springboot.utils.DateUntil;
import com.xpwi.springboot.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class surfaceRejServiceImpl implements SurfaceRejService {

    @Autowired
    PlatMaintainDao platMaintainDao;


    @Autowired
    FlowHisSignoffDao flowHisSignoffDao;

    @Autowired
    FlowBaseSignoffDao flowBaseSignoffDao;

    @Autowired
    EmailService emailService;
    @Autowired
    BakUserDao bakUserDao;
    /**
     * 图面驳回
     * @param formId
     * @param rejReason
     * @param loginUser
     * @return
     */
    @Override
    public JsonResult setSurfaceRej(String formId,String rejReason,String loginUser,int cancelType){

        Date date = new Date();
        //设置要获取到什么样的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //获取String类型的时间
        String createdate = sdf.format(date);
        JsonResult jsonResult=null;//代理人信息
        String agentId=null;//代理人Id
        PlatMaintain platMaintain = platMaintainDao.selectAllByFormIdAndStage(formId);
        if(platMaintain==null){
            return new JsonResult("232","此單據編碼" + formId+ "不存在或者單據狀態不正確,操作失敗!");
        }

        /**
         * 判断操作人是否为当前操作人
         */
        List data = FlowSign(formId).getData();
        if(data==null){
            return new JsonResult("233","此單據編碼" + formId+ "不存在或者單據狀態不正確,操作失敗!");
        }
        //操作人不为当前签核人
        if(!data.get(2).equals(loginUser)){
            return new JsonResult("234","您的工號為" + loginUser + "此單據" + formId + "流程操作人應為" + data.get(2)+ ",操作失敗!");
        }

        if(formId==null ||formId.length()==0){
            return new JsonResult("235","不在可操作页面,操作失败！");
        }
        try{
            boolean result = inertRej(formId,data.get(0).toString(),cancelType);
            if(result==true){
                Map<String, Object> stringObjectMap = flowHisSignoffDao.selectAllByFormIdAnd(formId);
                if(stringObjectMap==null){
                    return new JsonResult("236","此表單創建人未送核，請核實！");
                }
                String stage=stringObjectMap.get("stage").toString();
                String userId=stringObjectMap.get("create_user_id").toString();
                //查找有无代理人
                jsonResult = flowAgenCheck(formId, stage, userId);
                rejReason="驳回至申请人"+rejReason;
                if(jsonResult.getData()==null){
                    FlowHisSignoff f1=new FlowHisSignoff(formId,data.get(0).toString(),data.get(1).toString(),data.get(2).toString(),data.get(3).toString(),"Y",rejReason,createdate,"Y","N");
                    flowHisSignoffDao.insert(f1);


                     // 通过formId和stage更新数据信息 message:驳回重送
                    FlowBaseSignoff flowBaseSignoff=new FlowBaseSignoff(formId,stage,"驳回重送",createdate);
                    flowBaseSignoff.setFlag("Y");
                    flowBaseSignoffDao.updateSignoff(flowBaseSignoff);

                    // 删除阶段不为10的数据
                    flowBaseSignoffDao.deleteByFormIdAndState(formId,"10");

                    //更新dcc_plat_maintain状态为0
                    platMaintainDao.updateFormStateByFormIdToTarget(formId,"0");

                }else {
                    //获取代理人信息
                    String deptmentId=jsonResult.getData().get(0).toString();
                    agentId=jsonResult.getData().get(1).toString();
                    String agentName=jsonResult.getData().get(2).toString();
                    String mail=jsonResult.getData().get(3).toString();

                    //插入
                    FlowHisSignoff f1=new FlowHisSignoff(formId,data.get(0).toString(),data.get(1).toString(),data.get(2).toString(),data.get(3).toString(),"Y",rejReason,createdate,"Y","N");
                    flowHisSignoffDao.insert(f1);
                    //通过formId和stage更新数据信息 message:驳回代送
                    FlowBaseSignoff flowBaseSignoff=new FlowBaseSignoff(formId,stage,deptmentId,agentId,agentName,"N","驳回代送",createdate);
                    flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                    // 删除阶段不为10的数据
                    flowBaseSignoffDao.deleteByFormIdAndState(formId,"10");
                    //更新dcc_plat_maintain状态为0
                    platMaintainDao.updateFormStateByFormIdToTarget(formId,"N");
                }
            }
            else{
                return new JsonResult("400","駁回操作失敗,請聯繫資訊！");
            }
            //给签核人员发送邮件
            List<Map<String, Object>> maps = flowHisSignoffDao.selectUnionUser(formId);

            String emailConten="";
            String message="";
            Email email = new Email();
            for(int i=0;i<maps.size();i++){
                if(maps.get(i).get("stage").toString().equals("10")){
                    //原申請人有設置代理

                    if(jsonResult.getData()!=null){
                        // 發送郵件
                        message="<font size=2pt face=新明細體>申请日期:"+platMaintain.getApplyDate() ;
                        message=message+"<br/>申請人員:" + platMaintain.getCreateUser();
                        message=message+"<br/>申請部門:" + platMaintain.getDepartmentName() ;
                        message=message+"<br/>簽核意見:[駁回]" ;
                        message=message+"<br/>駁回意見:" + rejReason;
                        String sys_time = DateUntil.df.format(new Date()).toString();
                        message=message+"<br/>收件時間:" + sys_time;
                        email.setIs_mail("0");
                        email.setMail("Xinming_Chen@wistron.com");
                        email.setMessage(message);
                        email.setMail(maps.get(i).get("email").toString());
                        email.setUrl(StringUtils.url_ui + "/#/flow/dcc/ios/newfile?u=4&form_id="+formId+"&agentId="+agentId);
//                        maps.get(i).get("email").toString()=jsonResult.getData().get(3).toString();

                    }else{
                        // 發送郵件
                        message="<font size=2pt face=新明細體>申请日期:"+platMaintain.getApplyDate() ;
                        message=message+"<br/>申請人員:" + platMaintain.getCreateUser();
                        message=message+"<br/>申請部門:" + platMaintain.getDepartmentName() ;
                        message=message+"<br/>簽核意見:[駁回]" ;
                        message=message+"<br/>駁回意見:" + rejReason;
                        String sys_time = DateUntil.df.format(new Date()).toString();
                        message=message+"<br/>收件時間:" + sys_time;
                        email.setIs_mail("0");
                        email.setMail("Xinming_Chen@wistron.com");
                        email.setMessage(message);
                        email.setMail(maps.get(i).get("email").toString());
                        email.setUrl(StringUtils.url_ui + "/#/flow/dcc/ios/newfile?u=4&form_id="+formId+"&agentId="+maps.get(i).get("create_user_id"));
                    }
                }else{
                    // 發送郵件
                    message="<font size=2pt face=新明細體>申请日期:"+platMaintain.getApplyDate() ;
                    message=message+"<br/>申請人員:" + platMaintain.getCreateUser();
                    message=message+"<br/>申請部門:" + platMaintain.getDepartmentName() ;
                    message=message+"<br/>簽核意見:[駁回]" ;
                    message=message+"<br/>駁回意見:" + rejReason;
                    String sys_time = DateUntil.df.format(new Date()).toString();
                    message=message+"<br/>收件時間:" + sys_time;
                    email.setIs_mail("0");
                    email.setMail(maps.get(i).get("email").toString());
                    email.setMessage(message);
                }
                email.setTheme("圖面申請(" + formId + ")被用戶" + loginUser + "駁回");
                emailService.insertEmailReject(email);
            }
            FlowHisSignoff flowHisSignoff=new FlowHisSignoff();
            flowHisSignoff.setRej10("Y");
            flowHisSignoff.setForm_id(formId);
            flowHisSignoffDao.updateFlowHisSignoff(flowHisSignoff);

        }catch (Exception e){
//            Email email = new Email();
//            email.setIs_email("0");
//            email.setMail("Xinming_Chen@wistron.com");
//            email.setMessage("图面驳回发送邮件失败！");
//            emailService.insertEmailReject(email);
              e.printStackTrace();
            return new JsonResult("500");
        }

        return new JsonResult("200","驳回成功！");
    }
    /**
     * 找到当前操作人
     */
    public JsonResult FlowSign(String formId){
        Map<String, Object> stringObjectMap = flowBaseSignoffDao.selectAllByFormIdAndFlagOrderByStatelimit1(formId);
        if(stringObjectMap==null){
            return new JsonResult("233","签核完成",null);
        }
        String state=stringObjectMap.get("state").toString();
        String dept_id=stringObjectMap.get("dept_id").toString();
        String user_id=stringObjectMap.get("user_id").toString();
        String email=stringObjectMap.get("email").toString();
        List list = new ArrayList();
        list.add(state);
        list.add(dept_id);
        list.add(user_id);
        list.add(email);
        return new JsonResult("200","可进行操作",list);
    }


    /**
     * 將當前關卡上層所有已簽核插入rej檔
     */
    public Boolean  inertRej(String formId,String state,int cancelType){
        Boolean flag=false;
//       int intState = Integer.parseInt(state);
        try {
            List<FlowBaseSignoff> flowBaseSignoffs = flowBaseSignoffDao.selectRejByFormId(formId, state, cancelType);
            if(flowBaseSignoffs.size()==0){
                return false;
            }else{
                //插入驳回记录档
                for(int i=0;i<flowBaseSignoffs.size();i++){
                    FlowBaseSignoff flowBaseSignoff = flowBaseSignoffs.get(i);
                    FlowHisSignoff f1=new FlowHisSignoff(formId,flowBaseSignoff.getState(),flowBaseSignoff.getDeptId(),flowBaseSignoff.getUserId(),flowBaseSignoff.getUserName(),flowBaseSignoff.getFlag(),flowBaseSignoff.getMessage(),flowBaseSignoff.getSysTime(),"N","N");
                    flowHisSignoffDao.insert(f1);
                }
                flag=true;
            }
        }catch (Exception e){
            flag=false;
        }
        return flag;


    }

    /**
     * 查找是否有代理人
     */
    public JsonResult flowAgenCheck(String formId, String nextState, String nextId){
        String agent="";
        Date date = new Date();
        //设置要获取到什么样的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //获取String类型的时间
        String createdate = sdf.format(date);

        List<Map<String, Object>> maps = bakUserDao.selectUserInfoUnionAgent(nextId, createdate);

        if(maps.size()==0){
            return new JsonResult("500","无代理人");
        }
        List list = new ArrayList();
        for(int i=0;i<maps.size();i++){
            if(maps.get(i).get("ent09").equals("ALL") || (maps.get(i).get("ent09").equals(formId.substring(0,6)))){

                list.add(maps.get(i).get("ddepartment_id"));
                list.add(maps.get(i).get("duser_id"));
                list.add(maps.get(i).get("duser_name"));
                list.add(maps.get(i).get("email"));
                break;

            }
        }
        return new JsonResult("200","查找代理人成功",list);
    }



}

