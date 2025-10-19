#!/bin/bash

# Script de entrada para configurar la aplicación antes de ejecutarla

# Esperar a que MariaDB esté disponible
echo "Esperando a que MariaDB esté disponible..."
while ! nc -z $DB_HOST $DB_PORT; do
  sleep 1
done
echo "MariaDB está listo!"

# Crear archivo database.properties con variables de entorno
cat > /app/resources/database.properties <<EOF
# MariaDB Database Configuration (Docker)
db.url=jdbc:mariadb://${DB_HOST}:${DB_PORT}/${DB_NAME}
db.username=${DB_USER}
db.password=${DB_PASSWORD}
db.driver=org.mariadb.jdbc.Driver
EOF

echo "Configuración de base de datos aplicada:"
echo "  Host: $DB_HOST:$DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"

# Determinar qué aplicación ejecutar
if [ "$1" = "console" ]; then
    echo "Iniciando aplicación de consola..."
    java -cp /app/app.jar:/app/resources org.cosmeticos.console.ConsoleApp
elif [ "$1" = "gui" ]; then
    echo "Iniciando interfaz gráfica..."
    java -cp /app/app.jar:/app/resources org.cosmeticos.gui.MainFrame
else
    # Ejecutar comando personalizado
    exec "$@"
fi

