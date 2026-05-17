package com.itheima.fin.account.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.fin.account.entity.Account;
import com.itheima.fin.account.service.AccountService;
import com.itheima.fin.account.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/acct") // 访问路径的前缀
public class AccountController {

    @Autowired
    private AccountService acctSvc; // 注入咱们刚才写的业务组件

    /**
     * 访问地址：POST http://localhost:8081/api/acct/pay
     */
    @PostMapping("/pay")
    public Result<String> doPayment(@RequestParam Long uid, @RequestParam BigDecimal amt) {
        // 删掉所有 try-catch！
        // 哪怕底层抛出“余额不足”，也会被上面的拦截器自动抓住并返回 JSON
        acctSvc.executePayment(uid, amt);
        return Result.success("支付成功，已扣除：" + amt);
    }

    // 1. 改造：查询余额接口
    @GetMapping("/balance")
    public Result<BigDecimal> getBalance(@RequestParam Long uid) {
        // 从底层拿到真实的数字，比如 700
        BigDecimal bal = acctSvc.getBalanceWithCache(uid);
        // 装进快递盒发给前端！
        return Result.success(bal);
    }

    // 2. 改造：分页查询接口 (让前端传页码过来)
    @GetMapping("/test-page")
    public Result<Page<Account>> testPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 拿到底层查出来的那一页数据
        Page<Account> pageData = acctSvc.searchBigBalanceUsers(pageNo, pageSize);
        // 装进快递盒发给前端！网页拿到后可以直接渲染表格！
        return Result.success(pageData);
    }
}