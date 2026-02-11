# 多阶段构建：第一阶段构建，第二阶段运行
FROM maven:3.9.6-amazoncorretto-21 AS builder

# 复制源码
WORKDIR /app
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests -Dmaven.gitcommitid.skip=true

# 第二阶段：运行环境
#FROM openjdk:23-jdk-bookworm
FROM amazoncorretto:21-alpine-jdk

# 安装字体和字体配置工具
RUN apk --no-cache add \n    fontconfig \n    ttf-dejavu \n    ttf-droid \n    ttf-freefont \n    ttf-liberation \n    font-noto-cjk \n    && fc-cache -f -v

# 从构建阶段复制 jar 文件
COPY --from=builder /app/target/phantoms-backend-1.0-SNAPSHOT.jar /app/phantoms-backend.jar

# 配置容器启动时的命令
ENTRYPOINT ["java", "-jar", "/app/phantoms-backend.jar"]

# 暴露端口
EXPOSE 8080