gRPC Spring Boot Project
========================================

优雅的集成gRPC到Spring Boot项目。分[grpc-server-spring-boot-starter](grpc-server-spring-boot-starter)、[grpc-client-spring-boot-starter](grpc-client-spring-boot-starter)。

支持四种模式：

- inProcess 进程内模式，只使用在测试和功能演示场景
- simple 明文模式，可使用在内网微服务
- tls TLS模式，服务端、客户端使用证书保证通信安全，可对公网提供服务
- custom 自定义模式，在以上模式不满足要求的情况下，可以对服务端、客户端进行自定义

----------
## 示例

- [InProcess](grpc-spring-boot-samples/grpc-spring-boot-sample-inprocess)

- [Server Simple](grpc-spring-boot-samples/grpc-spring-boot-sample-server-simple)

- [Client Simple](grpc-spring-boot-samples/grpc-spring-boot-sample-client-simple)

- [Server Tls](grpc-spring-boot-samples/grpc-spring-boot-sample-server-tls)

- [Client Tls](grpc-spring-boot-samples/grpc-spring-boot-sample-client-tls)

- [Server Custom](grpc-spring-boot-samples/grpc-spring-boot-sample-server-custom)

- [Client Custom](grpc-spring-boot-samples/grpc-spring-boot-sample-client-custom)


#### 生成Tls测试证书

```
$ sh grpc-spring-boot-samples/tools/create_openssl_key.sh
```

证书默认生成在/tmp/sslcert，可修改脚本自定义

----------
## 使用

### Server
##### Maven
```xml
<dependency>
    <groupId>io.nity.grpc</groupId>
    <artifactId>grpc-server-spring-boot-starter</artifactId>
    <version>0.9.1-SNAPSHOT</version>
</dependency>
```

##### Gradle
```gradle
compile 'io.nity.grpc:grpc-server-spring-boot-starter:0.9.1-SNAPSHOT'
```

### Client
##### Maven
```xml
<dependency>
    <groupId>io.nity.grpc</groupId>
    <artifactId>grpc-client-spring-boot-starter</artifactId>
    <version>0.9.1-SNAPSHOT</version>
</dependency>
```

##### Gradle
```gradle
compile 'io.nity.grpc:grpc-client-spring-boot-starter:0.9.1-SNAPSHOT'
```

### Server + Client
##### Maven
```xml
<dependency>
    <groupId>io.nity.grpc</groupId>
    <artifactId>grpc-spring-boot-starter</artifactId>
    <version>0.9.1-SNAPSHOT</version>
</dependency>
```

##### Gradle
```gradle
compile 'io.nity.grpc:grpc-spring-boot-starter:0.9.1-SNAPSHOT'
```

具体的代码请参照各示例模块。

----------
## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
