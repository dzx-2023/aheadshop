# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Maven wrapper location (not in PATH)
MVN=/c/Users/c/.m2/wrapper/dists/apache-maven-3.9.11-bin/6mqf5t809d9geo83kj4ttckcbc/apache-maven-3.9.11/bin/mvn.cmd

# Build everything
$MVN clean install

# Build a single module with dependencies
$MVN clean compile -pl aheadshop-user -am

# Build common modules only
$MVN clean install -pl aheadshop-common/common-core,aheadshop-common/common-exception,aheadshop-common/common-redis,aheadshop-common/common-mybatis,aheadshop-common/common-swagger -am
```

### Frontend (in aheadshop-front/)

```bash
npm run dev        # Vite dev server at localhost:5173, proxies /api → localhost:8080
npm run build      # vue-tsc -b && vite build (type-check + production build)
npm run preview    # Preview production build
npx vue-tsc --noEmit  # Type-check only
```

No test runner or linter is configured for the frontend.

### App (in aheadshop-app/)

```bash
npm run dev:h5          # H5 dev server (browser)
npm run dev:mp-weixin   # WeChat mini-program dev
npm run dev:app         # Native app dev
npm run build:h5        # H5 production build
npm run build:mp-weixin # WeChat mini-program build
npm run build:app       # Native app build
```

## Architecture

Spring Boot 3.2.5 + Spring Cloud 2023.0.1 microservice e-commerce system. Java 17, Maven multi-module.

### Module Structure

- **aheadshop-common** — Shared libraries (aggregator POM, not a runnable service)
  - `common-core` — Result wrapper, BusinessException/Code, enums, JwtUtil, SnowflakeIdWorker
  - `common-exception` — GlobalExceptionHandler (@RestControllerAdvice)
  - `common-redis` — RedisConfig + CacheService (StringRedisTemplate wrapper)
  - `common-mybatis` — MybatisPlusConfig (pagination, optimistic lock, auto-fill), BaseEntity
  - `common-swagger` — Knife4j OpenAPI 3 config with JWT bearer auth
- **aheadshop-gateway** (port 8080) — Spring Cloud Gateway (WebFlux), JWT auth filter, CORS, Redis rate limiting
- **aheadshop-user** (port 8081) — User registration/login/info, BCrypt passwords, JWT token pairs, RBAC, 签到积分系统(每日签到/连续奖励/积分流水)
- **aheadshop-product** (port 8082) — Skeleton
- **aheadshop-cart** (port 8083) — Skeleton (Redis-only, no DB)
- **aheadshop-order** (port 8084) — Skeleton
- **aheadshop-pay** (port 8085) — Skeleton
- **aheadshop-aichat** (port 8086) — AI客服服务, Spring AI + WebSocket 流式对话, RAG 知识检索
- **aheadshop-distribution** (port 8089) — 分销系统: 邀请码绑定推荐关系, 佣金计算(订单支付MQ触发), 冻结/解冻定时任务, 提现审核
- **aheadshop-admin** (port 8087) — Skeleton (Feign aggregation, no DB)
- **aheadshop-front** — Vue 3 + TypeScript + Vite frontend
- **aheadshop-app** — uni-app (Vue 3 + Pinia) mobile client, targets H5/微信小程序/Android/iOS

### Key Design Patterns

- **Database per service**: 6 separate MySQL databases (`aheadshop_user`, `aheadshop_product`, `aheadshop_order`, `aheadshop_pay`, `aheadshop_aichat`, `aheadshop_distribution`)
- **Gateway auth flow**: `AuthGlobalFilter` validates JWT → injects `X-User-Id` / `X-User-Role` headers → downstream services read from headers
- **Token management**: AuthService stores access+refresh tokens in Redis (`user:token:{id}`, `user:refresh:{id}`) for invalidation support
- **Unified response**: All endpoints return `Result<T>` with `{code, msg, data}`
- **Error codes**: 400/401/403/404/500 generic; 10xxx user; 20xxx product; 30xxx order; 40xxx pay
- **Base entity**: All main tables extend `BaseEntity` (id AUTO, createTime, updateTime, deleted @TableLogic, version @Version)
- **MyBatis-Plus mapper scan**: `@MapperScan("com.aheadshop.*.mapper")` in MybatisPlusConfig

### Infrastructure Dependencies

| Service | Host | Port | Notes |
|---------|------|------|-------|
| Nacos | 127.0.0.1 | 8848 | Service discovery + config center, namespace `public` |
| Redis | 127.0.0.1 | 6379 | DB 0, password `123456` |
| MySQL | localhost | 3306 | 6 databases: `aheadshop_user`, `aheadshop_product`, `aheadshop_order`, `aheadshop_pay`, `aheadshop_aichat`, `aheadshop_distribution` |

### Middleware Per Service

| Service | MySQL | Redis | RabbitMQ | OpenFeign | Sentinel |
|---------|-------|-------|----------|-----------|----------|
| gateway | — | Yes (reactive) | — | — | Yes |
| user | Yes | Yes | — | Yes | Yes |
| product | Yes | Yes | Yes | Yes | Yes |
| cart | — | Yes | — | Yes | Yes |
| order | Yes | Yes | Yes | Yes | Yes |
| pay | Yes | Yes | Yes | Yes | Yes |
| aichat | Yes | Yes | — | Yes | Yes |
| distribution | Yes | Yes | Yes | Yes | Yes |
| admin | — | — | — | Yes | Yes |

### Port Map

| Service | Port |
|---------|------|
| gateway | 8080 |
| user | 8081 |
| product | 8082 |
| cart | 8083 |
| order | 8084 |
| pay | 8085 |
| aichat | 8086 |
| admin | 8087 |
| distribution | 8089 |
| frontend | 5173 |

## Important Notes

- **Gateway is WebFlux-based**: Cannot use Spring MVC annotations. Filters implement `GlobalFilter`. CORS uses `CorsWebFilter` (reactive), not `WebCorsConfigurer`.
- **Gateway has its own JwtUtil**: Separate from common-core's JwtUtil to avoid pulling spring-boot-starter-web into WebFlux context.
- **Component scan**: Business services need `@SpringBootApplication(scanBasePackages = "com.aheadshop")` to pick up common module beans (CacheService, MybatisPlusConfig, etc.).
- **Table naming**: Entity `@TableName` values use bare names (e.g., `"user"`) which differ from the DB design doc's `t_` prefix convention. Ensure DDL matches entity mappings.
- **Nacos config**: Uses `spring.config.import: nacos:{app-name}.yml` (no bootstrap.yml). Namespace is `public` for dev.
- **Message queue**: RabbitMQ (not RocketMQ). Used by product, order, pay, distribution services.
- **No bootstrap.yml**: All services use application.yml with `spring.config.import` for Nacos config center.
- **AI客服模块 (aichat)**: Uses Spring AI (OpenAI-compatible API) for LLM integration, WebSocket for streaming responses, Redis for conversation context caching, Feign for product knowledge retrieval (RAG). Design doc: `docs/7-AI客服系统设计文档.md`

## Documentation

- `docs/1-系统需求分析报告.md` — Requirements
- `docs/2-系统模块设计文档.md` — Module design + port mapping + API specs
- `docs/3-系统数据库设计文档.md` — Full DDL for 17 tables across 4 databases
- `docs/4-软件设计说明书.md` — Architecture, patterns, coding standards
- `docs/5-开发流程与任务规划.md` — 40-day development plan with daily task checklists
- `docs/6-App端设计与开发文档.md` — uni-app mobile client design (aheadshop-app)
- `docs/7-AI客服系统设计文档.md` — AI客服系统设计文档 (aheadshop-aichat)
- `docs/8-签到积分系统设计文档.md` — 签到积分系统 (aheadshop-user)
- `docs/9-分销系统设计文档.md` — 分销系统 (aheadshop-distribution)
- `sql/init-data.sql` — Seed data (users, roles, menus, categories, brands, SPUs, SKUs)