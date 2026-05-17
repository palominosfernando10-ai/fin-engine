package com.itheima.fin.account.utils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 这个注解的作用是：像个雷达一样，监控所有 Controller 抛出的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) // 只要代码里报错，就会自动钻进这个方法
    public Result<String> handleDeepError(Exception e) {

        // 在后台打印一下具体错误，方便咱们排错
        System.err.println("【系统护卫】检测到程序异常跑飞了: " + e.getMessage());

        // 自动把错误包装成咱们定义的标准快递盒发送给前端
        // 这样 Controller 里的代码就不用再写 try-catch 了
        return Result.error("财务指令处理中断：" + e.getMessage());
    }
}