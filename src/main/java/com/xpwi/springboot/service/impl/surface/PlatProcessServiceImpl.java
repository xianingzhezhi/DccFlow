package com.xpwi.springboot.service.impl.surface;

import com.xpwi.springboot.dao.*;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.surface.PlatProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

@Service
public class PlatProcessServiceImpl implements PlatProcessService {

    @Autowired
    PlatMaintainDao platMaintainDao;

    @Autowired
    FlowHisSignoffDao flowHisSignoffDao;

    @Autowired
    FlowBaseSignoffDao flowBaseSignoffDao;

    @Autowired
    BakUserDao bakUserDao;

    @Autowired
    BakPrincipalDao bakPrincipalDao;

    @Autowired
    FlowBaseSignprocessDao flowBaseSignprocessDao;

    @Autowired
    FlowBaseFileinfoDao flowBaseFileinfoDao;




    /**
     * 获取所有签核人员（根据已设定或者未设定显示）
     * @param form_id
     * @param login_user_id
     * @param department_id
     * @return
     */
    public JsonResult getPlatProcessAll(String form_id, String login_user_id,String department_id){


        String isSee="true";//true为查看，false为编 辑

        PlatMaintain platMaintain = platMaintainDao.selectAllByFormId(form_id);
        String file_type = platMaintain.getFormState();
       // String file_type = "0";
       try{
           if(!file_type.equals("3")){ //3:作废
               if(file_type.equals("1")){ //1：送签
                   //查询签核流程信息给前台
                   isSee="true";
               }else{
                   List<FlowHisSignoff> flowHisSignoffs = flowHisSignoffDao.selectAllByFormIdList(form_id);
                   if(flowHisSignoffs.size()==0){

                       isSee=file_type.equals("0")?"false":"true";

                   }else{
                       FlowBaseSignoff flowBaseSignoff = flowBaseSignoffDao.selectAllByFormId(form_id);
                       String create_user =  flowBaseSignoff.getUserId();
                       isSee = create_user.equals(login_user_id)?"false":"true";

                   }
               }
               List list = new ArrayList();
               if(isSee=="false"){
                   List<FlowBaseSignprocess> flowBaseSignprocesses = flowBaseSignprocessDao.selectAllByFormId(form_id);
                   if(flowBaseSignprocesses.size()==0){
                       isSee="N";//未设定流程
                   }else{
                       isSee="Y";//已设定流程
                   }
                   Map map = processInfo(form_id, department_id,flowBaseSignprocesses.size());
                   list.add(map);
               }else{
                   list=showProcessInfo(form_id);
               }


               return new JsonResult("200",isSee, list);
           }else{

               return new JsonResult("323","此文件編號已填寫超過30天未送簽,已被佔用不可以操作！");

           }
       }
       catch (Exception e){
           e.printStackTrace();
            return new JsonResult("500","数据异常！");

        }
    }

    /**
     * 未设定时显示可设定人员
     * @param form_id
     * @param department_id
     * @return
     */
    //图面签核设定
    public Map processInfo(String form_id,String department_id,int size){

//        List result=new ArrayList();
//        List<Map<String, Object>> result=null;
        Map map = new LinkedHashMap();
//        FlowBaseSignoff flowBaseSignoff = flowBaseSignoffDao.selectAllByFormId(form_id);
        if(size==0){

            department_id=department_id.substring(0,4)+"%";
            List<BakUser> bakUsers = bakUserDao.selectE110(department_id);
            String[] E110 = new String[bakUsers.size()];
//              List E110 = new ArrayList();

            for(int i=0;i<bakUsers.size();i++){
//              E110.add(bakUsers.get(i).getProcessInfo());
                E110[i]=bakUsers.get(i).getProcessInfo();
             }
             map.put("one",E110);

        List<BakUser> bakUsers1 = bakUserDao.selectE130();
        String[] E130 = new String[bakUsers1.size()];
        for(int i=0;i<bakUsers1.size();i++){
            E130[i]=bakUsers1.get(i).getProcessInfo();
        }
        map.put("two",E130);

        List<BakPrincipal> bakPrincipals = bakPrincipalDao.selectAllM();
        String[] selectAllM=new String[bakPrincipals.size()];
        for(int i=0;i<bakPrincipals.size();i++){
            selectAllM[i]=bakPrincipals.get(i).getProcessInfo();
        }
        map.put("three",selectAllM);

        List<BakPrincipal> bakPrincipals1 = bakPrincipalDao.selectAllDWG();
        String[] selectAllDWG = new String[bakPrincipals1.size()];
        for(int i=0;i<bakPrincipals1.size();i++){
            selectAllDWG[i]=bakPrincipals1.get(i).getProcessInfo();
        }
        map.put("four",selectAllDWG);

       // System.out.println(map);
        return map;

        }else{
            for(int i=1;i<5;i++){
                String key;
                List<FlowBaseSignprocess> flowBaseSignprocesses1 = flowBaseSignprocessDao.selectByFormIdAndStage(form_id,String.valueOf(i));
                String[] temp = new String[flowBaseSignprocesses1.size()];
                for(int j=0;j<flowBaseSignprocesses1.size();j++){
                    temp[j]=flowBaseSignprocesses1.get(j).getprocessInfo();
                }
                switch (i){
                    case 1:key="one";break;
                    case 2:key="two";break;
                    case 3:key="three";break;
                    case 4:key="four";break;
                    default:key="false";break;
                }
                map.put(key,temp);
            }
            return map;
        }
    }

