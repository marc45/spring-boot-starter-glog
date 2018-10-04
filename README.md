# glog
> 本项目旨在减少日志配置，优化日志打印，本质上是使用springboot推荐的logback，同时加入请求和返回参数拦截打印的功能，使用方法如下

### 1. pom.xml引入starter
> 配置我的仓库
```xml
<repositories>
    <repository>
        <id>maven-repository-master</id>
        <url>https://raw.github.com/changgui/maven-repository/glog</url>
    </repository>
</repositories>
```

> 增加starter
```xml
<dependency>
    <groupId>com.guichang.starter</groupId>
    <artifactId>glog-spring-boot-starter</artifactId>
    <version>0.0.15</version>
</dependency>
```

### 2. application.yml增加配置
```yaml
server:
  port: 8080

spring:
  # 项目名
  application.name: glog
  # 环境 -- 可选 dev/test/prod 分别对应 开发环境/测试环境/生产环境
  profiles.active: dev

glog:
  # 是否开启插件
  enable: false
  # 是否开启ModuleLog拦截
  aspect: false
  # 日志级别
  level: debug
  # 环境中文名
  envCN: 开发环境
  # 控制台配置 %caller{1}
  stdout:
    # 日志打印格式
    pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [${glog.envCN} ${server.port}] [%X{trace}] [%X{moduleName}] - %-5level - %logger{50}.%L - %msg%n"
  # 日志文件配置
  file:
    # 日志存放目录
    logDir: C:/logs
    # 日志打印格式
    pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS} [${glog.envCN} ${server.port}] [%X{trace}] [%X{moduleName}] - %-5level - %logger{50}.%L - %msg%n"
  # 邮件配置 -- 目前配置error级别发送邮件
  email:
    # 日志打印格式
    pattern: "%d{yyyy-MM-dd HH:mm:ss.SSS}%X{trace}%X{moduleName}%-5level%logger{50}%L%msg"
    # SMTP server的地址，必需指定
    smtpHost: smtp.exmail.qq.com
    # SMTP server的端口地址
    smtpPort: 465
    # 发送邮件账号
    username: xxx@qq.com
    # 发送邮件密码
    password: xxxxxxxxxx
    # 指定发送到那个邮箱，设置多个用","分隔
    emailTo: xxx@qq.com
    # 指定email的标题 -- 可修改
    subject: 【${spring.application.name}-${glog.envCN}-${server.port}-报错】
```
> 说明几点
> 1. dev环境仅在控制台打印日志，也就是glog.stdout；test环境在仅打日志到文件里，也就是glog.file；prod打日志到文件和error发邮件，也就是glog.file和glog.email。目前暂不支持配置。
> 2. 打印日志的pattern建议按我的配置来，否则部分关键信息可能不会打印,如果点击行数进入具体代码可以用%caller{1}，具体用法参见[百度](http://www.baidu.com)。
> 3. glog.email.subject可以改成任意你喜欢的邮件标题。
> 4. 建议配置参数按我提供的配置齐全，否则项目启动可能会报错。

### 3. 日志打印预览(不加拦截)
```text
2018-10-05 00:28:20.434 [开发环境 8080] [] [] - INFO  - o.s.jmx.export.annotation.AnnotationMBeanExporter.433 - Registering beans for JMX exposure on startup
2018-10-05 00:28:20.449 [开发环境 8080] [] [] - INFO  - org.apache.coyote.http11.Http11NioProtocol.180 - Starting ProtocolHandler ["http-nio-8080"]
2018-10-05 00:28:20.463 [开发环境 8080] [] [] - INFO  - org.apache.tomcat.util.net.NioSelectorPool.180 - Using a shared selector for servlet write/read
2018-10-05 00:28:20.482 [开发环境 8080] [] [] - INFO  - o.s.boot.web.embedded.tomcat.TomcatWebServer.206 - Tomcat started on port(s): 8080 (http) with context path ''
2018-10-05 00:28:20.488 [开发环境 8080] [] [] - INFO  - com.guichang.test.springboot.BootApplication.59 - Started BootApplication in 3.326 seconds (JVM running for 4.432)
```
> 其中com.guichang.test.springboot.BootApplication.59的59表示的是打印的类的行号
### 4. 开启请求拦截
> 配置 glog.aspect: true
```java
@Slf4j
@RestController
@RequestMapping("/api")
public class TestController {

    @ModuleLog("测试专用")
    @GetMapping("/glog")
    public Map glog(String name, int age) {
        log.info("请求成功");
        //log.error("测试发邮件", new Exception("我是异常"));

        Map result = new HashMap(2);
        result.put("code", "0000");
        result.put("message", "处理成功");
        return result;
    }
}
```
> 访问 http://localhost:8080/api/glog?name=gui&age=19 可看到打印日志
```text
2018-10-05 00:38:45.197 [开发环境 8080] [182fdbea447fdb137] [测试专用] - INFO  - com.guichang.starter.glog.ModuleLogAspect.31 - 请求报文：["gui",19]
2018-10-05 00:38:45.203 [开发环境 8080] [182fdbea447fdb137] [测试专用] - INFO  - com.guichang.test.springboot.TestController.24 - 请求成功
2018-10-05 00:38:45.205 [开发环境 8080] [182fdbea447fdb137] [测试专用] - INFO  - com.guichang.starter.glog.ModuleLogAspect.33 - 返回报文：{"message":"处理成功","code":"0000"}
```
### 5. 报错邮件
> 如果打开上一步中的log.error则会收到报错邮件，样式如下图，拿到trace去日志文件就能搜到这个请求相关的所有日志了
![报错邮件图片](https://raw.githubusercontent.com/changgui/maven-repository/master/20181005/0001.png)  