package com.itheima.fin.ledger.utils;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码：200代表成功，500代表失败
    private String msg;   // 给前端网页弹窗用的提示信息
    private T data;       // 真正要传给前端的数据（可以是查到的余额，也可以是账户列表）

    // 成功时的快捷方法
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    // 失败时的快捷方法
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
