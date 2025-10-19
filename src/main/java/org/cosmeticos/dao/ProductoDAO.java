package org.cosmeticos.dao;

import org.cosmeticos.model.Producto;
import org.cosmeticos.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // CREATE - Insertar un nuevo producto
    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO productos (nombre_producto, precio_unitario, cantidad, categoria, fecha_ingreso) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, producto.getNombreProducto());
            stmt.setBigDecimal(2, producto.getPrecioUnitario());
            stmt.setInt(3, producto.getCantidad());
            stmt.setString(4, producto.getCategoria());
            stmt.setDate(5, Date.valueOf(producto.getFechaIngreso()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setCodigoProducto(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // READ - Obtener producto por código
    public Producto obtenerProductoPorCodigo(int codigo) {
        String sql = "SELECT * FROM productos WHERE codigo_producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener producto: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // READ - Obtener todos los productos
    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos ORDER BY codigo_producto";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // READ - Obtener productos por categoría
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE categoria = ? ORDER BY nombre_producto";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // UPDATE - Actualizar un producto
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE productos SET nombre_producto = ?, precio_unitario = ?, " +
                     "cantidad = ?, categoria = ?, fecha_ingreso = ? WHERE codigo_producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, producto.getNombreProducto());
            stmt.setBigDecimal(2, producto.getPrecioUnitario());
            stmt.setInt(3, producto.getCantidad());
            stmt.setString(4, producto.getCategoria());
            stmt.setDate(5, Date.valueOf(producto.getFechaIngreso()));
            stmt.setInt(6, producto.getCodigoProducto());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // DELETE - Eliminar un producto
    public boolean eliminarProducto(int codigo) {
        String sql = "DELETE FROM productos WHERE codigo_producto = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codigo);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Método para buscar productos por nombre
    public List<Producto> buscarProductosPorNombre(String nombre) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE nombre_producto LIKE ? ORDER BY nombre_producto";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // Método auxiliar para mapear ResultSet a Producto
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("codigo_producto"),
            rs.getString("nombre_producto"),
            rs.getBigDecimal("precio_unitario"),
            rs.getInt("cantidad"),
            rs.getString("categoria"),
            rs.getDate("fecha_ingreso").toLocalDate()
        );
    }
}

