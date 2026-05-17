package com.itheima.fin.ledger.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.fin.ledger.utils.Result; // 你刚才复制过来的快递盒
import com.itheima.fin.ledger.entity.LedgerEntry;
import com.itheima.fin.ledger.mapper.LedgerEntryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    @Autowired
    private LedgerEntryMapper ledgerEntryMapper;

    /**
     * 根据财务账号，查询它所有的资金流水明细
     */
    @GetMapping("/history")
    public Result<List<LedgerEntry>> getLedgerHistory(@RequestParam String accountNo) {

        // 使用 MyBatis-Plus 的条件构造器，按创建时间倒序排（最新的账在最前面）
        LambdaQueryWrapper<LedgerEntry> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LedgerEntry::getAccountNo, accountNo)
                .orderByDesc(LedgerEntry::getCreateTime);

        List<LedgerEntry> flowList = ledgerEntryMapper.selectList(wrapper);

        return Result.success(flowList);
    }
}