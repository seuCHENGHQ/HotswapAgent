package com.chenghq.testspringplugin.service;

import org.springframework.stereotype.Service;

@Service
public class WorkerBean {

    public void printMessage() {
        System.out.println("after hotswap");
    }
}
