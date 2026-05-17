package com.itheima.fin.ledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.fin.ledger.entity.LedgerEntry;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LedgerEntryMapper extends BaseMapper<LedgerEntry> {
}