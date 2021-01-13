package com.xpwi.springboot.service.impl.pqc;

import com.xpwi.springboot.dao.*;
import com.xpwi.springboot.model.*;
import com.xpwi.springboot.service.pqc.PqcSignOffService;
import com.xpwi.springboot.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

/**
 * @program: DccFlow
 * @description: pqc签核
 * @author: Cool
 * @create: 2020-12-19 09:51
 **/
@Service
public class PqcSignOffServiceImpl implements PqcSignOffService {
    @Autowired
    private PqcHisListDao pqcHisListDao;
    @Autowired
    private PqcHisListDetailDao pqcHisListDetailDao;

    @Autowired
    private SendEmailDao sendEmailDao;

    @Autowired
    private BakUserDao bakUserDao;
    //查看所有待签核表单
    @Override
    @Transactional
    public JsonResult selectPqcAfterForm(PqcHisList pqcHisList) {
        try{
            List<PqcHisList> pqcHisLists = this.pqcHisListDao.selectByPqcHisList(pqcHisList);
            return new JsonResult("200","查询成功",pqcHisLists);
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","查询失败");
        }

    }



    //PD、PIC待回复查询
    @Override
    public JsonResult selectPqcForm(PqcHisList pqcHisList) {
        try{
            List<PqcHisList> pqcHisLists =this.pqcHisListDao.selectPqcHisList(pqcHisList);
            return new JsonResult("200","查询成功",pqcHisLists);
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","查询失败");
        }
    }

    //查看单个表单详细信息
    @Override
    @Transactional
    public JsonResult selectPqcFormDetail(String listNo) {
        try{
            List<PqcHisListDetail> pqcHisListDetail = this.pqcHisListDetailDao.selectByPrimaryKey(listNo);
            return new JsonResult("200","查询成功",pqcHisListDetail);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("500","查询失败");
        }
    }

    @Override
    public JsonResult selectByListNoAndSuggetIsNull(String listNo,String picUser) {
        try {
            List<PqcHisListDetail> pqcHisListDetailList = this.pqcHisListDetailDao.selectByListNoAndSuggetIsNull(listNo,picUser);
            return new JsonResult("200","查询成功",pqcHisListDetailList);
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","查询失败");
        }
    }

    //签核表单
    @Override
    @Transactional
    public JsonResult updatePqcAfterForm(PqcHisList pqcHisList) {
        return null;
    }
    //组长修改并送核
    @Override
    @Transactional
    public JsonResult updateQcForm(PqcHisListDetail pqcHisListDetail,String countType) {
        try{
            String message="修改成功";
            if (countType.equals("1")){
                List<PqcHisListDetail> pqcHisListDetails = this.pqcHisListDetailDao.selectByPrimaryKey(pqcHisListDetail.getListNo());
                for (PqcHisListDetail listDetail : pqcHisListDetails) {
                    BakUser bakUser = this.bakUserDao.selectAllByUserId(listDetail.getPicUser());
                    Email email=new Email();
                    email.setNo(pqcHisListDetail.getListNo());
                    email.setIs_mail("0");
                    email.setUrl(StringUtils.url_ui + "/#/flow/pqc/reply");
                    email.setMail("O20110004@wistron.com");
                    //email.setMail(bakUser.getEmail());
                    email.setMessage("您有一份PQC表单待签核");
                    email.setTheme("PQC表单待签核通知");
                    email.setType("pqc");
                    this.sendEmailDao.insert(email);
                }
                this.pqcHisListDao.updatePqcStatus(pqcHisListDetail.getListNo(),"2");
                message="送核成功";
            }else {
                int count=this.pqcHisListDetailDao.updatePqcHisListDetail(pqcHisListDetail);
            }
            return new JsonResult("200", message);
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","数据连接失败，请联系IT");
        }
    }


    //改善对策回复
    @Override
    @Transactional
    public JsonResult updatePqcAfterForm(PqcHisListDetail pqcHisListDetail) {
        try{
            this.pqcHisListDetailDao.updatePqcHisListDetail(pqcHisListDetail);
            int count =this.pqcHisListDetailDao.selectSuggestCount(pqcHisListDetail.getListNo());
            if (count!=0){//还有未回复的
                this.pqcHisListDao.updatePqcStatus(pqcHisListDetail.getListNo(),"3");
            }else {//全部已回复
                this.pqcHisListDao.updatePqcStatus(pqcHisListDetail.getListNo(),"4");
            }
            return new JsonResult("200","回复成功");
        }catch(Exception e){
            e.printStackTrace();
            return new JsonResult("500","数据连接失败，请联系IT");
        }

    }
}
