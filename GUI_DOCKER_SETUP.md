# 🖥️ Guía para Ejecutar la Interfaz Gráfica en Docker

Esta guía te ayudará a ejecutar la aplicación con interfaz gráfica (Swing) dentro de Docker.

## 📋 Configuración por Sistema Operativo

### 🪟 Windows

#### Opción 1: VcXsrv (Recomendado)

1. **Descargar e instalar VcXsrv:**
   - Descarga desde: https://sourceforge.net/projects/vcxsrv/
   - Instala siguiendo el asistente

2. **Configurar VcXsrv:**
   ```
   - Ejecuta "XLaunch" desde el menú de inicio
   - Selecciona "Multiple windows"
   - Display number: 0
   - Start no client: ✓
   - Clipboard: ✓
   - Primary Selection: ✓
   - Native opengl: ✓
   - Disable access control: ✓ (IMPORTANTE)
   ```

3. **Guardar configuración:**
   - Guarda la configuración en "config.xlaunch"
   - Ejecútala cada vez que quieras usar la GUI

4. **Ejecutar la aplicación:**
   ```bash
   docker-compose up -d mariadb
   docker-compose up app-gui
   ```

#### Opción 2: X410 (Alternativa de pago)

1. **Instalar X410 desde Microsoft Store**

2. **Configurar X410:**
   - Iniciar en modo "Windowed Apps"
   - Allow Public Access: Sí

3. **Ejecutar:**
   ```bash
   docker-compose up app-gui
   ```

#### Solución de problemas en Windows:

Si aparece error de conexión:
```bash
# 1. Verifica que VcXsrv esté ejecutándose
# Busca el icono en la bandeja del sistema

# 2. Verifica la configuración de firewall
# Debe permitir conexiones en el puerto 6000

# 3. Reinicia VcXsrv con "Disable access control" activado
```

---

### 🐧 Linux

#### Configuración (más sencilla en Linux):

1. **Permitir conexiones X11:**
   ```bash
   xhost +local:docker
   ```

2. **Editar docker-compose.yml:**
   Comenta la línea de Windows y descomenta la de Linux:
   ```yaml
   environment:
     # DISPLAY: host.docker.internal:0  # Comentar esta línea
     DISPLAY: ${DISPLAY:-:0}            # Descomentar esta línea
   ```

3. **Ejecutar:**
   ```bash
   docker-compose up app-gui
   ```

4. **Al terminar, restaurar permisos:**
   ```bash
   xhost -local:docker
   ```

#### Para Ubuntu/Debian:

```bash
# Instalar dependencias si es necesario
sudo apt-get update
sudo apt-get install -y x11-xserver-utils

# Permitir conexiones
xhost +local:docker

# Ejecutar
docker-compose up app-gui
```

---

### 🍎 macOS

#### Configuración con XQuartz:

1. **Instalar XQuartz:**
   ```bash
   brew install --cask xquartz
   ```

2. **Configurar XQuartz:**
   - Abrir XQuartz
   - Preferencias → Seguridad
   - ✓ "Allow connections from network clients"
   - Reiniciar XQuartz

3. **Permitir conexiones:**
   ```bash
   xhost + $(hostname)
   ```

4. **Configurar variable DISPLAY:**
   ```bash
   export DISPLAY=$(hostname):0
   ```

5. **Editar docker-compose.yml:**
   ```yaml
   environment:
     DISPLAY: host.docker.internal:0
   ```

6. **Ejecutar:**
   ```bash
   docker-compose up app-gui
   ```

---

## 🚀 Comandos Rápidos

### Iniciar solo la GUI:
```bash
# 1. Iniciar base de datos primero
docker-compose up -d mariadb

# 2. Esperar a que esté lista (unos segundos)
docker-compose logs mariadb | grep "ready for connections"

# 3. Iniciar GUI
docker-compose up app-gui
```

### Iniciar TODO (Consola + GUI + PhpMyAdmin):
```bash
docker-compose up -d
```

### Ver logs de la GUI:
```bash
docker-compose logs -f app-gui
```

