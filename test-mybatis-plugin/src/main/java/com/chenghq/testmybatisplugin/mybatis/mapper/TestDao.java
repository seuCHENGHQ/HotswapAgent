package com.chenghq.testmybatisplugin.mybatis.mapper;

import com.chenghq.testmybatisplugin.mybatis.TestModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestDao {

    List<TestModel> list();
}
