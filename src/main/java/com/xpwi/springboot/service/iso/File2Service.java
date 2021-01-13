package com.xpwi.springboot.service.iso;

import com.xpwi.springboot.model.File2;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface File2Service {
    //添加小文件表
    int insertFile2(File2 file2);

    //根据大文件号查询 FILE2信息
    File2 selectFile2(String from_id);
    //根据大文件号查询 FILE2信息

    List<File2> selectFile2s(String from_id);
}
