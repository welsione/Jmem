# Jmem - Java Agent Memory System

## 项目简介

Jmem 是一个用 Java 编写的 Agent 记忆系统，旨在学习和探索 AI Agent 中的记忆管理技术。该项目参考了业界流行的记忆管理框架设计理念，实现了一个轻量级的记忆存储和检索系统。

## 学习目标

本项目主要用于学习和实践以下技术：

- **向量存储与检索**：理解如何在 Java 中实现基于向量的语义搜索
- **混合搜索融合**：学习 RRF（互惠排名融合）算法来融合多种检索结果
- **Spring Boot 自动配置**：掌握 Spring Boot Starter 的开发方式
- **模块化架构设计**：学习如何设计可扩展的组件架构

## 核心功能

- **记忆存储**：支持 User、Session、Agent 三种维度的记忆存储
- **向量检索**：基于 Embedder 将文本转换为向量进行语义搜索
- **混合搜索**：结合向量搜索和关键词搜索，提升检索效果
- **插件化设计**：支持多种向量存储后端（内存、Qdrant）

## 技术栈

- Java 17
- Spring Boot 3.x
- Maven
- Jackson（JSON 处理）
- HttpClient（向量存储通信）

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>com.jmem</groupId>
    <artifactId>Jmem-Core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 配置

```yaml
jmem:
  enabled: true
  embedder:
    apiKey: ${SILICONFLOW_API_KEY}
    model: BAAI/bge-m3
    dimension: 1024
  vectorStore:
    type: in-memory  # or "qdrant"
    url: http://localhost:6333
    collectionName: jmem_memories
```

### 使用示例

```java
@Autowired
private MemoryService memoryService;

// 添加记忆
Memory memory = Memory.builder()
    .data("今天学习了 Spring Boot 自动配置")
    .scope(MemoryScope.USER)
    .userId("user123")
    .build();
String id = memoryService.add(memory);

// 搜索记忆
List<Memory> results = memoryService.search("Spring Boot", MemoryScope.USER, 10);

// 混合搜索
List<Memory> hybridResults = memoryService.hybridSearch("自动配置", MemoryScope.USER, 10);
```

## 项目结构

```
Jmem
├── Jmem-Core/              # 核心模块
│   ├── src/main/java/com/jmem/
│   │   ├── config/         # 配置类
│   │   ├── core/           # 核心服务接口和实现
│   │   │   └── fusion/     # 结果融合策略
│   │   ├── embedder/       # 向量化接口和实现
│   │   ├── model/          # 数据模型
│   │   ├── storage/        # 存储接口和实现
│   │   └── util/           # 工具类
│   └── doc/                # 文档
│       └── fusion/          # 融合算法文档
└── Jmem-Control/           # Web 控制模块（可选）
```

## 算法说明

### RRF（互惠排名融合）

Jmem 使用 RRF 算法融合向量搜索和文档搜索的结果。详见 [RRF 算法文档](Jmem-Core/doc/fusion/RRF.md)。

### 公式

```
RRF_score(d) = Σ 1/(k + rank(d))
```

其中 k=60 是平滑参数。

## 贡献

这是一个学习项目，欢迎提出问题和建议。如果你对项目有任何改进意见，欢迎提交 Issue 或 Pull Request。

## 许可证

MIT License

## 致谢

本项目参考了以下开源项目的设计理念：

- [Spring AI](https://spring.io/projects/spring-ai) - Spring AI 框架
- [Qdrant](https://qdrant.tech/) - 向量数据库
- [SiliconFlow](https://siliconflow.cn/) - AI API 服务
