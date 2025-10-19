package org.cosmeticos.gui;

import org.cosmeticos.model.Producto;
import org.cosmeticos.model.Venta;
import org.cosmeticos.service.ProductoService;
import org.cosmeticos.service.VentaService;
import org.cosmeticos.util.ReportExporter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportesPanel extends JPanel {
    private final ProductoService productoService;
    private final VentaService ventaService;
    private JLabel lblTotalProductos, lblTotalUnidades, lblValorInventario;
    private JLabel lblTotalVentas, lblTotalRecaudado;
    private JTable tablaBajoStock, tablaCategoria;
    private DefaultTableModel modeloBajoStock, modeloCategoria;
    private JSpinner spinnerStockMinimo;

    public ReportesPanel() {
        this.productoService = new ProductoService();
        this.ventaService = new VentaService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        cargarReportes();
    }

    private void initComponents() {
        // Panel principal con grid
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Panel superior - Estadísticas
        JPanel panelEstadisticas = createEstadisticasPanel();
        mainPanel.add(panelEstadisticas);

        // Panel inferior - Tablas
        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel panelBajoStock = createBajoStockPanel();
        JPanel panelCategoria = createCategoriaPanel();

        panelTablas.add(panelBajoStock);
        panelTablas.add(panelCategoria);

        mainPanel.add(panelTablas);

        add(mainPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton btnActualizar = createButton("🔄 Actualizar Reportes", new Color(52, 152, 219));
        btnActualizar.addActionListener(e -> cargarReportes());
        panelBotones.add(btnActualizar);

        JButton btnExportar = createButton("📄 Exportar Reporte", new Color(149, 165, 166));
        btnExportar.addActionListener(e -> exportarReporte());
        panelBotones.add(btnExportar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel createEstadisticasPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Panel de inventario
        JPanel panelInventario = new JPanel(new GridLayout(4, 1, 5, 5));
        panelInventario.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 3),
            "📦 Estadísticas de Inventario",
            0, 0, new Font("Arial", Font.BOLD, 14)
        ));

        lblTotalProductos = createStatLabel("Total de Productos: 0");
        lblTotalUnidades = createStatLabel("Total de Unidades: 0");
        lblValorInventario = createStatLabel("Valor del Inventario: $0.00");

        panelInventario.add(lblTotalProductos);
        panelInventario.add(lblTotalUnidades);
        panelInventario.add(lblValorInventario);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Stock mínimo:"));
        spinnerStockMinimo = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
        spinnerStockMinimo.addChangeListener(e -> actualizarBajoStock());
        controlPanel.add(spinnerStockMinimo);
        panelInventario.add(controlPanel);

        // Panel de ventas
        JPanel panelVentas = new JPanel(new GridLayout(3, 1, 5, 5));
        panelVentas.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(231, 76, 60), 3),
            "💰 Estadísticas de Ventas",
            0, 0, new Font("Arial", Font.BOLD, 14)
        ));

        lblTotalVentas = createStatLabel("Total de Ventas: 0");
        lblTotalRecaudado = createStatLabel("Total Recaudado: $0.00");

        panelVentas.add(lblTotalVentas);
        panelVentas.add(lblTotalRecaudado);
        panelVentas.add(new JLabel("")); // Spacer

        panel.add(panelInventario);
        panel.add(panelVentas);

        return panel;
    }

    private JPanel createBajoStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(241, 196, 15), 2),
            "⚠️ Productos con Bajo Stock",
            0, 0, new Font("Arial", Font.BOLD, 13)
        ));

        String[] columnas = {"Código", "Producto", "Stock", "Categoría"};
        modeloBajoStock = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaBajoStock = new JTable(modeloBajoStock);
        tablaBajoStock.setRowHeight(22);
        tablaBajoStock.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tablaBajoStock.getTableHeader().setBackground(new Color(241, 196, 15));
        tablaBajoStock.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tablaBajoStock);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCategoriaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
            "📊 Resumen por Categoría",
            0, 0, new Font("Arial", Font.BOLD, 13)
        ));

        String[] columnas = {"Categoría", "Productos", "Unidades", "Valor"};
        modeloCategoria = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCategoria = new JTable(modeloCategoria);
        tablaCategoria.setRowHeight(22);
        tablaCategoria.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tablaCategoria.getTableHeader().setBackground(new Color(155, 89, 182));
        tablaCategoria.getTableHeader().setForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(tablaCategoria);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return label;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void cargarReportes() {
        cargarEstadisticasInventario();
        cargarEstadisticasVentas();
        actualizarBajoStock();
        cargarResumenCategoria();
    }

    private void cargarEstadisticasInventario() {
        List<Producto> productos = productoService.listarTodosLosProductos();

        int totalProductos = productos.size();
        int totalUnidades = 0;
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (Producto p : productos) {
            totalUnidades += p.getCantidad();
            valorTotal = valorTotal.add(
                p.getPrecioUnitario().multiply(BigDecimal.valueOf(p.getCantidad()))
            );
        }

        lblTotalProductos.setText("Total de Productos: " + totalProductos);
        lblTotalUnidades.setText("Total de Unidades: " + totalUnidades);
        lblValorInventario.setText(String.format("Valor del Inventario: $%,.2f", valorTotal));
    }

    private void cargarEstadisticasVentas() {
        List<Venta> ventas = ventaService.listarTodasLasVentas();

        int totalVentas = ventas.size();
        BigDecimal totalRecaudado = BigDecimal.ZERO;

        for (Venta v : ventas) {
            totalRecaudado = totalRecaudado.add(v.getTotal());
        }

        lblTotalVentas.setText("Total de Ventas: " + totalVentas);
        lblTotalRecaudado.setText(String.format("Total Recaudado: $%,.2f", totalRecaudado));
    }

    private void actualizarBajoStock() {
        modeloBajoStock.setRowCount(0);

        int stockMinimo = (Integer) spinnerStockMinimo.getValue();
        List<Producto> productos = productoService.listarTodosLosProductos();

        List<Producto> bajoStock = productos.stream()
            .filter(p -> p.getCantidad() <= stockMinimo)
            .collect(Collectors.toList());

        for (Producto p : bajoStock) {
            Object[] fila = {
                p.getCodigoProducto(),
                p.getNombreProducto(),
                p.getCantidad(),
                p.getCategoria()
            };
            modeloBajoStock.addRow(fila);
        }

        // Cambiar color de fondo si hay productos con bajo stock
        if (bajoStock.isEmpty()) {
            tablaBajoStock.setBackground(Color.WHITE);
        } else {
            tablaBajoStock.setBackground(new Color(255, 243, 205));
        }
    }

    private void cargarResumenCategoria() {
        modeloCategoria.setRowCount(0);

        List<Producto> productos = productoService.listarTodosLosProductos();

        // Agrupar por categoría
        Map<String, List<Producto>> porCategoria = productos.stream()
            .collect(Collectors.groupingBy(Producto::getCategoria));

        for (Map.Entry<String, List<Producto>> entry : porCategoria.entrySet()) {
            String categoria = entry.getKey();
            List<Producto> productosCategoria = entry.getValue();

            int totalProductos = productosCategoria.size();
            int totalUnidades = 0;
            BigDecimal valorTotal = BigDecimal.ZERO;

            for (Producto p : productosCategoria) {
                totalUnidades += p.getCantidad();
                valorTotal = valorTotal.add(
                    p.getPrecioUnitario().multiply(BigDecimal.valueOf(p.getCantidad()))
                );
            }

            Object[] fila = {
                categoria,
                totalProductos,
                totalUnidades,
                String.format("$%,.2f", valorTotal)
            };
            modeloCategoria.addRow(fila);
        }
    }

    private void exportarReporte() {
        // Crear diálogo de opciones de exportación
        JDialog dialogoExportar = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Exportar Reporte", true);
        dialogoExportar.setLayout(new BorderLayout(10, 10));
        dialogoExportar.setSize(450, 300);
        dialogoExportar.setLocationRelativeTo(this);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("📊 Seleccione el formato de exportación");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titulo, BorderLayout.NORTH);

        // Panel de botones
        JPanel botonesPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        botonesPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Botón PDF
        JButton btnPDF = new JButton("📄 Exportar a PDF (Profesional)");
        btnPDF.setFont(new Font("Arial", Font.BOLD, 14));
        btnPDF.setBackground(new Color(220, 53, 69));
        btnPDF.setForeground(Color.BLACK);
        btnPDF.setFocusPainted(false);
        btnPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPDF.setPreferredSize(new Dimension(300, 45));
        btnPDF.addActionListener(e -> {
            dialogoExportar.dispose();
            exportarAPDF();
        });

        // Botón CSV
        JButton btnCSV = new JButton("📊 Exportar a CSV (Excel)");
        btnCSV.setFont(new Font("Arial", Font.BOLD, 14));
        btnCSV.setBackground(new Color(40, 167, 69));
        btnCSV.setForeground(Color.BLACK);
        btnCSV.setFocusPainted(false);
        btnCSV.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCSV.setPreferredSize(new Dimension(300, 45));
        btnCSV.addActionListener(e -> {
            dialogoExportar.dispose();
            exportarACSV();
        });

        // Botón Vista Previa
        JButton btnVistaPrevia = new JButton("👁️ Vista Previa en Pantalla");
        btnVistaPrevia.setFont(new Font("Arial", Font.BOLD, 14));
        btnVistaPrevia.setBackground(new Color(0, 123, 255));
        btnVistaPrevia.setForeground(Color.BLACK);
        btnVistaPrevia.setFocusPainted(false);
        btnVistaPrevia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVistaPrevia.setPreferredSize(new Dimension(300, 45));
        btnVistaPrevia.addActionListener(e -> {
            dialogoExportar.dispose();
            mostrarVistaPreviaReporte();
        });

        // Botón Cancelar
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dialogoExportar.dispose());

        botonesPanel.add(btnPDF);
        botonesPanel.add(btnCSV);
        botonesPanel.add(btnVistaPrevia);
        botonesPanel.add(btnCancelar);

        mainPanel.add(botonesPanel, BorderLayout.CENTER);

        dialogoExportar.add(mainPanel);
        dialogoExportar.setVisible(true);
    }

    /**
     * Exporta el reporte a PDF con diseño profesional
     */
    private void exportarAPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte en PDF");

        // Nombre sugerido con fecha
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        fileChooser.setSelectedFile(new File("Reporte_CosmeticosMercy_" + fechaHoy + ".pdf"));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos PDF (*.pdf)", "pdf");
        fileChooser.setFileFilter(filter);

        int resultado = fileChooser.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String rutaArchivo = archivo.getAbsolutePath();

            // Asegurar extensión .pdf
            if (!rutaArchivo.toLowerCase().endsWith(".pdf")) {
                rutaArchivo += ".pdf";
            }

            try {
                // Preparar datos para el PDF
                String tituloReporte = "REPORTE DE GESTIÓN DE INVENTARIO Y VENTAS";
                String contenidoTexto = generarResumenEjecutivo();

                String[][] tablaInventario = prepararTablaInventario();
                String[][] tablaVentas = prepararTablaVentas();
                String[][] tablaBajoStock = prepararTablaBajoStock();
                String[][] tablaCategoria = prepararTablaCategoria();

                // Exportar usando la clase ReportExporter
                ReportExporter.exportarPDF(rutaArchivo, tituloReporte, contenidoTexto,
                    tablaInventario, tablaVentas, tablaBajoStock, tablaCategoria);

                // Mostrar mensaje de éxito
                int opcion = JOptionPane.showConfirmDialog(this,
                    "✅ Reporte exportado exitosamente a:\n" + rutaArchivo +
                    "\n\n¿Desea abrir el archivo?",
                    "Exportación Exitosa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File(rutaArchivo));
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al exportar a PDF:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Exporta el reporte a CSV
     */
    private void exportarACSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte en CSV");

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        fileChooser.setSelectedFile(new File("Reporte_CosmeticosMercy_" + fechaHoy + ".csv"));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv");
        fileChooser.setFileFilter(filter);

        int resultado = fileChooser.showSaveDialog(this);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            String rutaArchivo = archivo.getAbsolutePath();

            if (!rutaArchivo.toLowerCase().endsWith(".csv")) {
                rutaArchivo += ".csv";
            }

            try {
                String tituloReporte = "REPORTE DE GESTIÓN - COSMÉTICOS MERCY";
                String[][] tablaInventario = prepararTablaInventario();
                String[][] tablaVentas = prepararTablaVentas();
                String[][] tablaBajoStock = prepararTablaBajoStock();
                String[][] tablaCategoria = prepararTablaCategoria();

                ReportExporter.exportarCSV(rutaArchivo, tituloReporte,
                    tablaInventario, tablaVentas, tablaBajoStock, tablaCategoria);

                int opcion = JOptionPane.showConfirmDialog(this,
                    "✅ Reporte exportado exitosamente a:\n" + rutaArchivo +
                    "\n\n¿Desea abrir el archivo?",
                    "Exportación Exitosa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File(rutaArchivo));
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al exportar a CSV:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Muestra vista previa del reporte en pantalla
     */
    private void mostrarVistaPreviaReporte() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("═══════════════════════════════════════════════════════\n");
        reporte.append("          COSMÉTICOS MERCY\n");
        reporte.append("    REPORTE DE GESTIÓN DE INVENTARIO Y VENTAS\n");
        reporte.append("═══════════════════════════════════════════════════════\n\n");

        reporte.append("Fecha de generación: ").append(LocalDate.now().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        // Estadísticas de inventario
        reporte.append("📦 ESTADÍSTICAS DE INVENTARIO\n");
        reporte.append("─────────────────────────────────────────────────────\n");
        reporte.append(lblTotalProductos.getText()).append("\n");
        reporte.append(lblTotalUnidades.getText()).append("\n");
        reporte.append(lblValorInventario.getText()).append("\n\n");

        // Estadísticas de ventas
        reporte.append("💰 ESTADÍSTICAS DE VENTAS\n");
        reporte.append("─────────────────────────────────────────────────────\n");
        reporte.append(lblTotalVentas.getText()).append("\n");
        reporte.append(lblTotalRecaudado.getText()).append("\n\n");

        // Productos con bajo stock
        reporte.append("⚠️  PRODUCTOS CON BAJO STOCK\n");
        reporte.append("─────────────────────────────────────────────────────\n");
        for (int i = 0; i < modeloBajoStock.getRowCount(); i++) {
            reporte.append(String.format("• %s - %s (Stock: %s)\n",
                modeloBajoStock.getValueAt(i, 0),
                modeloBajoStock.getValueAt(i, 1),
                modeloBajoStock.getValueAt(i, 2)
            ));
        }
        if (modeloBajoStock.getRowCount() == 0) {
            reporte.append("No hay productos con bajo stock\n");
        }
        reporte.append("\n");

        // Resumen por categoría
        reporte.append("📊 RESUMEN POR CATEGORÍA\n");
        reporte.append("─────────────────────────────────────────────────────\n");
        for (int i = 0; i < modeloCategoria.getRowCount(); i++) {
            reporte.append(String.format("%s: %s productos, %s unidades, %s\n",
                modeloCategoria.getValueAt(i, 0),
                modeloCategoria.getValueAt(i, 1),
                modeloCategoria.getValueAt(i, 2),
                modeloCategoria.getValueAt(i, 3)
            ));
        }

        reporte.append("\n═══════════════════════════════════════════════════════\n");
        reporte.append("Documento confidencial - Solo para uso interno\n");
        reporte.append("© 2025 Cosméticos Mercy. Todos los derechos reservados.\n");
        reporte.append("═══════════════════════════════════════════════════════\n");

        // Mostrar en diálogo
        JTextArea textArea = new JTextArea(reporte.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(700, 600));

        JOptionPane.showMessageDialog(this,
            scrollPane,
            "Vista Previa - Reporte del Sistema",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Métodos auxiliares para preparar datos

    private String generarResumenEjecutivo() {
        return lblTotalProductos.getText() + "\n" +
               lblTotalUnidades.getText() + "\n" +
               lblValorInventario.getText() + "\n" +
               lblTotalVentas.getText() + "\n" +
               lblTotalRecaudado.getText();
    }

    private String[][] prepararTablaInventario() {
        List<String[]> datos = new ArrayList<>();
        datos.add(new String[]{"Métrica", "Valor"});
        datos.add(new String[]{lblTotalProductos.getText().split(":")[0],
                                lblTotalProductos.getText().split(":")[1].trim()});
        datos.add(new String[]{lblTotalUnidades.getText().split(":")[0],
                                lblTotalUnidades.getText().split(":")[1].trim()});
        datos.add(new String[]{lblValorInventario.getText().split(":")[0],
                                lblValorInventario.getText().split(":")[1].trim()});
        return datos.toArray(new String[0][]);
    }

    private String[][] prepararTablaVentas() {
        List<String[]> datos = new ArrayList<>();
        datos.add(new String[]{"Métrica", "Valor"});
        datos.add(new String[]{lblTotalVentas.getText().split(":")[0],
                                lblTotalVentas.getText().split(":")[1].trim()});
        datos.add(new String[]{lblTotalRecaudado.getText().split(":")[0],
                                lblTotalRecaudado.getText().split(":")[1].trim()});
        return datos.toArray(new String[0][]);
    }

    private String[][] prepararTablaBajoStock() {
        List<String[]> datos = new ArrayList<>();
        datos.add(new String[]{"Código", "Producto", "Stock", "Categoría"});

        for (int i = 0; i < modeloBajoStock.getRowCount(); i++) {
            datos.add(new String[]{
                modeloBajoStock.getValueAt(i, 0).toString(),
                modeloBajoStock.getValueAt(i, 1).toString(),
                modeloBajoStock.getValueAt(i, 2).toString(),
                modeloBajoStock.getValueAt(i, 3).toString()
            });
        }

        return datos.toArray(new String[0][]);
    }

    private String[][] prepararTablaCategoria() {
        List<String[]> datos = new ArrayList<>();
        datos.add(new String[]{"Categoría", "Productos", "Unidades", "Valor"});

        for (int i = 0; i < modeloCategoria.getRowCount(); i++) {
            datos.add(new String[]{
                modeloCategoria.getValueAt(i, 0).toString(),
                modeloCategoria.getValueAt(i, 1).toString(),
                modeloCategoria.getValueAt(i, 2).toString(),
                modeloCategoria.getValueAt(i, 3).toString()
            });
        }

        return datos.toArray(new String[0][]);
    }
}
