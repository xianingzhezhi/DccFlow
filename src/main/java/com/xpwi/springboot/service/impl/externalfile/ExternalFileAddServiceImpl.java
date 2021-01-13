package com.xpwi.springboot.service.impl.externalfile;

import com.xpwi.springboot.dao.*;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.externalfile.ExternalFileAddService;
import com.xpwi.springboot.service.iso.IsoService;
import com.xpwi.springboot.utils.DateUntil;
import com.xpwi.springboot.utils.NumberDateUtil;
import com.xpwi.springboot.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @program: DccFlow
 * @description: 外来文件批量保存
 * @author: Cool
 * @create: 2020-12-28 15:20
 **/
@Service
public class ExternalFileAddServiceImpl implements ExternalFileAddService {
    @Autowired
    private BakUserDao bakUserDao;

    @Autowired
    private  IsoService isoService;

    @Autowired
    private FlowBaseSignoffDao flowBaseSignoffDao;

    @Autowired
    private FlowHisSignoffDao flowHisSignoffDao;

    @Autowired
    private FlowMaintainIsofilesDao flowMaintainIsofilesDao;

    @Autowired
    private  FlowExternalMessageDao externalMessageDao;

    @Autowired
    private  FlowHisDccFilesDao flowHisDccFilesDao;

    //单个  新增使用
    @Override
    public JsonResult saveMain2(String user_id, String countType, FlowMaintainIsofiles flowMaintainIsofiles) {
        BakUser user = this.bakUserDao.selectAllByUserId(user_id);
        String form_id = flowMaintainIsofiles.getFormId();
        String file_id = flowMaintainIsofiles.getFileId();
        List list = new ArrayList();
        if (user == null) {
            return new JsonResult("400", "當前用戶信息未維護!");
        }
        try{
            // 判断单号 是否为空
            if (StringUtils.checkStrIsNull(form_id)) {
                // 获取系统单号
                // 获取系统单号
                JsonResult target_form = isoService.getNewSysid("flow_maintain_isofiles", "KS-OSO");
                if(target_form.getStatus().equalsIgnoreCase("200")) {
                    form_id = target_form.getData().get(0).toString();
                    list.add(form_id);
                }
                // 获取文件编号
                JsonResult target_file = isoService.getNewSysid("flow_maintain_isofiles",
                        (flowMaintainIsofiles.getFileCode1()+flowMaintainIsofiles.getFileCode2()));
                if(target_file.getStatus().equalsIgnoreCase("200")) {
                    file_id = target_file.getData().get(0).toString();
                    list.add(file_id);
                }
                //签核流程表插入一笔数据   ,创建人信息!
                FlowBaseSignoff flowBaseSignoff
                        = new FlowBaseSignoff(form_id, "10", flowMaintainIsofiles.getDepartmentId(),
                        flowMaintainIsofiles.getCreateUserId(), flowMaintainIsofiles.getCreateUser(),
                        "N", "新增申請", DateUntil.df.format(new Date()));
                flowBaseSignoffDao.insert(flowBaseSignoff);
            }else{
                list.add(form_id);
                // 判断点保存按钮的是不是申请人  ,不是则 驳回
                FlowHisSignoff flowHisSignoff = flowHisSignoffDao.selectAllByFormIdAndStage(new FlowHisSignoff(form_id, "10"));
                if (flowHisSignoff != null) {
                    FlowBaseSignoff flowBaseSignoff = flowBaseSignoffDao.selectByFormIdAndState(new FlowBaseSignoff(form_id, "10"));
                    if (!flowBaseSignoff.getUserId().equals(user_id)) {
                        return new JsonResult("400", "駁回單據,應由申請者修改,操作失敗！");
                    }
                }
                FlowMaintainIsofiles flowMaintainIsofiles1 = flowMaintainIsofilesDao.selectByPrimaryKey(form_id);
                if (flowMaintainIsofiles1!=null) {
                    System.out.println(flowMaintainIsofiles1);
                    System.out.println(flowMaintainIsofiles1.getRouterStage());
                    if (flowMaintainIsofiles1.getRouterStage().equals("1")) {
                        return new JsonResult("400", "此單據正在送核中，不能修改，操作失敗！");
                    }
                    if (flowMaintainIsofiles1.getRouterStage().equals("2")) {
                        return new JsonResult("400", "此單據已結案,不可操作！");
                    }
                    if (flowMaintainIsofiles1.getRouterStage().equals("3")) {
                        return new JsonResult("400", "此文件編號已填寫超過30天未送簽,已被佔用不可以操作！");
                    }
                }
                String substring1 = flowMaintainIsofiles.getFileId().substring(0, 1);
                String substring2 = flowMaintainIsofiles.getFileId().substring(1, 2);
                if (!substring1.equals(flowMaintainIsofiles.getFileCode1())  || !substring2.equals(flowMaintainIsofiles.getFileCode2())  ) {
                    if(countType.equals("2") || countType.equals("3")){
                        flowMaintainIsofilesDao.deleteByPrimaryKey(form_id);
                        flowBaseSignoffDao.deleteByFormId(form_id);
                        return new JsonResult("550", "此文件不是外來文件，此單據已刪除！");
                    }
                    else{
                        // 获取文件编号
                        JsonResult target_file = isoService.getNewSysid("flow_maintain_isofiles",
                                (flowMaintainIsofiles.getFileCode1()+flowMaintainIsofiles.getFileCode2()));
                        if(target_file.getStatus().equalsIgnoreCase("200")) {
                            file_id = target_file.getData().get(0).toString();
                            list.add(file_id);
                        }
                    }
                }else {
                    list.add(file_id);
                }
            }
            if (countType.equals("1")){
                flowMaintainIsofiles.setRouterStage2("0");
            }if (countType.equals("2")){
                flowMaintainIsofiles.setRouterStage2("1");
            }
            if (countType.equals("3")){
                flowMaintainIsofiles.setRouterStage2("2");
            }
            FlowMaintainIsofiles check_exit = flowMaintainIsofilesDao.selectByPrimaryKey(form_id);
            flowMaintainIsofiles.setFormId(form_id);
            flowMaintainIsofiles.setRouterStage("0");
            flowMaintainIsofiles.setFileType("O");
            flowMaintainIsofiles.setFileId(file_id);
            flowMaintainIsofiles.setCreateDate(new Date());
            if (check_exit!=null) {
                this.flowMaintainIsofilesDao.updateByPrimaryKeySelective(flowMaintainIsofiles);
            } else {
                this.flowMaintainIsofilesDao.insert(flowMaintainIsofiles);
            }
            List<FlowBaseSignoff> flowBaseSignoffList = this.flowBaseSignoffDao.selectsByFormIdAndState(form_id, "10");
            FlowHisSignoff flowHisSignoff = this.flowHisSignoffDao.selectAllByFormId(form_id);
            if (flowHisSignoff == null) {//無駁回
                if (flowMaintainIsofiles.getCreateUserId() != flowBaseSignoffList.get(0).getUserId()) {
                    FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(flowMaintainIsofiles.getDepartmentId(),
                            flowMaintainIsofiles.getCreateUserId(), flowMaintainIsofiles.getCreateUser(), flowMaintainIsofiles.getFormId(), flowMaintainIsofiles.getRouterStage());
                    this.flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                }
            }
            return new JsonResult(list);
        } catch (Exception e){
            e.printStackTrace();
            return new JsonResult("500", e.toString());
        }
    }

