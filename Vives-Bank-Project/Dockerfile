# Etapa de compilación, un docker especifico, que se etiqueta como build
FROM gradle:jdk21-alpine AS build

# Directorio de trabajo
WORKDIR /app

# Copia los archivos build.gradle y src de nuestro proyecto
COPY build.gradle.kts .
COPY gradlew .
COPY gradle gradle
COPY src src

RUN ./gradlew clean build -x test

FROM openjdk:21 AS run

# Directorio de trabajo
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/my-app.jar

# Expone el puerto 3000
EXPOSE 3000

ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app/my-app.jar"]