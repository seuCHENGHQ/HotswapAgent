package com.chenghq.testmybatisplugin.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ExecutorBean {

    @Autowired
    private WorkerBean workerBean;

    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    private void init() {
        scheduledExecutorService = Executors.newScheduledThreadPool(5);
        scheduledExecutorService.scheduleAtFixedRate(() -> workerBean.printMessage(), 0, 2000, TimeUnit.MILLISECONDS);
    }
}
