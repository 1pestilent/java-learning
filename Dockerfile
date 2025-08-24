FROM maven:3.9.11-amazoncorretto-24-debian AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -q

FROM openjdk:24-jdk-slim
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN addgroup --system springboot && adduser --system --group springboot
USER springboot


EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]