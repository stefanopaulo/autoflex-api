# Estágio 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências (Cache de camadas)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código e compila (Só essa parte roda se houver alteração código)
COPY src ./src
RUN mvn clean package -DskipTests

# Execução (A imagem final que vai rodar no Docker)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia apenas o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Variável de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]