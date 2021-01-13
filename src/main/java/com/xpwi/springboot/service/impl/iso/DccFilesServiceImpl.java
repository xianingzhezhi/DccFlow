package com.xpwi.springboot.service.impl.iso;

import com.xpwi.springboot.dao.DccFilesDao;
import com.xpwi.springboot.model.DccFiles;
import com.xpwi.springboot.model.QueryFile;
import com.xpwi.springboot.service.iso.DccFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/*
操作flow_his_dcc_files 表，
 */
@Service
public class DccFilesServiceImpl implements DccFilesService {

    @Autowired
    private DccFilesDao dccFilesDao;

    /**
     * 公共查询flow_his_dcc_files  ，状态为开立
     * @param  fistId   OSO   ISO
     * @param propertyName   数据库属性
     * @param input  输入框内容
     * @param symbol  符号  >= >  < <=  like =
     * @return
     */
    @Override
    public List<QueryFile> selectDccFiles(String fistId, String propertyName, String input, String symbol) {
        if (input!=""&& input!=null){
            input=input.replace("*", "%");
        }
        String sql = "SELECT file_id, version,  file_name FROM flow_system.flow_his_dcc_files where flag='Y'";
        if (fistId !=null && fistId !=""){
            if (fistId.equals("OSO")){
                sql =sql + " and file_id like 'O%'";
            } else if (fistId.equals("ISO")) {
                sql = sql + " and file_id not like 'O%'";
            }
        }
        if(propertyName !=null && propertyName !=""){
            if ( (input != null && input != "") && (symbol != null && symbol != "")){
                sql = sql + " and "+propertyName+" "+symbol + "'"+input+"'";
            }
        }
        //排序
        sql= sql + "  ORDER BY file_id";
        System.out.println("\033[34;4m" + "公共查询flow_his_dcc_files sql " +sql+ "\033[0m");
        List<QueryFile> list = this.dccFilesDao.selectDccFiles(sql);
        return list;
    }

    /**
     * 根据file_id，file_name，version查询出文件
     * @param file_id
     * @param file_name
     * @param version
     * @return
     */
    @Override
    public List<DccFiles> selectDccFile(String file_id, String file_name,String version) {
        String sql="select * from flow_system.flow_his_dcc_files where " +
                "file_id='" + file_id + "' and file_name='" + file_name + "'and version='" + version + "'";
        System.out.println("DccFiles  sql   "+sql);
        List<DccFiles> dccFilesList=this.dccFilesDao.findDccFiles(sql);
        return dccFilesList;
    }

}
