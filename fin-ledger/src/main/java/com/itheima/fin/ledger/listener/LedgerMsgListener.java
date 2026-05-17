package com.itheima.fin.ledger.listener;

import com.itheima.fin.ledger.entity.LedgerEntry;
import com.itheima.fin.ledger.mapper.LedgerEntryMapper;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class LedgerMsgListener {

    @Autowired
    private LedgerEntryMapper ledgerEntryMapper;

    /**
     * @RabbitListener 注解的威力：
     * 它会自动去 RabbitMQ 里声明一个叫 "fin.ledger.queue" 的队列。
     * 只要这个队列里有消息，它就会立刻把消息抓过来，塞进入参 msgData 里。
     */
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "fin.ledger.queue", durable = "true"),
//            exchange = @Exchange(name = "fin.exchange", type = "direct"), // 这里为了演示简单，直接绑队列，如果是真实复杂业务会用到交换机
//            key = "fin.ledger.routing"
//    ))
    // 注意：因为我们在 account 那边直接发给了队列名，上面虽然写了绑定，但最关键的是 value = @Queue(name = "fin.ledger.queue")
    // 为了极致契合刚才发送的代码，你可以直接用这种极简写法：
    @RabbitListener(queuesToDeclare = @Queue(name = "fin.ledger.queue"))
    public void receiveLedgerMsg(Map<String, Object> msgData) {
        System.out.println("====== 账务微服务收到记账任务！======");
        System.out.println("接收到的数据：" + msgData);

        // 1. 从 Map 里把扣款信息解析出来
        String tradeNo = (String) msgData.get("tradeNo");
        String accountNo = (String) msgData.get("accountNo");
        // 从 Map 序列化传过来，数字类型可能被转成了 Double 或 Integer，安全转换为 BigDecimal
        BigDecimal amount = new BigDecimal(msgData.get("amount").toString());

        // 2. 组装一条流水记录
        LedgerEntry entry = new LedgerEntry();
        entry.setTradeNo(tradeNo);
        entry.setAccountNo(accountNo);
        entry.setDrCr("DR"); // 扣款记借方
        entry.setAmount(amount);
        entry.setStatus("SUCCESS");
        entry.setCreateTime(LocalDateTime.now());

        // 3. 写入 ledger_db 数据库！
        ledgerEntryMapper.insert(entry);
        System.out.println("====== 入账成功！流水号：" + tradeNo + " 已写入 ledger_db 数据库！======");
    }
}