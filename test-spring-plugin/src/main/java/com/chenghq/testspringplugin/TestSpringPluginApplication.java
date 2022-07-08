package com.chenghq.testspringplugin;

import com.chenghq.testspringplugin.service.ExecutorBean;
import com.chenghq.testspringplugin.service.WorkerBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestSpringPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSpringPluginApplication.class, args);
    }

}
