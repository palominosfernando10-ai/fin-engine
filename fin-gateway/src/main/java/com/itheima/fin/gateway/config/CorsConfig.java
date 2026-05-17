package com.itheima.fin.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 1. 允许任何前端来源的跨域请求 (比如咱们的 localhost:63342)
        config.addAllowedOriginPattern("*");
        // 2. 允许前端携带任何自定义请求头 (比如 X-Fin-Auth-Key)
        config.addAllowedHeader("*");
        // 3. 允许前端使用任何请求方法 (GET, POST, OPTIONS, PUT, DELETE)
        config.addAllowedMethod("*");
        // 4. 允许携带 Cookie 凭证
        config.setAllowCredentials(true);
        // 5. 探路小兵 (OPTIONS) 的免检通行证有效期：1小时
        config.setMaxAge(3600L);

        // 把这个跨域规则应用到网关的所有路由路径上 (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        // 终极绝杀：生成底层的 WebFlux 跨域过滤器
        return new CorsWebFilter(source);
    }
}