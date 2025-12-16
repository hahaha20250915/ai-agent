# ai-agent
AI-智能聊天机器人

基于 Spring Boot 3.2.0 构建的 Web 应用程序。

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.0
- Maven

### 前端
- Vue 3
- Vite 5
- Node.js 16+ (推荐 18 LTS 或 20 LTS)

## 运行项目

### 后端运行

#### 方式一：使用 Maven

```bash
mvn -pl agent-system spring-boot:run
```

#### 方式二：打包后运行

```bash
mvn clean package
java -jar agent-system/target/agent-system-1.0-SNAPSHOT.jar
```

后端启动后，访问 `http://localhost:8080/api/hello` 验证是否正常运行。

### 前端运行

**前置要求：Node.js 16+（推荐 18 LTS 或 20 LTS）**

1. **安装依赖**
   ```bash
   cd agent-ui
   npm install
   ```

2. **启动开发服务器**
   ```bash
   npm run dev
   ```

3. **访问前端**
   
   浏览器打开 `http://localhost:5173`

**注意：** 前端开发服务器会自动代理 `/ai` 路径的请求到后端 `http://localhost:8080`，确保后端已启动。

## 运行测试

```bash
mvn test
```

## API 端点

应用启动后，访问以下端点：

- 健康检查：`http://localhost:8080/api/hello`
- AI 聊天（SSE 流式）：`http://localhost:8080/ai/chat?memoryId=1&message=你好`

## 项目结构

```
ai-agent/
├── agent-system/                          # 后端 Spring Boot 项目
│   ├── src/main/java/com/agent/
│   │   ├── App.java                        # Spring Boot 主类
│   │   ├── controller/                     # 控制器
│   │   │   ├── HelloController.java
│   │   │   └── AIController.java           # AI 聊天接口
│   │   └── service/                        # 服务层
│   └── pom.xml
├── agent-ui/                              # 前端 Vue 项目
│   ├── src/
│   │   ├── App.vue
│   │   ├── components/
│   │   │   └── ChatPage.vue
│   │   └── main.js
│   ├── vite.config.js                      # Vite 配置（含代理）
│   └── package.json
└── pom.xml                                 # 父级 Maven 配置
```

## 配置

应用配置位于 `src/main/resources/application.properties`，默认端口为 8080。