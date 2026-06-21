# AheadShop-Plus AI客服系统设计文档

## 1. 概述

### 1.1 项目背景

为提升用户体验和降低人工客服成本，基于现有微服务架构新增 AI 智能客服模块。用户可通过文字对话获取商品咨询、订单查询、售后服务等支持。

### 1.2 技术选型

| 技术 | 选型 | 说明 |
|------|------|------|
| 大模型接入 | Spring AI | 统一抽象层，支持切换 OpenAI / 通义千问 / Ollama |
| 流式输出 | WebSocket | 逐字推送，提升用户体验 |
| 会话存储 | MySQL | 聊天记录持久化 |
| 上下文缓存 | Redis | 最近 N 轮对话，加速上下文组装 |
| 知识检索 | RAG | Feign 调用商品服务，注入商品信息到 prompt |

---

## 2. 整体架构

```
┌──────────────────────────────────────────────────────────────┐
│                         前端层                                 │
│                  ChatWindow.vue (WebSocket)                    │
└────────────────────────────┬─────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────┐
│                     Gateway (8080)                            │
│                   路由 + JWT 鉴权                              │
└────────────────────────────┬─────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────┐
│                  AIChat Service (8086)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │ ChatService  │  │ LlmService  │  │ KnowledgeService    │  │
│  │  会话管理     │  │  大模型调用   │  │  RAG知识库检索       │  │
│  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘  │
└─────────┼────────────────┼────────────────────┼──────────────┘
          │                │                    │
    ┌─────▼─────┐   ┌─────▼─────┐   ┌─────────▼─────────┐
    │   MySQL   │   │   LLM API │   │ Product Service   │
    │  聊天记录  │   │  大模型    │   │   (Feign 调用)     │
    └───────────┘   └───────────┘   └───────────────────┘
```

---

## 3. 模块结构

```
aheadshop-aichat (port 8086)
├── pom.xml
└── src/main/
    ├── java/com/aheadshop/aichat/
    │   ├── AichatApplication.java
    │   ├── config/
    │   │   ├── LlmConfig.java              # LLM 客户端配置
    │   │   └── WebsocketConfig.java         # WebSocket 配置
    │   ├── controller/
    │   │   ├── ChatController.java          # REST API
    │   │   └── ChatWebSocketHandler.java    # WebSocket 流式对话
    │   ├── service/
    │   │   ├── ChatService.java             # 会话业务逻辑
    │   │   ├── LlmService.java              # 大模型调用封装
    │   │   ├── ContextService.java          # 上下文组装
    │   │   └── KnowledgeService.java        # RAG 知识检索
    │   ├── feign/
    │   │   └── ProductFeignClient.java      # 商品服务 Feign
    │   ├── mapper/
    │   │   ├── ChatSessionMapper.java
    │   │   └── ChatMessageMapper.java
    │   ├── entity/
    │   │   ├── ChatSession.java
    │   │   └── ChatMessage.java
    │   └── dto/
    │       ├── ChatRequest.java
    │       └── ChatResponse.java
    └── resources/
        ├── application.yml
        └── prompts/
            └── system-prompt.st             # 系统提示词模板
```

---

## 4. 数据库设计

数据库名：`aheadshop_aichat`

### 4.1 对话会话表 chat_session

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | Y | 自增 | 主键 |
| user_id | BIGINT | Y | — | 关联用户 ID |
| title | VARCHAR(100) | N | — | 会话标题（首条消息摘要） |
| status | TINYINT | N | 1 | 1=进行中 2=已结束 |
| create_time | DATETIME | Y | NOW() | 创建时间 |
| update_time | DATETIME | Y | NOW() | 更新时间 |
| deleted | TINYINT | N | 0 | 逻辑删除 |
| version | INT | N | 0 | 乐观锁版本号 |

```sql
CREATE TABLE chat_session (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    title       VARCHAR(100),
    status      TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT DEFAULT 0,
    version     INT DEFAULT 0,
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客服对话会话表';
```

