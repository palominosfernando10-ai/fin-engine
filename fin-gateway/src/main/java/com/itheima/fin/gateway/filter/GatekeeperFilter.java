package com.itheima.fin.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class GatekeeperFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // ==================================================
        // 🌟 核心破局点：遇到浏览器的 OPTIONS 探路兵，直接无条件放行！
        // ==================================================
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 1. 金融级安检：坚决不从 URL 里取参数，而是去极其隐蔽的 HTTP Headers 里找咱们项目专属的通行证！
        List<String> authKeys = exchange.getRequest().getHeaders().get("X-Fin-Auth-Key");
        String currentToken = (authKeys != null && !authKeys.isEmpty()) ? authKeys.get(0) : null;

        // 2. 拦截非法请求
        if (!StringUtils.hasText(currentToken)) {
            System.out.println("【安全拦截】检测到尝试非法访问核心财务链路！已阻断该请求。");
            // 拒绝访问
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 3. (预留位) 以后咱们如果做了登录微服务，可以在这里用 Redis 校验这个 currentToken 是不是伪造的。
        // 目前只要带了这个专属头，咱们就放行。
        System.out.println("【网关放行】财务指令鉴权通过，指令标识: " + currentToken);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // 优先级调高！设置为负数，确保这个安检步骤在其他所有路由操作之前执行！
    }
}