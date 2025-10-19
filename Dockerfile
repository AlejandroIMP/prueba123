# Etapa 1: Construcción
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final ligera
FROM eclipse-temurin:21-jre-alpine

# Metadatos
LABEL maintainer="Cosméticos Mercy"
LABEL description="Sistema de Gestión de Inventario y Ventas"
LABEL version="1.0"

# Instalar bash para scripts (opcional)
RUN apk add --no-cache bash

# Crear usuario no-root para seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Copiar recursos si es necesario
COPY --from=builder /app/src/main/resources/ /app/resources/

# Cambiar permisos
RUN chown -R appuser:appgroup /app

# Cambiar al usuario no-root
USER appuser

# Exponer puerto (si en el futuro añades una API REST)
EXPOSE 8080

# Variables de entorno con valores por defecto
ENV DB_HOST=mariadb \
    DB_PORT=3306 \
    DB_NAME=cosmeticos_mercy \
    DB_USER=root \
    DB_PASSWORD=mercy_pass_2025

# Script de entrada para configurar database.properties dinámicamente
COPY docker-entrypoint.sh /app/
USER root
RUN chmod +x /app/docker-entrypoint.sh
USER appuser

# Punto de entrada
ENTRYPOINT ["/app/docker-entrypoint.sh"]

# Comando por defecto - Consola
CMD ["console"]

