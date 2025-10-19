package org.cosmeticos.gui;

import org.cosmeticos.model.Producto;
import org.cosmeticos.model.Venta;
import org.cosmeticos.service.ProductoService;
import org.cosmeticos.service.VentaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentasPanel extends JPanel {
    private final VentaService ventaService;
    private final ProductoService productoService;
    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;
    private JTextField txtIdVenta, txtCodigoProducto, txtNombreProducto, txtCantidad, txtTotal;
    private JButton btnNuevaVenta, btnCancelarVenta, btnBuscarProducto;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public VentasPanel() {
        this.ventaService = new VentaService();
        this.productoService = new ProductoService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        cargarVentas();
    }

    private void initComponents() {
        // Panel superior - Formulario de venta
        JPanel panelVenta = createVentaPanel();
        add(panelVenta, BorderLayout.NORTH);

        // Panel central - Tabla de ventas
        JPanel panelTabla = createTablePanel();
        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior - Información
        JPanel panelInfo = createInfoPanel();
        add(panelInfo, BorderLayout.SOUTH);
    }

    private JPanel createVentaPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            "Registrar Venta",
            0, 0, new Font("Arial", Font.BOLD, 14)
        ));

        // Panel de campos
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID Venta
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("ID Venta:"), gbc);

        gbc.gridx = 1;
        txtIdVenta = new JTextField(10);
        txtIdVenta.setEnabled(false);
        fieldsPanel.add(txtIdVenta, gbc);

        // Código Producto
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Código Producto:"), gbc);

        gbc.gridx = 3;
        txtCodigoProducto = new JTextField(10);
        fieldsPanel.add(txtCodigoProducto, gbc);

        gbc.gridx = 4;
        btnBuscarProducto = createButton("🔍", new Color(52, 152, 219));
        btnBuscarProducto.addActionListener(e -> buscarProducto());
        fieldsPanel.add(btnBuscarProducto, gbc);

        // Nombre Producto (solo lectura)
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Producto:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 4;
        txtNombreProducto = new JTextField(30);
        txtNombreProducto.setEnabled(false);
        txtNombreProducto.setBackground(Color.LIGHT_GRAY);
        fieldsPanel.add(txtNombreProducto, gbc);

        // Cantidad
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        fieldsPanel.add(new JLabel("Cantidad:"), gbc);

        gbc.gridx = 1;
        txtCantidad = new JTextField(10);
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularTotal();
            }
        });
        fieldsPanel.add(txtCantidad, gbc);

        // Total
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Total:"), gbc);

        gbc.gridx = 3;
        txtTotal = new JTextField(10);
        txtTotal.setEnabled(false);
        txtTotal.setBackground(Color.LIGHT_GRAY);
        txtTotal.setFont(new Font("Arial", Font.BOLD, 14));
        fieldsPanel.add(txtTotal, gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnNuevaVenta = createButton("💰 Registrar Venta", new Color(46, 204, 113));
        btnNuevaVenta.addActionListener(e -> registrarVenta());

        JButton btnLimpiar = createButton("🆕 Nueva", new Color(52, 152, 219));
        btnLimpiar.addActionListener(e -> limpiarCampos());

        btnCancelarVenta = createButton("❌ Cancelar Venta Seleccionada", new Color(231, 76, 60));
        btnCancelarVenta.addActionListener(e -> cancelarVenta());

        botonesPanel.add(btnLimpiar);
        botonesPanel.add(btnNuevaVenta);
        botonesPanel.add(btnCancelarVenta);

        panel.add(botonesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            "Historial de Ventas",
            0, 0, new Font("Arial", Font.BOLD, 14)
        ));

        String[] columnas = {"ID Venta", "Cód. Producto", "Producto", "Cantidad", "Total", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVentas.setRowHeight(25);
        tablaVentas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaVentas.getTableHeader().setBackground(new Color(139, 69, 19));
        tablaVentas.getTableHeader().setForeground(Color.BLACK);

        tablaVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaVentas.getSelectedRow() != -1) {
                cargarVentaSeleccionada();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaVentas);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones de tabla
        JPanel botonesTablaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnActualizar = createButton("🔄 Actualizar", new Color(149, 165, 166));
        btnActualizar.addActionListener(e -> cargarVentas());
        botonesTablaPanel.add(btnActualizar);

        panel.add(botonesTablaPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder("Información"));

        JLabel infoLabel = new JLabel("💡 Seleccione una venta de la tabla para ver detalles o cancelarla");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(52, 73, 94));

        panel.add(infoLabel);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void buscarProducto() {
        try {
            String codigoStr = txtCodigoProducto.getText().trim();
            if (codigoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ingrese el código del producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int codigo = Integer.parseInt(codigoStr);
            Producto producto = productoService.obtenerProducto(codigo);

            if (producto != null) {
                txtNombreProducto.setText(producto.getNombreProducto() +
                    " - Precio: $" + producto.getPrecioUnitario() +
                    " - Stock: " + producto.getCantidad());
                calcularTotal();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Producto no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                txtNombreProducto.setText("");
                txtTotal.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Código inválido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calcularTotal() {
        try {
            String codigoStr = txtCodigoProducto.getText().trim();
            String cantidadStr = txtCantidad.getText().trim();

            if (!codigoStr.isEmpty() && !cantidadStr.isEmpty()) {
                int codigo = Integer.parseInt(codigoStr);
                int cantidad = Integer.parseInt(cantidadStr);

                Producto producto = productoService.obtenerProducto(codigo);
                if (producto != null) {
                    BigDecimal total = producto.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad));
                    txtTotal.setText(String.format("$%.2f", total));
                }
            }
        } catch (NumberFormatException ex) {
            txtTotal.setText("");
        }
    }

    private void registrarVenta() {
        try {
            if (txtCodigoProducto.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Busque un producto primero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (txtCantidad.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Ingrese la cantidad",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int codigoProducto = Integer.parseInt(txtCodigoProducto.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser mayor a 0",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Verificar stock
            if (!productoService.verificarStock(codigoProducto, cantidad)) {
                Producto producto = productoService.obtenerProducto(codigoProducto);
                JOptionPane.showMessageDialog(this,
                    "Stock insuficiente. Disponible: " + producto.getCantidad(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate fecha = LocalDate.now();

            if (ventaService.registrarVenta(codigoProducto, cantidad, fecha)) {
                JOptionPane.showMessageDialog(this,
                    "✓ Venta registrada exitosamente\nTotal: " + txtTotal.getText(),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarVentas();
            } else {
                JOptionPane.showMessageDialog(this,
                    "✗ Error al registrar la venta",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese valores numéricos válidos",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarVenta() {
        if (txtIdVenta.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una venta de la tabla",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de cancelar esta venta?\n" +
            "Los productos serán devueltos al inventario.",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            int idVenta = Integer.parseInt(txtIdVenta.getText().trim());

            if (ventaService.cancelarVenta(idVenta)) {
                JOptionPane.showMessageDialog(this,
                    "✓ Venta cancelada y stock devuelto al inventario",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarVentas();
            } else {
                JOptionPane.showMessageDialog(this,
                    "✗ Error al cancelar la venta",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarVentas() {
        modeloTabla.setRowCount(0);
        List<Venta> ventas = ventaService.listarTodasLasVentas();

        for (Venta v : ventas) {
            Producto producto = productoService.obtenerProducto(v.getCodigoProducto());
            String nombreProducto = producto != null ? producto.getNombreProducto() : "N/A";

            Object[] fila = {
                v.getIdVenta(),
                v.getCodigoProducto(),
                nombreProducto,
                v.getCantidadVendida(),
                String.format("$%.2f", v.getTotal()),
                v.getFechaVenta().format(dateFormatter)
            };
            modeloTabla.addRow(fila);
        }
    }

    private void cargarVentaSeleccionada() {
        int filaSeleccionada = tablaVentas.getSelectedRow();
        if (filaSeleccionada != -1) {
            txtIdVenta.setText(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
            txtCodigoProducto.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString());
            txtNombreProducto.setText(modeloTabla.getValueAt(filaSeleccionada, 2).toString());
            txtCantidad.setText(modeloTabla.getValueAt(filaSeleccionada, 3).toString());
            txtTotal.setText(modeloTabla.getValueAt(filaSeleccionada, 4).toString());
        }
    }

    private void limpiarCampos() {
        txtIdVenta.setText("");
        txtCodigoProducto.setText("");
        txtNombreProducto.setText("");
        txtCantidad.setText("");
        txtTotal.setText("");
        tablaVentas.clearSelection();
    }
}

