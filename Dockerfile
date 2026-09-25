FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre
ENV JAVA_OPTS=""
WORKDIR /app
RUN groupadd --system trading && useradd --system --gid trading --create-home trading
COPY --from=build /workspace/target/neu-trading-app-demo-api-*.jar /app/app.jar
USER trading
EXPOSE 8081
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
