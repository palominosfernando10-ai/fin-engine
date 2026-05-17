package com.itheima.fin.account.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.fin.account.entity.Account;
import java.math.BigDecimal;

public interface AccountService extends IService<Account> {

    /**
     * 核心业务：执行扣款动作
     * @param targetId 目标账户的主键ID
     * @param costAmt 要扣除的金额
     * @return 成功返回true，失败（或余额不足）返回false
     */
    boolean executePayment(Long targetId, BigDecimal costAmt);

    // ===== 正式把缓存查询方法登记到接口里 =====
    BigDecimal getBalanceWithCache(Long targetId);

    // 新增：登记分页查询方法
    Page<Account> searchBigBalanceUsers(Integer pageNo, Integer pageSize);
}