# Build Stage
# Sử dụng Maven và OpenJDK 17 để biên dịch ứng dụng
FROM maven:3-openjdk-17 AS build

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép tất cả các file từ thư mục hiện tại vào thư mục làm việc của container
COPY . .

# Chạy lệnh Maven để biên dịch ứng dụng, bỏ qua kiểm tra
RUN mvn clean package -DskipTests

# Run Stage
# Sử dụng OpenJDK 17 Slim để chạy ứng dụng đã biên dịch
FROM openjdk:17-jdk-slim

# Thiết lập thư mục làm việc trong container
WORKDIR /app

# Sao chép file WAR đã biên dịch từ build stage vào container
COPY --from=build /app/target/DrComputer-0.0.1-SNAPSHOT.war drcomputer.war

# Mở port 8080 để ứng dụng có thể lắng nghe các yêu cầu HTTP
EXPOSE 8080

# Chạy ứng dụng bằng lệnh Java
ENTRYPOINT ["java", "-jar", "drcomputer.war"]
