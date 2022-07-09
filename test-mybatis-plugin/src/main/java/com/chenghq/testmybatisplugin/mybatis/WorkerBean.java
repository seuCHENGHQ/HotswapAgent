package com.chenghq.testmybatisplugin.mybatis;

import com.chenghq.testmybatisplugin.mybatis.mapper.TestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkerBean {

    @Autowired
    private TestDao testDao;

    public void printMessage() {
        List<TestModel> models = testDao.list();
        models.forEach(model -> System.out.println(model.getId()));
        System.out.println("=====================================");
    }
}
