package com.itheima.fin.ledger.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_ledger_entry") // 对应咱们之前建好的复式记账流水表
public class LedgerEntry {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String tradeNo; // 上游传过来的流水号
    private String accountNo; // 发生变动的账户
    private String drCr; // 借贷方向 (DR借 / CR贷)
    private BigDecimal amount; // 发生金额
    private String status; // 状态 (SUCCESS)
    private LocalDateTime createTime;
}