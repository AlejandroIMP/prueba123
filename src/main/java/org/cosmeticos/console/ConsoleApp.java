package org.cosmeticos.console;

import org.cosmeticos.model.Producto;
import org.cosmeticos.model.Venta;
import org.cosmeticos.service.ProductoService;
import org.cosmeticos.service.VentaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductoService productoService = new ProductoService();
    private static final VentaService ventaService = new VentaService();
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  SISTEMA DE GESTIÓN - COSMÉTICOS MERCY");
        System.out.println("===========================================\n");

        boolean continuar = true;

        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> gestionarProductos();
                case 2 -> gestionarVentas();
                case 3 -> consultasReportes();
                case 0 -> {
                    System.out.println("\n¡Gracias por usar el sistema!");
                    continuar = false;
                }
                default -> System.out.println("\n⚠ Opción inválida. Intente de nuevo.\n");
            }
        }

        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n╔═══════════════════════════════════╗");
        System.out.println("║        MENÚ PRINCIPAL             ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("1. Gestión de Productos");
        System.out.println("2. Gestión de Ventas");
        System.out.println("3. Consultas y Reportes");
        System.out.println("0. Salir");
        System.out.println("───────────────────────────────────");
    }

    // ========== GESTIÓN DE PRODUCTOS ==========

    private static void gestionarProductos() {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n╔═══════════════════════════════════╗");
            System.out.println("║     GESTIÓN DE PRODUCTOS          ║");
            System.out.println("╚═══════════════════════════════════╝");
            System.out.println("1. Agregar Producto");
            System.out.println("2. Buscar Producto por Código");
            System.out.println("3. Listar Todos los Productos");
            System.out.println("4. Buscar por Nombre");
            System.out.println("5. Filtrar por Categoría");
            System.out.println("6. Actualizar Producto");
            System.out.println("7. Eliminar Producto");
            System.out.println("8. Actualizar Inventario");
            System.out.println("0. Volver al Menú Principal");
            System.out.println("───────────────────────────────────");

            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> agregarProducto();
                case 2 -> buscarProductoPorCodigo();
                case 3 -> listarTodosLosProductos();
                case 4 -> buscarProductoPorNombre();
                case 5 -> filtrarPorCategoria();
                case 6 -> actualizarProducto();
                case 7 -> eliminarProducto();
                case 8 -> actualizarInventario();
                case 0 -> continuar = false;
                default -> System.out.println("\n⚠ Opción inválida.\n");
            }
        }
    }

    private static void agregarProducto() {
        System.out.println("\n--- AGREGAR NUEVO PRODUCTO ---");

        scanner.nextLine(); // Limpiar buffer
        System.out.print("Nombre del producto: ");
        String nombre = scanner.nextLine();

        BigDecimal precio = leerBigDecimal("Precio unitario: ");
        int cantidad = leerEntero("Cantidad inicial: ");

        System.out.print("Categoría (Maquillaje/Cuidado Facial/Cuidado Capilar/Fragancias): ");
        String categoria = scanner.nextLine();

        LocalDate fecha = leerFecha("Fecha de ingreso (dd/MM/yyyy): ");

        Producto producto = new Producto(nombre, precio, cantidad, categoria, fecha);

        if (productoService.crearProducto(producto)) {
            System.out.println("✓ Producto agregado exitosamente con código: " + producto.getCodigoProducto());
        } else {
            System.out.println("✗ Error al agregar el producto");
        }
    }

    private static void buscarProductoPorCodigo() {
        System.out.println("\n--- BUSCAR PRODUCTO ---");
        int codigo = leerEntero("Ingrese el código del producto: ");

        Producto producto = productoService.obtenerProducto(codigo);

        if (producto != null) {
            mostrarProducto(producto);
        } else {
            System.out.println("✗ Producto no encontrado");
        }
    }

    private static void listarTodosLosProductos() {
        System.out.println("\n--- LISTADO DE PRODUCTOS ---");
        List<Producto> productos = productoService.listarTodosLosProductos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados");
        } else {
            mostrarListaProductos(productos);
        }
    }

    private static void buscarProductoPorNombre() {
        scanner.nextLine(); // Limpiar buffer
        System.out.print("\nIngrese el nombre a buscar: ");
        String nombre = scanner.nextLine();

        List<Producto> productos = productoService.buscarProductos(nombre);

        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos");
        } else {
            mostrarListaProductos(productos);
        }
    }

    private static void filtrarPorCategoria() {
        scanner.nextLine(); // Limpiar buffer
        System.out.print("\nIngrese la categoría: ");
        String categoria = scanner.nextLine();

        List<Producto> productos = productoService.listarProductosPorCategoria(categoria);

        if (productos.isEmpty()) {
            System.out.println("No se encontraron productos en esa categoría");
        } else {
            mostrarListaProductos(productos);
        }
    }

    private static void actualizarProducto() {
        System.out.println("\n--- ACTUALIZAR PRODUCTO ---");
        int codigo = leerEntero("Ingrese el código del producto: ");

        Producto producto = productoService.obtenerProducto(codigo);

        if (producto == null) {
            System.out.println("✗ Producto no encontrado");
            return;
        }

        System.out.println("\nProducto actual:");
        mostrarProducto(producto);

        scanner.nextLine(); // Limpiar buffer
        System.out.println("\nIngrese los nuevos datos (Enter para mantener el actual):");

        System.out.print("Nombre [" + producto.getNombreProducto() + "]: ");
        String nombre = scanner.nextLine();
        if (!nombre.isEmpty()) {
            producto.setNombreProducto(nombre);
        }

        System.out.print("Precio [" + producto.getPrecioUnitario() + "]: ");
        String precioStr = scanner.nextLine();
        if (!precioStr.isEmpty()) {
            producto.setPrecioUnitario(new BigDecimal(precioStr));
        }

        System.out.print("Cantidad [" + producto.getCantidad() + "]: ");
        String cantidadStr = scanner.nextLine();
        if (!cantidadStr.isEmpty()) {
            producto.setCantidad(Integer.parseInt(cantidadStr));
        }

        System.out.print("Categoría [" + producto.getCategoria() + "]: ");
        String categoria = scanner.nextLine();
        if (!categoria.isEmpty()) {
            producto.setCategoria(categoria);
        }

        if (productoService.actualizarProducto(producto)) {
            System.out.println("✓ Producto actualizado exitosamente");
        } else {
            System.out.println("✗ Error al actualizar el producto");
        }
    }

    private static void eliminarProducto() {
        System.out.println("\n--- ELIMINAR PRODUCTO ---");
        int codigo = leerEntero("Ingrese el código del producto: ");

        Producto producto = productoService.obtenerProducto(codigo);

        if (producto == null) {
            System.out.println("✗ Producto no encontrado");
            return;
        }

        mostrarProducto(producto);

        scanner.nextLine(); // Limpiar buffer
        System.out.print("\n¿Está seguro de eliminar este producto? (S/N): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("S")) {
            if (productoService.eliminarProducto(codigo)) {
                System.out.println("✓ Producto eliminado exitosamente");
            } else {
                System.out.println("✗ Error al eliminar el producto");
            }
        } else {
            System.out.println("Operación cancelada");
        }
    }

    private static void actualizarInventario() {
        System.out.println("\n--- ACTUALIZAR INVENTARIO ---");
        int codigo = leerEntero("Ingrese el código del producto: ");

        Producto producto = productoService.obtenerProducto(codigo);

        if (producto == null) {
            System.out.println("✗ Producto no encontrado");
            return;
        }

        System.out.println("Stock actual: " + producto.getCantidad());
        int cambio = leerEntero("Ingrese cantidad (positivo para agregar, negativo para reducir): ");

        if (productoService.actualizarInventario(codigo, cambio)) {
            System.out.println("✓ Inventario actualizado. Nuevo stock: " + (producto.getCantidad() + cambio));
        } else {
            System.out.println("✗ Error al actualizar el inventario");
        }
    }

    // ========== GESTIÓN DE VENTAS ==========

    private static void gestionarVentas() {
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n╔═══════════════════════════════════╗");
            System.out.println("║      GESTIÓN DE VENTAS            ║");
            System.out.println("╚═══════════════════════════════════╝");
            System.out.println("1. Registrar Venta");
            System.out.println("2. Buscar Venta por ID");
            System.out.println("3. Listar Todas las Ventas");
            System.out.println("4. Ventas por Producto");
            System.out.println("5. Cancelar Venta");
            System.out.println("0. Volver al Menú Principal");
            System.out.println("───────────────────────────────────");

            int opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> registrarVenta();
                case 2 -> buscarVentaPorId();
                case 3 -> listarTodasLasVentas();
                case 4 -> ventasPorProducto();
                case 5 -> cancelarVenta();
                case 0 -> continuar = false;
                default -> System.out.println("\n⚠ Opción inválida.\n");
            }
        }
    }

    private static void registrarVenta() {
        System.out.println("\n--- REGISTRAR VENTA ---");

        int codigoProducto = leerEntero("Código del producto: ");

        Producto producto = productoService.obtenerProducto(codigoProducto);

        if (producto == null) {
            System.out.println("✗ Producto no encontrado");
            return;
        }

        System.out.println("\nProducto: " + producto.getNombreProducto());
        System.out.println("Precio: $" + producto.getPrecioUnitario());
        System.out.println("Stock disponible: " + producto.getCantidad());

        int cantidad = leerEntero("\nCantidad a vender: ");

        LocalDate fecha = leerFecha("Fecha de venta (dd/MM/yyyy): ");

        if (ventaService.registrarVenta(codigoProducto, cantidad, fecha)) {
            BigDecimal total = producto.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));
            System.out.println("✓ Venta registrada exitosamente");
            System.out.println("Total: $" + total);
        } else {
            System.out.println("✗ Error al registrar la venta");
        }
    }

    private static void buscarVentaPorId() {
        System.out.println("\n--- BUSCAR VENTA ---");
        int id = leerEntero("Ingrese el ID de la venta: ");

        Venta venta = ventaService.obtenerVenta(id);

        if (venta != null) {
            mostrarVenta(venta);
        } else {
            System.out.println("✗ Venta no encontrada");
        }
    }

    private static void listarTodasLasVentas() {
        System.out.println("\n--- LISTADO DE VENTAS ---");
        List<Venta> ventas = ventaService.listarTodasLasVentas();

        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas");
        } else {
            mostrarListaVentas(ventas);
        }
    }

    private static void ventasPorProducto() {
        int codigoProducto = leerEntero("\nIngrese el código del producto: ");

        List<Venta> ventas = ventaService.listarVentasPorProducto(codigoProducto);

        if (ventas.isEmpty()) {
            System.out.println("No hay ventas para este producto");
        } else {
            mostrarListaVentas(ventas);
        }
    }

    private static void cancelarVenta() {
        System.out.println("\n--- CANCELAR VENTA ---");
        int id = leerEntero("Ingrese el ID de la venta: ");

        Venta venta = ventaService.obtenerVenta(id);

        if (venta == null) {
            System.out.println("✗ Venta no encontrada");
            return;
        }

        mostrarVenta(venta);

        scanner.nextLine(); // Limpiar buffer
        System.out.print("\n¿Está seguro de cancelar esta venta? (S/N): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("S")) {
            if (ventaService.cancelarVenta(id)) {
                System.out.println("✓ Venta cancelada y stock devuelto");
            } else {
                System.out.println("✗ Error al cancelar la venta");
            }
        } else {
            System.out.println("Operación cancelada");
        }
    }

    // ========== CONSULTAS Y REPORTES ==========

    private static void consultasReportes() {
        System.out.println("\n╔═══════════════════════════════════╗");
        System.out.println("║    CONSULTAS Y REPORTES           ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("1. Resumen de Inventario");
        System.out.println("2. Productos con Bajo Stock");
        System.out.println("3. Productos por Categoría");
        System.out.println("0. Volver");
        System.out.println("───────────────────────────────────");

        int opcion = leerEntero("Seleccione una opción: ");

        switch (opcion) {
            case 1 -> resumenInventario();
            case 2 -> productosBajoStock();
            case 3 -> listarCategorias();
        }
    }

    private static void resumenInventario() {
        List<Producto> productos = productoService.listarTodosLosProductos();

        if (productos.isEmpty()) {
            System.out.println("\nNo hay productos en el inventario");
            return;
        }

        int totalProductos = productos.size();
        int totalUnidades = 0;
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (Producto p : productos) {
            totalUnidades += p.getCantidad();
            valorTotal = valorTotal.add(p.getPrecioUnitario().multiply(BigDecimal.valueOf(p.getCantidad())));
        }

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      RESUMEN DE INVENTARIO             ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Total de productos diferentes: " + totalProductos);
        System.out.println("Total de unidades en stock: " + totalUnidades);
        System.out.println("Valor total del inventario: $" + valorTotal);
        System.out.println("──────────────────────────────────────────");
    }

    private static void productosBajoStock() {
        int limite = leerEntero("\nIngrese el límite de stock: ");

        List<Producto> productos = productoService.listarTodosLosProductos();
        List<Producto> bajoStock = productos.stream()
                .filter(p -> p.getCantidad() <= limite)
                .toList();

        if (bajoStock.isEmpty()) {
            System.out.println("\nNo hay productos con stock bajo");
        } else {
            System.out.println("\n--- PRODUCTOS CON STOCK BAJO ---");
            mostrarListaProductos(bajoStock);
        }
    }

    private static void listarCategorias() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      PRODUCTOS POR CATEGORÍA           ║");
        System.out.println("╚════════════════════════════════════════╝");

        String[] categorias = {"Maquillaje", "Cuidado Facial", "Cuidado Capilar", "Fragancias"};

        for (String categoria : categorias) {
            List<Producto> productos = productoService.listarProductosPorCategoria(categoria);
            System.out.println("\n" + categoria + " (" + productos.size() + " productos)");
            System.out.println("─".repeat(50));

            if (!productos.isEmpty()) {
                for (Producto p : productos) {
                    System.out.printf("  [%d] %s - $%.2f (Stock: %d)%n",
                        p.getCodigoProducto(), p.getNombreProducto(),
                        p.getPrecioUnitario(), p.getCantidad());
                }
            }
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    private static void mostrarProducto(Producto p) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("  Código: " + p.getCodigoProducto());
        System.out.println("  Nombre: " + p.getNombreProducto());
        System.out.println("  Precio: $" + p.getPrecioUnitario());
        System.out.println("  Stock: " + p.getCantidad());
        System.out.println("  Categoría: " + p.getCategoria());
        System.out.println("  Fecha Ingreso: " + p.getFechaIngreso().format(dateFormatter));
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void mostrarListaProductos(List<Producto> productos) {
        System.out.println("\n┌─────┬──────────────────────────────┬─────────┬────────┬─────────────────┐");
        System.out.println("│ Cód │ Nombre                       │ Precio  │ Stock  │ Categoría       │");
        System.out.println("├─────┼──────────────────────────────┼─────────┼────────┼─────────────────┤");

        for (Producto p : productos) {
            System.out.printf("│ %-3d │ %-28s │ $%-6.2f │ %-6d │ %-15s │%n",
                p.getCodigoProducto(),
                truncar(p.getNombreProducto(), 28),
                p.getPrecioUnitario(),
                p.getCantidad(),
                truncar(p.getCategoria(), 15));
        }

        System.out.println("└─────┴──────────────────────────────┴─────────┴────────┴─────────────────┘");
        System.out.println("Total: " + productos.size() + " productos");
    }

    private static void mostrarVenta(Venta v) {
        Producto producto = productoService.obtenerProducto(v.getCodigoProducto());

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("  ID Venta: " + v.getIdVenta());
        System.out.println("  Producto: " + (producto != null ? producto.getNombreProducto() : "N/A"));
        System.out.println("  Código Producto: " + v.getCodigoProducto());
        System.out.println("  Cantidad: " + v.getCantidadVendida());
        System.out.println("  Total: $" + v.getTotal());
        System.out.println("  Fecha: " + v.getFechaVenta().format(dateFormatter));
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void mostrarListaVentas(List<Venta> ventas) {
        System.out.println("\n┌────────┬─────────────┬──────────┬────────────┬────────────┐");
        System.out.println("│ ID     │ Cód. Prod.  │ Cantidad │ Total      │ Fecha      │");
        System.out.println("├────────┼─────────────┼──────────┼────────────┼────────────┤");

        BigDecimal totalVentas = BigDecimal.ZERO;

        for (Venta v : ventas) {
            System.out.printf("│ %-6d │ %-11d │ %-8d │ $%-9.2f │ %-10s │%n",
                v.getIdVenta(),
                v.getCodigoProducto(),
                v.getCantidadVendida(),
                v.getTotal(),
                v.getFechaVenta().format(dateFormatter));

            totalVentas = totalVentas.add(v.getTotal());
        }

        System.out.println("└────────┴─────────────┴──────────┴────────────┴────────────┘");
        System.out.println("Total de ventas: " + ventas.size() + " | Total recaudado: $" + totalVentas);
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("⚠ Por favor ingrese un número válido");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }

    private static BigDecimal leerBigDecimal(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return scanner.nextBigDecimal();
            } catch (Exception e) {
                System.out.println("⚠ Por favor ingrese un número válido");
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }

    private static LocalDate leerFecha(String mensaje) {
        scanner.nextLine(); // Limpiar buffer

        while (true) {
            try {
                System.out.print(mensaje);
                String fechaStr = scanner.nextLine();

                if (fechaStr.trim().isEmpty()) {
                    return LocalDate.now();
                }

                return LocalDate.parse(fechaStr, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("⚠ Formato de fecha inválido. Use dd/MM/yyyy");
            }
        }
    }

    private static String truncar(String texto, int longitud) {
        if (texto.length() <= longitud) {
            return texto;
        }
        return texto.substring(0, longitud - 3) + "...";
    }
}

