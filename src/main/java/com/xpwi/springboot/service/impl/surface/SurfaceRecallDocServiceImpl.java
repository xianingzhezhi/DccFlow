package com.xpwi.springboot.service.impl.surface;

import com.xpwi.springboot.dao.FlowBaseSignoffDao;
import com.xpwi.springboot.dao.FlowBaseUserDao;
import com.xpwi.springboot.dao.FlowHisSignoffDao;
import com.xpwi.springboot.dao.PlatMaintainDao;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.EmailService;
import com.xpwi.springboot.service.surface.SurfaceRecallDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SurfaceRecallDocServiceImpl  implements SurfaceRecallDocService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private FlowBaseUserDao flowBaseUserDao;
    @Autowired
     private PlatMaintainDao platMaintainDao;
    @Autowired
    private FlowBaseSignoffDao flowBaseSignoffDao;
    @Autowired
    private FlowHisSignoffDao flowHisSignoffDao;
    @Override
    public JsonResult recallDoc(String user_id, String form_id) {
        FlowBaseUserKey flowBaseUserKey =new FlowBaseUserKey(user_id,"dcc");
        FlowBaseUser user = this.flowBaseUserDao.selectByPrimaryKey(flowBaseUserKey);
        System.out.println("测试form_id=="+form_id);
        List<String> userids = this.platMaintainDao.selectByFormIdAndUserId(form_id, user_id);
        PlatMaintain platMaintain =new PlatMaintain();
        platMaintain.setFormId(form_id);
        platMaintain.setFormState("1");//送签状态
        //判断单号是否存在且处于送签状态
        List<PlatMaintain> platMaintains = this.platMaintainDao.selectByPlatMaintain(platMaintain);
        if(platMaintains.size()==0){
            String message="此單據編碼" + form_id + "不存在或者單據狀態不正確,操作失敗!";
            return new JsonResult("300",message);
        }
        if(userids.size()==0){//比对单号创建的用户id和登陆的用户id
            return new JsonResult("300","抽單操作應為申請人或操作人,操作失敗!");
        }
         try {
             //根据表单号和状态为送签中的条件查询flowBaseSignoff
             List<FlowBaseSignoff> list = this.flowBaseSignoffDao.selectByFormIdAndFlag(form_id,"Y");
             //获取当前时间，并指定格式
             SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");//设置日期格式
             String sys_time = df.format(new Date()).toString();
             //駁回將已簽核的資料插圖入his_sign_off表中
             if(list.size()>0){
                 for (FlowBaseSignoff flowBaseSignoff : list) {
                     FlowHisSignoff flowHisSignoff=new FlowHisSignoff(form_id,flowBaseSignoff.getState(),
                             flowBaseSignoff.getDeptId(),flowBaseSignoff.getUserId(),
                             flowBaseSignoff.getUserName(),flowBaseSignoff.getFlag(),
                             flowBaseSignoff.getMessage(),sys_time,"R","Y");
                     System.out.println("测试=======flowBaseSignoff"+flowHisSignoff);
                     int count = this.flowHisSignoffDao.insert(flowHisSignoff);
                 }
             }
             //更新基础表信息
             PlatMaintain obj=new  PlatMaintain();
             obj.setFormId(form_id);
             obj.setFormState("0");//更新状态
             this.platMaintainDao.update(obj);
             //删除flowBaseSignoff对应的信息  state!=10
             this.flowBaseSignoffDao.deleteByFormIdAndState(form_id,"10");
             FlowBaseSignoff signoff=new FlowBaseSignoff(form_id, "10", user.getDepartmentId(), user.getUserId(), user.getUserName(),
                     "N", "抽單重送", sys_time);
             System.out.println("测试=="+signoff);
             int count = this.flowBaseSignoffDao.updateSignoff(signoff);
             if(count>0){
                 return new JsonResult("200","抽單成功！");
             }else {
                 String message="圖面申請(" + form_id + ")抽單失敗,請檢查數據！";
                 return new JsonResult("300",message);
             }

         }catch (Exception e){
             Email email=new Email();
             email.setIs_mail("0");
             email.setMail("O20110001@wistron.com");
             email.setNo(form_id);
             email.setMessage("異常信息"+e.toString());
             email.setTheme("圖面抽單功能出現異常！");
             e.printStackTrace();
             this.emailService.insertEmailReject(email);
             return new JsonResult("500","抽單操作失敗,請聯繫資訊！");
         }

    }
}
