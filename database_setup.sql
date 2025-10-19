-- =============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS
-- Sistema de Gestión - Cosméticos Mercy
-- =============================================

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS cosmeticos_mercy
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE cosmeticos_mercy;

-- Tabla principal de productos
CREATE TABLE productos (
    codigo_producto INT PRIMARY KEY AUTO_INCREMENT,
    nombre_producto VARCHAR(100) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    categoria VARCHAR(50) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    INDEX idx_categoria (categoria),
    INDEX idx_nombre (nombre_producto)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

-- Insertar productos de ejemplo
INSERT INTO productos (nombre_producto, precio_unitario, cantidad, categoria, fecha_ingreso) VALUES
('Labial Mate Rojo Intenso', 85.50, 50, 'Maquillaje', '2024-10-01'),
('Crema Facial Hidratante 50ml', 150.00, 30, 'Cuidado Facial', '2024-10-05'),
('Shampoo Reparador 400ml', 95.00, 40, 'Cuidado Capilar', '2024-10-10'),
('Perfume Floral Mujer 50ml', 250.00, 20, 'Fragancias', '2024-10-12'),
('Delineador Negro Waterproof', 65.00, 60, 'Maquillaje', '2024-10-15'),
('Mascarilla Facial Carbón', 120.00, 25, 'Cuidado Facial', '2024-10-18'),
('Acondicionador Nutrición', 98.00, 35, 'Cuidado Capilar', '2024-10-20'),
('Base Líquida Mate', 180.00, 28, 'Maquillaje', '2024-10-22'),
('Serum Antiarrugas', 220.00, 15, 'Cuidado Facial', '2024-10-25'),
('Esmalte Uñas Rojo', 45.00, 70, 'Maquillaje', '2024-10-28');

-- Tabla de ventas para registrar transacciones
CREATE TABLE ventas (
    id_venta INT PRIMARY KEY AUTO_INCREMENT,
    codigo_producto INT NOT NULL,
    cantidad_vendida INT NOT NULL,
    fecha_venta DATE NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (codigo_producto) REFERENCES productos(codigo_producto)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- CONSULTAS ÚTILES
-- =============================================

-- Ver todos los productos
SELECT * FROM productos;

-- Ver productos por categoría
SELECT * FROM productos WHERE categoria = 'Maquillaje';

-- Ver total de productos y valor del inventario
SELECT
    COUNT(*) as total_productos,
    SUM(cantidad) as total_unidades,
    SUM(precio_unitario * cantidad) as valor_inventario
FROM productos;

-- Ver todas las ventas con información del producto
SELECT
    v.id_venta,
    v.fecha_venta,
    p.nombre_producto,
    v.cantidad_vendida,
    v.total
FROM ventas v
INNER JOIN productos p ON v.codigo_producto = p.codigo_producto
ORDER BY v.fecha_venta DESC;

-- Ver productos con bajo stock (menos de 20 unidades)
SELECT * FROM productos WHERE cantidad < 20;

-- Ver ventas totales por producto
SELECT
    p.nombre_producto,
    COUNT(v.id_venta) as num_ventas,
    SUM(v.cantidad_vendida) as unidades_vendidas,
    SUM(v.total) as total_ventas
FROM productos p
LEFT JOIN ventas v ON p.codigo_producto = v.codigo_producto
GROUP BY p.codigo_producto, p.nombre_producto
ORDER BY total_ventas DESC;

