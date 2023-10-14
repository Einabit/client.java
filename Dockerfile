FROM maven:3.9.4-eclipse-temurin-17 AS BUILD

COPY ./ ./

RUN mvn clean package

FROM eclipse-temurin:17-jre-jammy

COPY --from=BUILD /target/client-java-1.0.0.jar /client.jar

CMD ["java", "-jar", "/client.jar"]
