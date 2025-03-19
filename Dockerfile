# 使用 OpenJDK 作为基础镜像
FROM openjdk:17-jre-slim

# 将 JAR 包复制到容器中
COPY target/phantoms-backend-1.0-SNAPSHOT.jar /app/phantoms-backend.jar

# 配置容器启动时的命令
ENTRYPOINT ["java", "-jar", "/app/phantoms-backend.jar"]

# 暴露端口
EXPOSE 8080