### 4.2 对话消息表 chat_message

| 字段 | 类型 | 必填 | 默认值 | 说明 |
|------|------|------|--------|------|
| id | BIGINT | Y | 自增 | 主键 |
| session_id | BIGINT | Y | — | 关联会话 ID |
| role | VARCHAR(20) | Y | — | user / assistant / system |
| content | TEXT | Y | — | 消息内容 |
| token_count | INT | N | 0 | 消耗 token 数 |
| create_time | DATETIME | Y | NOW() | 创建时间 |
| update_time | DATETIME | Y | NOW() | 更新时间 |
| deleted | TINYINT | N | 0 | 逻辑删除 |
| version | INT | N | 0 | 乐观锁版本号 |

```sql
CREATE TABLE chat_message (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id  BIGINT       NOT NULL,
    role        VARCHAR(20)  NOT NULL,
    content     TEXT         NOT NULL,
    token_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT DEFAULT 0,
    version     INT DEFAULT 0,
    KEY idx_session_id (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客服消息表';
```

---

## 5. 接口设计

### 5.1 REST API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/chat/session` | 创建新会话 |
| GET | `/api/chat/sessions` | 获取当前用户会话列表 |
| GET | `/api/chat/session/{id}/messages` | 获取会话消息记录 |
| POST | `/api/chat/send` | 发送消息（非流式，返回完整回复） |
| DELETE | `/api/chat/session/{id}` | 删除会话 |

### 5.2 WebSocket

| 路径 | 说明 |
|------|------|
| `ws://host:8086/ws/chat/{sessionId}` | 流式对话，服务端逐 token 推送 |

**WebSocket 消息格式：**

```json
// 客户端发送
{
    "content": "这件衣服有什么尺码？"
}

// 服务端推送（逐 chunk）
{"type": "chunk", "content": "这"}
{"type": "chunk", "content": "件"}
{"type": "chunk", "content": "衣服"}
{"type": "done", "tokenCount": 128}
```

### 5.3 请求/响应 DTO

```java
// ChatRequest
public class ChatRequest {
    @NotBlank
    private Long sessionId;
    @NotBlank
    private String content;
}

// ChatResponse
public class ChatResponse {
    private Long sessionId;
    private Long messageId;
    private String content;
    private Integer tokenCount;
}
```

---

## 6. 核心流程

### 6.1 对话流程

```
用户发送消息
    ↓
ChatController 接收请求
    ↓
ChatService.saveMessage() 持久化用户消息
    ↓
ContextService.buildContext() 组装上下文
    ├── system prompt (客服人设 + 业务规则)
    ├── 最近 N 轮对话 (Redis 缓存)
    └── KnowledgeService.retrieve() RAG 检索商品/FAQ
    ↓
LlmService.streamChat() 调用大模型
    ↓
WebSocket 逐 chunk 推送 → 前端逐字渲染
    ↓
回复完成 → 持久化 assistant 消息 → 更新 Redis 上下文
```

### 6.2 RAG 知识检索流程

```
用户问题进入
    ↓
提取关键词 / 意图识别
    ↓
Feign 调用 Product Service
    ├── /api/product/search?keyword=xxx
    └── /api/product/{id}
    ↓
组装知识片段注入 prompt context
    ↓
LLM 基于知识上下文生成回复
```

---

## 7. LLM 集成配置

### 7.1 Maven 依赖

```xml
<!-- Spring AI (以 OpenAI 兼容接口为例) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

### 7.2 application.yml

```yaml
server:
  port: 8086

spring:
  application:
    name: aheadshop-aichat
  config:
    import: nacos:aheadshop-aichat.yml
  ai:
    openai:
      api-key: ${AI_API_KEY}
      base-url: ${AI_BASE_URL}          # 可替换为通义千问/Ollama 地址
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.7
          max-tokens: 1024
