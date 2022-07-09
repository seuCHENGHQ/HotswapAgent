package com.chenghq.testmybatisplugin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TODO mapper的xml更新后，有reload日志，但是看代码行为没reload成功 这块得跟代码看一下为什么
 */
@SpringBootApplication
@MapperScan(basePackages = "com.chenghq.testmybatisplugin.mybatis.mapper")
public class TestMybatisPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestMybatisPluginApplication.class, args);
    }

}
