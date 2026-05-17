package com.itheima.fin.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.fin.account.entity.Account;
import com.itheima.fin.account.mapper.AccountMapper;
import com.itheima.fin.account.service.AccountService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate; // ===== 新增 =====
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit; // ===== 新增 =====

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired // ===== 新增：注入 Redis 操作模板 =====
    private StringRedisTemplate stringRedisTemplate;

    // ===== 新增：查询余额的高性能方法 =====
    @Override
    public BigDecimal getBalanceWithCache(Long targetId) {
        // 1. 定义 Redis 的 Key (标准的大厂命名规范：项目名:模块名:主键)
        String cacheKey = "fin:acct:bal:" + targetId;

        // 2. 先去 Redis 里找
        String cachedBal = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cachedBal != null) {
            System.out.println("命中 Redis 缓存！直接返回内存数据，速度飞快！");
            return new BigDecimal(cachedBal);
        }

        // 3. Redis 里没有，说明是“缓存未命中”，只能去 MySQL 查底单了
        System.out.println("未命中缓存，苦逼地去查 MySQL 数据库...");
        Account dbAcct = this.getById(targetId);
        if (dbAcct == null) {
            throw new RuntimeException("账户不存在！");
        }

        // 4. 查到了！赶紧把热乎的数据塞进 Redis，并设置 30 分钟的过期时间 (防止占用太多内存)
        stringRedisTemplate.opsForValue().set(cacheKey, dbAcct.getBalance().toString(), 30, TimeUnit.MINUTES);

        return dbAcct.getBalance();
    }
    // ===================================


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executePayment(Long targetId, BigDecimal costAmt) {

        Account dbAcct = this.getById(targetId);
        if (dbAcct == null) {
            throw new RuntimeException("操作失败：没找到这个账户！");
        }

        BigDecimal currentMny = dbAcct.getBalance();
        if (currentMny.compareTo(costAmt) < 0) {
            throw new RuntimeException("扣款失败：余额不足！");
        }

        BigDecimal leftMny = currentMny.subtract(costAmt);
        dbAcct.setBalance(leftMny);

        boolean isOk = this.updateById(dbAcct);

        if (!isOk) {
            throw new RuntimeException("网络拥挤，扣款失败，请重试！");
        }

        // ===== 极其关键的一步：双写一致性保障 =====
        // 既然数据库里的钱已经被扣掉了，Redis 里存的还是以前的老余额。
        // 我们必须残忍地把 Redis 里的老数据删掉！下次用户再查，就会重新触发查 MySQL 并载入新余额。
        // 这就是面试常问的“延迟双删”或“删缓存策略”！
        stringRedisTemplate.delete("fin:acct:bal:" + targetId);
        System.out.println("====== 扣款成功，已同步清除 Redis 历史缓存！======");
        // =======================================

        Map<String, Object> msgData = new HashMap<>();
        msgData.put("tradeNo", UUID.randomUUID().toString().replace("-", ""));
        msgData.put("accountNo", dbAcct.getAccountNo());
        msgData.put("amount", costAmt);

        rabbitTemplate.convertAndSend("fin.ledger.queue", msgData);
        System.out.println("====== 已向消息队列发送记账凭证任务！流水号: " + msgData.get("tradeNo") + " ======");

        return true;
    }
    // ===================================
    // ===== 新增：分页查询大额度账户的方法 =====
    // ===================================
    @Override
    public Page<Account> searchBigBalanceUsers(Integer pageNo, Integer pageSize) {

        // 1. 动态接收前端传来的页码
        Page<Account> myPage = new Page<>(pageNo, pageSize);

        // 2. 拼装查询条件
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Account::getAccountType, "SAVING")
//                .gt(Account::getBalance, new BigDecimal("500"))
                .orderByDesc(Account::getBalance);

        // 3. 执行分页查询
        Page<Account> resultPage = this.page(myPage, wrapper);

        // 4. 不要打印了，直接把这个装满数据的“大果篮”退回去给 Controller！
        return resultPage;
    }
}