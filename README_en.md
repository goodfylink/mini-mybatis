# mini-mybatis

English | **[中文](README.md)**

`mini-mybatis` is a simplified implementation of MyBatis based on the mybatis-3.5.18 branch. It reconstructs the core design and implementation of MyBatis, covering mapper proxying, XML parsing, data source management, execution engine, and result set mapping.

If this project helps you, please give it a ⭐ STAR — thank you!

## Design Goals

This project aims to help developers gain a deep understanding of MyBatis internals through simplified code, including:
1. **Interface-based programming**: How JDK dynamic proxies abstract away DAO implementations.
2. **Configuration automation**: XML parsing and `MappedStatement` encapsulation.
3. **Execution engine**: Collaboration between `Executor`, `StatementHandler`, and `ParameterHandler`.
4. **Object mapping**: How reflection and `MetaObject` automate result set mapping.

## Features

- **Lightweight**: Only core functionality — no caching, second-level cache, or dynamic SQL.
- **Dynamic proxy**: Deep application of the JDK Proxy pattern.
- **Reflection tools**: Built-in powerful `MetaObject` expression engine.
- **Multi-datasource support**: Built-in unpooled data source with H2 in-memory database testing.
- **End-to-end flow**: Full pipeline from XML configuration loading to SQL execution.

## Project Structure

```text
├── src
│   ├── main/java/org/apache/ibatis
│   │   ├── binding         # Mapper proxy & registry
│   │   ├── build           # XML builders
│   │   ├── datasource      # DataSource implementations
│   │   ├── executor        # Execution engine (SimpleExecutor, etc.)
│   │   ├── mapping         # SQL mapping meta-objects (MappedStatement, etc.)
│   │   ├── parsing         # XML/Token parsing utilities
│   │   ├── reflection      # Reflection tools & MetaObject
│   │   ├── session         # Session hub (SqlSession, Configuration)
│   │   └── type            # Type handlers & alias registry
│   └── test/java           # Progressive unit tests
├── changelog_en.md         # Step-by-step design guide (English)
└── pom.xml                 # Dependency management
```


*This project is for educational and learning purposes only.*
*Commercial use is prohibited without the author's written permission.*
