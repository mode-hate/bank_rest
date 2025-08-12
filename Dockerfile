# Этап сборки
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Копируем всё в контейнер
COPY . .

# Сборка jar без тестов
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:21-jre

WORKDIR /app

# Копируем jar из билда
COPY --from=builder /app/target/*.jar app.jar

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
