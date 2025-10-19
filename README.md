# Sistema de Gestión - Cosméticos Mercy

Sistema completo de gestión de inventario y ventas para Cosméticos Mercy con conexión a MariaDB.

## 📋 Características

- ✅ CRUD completo de productos
- ✅ CRUD completo de ventas
- ✅ Gestión automática de inventario
- ✅ Búsqueda y filtrado de productos
- ✅ Reportes y consultas
- ✅ Arquitectura en capas (Model, DAO, Service)
- ✅ Lógica reutilizable para consola e interfaz gráfica

## 🏗️ Arquitectura del Proyecto

```
src/main/java/org/cosmeticos/
├── model/              # Entidades (Producto, Venta)
├── dao/                # Acceso a datos (ProductoDAO, VentaDAO)
├── service/            # Lógica de negocio (ProductoService, VentaService)
├── util/               # Utilidades (DatabaseConnection)
└── console/            # Aplicación de consola
```

## 🔧 Configuración Inicial

### 1. Prerrequisitos

- Java 21 o superior
- MariaDB/HeidiSQL instalado y ejecutándose
- Maven

### 2. Configurar Base de Datos

1. Abre HeidiSQL y conéctate a tu servidor MariaDB
2. Ejecuta el script SQL ubicado en `database_setup.sql`
3. Verifica que la base de datos `cosmeticos_mercy` se haya creado correctamente

### 3. Configurar Conexión

Edita el archivo `src/main/resources/database.properties` con tus credenciales:

```properties
db.url=jdbc:mariadb://localhost:3306/cosmeticos_mercy
db.username=root
db.password=tu_contraseña
db.driver=org.mariadb.jdbc.Driver
```

### 4. Compilar el Proyecto

```bash
mvn clean compile
```

## ▶️ Ejecutar la Aplicación

### Desde la consola:

```bash
mvn exec:java -Dexec.mainClass="org.cosmeticos.console.ConsoleApp"
```

### Desde IntelliJ IDEA:

1. Abre el archivo `ConsoleApp.java`
2. Click derecho → Run 'ConsoleApp.main()'

## 📚 Uso de la Aplicación

### Menú Principal

La aplicación ofrece tres módulos principales:

1. **Gestión de Productos**
   - Agregar nuevos productos
   - Buscar por código o nombre
   - Listar todos los productos
   - Filtrar por categoría
   - Actualizar información
   - Eliminar productos
   - Actualizar inventario

2. **Gestión de Ventas**
   - Registrar nueva venta (actualiza inventario automáticamente)
   - Buscar venta por ID
   - Listar todas las ventas
   - Ver ventas por producto
   - Cancelar venta (devuelve al inventario)

3. **Consultas y Reportes**
   - Resumen de inventario
   - Productos con bajo stock
   - Productos por categoría

## 💻 Uso Programático

### Ejemplo: Gestión de Productos

```java
ProductoService productoService = new ProductoService();

// Crear producto
Producto producto = new Producto(
    "Labial Rosa",
    new BigDecimal("75.00"),
    50,
    "Maquillaje",
    LocalDate.now()
);
productoService.crearProducto(producto);

// Buscar producto
Producto encontrado = productoService.obtenerProducto(1);

// Listar todos
List<Producto> productos = productoService.listarTodosLosProductos();

// Actualizar
producto.setCantidad(100);
productoService.actualizarProducto(producto);

// Eliminar
productoService.eliminarProducto(1);
```

### Ejemplo: Gestión de Ventas

```java
VentaService ventaService = new VentaService();

// Registrar venta (actualiza inventario automáticamente)
ventaService.registrarVenta(
    1,                    // Código del producto
    5,                    // Cantidad
    LocalDate.now()       // Fecha
);

// Listar ventas
List<Venta> ventas = ventaService.listarTodasLasVentas();

// Cancelar venta (devuelve productos al inventario)
ventaService.cancelarVenta(1);
```

## 🗄️ Estructura de la Base de Datos

### Tabla: productos
- `codigo_producto` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `nombre_producto` (VARCHAR(100))
- `precio_unitario` (DECIMAL(10,2))
- `cantidad` (INT)
- `categoria` (VARCHAR(50))
- `fecha_ingreso` (DATE)

### Tabla: ventas
- `id_venta` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `codigo_producto` (INT, FOREIGN KEY)
- `cantidad_vendida` (INT)
- `fecha_venta` (DATE)
- `total` (DECIMAL(10,2))

## 🔐 Validaciones Implementadas

- Nombres de productos no vacíos
- Precios mayores a 0
- Cantidades no negativas
- Verificación de stock antes de vender
- Validación de existencia de productos antes de operaciones
- Control de transacciones en ventas

## 🚀 Extensión a Interfaz Gráfica

La arquitectura está diseñada para reutilizar la lógica:

```java
// En tu GUI (Swing, JavaFX, etc.)
public class ProductoGUI {
    private ProductoService productoService = new ProductoService();
    
    public void botonGuardarClick() {
        Producto producto = obtenerDatosFormulario();
        if (productoService.crearProducto(producto)) {
            mostrarMensaje("Producto guardado");
            actualizarTabla();
        }
    }
}
```

## 📝 Categorías Disponibles

- Maquillaje
- Cuidado Facial
- Cuidado Capilar
- Fragancias

## ⚠️ Notas Importantes

1. **Contraseña de Base de Datos**: Asegúrate de configurar correctamente la contraseña en `database.properties`
2. **Puerto de MariaDB**: El puerto predeterminado es 3306, cámbialo si usas otro
3. **Respaldo**: Realiza respaldos periódicos de la base de datos
4. **Ventas**: Al registrar una venta, el inventario se actualiza automáticamente
5. **Cancelación**: Solo cancela ventas si realmente deseas devolver el stock

## 🛠️ Solución de Problemas

### Error de conexión a la base de datos
- Verifica que MariaDB esté ejecutándose
- Confirma usuario y contraseña en `database.properties`
- Asegúrate de que la base de datos existe

### Error "Access denied"
- Verifica las credenciales en `database.properties`
- Asegúrate de que el usuario tiene permisos en la base de datos

### No se encuentra el archivo database.properties
- Verifica que esté en `src/main/resources/`
- Ejecuta `mvn clean compile` nuevamente

## 👨‍💻 Desarrollado con

- Java 21
- MariaDB JDBC Driver 3.3.0
- Maven
- Arquitectura en capas
- Patrones DAO y Service

---

**¡Sistema listo para usar! 🎉**

