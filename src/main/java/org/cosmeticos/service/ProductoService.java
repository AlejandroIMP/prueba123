package org.cosmeticos.service;

import org.cosmeticos.dao.ProductoDAO;
import org.cosmeticos.model.Producto;

import java.util.List;

public class ProductoService {
    private final ProductoDAO productoDAO;

    public ProductoService() {
        this.productoDAO = new ProductoDAO();
    }

    // Crear producto
    public boolean crearProducto(Producto producto) {
        // Validaciones de negocio
        if (producto.getNombreProducto() == null || producto.getNombreProducto().trim().isEmpty()) {
            System.err.println("El nombre del producto no puede estar vacío");
            return false;
        }

        if (producto.getPrecioUnitario() == null || producto.getPrecioUnitario().doubleValue() <= 0) {
            System.err.println("El precio debe ser mayor a 0");
            return false;
        }

        if (producto.getCantidad() < 0) {
            System.err.println("La cantidad no puede ser negativa");
            return false;
        }

        return productoDAO.insertarProducto(producto);
    }

    // Obtener producto por código
    public Producto obtenerProducto(int codigo) {
        return productoDAO.obtenerProductoPorCodigo(codigo);
    }

    // Listar todos los productos
    public List<Producto> listarTodosLosProductos() {
        return productoDAO.obtenerTodosLosProductos();
    }

    // Listar productos por categoría
    public List<Producto> listarProductosPorCategoria(String categoria) {
        return productoDAO.obtenerProductosPorCategoria(categoria);
    }

    // Buscar productos por nombre
    public List<Producto> buscarProductos(String nombre) {
        return productoDAO.buscarProductosPorNombre(nombre);
    }

    // Actualizar producto
    public boolean actualizarProducto(Producto producto) {
        // Validaciones de negocio
        if (producto.getCodigoProducto() == null) {
            System.err.println("El código del producto es requerido");
            return false;
        }

        if (producto.getNombreProducto() == null || producto.getNombreProducto().trim().isEmpty()) {
            System.err.println("El nombre del producto no puede estar vacío");
            return false;
        }

        if (producto.getPrecioUnitario() == null || producto.getPrecioUnitario().doubleValue() <= 0) {
            System.err.println("El precio debe ser mayor a 0");
            return false;
        }

        if (producto.getCantidad() < 0) {
            System.err.println("La cantidad no puede ser negativa");
            return false;
        }

        return productoDAO.actualizarProducto(producto);
    }

    // Eliminar producto
    public boolean eliminarProducto(int codigo) {
        // Verificar que el producto existe
        Producto producto = productoDAO.obtenerProductoPorCodigo(codigo);
        if (producto == null) {
            System.err.println("El producto con código " + codigo + " no existe");
            return false;
        }

        return productoDAO.eliminarProducto(codigo);
    }

    // Actualizar inventario (aumentar o disminuir stock)
    public boolean actualizarInventario(int codigo, int cantidadCambio) {
        Producto producto = productoDAO.obtenerProductoPorCodigo(codigo);

        if (producto == null) {
            System.err.println("Producto no encontrado");
            return false;
        }

        int nuevaCantidad = producto.getCantidad() + cantidadCambio;

        if (nuevaCantidad < 0) {
            System.err.println("No hay suficiente inventario. Stock actual: " + producto.getCantidad());
            return false;
        }

        producto.setCantidad(nuevaCantidad);
        return productoDAO.actualizarProducto(producto);
    }

    // Verificar disponibilidad de stock
    public boolean verificarStock(int codigo, int cantidadRequerida) {
        Producto producto = productoDAO.obtenerProductoPorCodigo(codigo);

        if (producto == null) {
            return false;
        }

        return producto.getCantidad() >= cantidadRequerida;
    }
}

