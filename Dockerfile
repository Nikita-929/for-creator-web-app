FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM eclipse-temurin:17-jdk-jammy AS backend-build
WORKDIR /app
COPY backend/.mvn .mvn
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x ./mvnw && ./mvnw -q -DskipTests dependency:go-offline
COPY backend/src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=backend-build /app/target/api-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=dev \
    JAVA_TOOL_OPTIONS="-Xmx256m -Xss512k -XX:MaxRAM=512m -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "mkdir -p ./data ./uploads && java -jar app.jar --server.port=${PORT}"]
