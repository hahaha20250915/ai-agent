# ai-agent
AI-智能聊天机器人

基于 Spring Boot 3.2.0 构建的 Web 应用程序。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Maven

## 运行项目

### 方式一：使用 Maven

```bash
mvn spring-boot:run
```

### 方式二：打包后运行

```bash
mvn clean package
java -jar target/ai-agent-1.0-SNAPSHOT.jar
```

## 运行测试

```bash
mvn test
```

## API 端点

应用启动后，访问以下端点：

- 健康检查：`http://localhost:8080/api/hello`

## 项目结构

```
ai-agent/
├── src/
│   ├── main/
│   │   ├── java/com/agent/
│   │   │   ├── App.java                    # Spring Boot 主类
│   │   │   └── controller/
│   │   │       └── HelloController.java    # 示例 Controller
│   │   └── resources/
│   │       └── application.properties      # 应用配置
│   └── test/
│       └── java/com/agent/
│           └── AppTest.java                # 测试类
└── pom.xml                                 # Maven 配置
```

## 配置

应用配置位于 `src/main/resources/application.properties`，默认端口为 8080。