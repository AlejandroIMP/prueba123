package org.cosmeticos.dao;

import org.cosmeticos.model.Venta;
import org.cosmeticos.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    // CREATE - Insertar una nueva venta
    public boolean insertarVenta(Venta venta) {
        String sql = "INSERT INTO ventas (codigo_producto, cantidad_vendida, fecha_venta, total) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, venta.getCodigoProducto());
            stmt.setInt(2, venta.getCantidadVendida());
            stmt.setDate(3, Date.valueOf(venta.getFechaVenta()));
            stmt.setBigDecimal(4, venta.getTotal());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        venta.setIdVenta(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar venta: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // READ - Obtener venta por ID
    public Venta obtenerVentaPorId(int idVenta) {
        String sql = "SELECT * FROM ventas WHERE id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearVenta(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener venta: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // READ - Obtener todas las ventas
    public List<Venta> obtenerTodasLasVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas ORDER BY fecha_venta DESC, id_venta DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }

    // READ - Obtener ventas por producto
    public List<Venta> obtenerVentasPorProducto(int codigoProducto) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE codigo_producto = ? ORDER BY fecha_venta DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codigoProducto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por producto: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }

    // UPDATE - Actualizar una venta
    public boolean actualizarVenta(Venta venta) {
        String sql = "UPDATE ventas SET codigo_producto = ?, cantidad_vendida = ?, " +
                     "fecha_venta = ?, total = ? WHERE id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, venta.getCodigoProducto());
            stmt.setInt(2, venta.getCantidadVendida());
            stmt.setDate(3, Date.valueOf(venta.getFechaVenta()));
            stmt.setBigDecimal(4, venta.getTotal());
            stmt.setInt(5, venta.getIdVenta());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar venta: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // DELETE - Eliminar una venta
    public boolean eliminarVenta(int idVenta) {
        String sql = "DELETE FROM ventas WHERE id_venta = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idVenta);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Método auxiliar para mapear ResultSet a Venta
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        return new Venta(
            rs.getInt("id_venta"),
            rs.getInt("codigo_producto"),
            rs.getInt("cantidad_vendida"),
            rs.getDate("fecha_venta").toLocalDate(),
            rs.getBigDecimal("total")
        );
    }
}

