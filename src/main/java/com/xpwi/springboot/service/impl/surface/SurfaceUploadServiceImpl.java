package com.xpwi.springboot.service.impl.surface;

import com.xpwi.springboot.dao.PlatMaintainDao;
import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PlatMaintain;
import com.xpwi.springboot.service.BaseService;
import com.xpwi.springboot.service.surface.SurfaceUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SurfaceUploadServiceImpl implements SurfaceUploadService {
    @Autowired
    private BaseService baseService;
    @Autowired
    private PlatMaintainDao platMaintainDao;

    @Override
    public JsonResult uploadFile(String file_id, MultipartFile file, String file_type, String form_id) {
        PlatMaintain platMaintain=null;
        if(form_id!=null&&!form_id.equals("undefined")){
            platMaintain = this.platMaintainDao.selectAllByFormId(form_id);
            if (!("3".equals(platMaintain.getFormState()))) {//作废状态
                return this.baseService.upfile(file_id,file,file_type,form_id);
            }else{
                return new JsonResult("300","此文件編號已填寫超過30天未送簽,已被佔用不可以操作！");
            }
        }else{
            return new JsonResult("400","表单号为空，请先保存生成表单号再執行上傳附件操作！");
        }
    }
}
