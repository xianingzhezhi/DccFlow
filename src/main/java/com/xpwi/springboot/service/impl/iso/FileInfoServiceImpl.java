package com.xpwi.springboot.service.impl.iso;

import com.xpwi.springboot.dao.FileInfoDao;
import com.xpwi.springboot.model.FileInfo;
import com.xpwi.springboot.service.iso.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FileInfoServiceImpl implements FileInfoService {
    @Autowired
    private FileInfoDao fileInfoDao;
    @Override
    public List<FileInfo> findAll(FileInfo fileInfo) {
        return this.fileInfoDao.findAll(fileInfo);
    }
}
