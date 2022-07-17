package com.chenghq.testspringplugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication 注解会默认扫描该注解所在包, 以及所有子包中的bean
 * 因此, 这里其实默认了basePackage就是TestSpringPluginApplication所在的路径
 */
@SpringBootApplication
public class TestSpringPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSpringPluginApplication.class, args);
    }

}
