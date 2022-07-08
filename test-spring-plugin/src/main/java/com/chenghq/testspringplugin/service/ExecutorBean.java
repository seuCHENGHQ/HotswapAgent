package com.chenghq.testspringplugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ExecutorBean {

    @Autowired
    private WorkerBean workerBean;

    private ScheduledExecutorService executorService;

    @PostConstruct
    private void init() {
        executorService = Executors.newScheduledThreadPool(10);
        executorService.scheduleWithFixedDelay(() -> workerBean.printMessage(), 0, 2000, TimeUnit.MILLISECONDS);
    }

    public void submitScheduledTask(Runnable runnable) {

    }
}