    @Override
    public JsonResult FileInitialization(String form_id) {
        try{
            FlowMaintainIsofiles  flowMaintainIsofiles= this.flowMaintainIsofilesDao.selectByPrimaryKey(form_id);
            List list = new ArrayList();
            list.add(flowMaintainIsofiles);
            return  new JsonResult(list);
        }catch(Exception e){
            e.printStackTrace();
            return  new JsonResult("500","初始化失败");
        }
    }

    @Override
    public JsonResult FileInitialization2(String form_id) {
        try{
            List<FlowExternalMessage> flowExternalMessageList = this.externalMessageDao.selectAllByFormId(form_id);
            return  new JsonResult(flowExternalMessageList);
        }catch(Exception e){
            e.printStackTrace();
            return  new JsonResult("500","行信息初始化失败");
        }
    }

    //保存  //批量新增使用
    @Override
    public JsonResult saveMain(String user_id,String countType, FlowMaintainIsofiles flowMaintainIsofiles) {
        BakUser user = this.bakUserDao.selectAllByUserId(user_id);
        String form_id = flowMaintainIsofiles.getFormId();

        List list = new ArrayList();
        if (user == null) {
            return new JsonResult("400", "當前用戶信息未維護!");
        }
        try{
            // 判断单号 是否为空
            if (StringUtils.checkStrIsNull(form_id)) {
                // 获取系统单号
                // 获取系统单号
                JsonResult target_form = isoService.getNewSysid("flow_maintain_isofiles", "KS-OSO");
                if(target_form.getStatus().equalsIgnoreCase("200")) {
                    form_id = target_form.getData().get(0).toString();
                    list.add(form_id);
                }

                //签核流程表插入一笔数据   ,创建人信息!
                FlowBaseSignoff flowBaseSignoff
                        = new FlowBaseSignoff(form_id, "10", flowMaintainIsofiles.getDepartmentId(),
                        flowMaintainIsofiles.getCreateUserId(), flowMaintainIsofiles.getCreateUser(),
                        "N", "新增申請", DateUntil.df.format(new Date()));
                flowBaseSignoffDao.insert(flowBaseSignoff);
            }else{
                list.add(form_id);
                // 判断点保存按钮的是不是申请人  ,不是则 驳回
                FlowHisSignoff flowHisSignoff = flowHisSignoffDao.selectAllByFormIdAndStage(new FlowHisSignoff(form_id, "10"));
                if (flowHisSignoff != null) {
                    FlowBaseSignoff flowBaseSignoff = flowBaseSignoffDao.selectByFormIdAndState(new FlowBaseSignoff(form_id, "10"));
                    if (!flowBaseSignoff.getUserId().equals(user_id)) {
                        return new JsonResult("400", "駁回單據,應由申請者修改,操作失敗！");
                    }
                }
                FlowMaintainIsofiles flowMaintainIsofiles1 = flowMaintainIsofilesDao.selectByPrimaryKey(form_id);
                if (flowMaintainIsofiles1!=null) {
                    System.out.println(flowMaintainIsofiles1);
                    System.out.println(flowMaintainIsofiles1.getRouterStage());
                    if (flowMaintainIsofiles1.getRouterStage().equals("1")) {
                        return new JsonResult("400", "此單據正在送核中，不能修改，操作失敗！");
                    }
                    if (flowMaintainIsofiles1.getRouterStage().equals("2")) {
                        return new JsonResult("400", "此單據已結案,不可操作！");
                    }
                    if (flowMaintainIsofiles1.getRouterStage().equals("3")) {
                        return new JsonResult("400", "此文件編號已填寫超過30天未送簽,已被佔用不可以操作！");
                    }
                }

            }
            if (countType.equals("1")){
                flowMaintainIsofiles.setRouterStage2("0");
            }if (countType.equals("2")){
                flowMaintainIsofiles.setRouterStage2("1");
            }
            if (countType.equals("3")){
                flowMaintainIsofiles.setRouterStage2("2");
            }
            FlowMaintainIsofiles check_exit = flowMaintainIsofilesDao.selectByPrimaryKey(form_id);
            flowMaintainIsofiles.setFormId(form_id);
            flowMaintainIsofiles.setRouterStage("0");
            flowMaintainIsofiles.setFileType("O");

            flowMaintainIsofiles.setCreateDate(new Date());
            if (check_exit!=null) {
                this.flowMaintainIsofilesDao.updateByPrimaryKeySelective(flowMaintainIsofiles);
            } else {
                this.flowMaintainIsofilesDao.insert(flowMaintainIsofiles);
            }
            List<FlowBaseSignoff> flowBaseSignoffList = this.flowBaseSignoffDao.selectsByFormIdAndState(form_id, "10");
            FlowHisSignoff flowHisSignoff = this.flowHisSignoffDao.selectAllByFormId(form_id);
            if (flowHisSignoff == null) {//無駁回
                if (flowMaintainIsofiles.getCreateUserId() != flowBaseSignoffList.get(0).getUserId()) {
                    FlowBaseSignoff flowBaseSignoff = new FlowBaseSignoff(flowMaintainIsofiles.getDepartmentId(),
                            flowMaintainIsofiles.getCreateUserId(), flowMaintainIsofiles.getCreateUser(), flowMaintainIsofiles.getFormId(), flowMaintainIsofiles.getRouterStage());
                    this.flowBaseSignoffDao.updateSignoff(flowBaseSignoff);
                }
            }
            return new JsonResult(list);
        } catch (Exception e){
            e.printStackTrace();
            return new JsonResult("500", e.toString());
        }
    }

