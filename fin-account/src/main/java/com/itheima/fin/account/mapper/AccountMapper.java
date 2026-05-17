package com.itheima.fin.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.fin.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 告诉 Spring Boot 这是一个 MyBatis 的 Mapper 接口
public interface AccountMapper extends BaseMapper<Account> {
    // 继承 BaseMapper 后，你直接白嫖了单表的增删改查方法，一行 SQL 都不用写！
}