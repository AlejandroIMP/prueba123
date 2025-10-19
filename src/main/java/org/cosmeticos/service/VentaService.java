package org.cosmeticos.service;

import org.cosmeticos.dao.ProductoDAO;
import org.cosmeticos.dao.VentaDAO;
import org.cosmeticos.model.Producto;
import org.cosmeticos.model.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class VentaService {
    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;
    private final ProductoService productoService;

    public VentaService() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.productoService = new ProductoService();
    }

    // Registrar una venta (con actualización de inventario)
    public boolean registrarVenta(int codigoProducto, int cantidad, LocalDate fechaVenta) {
        // Verificar que el producto existe
        Producto producto = productoDAO.obtenerProductoPorCodigo(codigoProducto);
        if (producto == null) {
            System.err.println("El producto no existe");
            return false;
        }

        // Verificar stock disponible
        if (!productoService.verificarStock(codigoProducto, cantidad)) {
            System.err.println("Stock insuficiente. Disponible: " + producto.getCantidad());
            return false;
        }

        // Calcular total
        BigDecimal total = producto.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));

        // Crear venta
        Venta venta = new Venta(codigoProducto, cantidad, fechaVenta, total);

        // Insertar venta
        boolean ventaCreada = ventaDAO.insertarVenta(venta);

        if (ventaCreada) {
            // Actualizar inventario (disminuir stock)
            boolean inventarioActualizado = productoService.actualizarInventario(codigoProducto, -cantidad);

            if (!inventarioActualizado) {
                System.err.println("Advertencia: La venta se registró pero no se pudo actualizar el inventario");
            }

            return true;
        }

        return false;
    }

    // Obtener venta por ID
    public Venta obtenerVenta(int idVenta) {
        return ventaDAO.obtenerVentaPorId(idVenta);
    }

    // Listar todas las ventas
    public List<Venta> listarTodasLasVentas() {
        return ventaDAO.obtenerTodasLasVentas();
    }

    // Listar ventas por producto
    public List<Venta> listarVentasPorProducto(int codigoProducto) {
        return ventaDAO.obtenerVentasPorProducto(codigoProducto);
    }

    // Actualizar venta
    public boolean actualizarVenta(Venta venta) {
        if (venta.getIdVenta() == null) {
            System.err.println("El ID de venta es requerido");
            return false;
        }

        if (venta.getCantidadVendida() <= 0) {
            System.err.println("La cantidad vendida debe ser mayor a 0");
            return false;
        }

        return ventaDAO.actualizarVenta(venta);
    }

    // Eliminar venta
    public boolean eliminarVenta(int idVenta) {
        Venta venta = ventaDAO.obtenerVentaPorId(idVenta);

        if (venta == null) {
            System.err.println("La venta no existe");
            return false;
        }

        return ventaDAO.eliminarVenta(idVenta);
    }

    // Cancelar venta (eliminar y devolver al inventario)
    public boolean cancelarVenta(int idVenta) {
        Venta venta = ventaDAO.obtenerVentaPorId(idVenta);

        if (venta == null) {
            System.err.println("La venta no existe");
            return false;
        }

        // Devolver productos al inventario
        boolean inventarioActualizado = productoService.actualizarInventario(
            venta.getCodigoProducto(),
            venta.getCantidadVendida()
        );

        if (!inventarioActualizado) {
            System.err.println("No se pudo actualizar el inventario");
            return false;
        }

        // Eliminar venta
        return ventaDAO.eliminarVenta(idVenta);
    }
}

