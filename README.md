# mini-mybatis

`mini-mybatis` 是一个依据mybatis-3.5.18分支简写的mybatis，还原了 MyBatis 从映射器代理、XML 解析、数据源管理、执行引擎到结果集封装的核心设计与实现过程。

如果本项目能帮助到你，请给个STAR，谢谢！！！

## 设计目标

本项目旨在通过简化的代码实现，帮助开发者深入理解 MyBatis 的底层原理，包括：
1. **接口式编程**：如何通过 JDK 动态代理屏蔽 DAO 实现。
2. **配置自动化**：XML 解析与 MappedStatement 的封装。
3. **执行引擎**：Executor、StatementHandler 与 ParameterHandler 的协作。
4. **对象映射**：如何利用反射与 MetaObject 实现结果集自动封装。

## 特性

- **轻量化实现**：mini版本只有核心功能，没有缓存、二级缓存、动态sql等高级功能。
- **动态代理**：深度应用 JDK Proxy 模式。
- **反射工具**：内建强大的 `MetaObject` 表达式引擎。
- **多数据源支持**：内置非池化数据源，支持 H2 内存数据库测试。
- **全链路闭环**：支持从 XML 配置加载到执行 SQL 的完整流程。


## 项目结构

```text
├── src
│   ├── main/java/org/apache/ibatis
│   │   ├── binding         # 映射器代理与注册
│   │   ├── build           # XML 构建器
│   │   ├── datasource      # 数据源实现
│   │   ├── executor        # 执行器引擎 (SimpleExecutor等)
│   │   ├── mapping         # SQL 映射元对象 (MappedStatement等)
│   │   ├── parsing         # XML/Token 解析工具
│   │   ├── reflection      # 反射工具与 MetaObject
│   │   ├── session         # 会话中心 (SqlSession, Configuration)
│   │   └── type            # 类型处理器与别名注册
│   └── test/java           # 渐进式单元测试
├── changelog.md            # 详细的步骤说明与设计文档
└── pom.xml                 # 依赖管理
```



*本项目仅供交流学习使用。*
*未取得本人书面许可，不得将该项目用于商业用途*
