package com.xpwi.springboot.controller.pqc;

import com.xpwi.springboot.dao.PqcLineDao;
import com.xpwi.springboot.dao.PqcSubDao;
import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PqcHisList;
import com.xpwi.springboot.model.PqcHisListDetail;
import com.xpwi.springboot.service.pqc.PqcSignOffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(value = "desc of class",tags = "PQC签核")
@RestController
@CrossOrigin
@RequestMapping("pqc")
public class PqcSignOffController {
    @Autowired
    private PqcSignOffService pqcSignOffService;
    @Autowired
    private PqcLineDao pqcLineDao;
    @Autowired
    private PqcSubDao pqcSubDao;

    @ApiOperation(value = "PQC待签核查询", notes = "")
    @PostMapping("/toBeSigned")
    public JsonResult getToBeSigned(@ApiParam(value = "查询条件" , required=false ) PqcHisList pqcHisList){
        return this.pqcSignOffService.selectPqcAfterForm(pqcHisList);
    }

    @ApiOperation(value = "组长修改(送核)", notes = "")
    @PostMapping("/qcUpdate")
    public JsonResult qcUpdate(@ApiParam(value = "修改的表单内容" , required=false ) PqcHisListDetail PqcHisListDetail,String countType){
        return  this.pqcSignOffService.updateQcForm(PqcHisListDetail,countType);
    }

    @ApiOperation(value = "查看表单详情", notes = "")
    @PostMapping("/getFormDetail")
    public JsonResult getPQCFormDetail(@ApiParam(value = "表单号" , required=true ) @RequestParam String listNo){
        return this.pqcSignOffService.selectPqcFormDetail(listNo);
    }
    @ApiOperation(value = "查看表单详情(已回复的不再查看)", notes = "")
    @PostMapping("/getFormDetailSuggetIsNull")
    public JsonResult getPQCFormDetailSuggetIsNull(@ApiParam(value = "表单号" , required=true ) @RequestParam String listNo,
                                                   @ApiParam(value = "picUser" , required=true ) @RequestParam String picUser){
        return this.pqcSignOffService.selectByListNoAndSuggetIsNull(listNo,picUser);
    }

    @ApiOperation(value = "PD/PIC待回复查询", notes = "")
    @PostMapping("/toSuggestFrom")
    public JsonResult getToSuggestFrom(@ApiParam(value = "查询条件" , required=false ) PqcHisList pqcHisList){
        return this.pqcSignOffService.selectPqcForm(pqcHisList);
    }
    @ApiOperation(value = "回复", notes = "")
    @PostMapping("/doReply")
    public JsonResult updatePQCReply(@ApiParam(value = "回复内容" , required=false ) PqcHisListDetail PqcHisListDetail){
        return this.pqcSignOffService.updatePqcAfterForm(PqcHisListDetail);
    }
    @GetMapping("/getSelect")
    public JsonResult getSelect(){
        List list=new ArrayList();
        list.add(this.pqcLineDao.selectAll());
        list.add(this.pqcSubDao.selectAll());
        return new JsonResult("200","查询成功",list);
    }
}