    /**
     * 已设定时显示
     * @param form_id
     * @return
     */
    //图面签核显示
    public List<FlowBaseSignprocess> showProcessInfo(String form_id){
        List<FlowBaseSignprocess> flowBaseSignprocesses = flowBaseSignprocessDao.selectAllProcessed(form_id);
        return flowBaseSignprocesses;
    }

    /**
     * 根据前台输入值来查询出第二关签核人员
     * @param twoProcessInput
     * @return
     */
    public JsonResult serchTwoProcess(String twoProcessInput){
        try{
            if(twoProcessInput.indexOf("*")!=-1){
                twoProcessInput=twoProcessInput.replace("*","%");
            }else{
                twoProcessInput=twoProcessInput+"%";
            }

            List<BakUser> bakUsers = bakUserDao.selectTwoProcessValue(twoProcessInput);
            List list = new ArrayList();
            for(int i=0;i<bakUsers.size();i++){
                list.add(bakUsers.get(i).getProcessInfo());
            }
            return new JsonResult("200","查询签核第二阶段可签核人员成功！",list);

        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("500","查询数据异常！");
        }
    }

    public JsonResult insertSetProcess(String form_id,String one,String two,String three,String four){

        List<FlowBaseSignprocess> flowBaseSignprocesses = flowBaseSignprocessDao.selectAllByFormId(form_id);
        if(flowBaseSignprocesses.size()!=0){
           flowBaseSignprocessDao.delByFormId(form_id);
        }
        List list = new ArrayList();
        list.add(one);
        list.add(two);
        list.add(three);
        list.add(four);

       try{
           for(int i=0;i<list.size();i++){
               if(list.get(i)==""){
                   continue;
               }
               String[] arrj=list.get(i).toString().split(",");
               for(int j=0;j<arrj.length;j++){
                   String[] arr = arrj[j].split("-");
                   String department_id = arr[0];
                   String user_id = arr[1];
                   String user_name = arr[2];
//            (String form_id,String stage_id,String department_id,String user_id,String user_name);
                   flowBaseSignprocessDao.insertProcess(form_id,String.valueOf(i+1),department_id,user_id,user_name);
               }

           }
           return new JsonResult("200","簽核流程存儲成功!");
       }catch (Exception e){
           e.printStackTrace();
           return new JsonResult("500","数据异常,簽核流程存儲失敗!");
       }
    }

    /**
     * 加签
     */
    public JsonResult addSerProcess(String form_id){
        List<FlowBaseFileinfo> flowBaseFileinfos = flowBaseFileinfoDao.selectAllByFormId(form_id);
        List<FlowBaseSignoff> flowBaseSignoffs = flowBaseSignoffDao.selectAllByFormIdList(form_id);
        if(flowBaseFileinfos.size()==0||flowBaseSignoffs.size()==0){
            return new JsonResult("323","未上傳附件或未勾選會簽人,操作失敗！");
        }
        PlatMaintain platMaintain = platMaintainDao.selectAllByFormId(form_id);
        String state =platMaintain.getFormState();
        if(state.equals("2")){
            return new JsonResult("324","此單據已結案!");
        }
        if(state.equals("3")){
            return new JsonResult("324","此文件編號已填寫超過30天未送簽,已被佔用不可以操作！");
        }
        return new JsonResult("200","初始化成功，可进行加签操作！");
    }
}