### Detener solo la GUI:
```bash
docker-compose stop app-gui
```

### Reiniciar la GUI:
```bash
docker-compose restart app-gui
```

---

## 🔍 Verificación

### Verificar que X11 funciona:

**Windows (PowerShell):**
```powershell
# Verificar que VcXsrv esté ejecutándose
Get-Process vcxsrv
```

**Linux/Mac:**
```bash
# Verificar DISPLAY
echo $DISPLAY

# Test simple con xeyes
docker run --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix alpine sh -c "apk add xeyes && xeyes"
```

---

## ⚙️ Configuración Avanzada

### Ejecutar solo GUI sin consola:

Edita `docker-compose.yml` y comenta el servicio `app-console`:

```yaml
# app-console:
#   build:
#     ...
```

Luego:
```bash
docker-compose up app-gui
```

### Diferentes displays:

Si tienes múltiples displays, cambia el número:

```yaml
environment:
  DISPLAY: host.docker.internal:1  # Para display 1
```

---

## 🐛 Solución de Problemas Comunes

### Error: "Can't connect to X11 window server"

**Windows:**
- Verifica que VcXsrv esté ejecutándose (icono en bandeja)
- Reinicia VcXsrv con "Disable access control"
- Revisa el firewall de Windows

**Linux:**
- Ejecuta: `xhost +local:docker`
- Verifica: `echo $DISPLAY`

**Mac:**
- Reinicia XQuartz
- Ejecuta: `xhost + $(hostname)`

### Error: "Connection refused"

```bash
# Verificar conectividad desde el contenedor
docker exec cosmeticos_app_gui env | grep DISPLAY

# Probar conexión
docker exec cosmeticos_app_gui sh -c "echo \$DISPLAY"
```

### La ventana no aparece pero no hay errores:

```bash
# Ver logs completos
docker-compose logs app-gui

# Verificar que Java Swing esté disponible
docker exec cosmeticos_app_gui java -version
```

### Rendimiento lento:

- En Windows: Usa X410 en lugar de VcXsrv
- En Mac: XQuartz puede ser lento, considera ejecutar nativamente
- Reduce la resolución en las preferencias de X11

---

## 📊 Comparación de Opciones

| Sistema | Herramienta | Dificultad | Rendimiento |
|---------|-------------|------------|-------------|
| Windows | VcXsrv      | Media      | Bueno       |
| Windows | X410        | Fácil      | Excelente   |
| Linux   | X11 nativo  | Fácil      | Excelente   |
| macOS   | XQuartz     | Media      | Bueno       |

---

## 💡 Recomendaciones

1. **Para Desarrollo:** Ejecuta la aplicación nativamente (sin Docker) para mejor rendimiento
2. **Para Producción:** Usa la versión de consola en Docker
3. **Para Demos:** La GUI en Docker funciona bien con VcXsrv/X410
4. **Para CI/CD:** Solo usa la versión de consola

---

## 🎯 Ejemplo Completo - Windows

```powershell
# 1. Iniciar VcXsrv (ejecutar XLaunch manualmente)

# 2. Construir y ejecutar
cd C:\Users\aleja\IdeaProjects\cosmeticos_Mercy
docker-compose build
docker-compose up -d mariadb
timeout /t 5
docker-compose up app-gui

# 3. La ventana de la aplicación debería aparecer
# 4. Para detener: Ctrl+C
```

## 🎯 Ejemplo Completo - Linux

```bash
# 1. Permitir X11
xhost +local:docker

# 2. Construir y ejecutar
cd ~/projects/cosmeticos_Mercy
docker-compose build
docker-compose up -d mariadb
sleep 5
docker-compose up app-gui

# 3. Al terminar
xhost -local:docker
```

---

## ℹ️ Notas Adicionales

- La GUI en Docker es principalmente para testing/demos
- Para uso diario, se recomienda ejecutar nativamente
- El modo consola es más ligero y no requiere configuración X11
- PhpMyAdmin siempre está disponible en http://localhost:8080

---

**¿Problemas?** Consulta los logs:
```bash
docker-compose logs app-gui
```

