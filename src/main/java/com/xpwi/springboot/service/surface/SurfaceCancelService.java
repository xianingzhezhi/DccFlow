package com.xpwi.springboot.service.surface;

import com.xpwi.springboot.model.JsonResult;
import com.xpwi.springboot.model.PlatData;

import java.util.List;

/**
 * @program: idea_server
 * @description: 圖面作廢
 * @author: PengFei_Ge
 * @create: 2020-12-16 15:01
 **/
public interface SurfaceCancelService {
    JsonResult getCancelList(String modelId,String drawId,String drawType, String drawOther);

    JsonResult selectCheck(List<PlatData> PlatDataList);

    JsonResult cancelClick(String drawType, String drawOther,String userId, List<PlatData> PlatDataList);
}
