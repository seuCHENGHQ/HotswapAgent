package com.chenghq.testspringplugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkerBean {

    @Autowired
    private HotReloadBean hotReloadBean;

    public void printMessage() {
        System.out.println("before hotswap");
    }
}
