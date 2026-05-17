# 🏦 FinEngine 分布式金融账务核心引擎

## 📖 项目简介
FinEngine 是一套基于 Spring Cloud Alibaba 构建的高并发分布式金融账务核心系统。项目完美契合真实业务场景下的专业复式记账法则，实现了账户管理、资金扣减与流水入账的彻底解耦，保障金融级数据安全与最终一致性。

## 🛠️ 核心技术栈
* **微服务架构**: Spring Cloud Gateway, Spring Cloud Alibaba Nacos
* **后端技术**: Spring Boot 3, MyBatis-Plus, MySQL
* **异步解耦**: RabbitMQ (消息队列)
* **前端渲染**: Vue 3, Element Plus, Axios

## 🧩 微服务模块架构
系统采用标准微服务架构拆分，职责边界清晰：
* `fin-gateway` (端口 8080): **统一流量网关**。负责全局跨域处理 (CorsWebFilter)、无状态 Token 鉴权与安全拦截，路由分发。
* `fin-account` (端口 8081): **核心账户服务**。处理高并发下的资金扣减，采用数据库版本号 (Version) 乐观锁机制防止高并发“超扣/脏写”，保障资金安全。
* `fin-ledger` (端口 8082): **异步流水服务**。通过 Direct 交换机异步消费 RabbitMQ 交易凭证，自动生成带有借贷方向 (DR/CR) 的专业会计分录，实现核心支付接口的削峰提速。
* `index.html`: **极简前端控制台**。提供零配置开箱即用的 Web 交互界面。

## 🚀 核心技术亮点
1. **彻底攻克 WebFlux 网关跨域神坑**: 底层重写并注入 `CorsConfig` 全局跨域天网，完美解决前端携带自定义鉴权 Header (`X-Fin-Auth-Key`) 时触发的 `OPTIONS` 预检请求拦截问题。
2. **MQ 削峰与柔性事务**: 扣款成功后不等待流水落库，直接投递消息至 RabbitMQ，大幅降低核心链路 RT（响应时间）。
3. **全局异常与统一响应**: 封装泛型 `Result<T>` 数据结构，结合 `@RestControllerAdvice` 构建全局异常监控哨兵，实现异常的优雅 JSON 降级返回。
