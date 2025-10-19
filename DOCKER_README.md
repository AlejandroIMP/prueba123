# 🐳 Docker - Sistema de Gestión Cosméticos Mercy

Guía completa para ejecutar el sistema usando Docker y Docker Compose.

## 📋 Prerrequisitos

- Docker Desktop instalado (Windows/Mac/Linux)
- Docker Compose (incluido en Docker Desktop)
- Git (opcional, para clonar el proyecto)

## 🚀 Inicio Rápido

### 1. Construir y Ejecutar con Docker Compose

```bash
# Construir las imágenes
docker-compose build

# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app-console
```

### 2. Acceder a la Aplicación de Consola

```bash
# Conectar a la aplicación interactiva
docker attach cosmeticos_app_console

# O ejecutar en una nueva sesión
docker exec -it cosmeticos_app_console java -cp /app/app.jar:/app/resources org.cosmeticos.console.ConsoleApp
```

### 3. Acceder a PhpMyAdmin

Abre tu navegador en: http://localhost:8080

- **Usuario:** root
- **Contraseña:** mercy_pass_2025

## 🏗️ Arquitectura Docker

```
┌─────────────────────────────────────────┐
│         Docker Compose Stack            │
├─────────────────────────────────────────┤
│                                         │
│  ┌─────────────────────────────────┐   │
│  │   app-console                   │   │
│  │   (Aplicación Java)             │   │
│  │   Puerto: Interno               │   │
│  └────────────┬────────────────────┘   │
│               │                         │
│               ▼                         │
│  ┌─────────────────────────────────┐   │
│  │   mariadb                       │   │
│  │   (Base de Datos)               │   │
│  │   Puerto: 3306                  │   │
│  └─────────────────────────────────┘   │
│               │                         │
│               ▼                         │
│  ┌─────────────────────────────────┐   │
│  │   phpmyadmin                    │   │
│  │   (Administración Web)          │   │
│  │   Puerto: 8080                  │   │
│  └─────────────────────────────────┘   │
│                                         │
└─────────────────────────────────────────┘
```

## 📝 Comandos Útiles

### Gestión de Contenedores

```bash
# Iniciar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (¡CUIDADO! Borra la BD)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart app-console

# Ver estado de los servicios
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f mariadb
```

### Construcción

```bash
# Reconstruir imágenes
docker-compose build --no-cache

# Reconstruir solo la aplicación
docker-compose build app-console
```

### Depuración

```bash
# Entrar al contenedor de la aplicación
docker exec -it cosmeticos_app_console sh

# Entrar al contenedor de MariaDB
docker exec -it cosmeticos_mariadb bash

# Ver variables de entorno
docker exec cosmeticos_app_console env

# Verificar conectividad a la base de datos
docker exec cosmeticos_app_console nc -zv mariadb 3306
```

## 🔧 Configuración

### Variables de Entorno

Puedes personalizar la configuración editando `docker-compose.yml`:

```yaml
environment:
  DB_HOST: mariadb          # Host de la base de datos
  DB_PORT: 3306             # Puerto de la base de datos
  DB_NAME: cosmeticos_mercy # Nombre de la base de datos
  DB_USER: root             # Usuario
  DB_PASSWORD: mercy_pass_2025 # Contraseña
```

### Puertos

- **3306**: MariaDB (acceso directo desde el host)
- **8080**: PhpMyAdmin (interfaz web)

Para cambiar los puertos, edita la sección `ports` en `docker-compose.yml`:

```yaml
ports:
  - "PUERTO_HOST:PUERTO_CONTENEDOR"
```

## 🎯 Modos de Ejecución

### Modo Consola (Por Defecto)

```bash
docker-compose up app-console
```

### Modo GUI (Requiere Configuración X11)

**Linux:**
```bash
# Permitir conexiones X11
xhost +local:docker

# Descomentar servicio app-gui en docker-compose.yml

# Ejecutar
docker-compose up app-gui
```

**Windows (con WSL2 y VcXsrv):**
1. Instalar VcXsrv
2. Configurar `DISPLAY=host.docker.internal:0`
3. Ejecutar con la configuración adecuada

## 💾 Persistencia de Datos

Los datos de MariaDB se almacenan en un volumen Docker:

