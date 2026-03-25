FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace

# Copy Gradle wrapper and build files first to improve layer caching.
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle
RUN chmod +x gradlew

# Copy source and build the Spring Boot executable jar.
COPY src ./src
RUN ./gradlew --no-daemon clean bootJar && \
	JAR_PATH=$(find build/libs -maxdepth 1 -name "*.jar" ! -name "*-plain.jar" | head -n 1) && \
	test -n "$JAR_PATH" && cp "$JAR_PATH" app.jar

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/app.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -jar app.jar"]
