package com.xpwi.springboot.service.impl.iso;

import com.xpwi.springboot.dao.FlowAddpeopleDao;
import com.xpwi.springboot.model.FlowAddpeople;
import com.xpwi.springboot.service.iso.FlowAddpeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowAddpeopleServiceImpl implements FlowAddpeopleService {
    @Autowired
    private FlowAddpeopleDao flowAddpeopleDao;
    @Override
    public int insertAddpeople(FlowAddpeople flowAddpeople) {
        return this.flowAddpeopleDao.insertAddpeople(flowAddpeople);
    }
}