    @Override
    public JsonResult externalFileRowSave(FlowExternalMessage[] flowExternalMessage_arr, String form_id, String countType) {
       List list=new ArrayList();
        FlowExternalMessage flowExternalMessage = null;
        //保存所有行先刪除以前舊資料重新做保存
        externalMessageDao.delete(flowExternalMessage_arr[0].getFormId());
        for(int i = 0 ;i< flowExternalMessage_arr.length; i++ ){
            flowExternalMessage = flowExternalMessage_arr[i];
            try {

                if(StringUtils.checkStrIsNull(flowExternalMessage.getFileName())||StringUtils.checkStrIsNull(flowExternalMessage.getFileId()) ){
                    continue;
                }
                FlowHisDccFiles flowHisDccFiles = this.flowHisDccFilesDao.selectByFileNameLike(flowExternalMessage.getFileName());
                if (flowHisDccFiles != null ){
                    String str_err = "第" + (i+1) + "行文件名稱已存在,操作失敗!";
                    return new JsonResult("400",str_err);
                }
                flowExternalMessage.setFormId(form_id);
                list.add(form_id);
                externalMessageDao.insert(flowExternalMessage);
            } catch (Exception e) {
                e.printStackTrace();
                return new JsonResult("500", e.toString());
            }
        }
        return new JsonResult("200","保存成功",list);
    }
}
