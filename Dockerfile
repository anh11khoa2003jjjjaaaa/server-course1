

# Build Stage
FROM maven:3-openjdk-17 AS build

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép tất cả file từ thư mục hiện tại vào container
COPY . .

# Chạy lệnh Maven để biên dịch ứng dụng và tạo file JAR
RUN mvn clean package -DskipTests

# Run Stage

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/SellingCourese-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java","-jar","app.war"]


# Sao chép file JAR đã được biên dịch từ giai đoạn build
COPY --from=build /app/target/SellingCourese-0.0.1-SNAPSHOT.jar app.jar

# Mở cổng 8080 để ứng dụng lắng nghe
EXPOSE 8080

# Chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