```bash
# Ver volúmenes
docker volume ls

# Inspeccionar el volumen
docker volume inspect cosmeticos_mercy_mariadb_data

# Backup de la base de datos
docker exec cosmeticos_mariadb mysqldump -u root -pmercy_pass_2025 cosmeticos_mercy > backup.sql

# Restaurar backup
docker exec -i cosmeticos_mariadb mysql -u root -pmercy_pass_2025 cosmeticos_mercy < backup.sql
```

## 🔒 Seguridad

### Cambiar Contraseñas en Producción

1. Edita `docker-compose.yml`:

```yaml
environment:
  MARIADB_ROOT_PASSWORD: TU_CONTRASEÑA_SEGURA
```

2. Actualiza también en el servicio `app-console`:

```yaml
environment:
  DB_PASSWORD: TU_CONTRASEÑA_SEGURA
```

3. Reconstruye y reinicia:

```bash
docker-compose down
docker-compose up -d --build
```

## 📊 Monitoreo

### Ver Uso de Recursos

```bash
# Estadísticas en tiempo real
docker stats

# Uso específico del contenedor
docker stats cosmeticos_app_console
```

### Health Check

El servicio MariaDB incluye un health check automático:

```bash
# Ver estado de salud
docker inspect --format='{{.State.Health.Status}}' cosmeticos_mariadb
```

## 🐛 Solución de Problemas

### La aplicación no se conecta a la base de datos

```bash
# Verificar que MariaDB está ejecutándose
docker-compose ps mariadb

# Ver logs de MariaDB
docker-compose logs mariadb

# Verificar conectividad
docker exec cosmeticos_app_console ping mariadb
```

### Error "port is already allocated"

Otro servicio está usando el puerto. Cambia el puerto en `docker-compose.yml` o detén el servicio conflictivo:

```bash
# Windows - Ver qué está usando el puerto 3306
netstat -ano | findstr :3306

# Linux/Mac
lsof -i :3306
```

### Reiniciar desde cero

```bash
# Detener y eliminar todo
docker-compose down -v

# Eliminar imágenes
docker-compose down --rmi all

# Reconstruir
docker-compose build --no-cache
docker-compose up -d
```

### La base de datos está vacía

```bash
# Verificar que el script de inicialización se ejecutó
docker-compose logs mariadb | grep "database_setup"

# Si no, ejecutar manualmente
docker exec -i cosmeticos_mariadb mysql -u root -pmercy_pass_2025 < database_setup.sql
```

## 🚀 Despliegue en Producción

### Recomendaciones

1. **Cambiar contraseñas por defecto**
2. **Usar variables de entorno externas:**

```bash
# Crear archivo .env
cat > .env <<EOF
DB_PASSWORD=contraseña_segura_aleatoria
MARIADB_ROOT_PASSWORD=contraseña_segura_aleatoria
EOF

# Referenciar en docker-compose.yml
environment:
  DB_PASSWORD: ${DB_PASSWORD}
```

3. **Configurar backups automáticos:**

```yaml
services:
  backup:
    image: mariadb:11.2
    depends_on:
      - mariadb
    volumes:
      - ./backups:/backups
    environment:
      - MYSQL_HOST=mariadb
      - MYSQL_USER=root
      - MYSQL_PASSWORD=mercy_pass_2025
    entrypoint: |
      bash -c 'while true; do
        mysqldump -h mariadb -u root -pmercy_pass_2025 cosmeticos_mercy > /backups/backup_$$(date +%Y%m%d_%H%M%S).sql
        sleep 86400
      done'
```

4. **Limitar recursos:**

```yaml
services:
  mariadb:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

## 📚 Comandos Rápidos

```bash
# Inicio completo
docker-compose up -d && docker-compose logs -f

# Reinicio rápido
docker-compose restart app-console

# Backup rápido
docker exec cosmeticos_mariadb mysqldump -u root -pmercy_pass_2025 cosmeticos_mercy > backup_$(date +%Y%m%d).sql

# Limpiar todo (¡CUIDADO!)
docker-compose down -v --rmi all

# Ver todo el sistema
docker-compose ps && docker volume ls && docker network ls
```

## 🆘 Soporte

Si encuentras problemas:

1. Revisa los logs: `docker-compose logs`
2. Verifica el estado: `docker-compose ps`
3. Consulta la documentación de Docker
4. Revisa el README.md principal del proyecto

---

**Versión Docker:** 1.0  
**Última actualización:** 2025-01-18  
**Mantenedor:** Cosméticos Mercy

