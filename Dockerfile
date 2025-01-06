FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/SellingCourese-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java","-jar","app.war"]