```

### 7.3 System Prompt 模板

```
你是 AheadShop 的智能客服助手。请遵守以下规则：
1. 友好、专业地回答用户关于商品、订单、售后的问题
2. 如果用户询问的商品信息，基于提供的上下文回答，不要编造
3. 涉及退款/投诉等敏感操作，引导用户联系人工客服
4. 回复简洁明了，适当使用 emoji 增加亲和力
5. 不要透露你是 AI 或任何系统内部信息
```

---

## 8. 前端组件设计

### 8.1 组件结构

```
aheadshop-front/src/views/chat/
├── ChatWindow.vue        # 聊天主窗口（会话列表 + 对话区）
├── ChatMessage.vue       # 单条消息组件（支持 Markdown 渲染）
├── ChatInput.vue         # 输入框 + 发送按钮
└── ChatSessionList.vue   # 左侧会话列表
```

### 8.2 WebSocket 连接示例

```typescript
const ws = new WebSocket(`ws://localhost:8086/ws/chat/${sessionId}`)

ws.onmessage = (event) => {
    const msg = JSON.parse(event.data)
    if (msg.type === 'chunk') {
        // 逐字追加到当前回复
        currentReply.value += msg.content
    } else if (msg.type === 'done') {
        // 回复完成
        isStreaming.value = false
    }
}
```

---

## 9. 现有模块改动

| 模块 | 改动内容 |
|------|----------|
| `pom.xml` (root) | `<modules>` 中新增 `aheadshop-aichat` |
| `aheadshop-gateway` | `application.yml` 增加 aichat 路由 + WebSocket 路由 |
| `sql/init-data.sql` | 新建 `aheadshop_aichat` 数据库及表 |
| `docs/2-系统模块设计文档.md` | 模块总览中补充 aichat 行 |

### 9.1 Gateway 路由配置

```yaml
# gateway application.yml 新增
spring:
  cloud:
    gateway:
      routes:
        - id: aheadshop-aichat
          uri: lb://aheadshop-aichat
          predicates:
            - Path=/api/chat/**,/ws/chat/**
```

---

## 10. 中间件依赖

| 中间件 | 用途 |
|--------|------|
| MySQL | 存储会话和消息记录 |
| Redis | 缓存最近 N 轮对话上下文 |
| Nacos | 服务注册与配置中心 |
| OpenFeign | 调用商品服务获取商品信息 |
| Sentinel | 限流降级，防止大模型 API 滥用 |

---

## 11. 开发计划

总工期：**10 个工作日**（约 2 周），按阶段递进，每天结束时有可运行成果。

---

### 第一阶段：模块骨架与基础设施（Day 1 - Day 2）

#### Day 1：模块骨架搭建

**目标：** aheadshop-aichat 模块能独立启动并注册到 Nacos

```
任务清单：
□ 1. 创建模块目录与 pom.xml
    - 在根 pom.xml 的 <modules> 中新增 aheadshop-aichat
    - 创建 aheadshop-aichat/pom.xml，继承父工程
    - 引入依赖：spring-boot-starter-web、spring-boot-starter-websocket、
      common-core、common-redis、common-mybatis、common-swagger、
      spring-ai-openai-spring-boot-starter、openfeign、sentinel

□ 2. 创建启动类
    - AichatApplication.java
    - @SpringBootApplication(scanBasePackages = "com.aheadshop")
    - @EnableFeignClients
    - @EnableDiscoveryClient

□ 3. 创建 application.yml
    - server.port: 8086
    - spring.application.name: aheadshop-aichat
    - spring.config.import: nacos:aheadshop-aichat.yml
    - MySQL 数据源配置（aheadshop_aichat）
    - Redis 连接配置
    - Nacos 服务发现配置
    - Spring AI 配置（api-key、base-url、model）占位

□ 4. Nacos 配置中心
    - 在 Nacos 控制台创建 aheadshop-aichat.yml 配置文件
    - 将敏感配置（AI_API_KEY、数据库密码等）放入 Nacos

□ 5. Gateway 路由配置
    - 在 aheadshop-gateway 的 application.yml 中新增：
      routes:
        - id: aheadshop-aichat
          uri: lb://aheadshop-aichat
          predicates:
            - Path=/api/chat/**,/ws/chat/**

□ 6. 验证
    - mvn clean compile -pl aheadshop-aichat -am 编译通过
    - 启动 AichatApplication，Nacos 控制台能看到服务注册
    - Gateway 能路由到 aichat 服务
```

**产出：** aichat 服务骨架可启动，Nacos 注册成功

---

#### Day 2：数据库与实体层

**目标：** 数据库表创建完成，Entity/Mapper/DTO 层代码就绪

```
任务清单：
□ 1. 创建数据库
    - MySQL 中执行 CREATE DATABASE aheadshop_aichat
    - 执行建表 SQL（chat_session、chat_message）

□ 2. 实体类
    - ChatSession.java（@TableName("chat_session")）
        · 继承 BaseEntity
        · 字段：userId、title、status
    - ChatMessage.java（@TableName("chat_message")）
        · 继承 BaseEntity
        · 字段：sessionId、role、content、tokenCount

□ 3. Mapper 接口
    - ChatSessionMapper.java（extends BaseMapper<ChatSession>）
    - ChatMessageMapper.java（extends BaseMapper<ChatMessage>）

□ 4. DTO 类
    - ChatRequest.java
        · @NotNull Long sessionId
        · @NotBlank String content
    - ChatResponse.java
        · sessionId、messageId、content、tokenCount
    - ChatSessionVO.java
        · id、title、status、createTime、lastMessage

□ 5. application.yml 补充 MyBatis-Plus 配置
    - mybatis-plus.mapper-locations
    - mybatis-plus.type-aliases-package

□ 6. 验证
    - 启动服务，确认 Mapper 扫描无报错
    - 手动插入一条测试数据验证表结构正确
```

**产出：** 数据库就绪，数据访问层代码完成

---

### 第二阶段：核心对话能力（Day 3 - Day 5）

#### Day 3：LLM 集成与基础对话

**目标：** 能通过 API 调用大模型并获取回复

```
任务清单：
□ 1. LlmService.java
    - 注入 ChatClient（Spring AI 自动配置）
    - 实现 chat(String systemPrompt, List<Message> history, String userMessage) 方法
    - 实现 streamChat(...) 方法，返回 Flux<String>
    - 异常处理：API 超时、API Key 无效、模型不可用

□ 2. LlmConfig.java
    - @Configuration 配置类
    - 配置 ChatClient.Builder
    - 设置默认 system prompt
    - 配置超时时间、重试策略

□ 3. System Prompt 模板
    - src/main/resources/prompts/system-prompt.st
    - 定义客服人设、回复规则、禁止事项
    - 支持变量替换（{storeName}、{returnPolicy} 等）

□ 4. ContextService.java
    - buildContext(Long userId, Long sessionId, String userMessage)
    - 从 Redis 获取最近 N 轮对话（List<Message>）
    - 拼装 system prompt + history + user message
    - 将本轮对话写入 Redis（LPUSH，保留最近 20 条）

□ 5. ChatService.java（基础版）
    - createSession(Long userId) → 创建会话，返回 sessionId
    - sendMessage(Long userId, ChatRequest req) → 调用 LlmService 获取回复
    - saveMessage(sessionId, role, content) → 持久化消息

□ 6. ChatController.java
    - POST /api/chat/session → 创建会话
    - POST /api/chat/send → 发送消息，返回完整回复

□ 7. 验证
    - 用 Postman/curl 测试 POST /api/chat/send
    - 发送"你好"，能收到 AI 客服回复
    - 消息记录正确写入 chat_message 表
```

**产出：** REST API 可对话，消息持久化正常

---

#### Day 4：会话管理与历史记录

**目标：** 完整的会话生命周期管理

```
任务清单：
□ 1. ChatService 补充
    - getSessions(Long userId) → 查询用户会话列表（按更新时间倒序）
    - getSessionMessages(Long userId, Long sessionId) → 查询会话消息列表
    - deleteSession(Long userId, Long sessionId) → 逻辑删除会话及消息
    - updateSessionTitle(Long sessionId, String firstMessage) → 首条消息截取作为标题

□ 2. ChatController 补充
    - GET /api/chat/sessions → 会话列表
    - GET /api/chat/session/{id}/messages → 消息列表（分页）
    - DELETE /api/chat/session/{id} → 删除会话

□ 3. Redis 上下文管理优化
    - 会话结束/删除时清除 Redis 缓存
    - 设置上下文过期时间（24 小时无活动自动清除）
    - 上下文长度限制（最多保留最近 10 轮 = 20 条消息）

□ 4. 接口鉴权
    - 通过 Gateway 注入的 X-User-Id Header 获取当前用户
    - 校验会话归属（用户只能操作自己的会话）

□ 5. Knife4j 接口文档
    - 确认所有接口在 Knife4j UI 中可查看、可调试
    - 补充 @Tag、@Operation、@Parameter 注解

□ 6. 验证
    - 完整流程测试：创建会话 → 发送多条消息 → 查看历史 → 删除会话
    - 验证 Redis 缓存正确清除
    - 验证跨用户无法访问他人会话
```

**产出：** 会话管理功能完整，接口文档可查阅

---

#### Day 5：WebSocket 流式输出

**目标：** AI 回复逐字推送到前端，体验类似 ChatGPT

```
任务清单：
□ 1. WebsocketConfig.java
    - @EnableWebSocket
    - 注册 ChatWebSocketHandler
    - 配置路径：/ws/chat/{sessionId}
    - 配置 allowedOrigins（开发环境允许 localhost:5173）

□ 2. ChatWebSocketHandler.java
    - 继承 TextWebSocketHandler
    - afterConnectionEstablished → 建立连接，验证 sessionId 有效性
    - handleTextMessage →
        · 解析客户端消息 JSON（{"content": "..."}）
        · 调用 ChatService.saveMessage() 保存用户消息
        · 调用 ContextService.buildContext() 组装上下文
        · 调用 LlmService.streamChat() 获取 Flux<String>
        · 逐 chunk 推送 {"type":"chunk","content":"..."}
        · 推送完成发送 {"type":"done","tokenCount":128}
        · 异常时发送 {"type":"error","message":"..."}
    - afterConnectionClosed → 清理资源

□ 3. WebSocket 鉴权
    - 从 URL 参数或首条消息中获取 token
    - 验证 JWT 有效性，提取 userId
    - 校验 sessionId 归属

□ 4. 连接管理
    - ConcurrentHashMap 维护 sessionId → WebSocketSession 映射
    - 同一会话只允许一个活跃连接（新连接踢掉旧连接）
    - 心跳检测（30 秒无消息发送 ping/pong）

□ 5. 验证
    - 用 wscat / Postman WebSocket 测试流式推送
    - 发送消息后逐字收到 chunk，最后收到 done
    - 断开连接后资源正确释放
```

**产出：** WebSocket 流式对话可用，逐字推送正常

---

### 第三阶段：前端开发（Day 6 - Day 7）

#### Day 6：聊天组件开发

**目标：** 前端聊天界面完成，能收发消息

```
任务清单：
□ 1. ChatSessionList.vue
    - 左侧会话列表（移动端为抽屉）
    - 显示会话标题 + 最后更新时间
    - 新建会话按钮
    - 点击切换会话
    - 长按/右键删除会话

□ 2. ChatMessage.vue
    - 单条消息气泡组件
    - 区分 user / assistant 样式（左右对齐）
    - 支持 Markdown 渲染（markdown-it 或 v-html + sanitize）
    - 流式消息显示打字机光标动画
    - 消息时间显示

□ 3. ChatInput.vue
    - 底部输入框 + 发送按钮
    - Enter 发送，Shift+Enter 换行
    - 发送中禁用输入（显示 loading）
    - 输入框自适应高度

□ 4. ChatWindow.vue（主容器）
    - 整合上述三个组件
    - 左右布局（桌面端）/ 上下布局（移动端）
    - 消息区域自动滚动到底部
    - 空会话显示欢迎语

□ 5. API 封装
    - src/api/chat.ts
        · createSession() → POST /api/chat/session
        · getSessions() → GET /api/chat/sessions
        · getMessages(sessionId) → GET /api/chat/session/{id}/messages
        · deleteSession(sessionId) → DELETE /api/chat/session/{id}

□ 6. 路由配置
    - src/router/index.ts 中新增 /chat 路由
    - 导航栏增加"智能客服"入口
```

**产出：** 聊天界面基本可用，能通过 REST API 收发消息

---

#### Day 7：WebSocket 对接与体验优化

**目标：** 前端对接 WebSocket 流式输出，打磨交互体验

```
任务清单：
□ 1. WebSocket Composable
    - src/composables/useChatWebSocket.ts
        · connect(sessionId) → 建立 WebSocket 连接
        · send(content) → 发送消息
        · onChunk 回调 → 逐字追加到当前回复
        · onDone 回调 → 流式结束
        · onError 回调 → 错误处理
        · disconnect() → 断开连接
        · 自动重连机制（断线后 3 秒重连）

□ 2. 流式显示
    - 发送消息后立即显示用户气泡
    - 创建空的 assistant 气泡，逐字填充
    - 流式过程中显示打字机光标 ▊
    - 流结束后隐藏光标，启用 Markdown 渲染

□ 3. Gateway WebSocket 路由
    - 确认 Gateway 正确代理 ws://localhost:8080/ws/chat/** → aichat 服务
    - 前端连接 ws://localhost:8080/ws/chat/{sessionId}

□ 4. 交互细节
    - 发送消息后自动滚动到底部
    - 流式回复过程中也跟随滚动
    - 网络断开提示 + 重连按钮
    - 移动端适配（安全区域、键盘弹出处理）

□ 5. 验证
    - 浏览器中完整测试：创建会话 → 发送消息 → 流式收到回复
    - 多轮对话上下文连续
    - 移动端浏览器测试
```

**产出：** 前端流式对话体验完整

---

### 第四阶段：RAG 知识库（Day 8）

#### Day 8：商品知识检索

**目标：** AI 客服能基于商品数据回答用户问题

```
任务清单：
□ 1. ProductFeignClient.java
    - @FeignClient("aheadshop-product")
    - getProduct(Long id) → GET /api/product/{id}
    - searchProducts(String keyword) → GET /api/product/search?keyword=xxx
    - getCategoryProducts(Long categoryId) → 按分类查询

□ 2. KnowledgeService.java
    - retrieve(String userMessage) → 知识检索入口
    - extractKeywords(String message) → 关键词提取
        · 简单方案：正则匹配商品名/品牌名/分类名
        · 进阶方案：LLM 意图识别（调用轻量模型）
    - searchProducts(String keyword) → 调用 Feign 搜索商品
    - buildKnowledgeContext(List<ProductVO> products) →
      格式化商品信息为文本片段，注入 prompt

□ 3. Prompt 增强
    - system-prompt.st 中增加知识注入占位符 {knowledge}
    - ContextService.buildContext() 中调用 KnowledgeService
    - 将检索到的商品信息拼入 system prompt

□ 4. 常见问题 FAQ
    - 可选：创建 chat_faq 表存储常见问答对
    - KnowledgeService 同时检索 FAQ
    - 高匹配度的 FAQ 直接回复，不调用 LLM

□ 5. 验证
    - 询问"有没有手机"→ AI 能列出相关商品
    - 询问"iPhone 15 多少钱"→ AI 能给出具体价格
    - 询问"退货政策"→ AI 基于 FAQ 回复
```

**产出：** AI 客服具备商品知识，能回答商品相关问题

---

### 第五阶段：完善与测试（Day 9 - Day 10）

#### Day 9：Token 统计与限流

**目标：** 成本可控，防滥用

```
任务清单：
□ 1. Token 统计
    - LlmService 调用完成后记录 token 用量
    - ChatMessage.tokenCount 字段更新
    - chat_session 表新增 total_tokens INT 字段，累加会话总用量
    - 可选：每日/每月 token 汇总统计接口

□ 2. 用户限流
    - 基于 Redis 的滑动窗口限流
    - 单用户每分钟最多 20 条消息
    - 单用户每日最多 200 条消息
    - 超限返回友好提示："您今天的对话次数已用完，请明天再来"

□ 3. Sentinel 降级
    - LLM API 调用配置 Sentinel 熔断规则
    - 连续失败 5 次触发熔断，30 秒后半开
    - 降级回复："系统繁忙，请稍后再试"

□ 4. 验证
    - 连续快速发送消息，验证限流生效
    - 模拟 LLM API 超时，验证降级回复
    - 检查 token 统计数据准确
```

**产出：** 成本可控，异常场景有兜底

---

#### Day 10：集成测试与文档

**目标：** 全流程跑通，文档更新

```
任务清单：
□ 1. 端到端测试
    - 启动全部服务：Gateway + User + Product + AIChat + 前端
    - 完整流程：
        · 用户注册/登录 → 获取 token
        · 进入客服页面 → 创建会话
        · 发送"你好"→ 流式收到问候
        · 发送"有什么手机推荐"→ AI 列出商品
        · 发送"iPhone 15 多少钱"→ AI 回答价格
        · 查看历史会话列表
        · 删除会话
    - 移动端浏览器测试

□ 2. 边界场景测试
    - 空消息发送（前端拦截 + 后端校验）
    - 超长消息（限制 2000 字符）
    - 并发创建多个会话
    - 会话中切换页面再回来（WebSocket 重连）
    - 未登录访问（Gateway 拦截 401）

□ 3. 文档更新
    - docs/2-系统模块设计文档.md → 模块总览补充 aichat
    - docs/3-系统数据库设计文档.md → 补充 aheadshop_aichat 库表
    - sql/init-data.sql → 补充 aichat 建表语句
    - CLAUDE.md → 补充 aichat 模块说明

□ 4. 代码清理
    - 删除调试日志和 TODO
    - 检查无硬编码的 API Key / 密码
    - 确认 application.yml 敏感配置已外部化

□ 5. Git 提交
    - git add aheadshop-aichat/ docs/ sql/ pom.xml
    - commit message: "feat: 新增 AI 客服模块 (aheadshop-aichat)"
```

**产出：** AI 客服功能完整可用，文档同步更新

---

### 开发计划总览

| 天数 | 阶段 | 核心任务 | 产出 |
|------|------|----------|------|
| Day 1 | 基础设施 | 模块骨架、pom.xml、Nacos 注册、Gateway 路由 | 服务可启动 |
| Day 2 | 基础设施 | 数据库建表、Entity、Mapper、DTO | 数据层就绪 |
| Day 3 | 核心对话 | LlmService、ChatService、REST API | API 可对话 |
| Day 4 | 核心对话 | 会话管理、历史记录、鉴权 | 会话功能完整 |
| Day 5 | 核心对话 | WebSocket、流式推送、连接管理 | 流式输出可用 |
| Day 6 | 前端开发 | 聊天组件、会话列表、API 封装 | UI 基本可用 |
| Day 7 | 前端开发 | WebSocket 对接、流式渲染、体验优化 | 前端完整 |
| Day 8 | RAG 知识库 | Feign 商品检索、知识注入、FAQ | AI 懂商品 |
| Day 9 | 完善 | Token 统计、限流、降级 | 成本可控 |
| Day 10 | 测试 | 端到端测试、文档更新、代码清理 | 功能上线就绪 |