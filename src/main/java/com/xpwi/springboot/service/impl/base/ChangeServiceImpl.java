package com.xpwi.springboot.service.impl.base;

import com.xpwi.springboot.dao.BakUserDao;
import com.xpwi.springboot.dao.FlowBaseSignoffDao;
import com.xpwi.springboot.dao.FlowHisDccFilesDao;
import com.xpwi.springboot.dao.FlowMaintainIsofilesDao;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.base.ChangeService;
import com.xpwi.springboot.service.iso.IsoService;
import com.xpwi.springboot.utils.NumberDateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: DccFlow
 * @description: 文件变更   作废
 * @author: Cool
 * @create: 2021-01-04 14:58
 **/
@Service
public class ChangeServiceImpl implements ChangeService {
    @Autowired
    private FlowMaintainIsofilesDao flowMaintainIsofilesDao;

    @Autowired
    private BakUserDao bakUserDao;
    @Autowired
    private IsoService isoService;

    @Autowired
    private  FlowHisDccFilesDao flowHisDccFilesDao;

    @Autowired
    private FlowBaseSignoffDao flowBaseSignoffDao;




    @Override
    public JsonResult scrapReasonChange(String user_id, String file_id, String file_name, String version, String scrapReason, String update_before, String update_after, int countType) {
        List list=new ArrayList();
        try{


        //获去登录用户信息
        BakUser bakUser=this.bakUserDao.selectAllByUserId(user_id);
        //从大文件表中 获取最新的数据的from_id
        String form_id=this.flowMaintainIsofilesDao.selectMax(file_id,file_name);
        //版次加一
        String str_ver = "A;B;C;D;E;F;G;H;I;J;K;L;M;N;O;P;Q;R;S;T;U;V;W;X;Y;Z;a;b;c;d;e;f;g;h;i;j;k;l;m;n;o;p;q;r;s;t;u;v;w;x;y;z;0;1;2;3;4;5;6;7;8;9";
        String[] str_array1 = str_ver.split(";");
        String versionX = "";
        for (int i = 0; i < str_array1.length; i++) {
            if (str_array1[i].equals(version)) {
                versionX = str_array1[i + 1];
                break;
            }
        }
        //生成文件编号
        JsonResult   jsonResult1 = isoService.getNewSysid("flow_maintain_isofiles", "KS-ISO");
        String form_idd = "";
        if (jsonResult1.getStatus() == "200") {
            form_idd = jsonResult1.getData().get(0).toString();
            System.out.println(form_idd);
        }
        List<FlowHisDccFiles> flowHisDccFilesList = this.flowHisDccFilesDao.selectByHisDccFiles(new FlowHisDccFiles(file_id, version));
        String fileFrom ="";
        String fileFlag ="";
        String fileNo ="";
        String fileVersion ="";
        String supplyPaper ="";
        if (flowHisDccFilesList.size()>0){
           fileFrom =flowHisDccFilesList.get(0).getFileFrom();
            fileFlag =flowHisDccFilesList.get(0).getFileFlag();
           fileNo =flowHisDccFilesList.get(0).getFileNo();
           fileVersion =flowHisDccFilesList.get(0).getFileVersion();
            supplyPaper =flowHisDccFilesList.get(0).getSupplyPaper();

        }
        FlowMaintainIsofiles flowMaintainIsofiles=new FlowMaintainIsofiles(form_idd,file_id,
                file_name,bakUser.getDepartmentId(),bakUser.getDepartmentName(),
                bakUser.getUserId(),bakUser.getUserName(),
                versionX,file_id.substring(0,1),file_id.substring(1,2),
                fileFrom,scrapReason,update_before,update_after,"O",new Date(),"0");

        this.flowMaintainIsofilesDao.insert(flowMaintainIsofiles);
        String sys_time= NumberDateUtil.getNewDate();
        this.flowBaseSignoffDao.insert(new FlowBaseSignoff(form_idd,"10",bakUser.getDepartmentId(),
                bakUser.getUserId(),bakUser.getUserName(),"N","變更申請",sys_time));
        list.add(form_idd);
        return new JsonResult(list);
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","数据库连接失败,请联系IT");
        }
    }
}
