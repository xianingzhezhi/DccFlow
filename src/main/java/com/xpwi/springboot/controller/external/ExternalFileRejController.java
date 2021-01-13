package com.xpwi.springboot.controller.external;


import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.EmailService;
import com.xpwi.springboot.service.UserService;
import com.xpwi.springboot.service.iso.*;
import com.xpwi.springboot.utils.NumberDateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
/**
 * 抽单,驳回
 */
@Api(value = "desc of class")
@RestController
@CrossOrigin
@RequestMapping("external")
public class ExternalFileRejController {
    @Autowired
    private UserService userService;

    @Autowired
    private FromService fromService;

    @Autowired
    private Sign_offService sign_offService;

    @Autowired
    private Flow_Rej_offService flow_rej_offService;

    @Autowired
    private IsoService isoService;

    @Autowired
    private Flow_agentService flow_agentService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FileInfoService fileInfoService;

    @Autowired
    private FlowBaseSignprocessService flowBaseSignprocessService;

    @Autowired
    private FlowAddpeopleService flowAddpeopleService;
    /**
     *
     * @param form_id     表单编号
     * @param file_id     文件编号
     * @param file_name    文件名称
     * @param user_id    登陆user的工号
     * @param int_CancelType  //驳回类型，申请人或者上一关，1代表
     * @param rejectIdea   驳回意见
     * @return
     */
    @ApiOperation(value = "Iso驳回", notes = "")
    @PostMapping("/externalReject")
    @Transactional
    public JsonResult isoReject(String form_id, String file_id, String file_name, String user_id, int int_CancelType, String rejectIdea) {
        User user=this.userService.getUsersByUserid(user_id);
        List<Flow_agent> flow_agentList=new ArrayList<>();
        int countEmail=0;//判断是否驳回成功，并发送邮件
        //驳回使用，查询此单号是否存在并且状态为送签中
        IsoForm isoForm2=new IsoForm();
        isoForm2.setForm_id(form_id);
        isoForm2.setRouter_stage("1");
        IsoForm isoForm = this.fromService.selectFromByFile_id(isoForm2);
        if (!isoForm.getRouter_stage().equals("1") ||isoForm==null) {
            String message = "此單據編碼" + form_id + "不存在或者單據狀態不正確,操作失敗";
            return new JsonResult("400", message);
        }
        Sign_off sign_off=new Sign_off();
        sign_off.setForm_id(form_id);
        sign_off.setFlag("N");
        Sign_off sign_off1=this.sign_offService.findSign_offJoinUser(sign_off);
        if (sign_off1==null){
            String message = "此單據編碼" + form_id + "不存在或者單據狀態不正確,操作失敗";
            return new JsonResult("400", message);
        }
        if (!user_id.equals(sign_off1.getUser_id())){
            String str_return = "您的工號為" + user_id + "此單據" + form_id + "流程操作人應為" + sign_off1.getUser_id() + ",操作失敗!";
            return new JsonResult("400", str_return);
        }
        Flow_agent flow_agentX=new Flow_agent();
        if (int_CancelType==1){//駁回至申謝人
            Sign_off sign_off2=new Sign_off();
            sign_off2.setForm_id(form_id);
            sign_off2.setState(sign_off1.getState());
            List<Sign_off> sign_offList=this.sign_offService.findSign_off(sign_off2);
            //获取当前时间，并指定格式
            String sys_time = NumberDateUtil.getNewDate();
            int count=0;
            if (sign_offList.size()>0){  //插入駁回記錄檔
                for (Sign_off signOff : sign_offList) {
                    Flow_Rej_off flow_rej_off=new Flow_Rej_off(form_id,signOff.getState(),signOff.getDept_id()
                            ,signOff.getUser_id(),signOff.getUser_name(),
                            signOff.getFlag(),signOff.getMessage(),sys_time,"N","N");
                    count=this.flow_rej_offService.insertRejOff(flow_rej_off);
                }
            }
            if (count>0){
                Flow_Rej_off flow_rej_off=new Flow_Rej_off();
                flow_rej_off.setForm_id(form_id);
                flow_rej_off.setStage("10");
                flow_rej_off.setRej09("N");
                flow_rej_off.setRej10("N");
                Flow_Rej_off flow_rej_off1= this.flow_rej_offService.findRejOff(flow_rej_off);
                if (flow_rej_off1==null){
                    return new JsonResult("500", "駁回操作失敗,請聯繫資訊！！");
                }else {
                    //查找有無代理人
                    Flow_agent flow_agent=new Flow_agent();
                    flow_agent.setUser_id(flow_rej_off1.getCreate_user_id());
                    flow_agent.setFlag("Y");
                    flow_agentList=this.flow_agentService.findAll(flow_agent);
                    //没有代理人
                    if (flow_agentList.size()==0){
                        //拒絕關插入rej
                        rejectIdea="駁回至申請人"+rejectIdea;
                        Flow_Rej_off flow_rej_off2=new Flow_Rej_off(form_id,sign_off1.getState(),
                                sign_off1.getDept_id(),sign_off1.getUser_id(),sign_off1.getUser_name(),
                                "Y",rejectIdea,sys_time,"Y","N");
                        int count05=this.flow_rej_offService.insertRejOff(flow_rej_off);
                        //更新簽核檔
                        Sign_off sign_off3=new Sign_off();
                        sign_off3.setForm_id(form_id);
                        sign_off3.setFlag("N");
                        sign_off3.setMessage("駁回重送");
                        sign_off3.setSys_time(sys_time);
                        sign_off3.setState(flow_rej_off1.getStage());
                        this.sign_offService.updateSign_off(sign_off3);
                        //刪除簽核記錄
                        Sign_off sign_off4=new Sign_off();
                        sign_off4.setForm_id(form_id);
                        sign_off4.setState("10");
                        int count4 = this.sign_offService.deleteSign_off(sign_off4);
                        ////更新表單狀態
                        IsoForm isoForm1=new IsoForm();
                        isoForm1.setForm_id(form_id);
                        isoForm1.setRouter_stage("0");
                        this.isoService.updateIsoFrom(isoForm1);

                    }else{ //有代理人
                        for (Flow_agent flowAgent : flow_agentList) {
                            if (flowAgent.getEnt09()=="ALL" || flowAgent.getEnt09()==form_id.substring(0,6)){
                                flow_agentX=flowAgent;
                            }
                        }
                        //拒絕關插入rej
                        rejectIdea="駁回至申請人"+rejectIdea;
                        Flow_Rej_off flow_rej_off2=new Flow_Rej_off(form_id,sign_off1.getState(),
                                sign_off1.getDept_id(),sign_off1.getUser_id(),sign_off1.getUser_name(),
                                "Y",rejectIdea,sys_time,"Y","N");
                        int count05=this.flow_rej_offService.insertRejOff(flow_rej_off);
                        //更新簽核檔,此处与无代理人有区别
                        Sign_off sign_off3=new Sign_off();
                        sign_off3.setForm_id(form_id);
                        sign_off3.setState(flow_rej_off1.getStage());
                        sign_off3.setUser_id(flow_agentX.getDuser_id());
                        sign_off3.setUser_name(flow_agentX.getDuser_name());
                        sign_off3.setDept_id(flow_agentX.getDdepartment_id());
                        sign_off3.setFlag("N");
                        sign_off3.setMessage("駁回重送");
                        sign_off3.setSys_time(sys_time);
                        sign_off3.setState(flow_rej_off1.getStage());
                        this.sign_offService.updateSign_off(sign_off3);
                        //刪除簽核記錄
                        Sign_off sign_off4=new Sign_off();
                        sign_off4.setForm_id(form_id);
                        sign_off4.setState("10");
                        int count4 = this.sign_offService.deleteSign_off(sign_off4);
                        ////更新表單狀態
                        IsoForm isoForm1=new IsoForm();
                        isoForm1.setForm_id(form_id);
                        isoForm1.setRouter_stage("0");
                        this.isoService.updateIsoFrom(isoForm1);
                    }

                }
            }else{
                return new JsonResult("500", "驳回失敗,請聯繫資訊！！");
            }

            //所有已簽核人員發送mail
            Flow_Rej_off flow_rej_off=new Flow_Rej_off();
            flow_rej_off.setForm_id(form_id);
            flow_rej_off.setRej09("N");
            flow_rej_off.setRej10("N");
            List<Flow_Rej_off> flow_rej_offList=this.flow_rej_offService.findRejOffAll(flow_rej_off);
            String url="";
            String email="";
            String message="";
            String theme="";
            message="<font size=2pt face=新明細體>表單編號: "+form_id+
                    "<br/>文件編號: " + file_id + "<br/>文件名稱: " + file_name;
            message=message+"<br/>申請部門: " + isoForm.getDepartment_name() + "<br/>申請人員: " + isoForm.getCreate_user();
            message=message+"<br/>簽核意見: [駁回] <br></br>駁回意見: " + rejectIdea;
            message=message+"<br/>收件時間: " + sys_time;
            Email emailDoc=new Email();
            for (Flow_Rej_off flowRejOff : flow_rej_offList) {
                if (flowRejOff.getStage()=="10"){
                    if (flow_agentList.size()>0){//原申請人有設置代理人
                        url="";
                        email=flow_agentX.getEmail();

                    }else{//无代理人
                        //发送给flowRejOff
                        url="";
                        email=flowRejOff.getEmail();
                    }
                }else{
                    url="";
                    email=flowRejOff.getEmail();
                }
                emailDoc.setIs_mail("0");
                emailDoc.setMessage(message);
                emailDoc.setUrl(url);
                //后期改为email
                emailDoc.setMail(email);
                theme="ISO文件申請表("+form_id+")被用戶"+user.getUser_name()+"駁回";
                emailDoc.setTheme(theme);
                emailDoc.setCreate_user(user.getUser_name());
                countEmail=this.emailService.insertEmailReject(emailDoc);
            }
        }

        if (countEmail>0){
            return new JsonResult("200", "驳回成功！！");
        }else{
            return new JsonResult("500", "驳回失敗,請聯繫資訊！！");
        }
    }

}
