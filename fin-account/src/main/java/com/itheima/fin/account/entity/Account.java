package com.itheima.fin.account.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // Lombok 注解，自动生成 get/set 方法
@TableName("t_account") // 告诉 MyBatis-Plus 这个类对应数据库的哪张表
public class Account {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;

    private String accountNo;

    private String accountType;

    // 涉及到钱，绝对不能用 Double，必须用 BigDecimal！
    private BigDecimal balance;

    private BigDecimal frozenAmount;

    /**
     * 【极其重要：乐观锁版本号】
     * 加上 @Version 注解后，MyBatis-Plus 在执行 update 时会自动带上版本号比对：
     * UPDATE t_account SET balance = balance - 100, version = version + 1 WHERE id = 1 AND version = 老版本号
     * 如果期间有其他高并发线程改了余额，版本号变了，这条更新就会失败，从而完美防止“超卖/把钱扣成负数”。
     */
    @Version
    private Integer version;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}