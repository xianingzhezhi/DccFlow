package com.xpwi.springboot.service.impl.iso;

import com.xpwi.springboot.dao.FlowBaseUserDao;
import com.xpwi.springboot.model.FlowBaseUser;
import com.xpwi.springboot.model.FlowBaseUserKey;
import com.xpwi.springboot.service.iso.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: workspace_svn
 * @description: 健康测试
 * @author: PengFei_Ge
 * @create: 2020-12-05 09:29
 **/
@Service
public class helloServiceImpl implements HelloService {

    @Autowired
    private FlowBaseUserDao flowBaseUserDao;

    @Override
    public FlowBaseUser getUser(String user_id) {

        FlowBaseUserKey key = new FlowBaseUserKey("O19040004", "dcc");

        FlowBaseUser flowBaseUser = flowBaseUserDao.selectByPrimaryKey(key);

        return flowBaseUser;
    }
}
