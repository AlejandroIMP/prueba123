package org.cosmeticos.gui;

import org.cosmeticos.model.Producto;
import org.cosmeticos.service.ProductoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProductosPanel extends JPanel {
    private final ProductoService productoService;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtCodigo, txtNombre, txtPrecio, txtCantidad, txtBuscar;
    private JComboBox<String> cmbCategoria;
    private JButton btnNuevo, btnGuardar, btnActualizar, btnEliminar, btnBuscar;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProductosPanel() {
        this.productoService = new ProductoService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        // Panel superior - Formulario
        JPanel panelFormulario = createFormPanel();
        add(panelFormulario, BorderLayout.NORTH);

        // Panel central - Tabla
        JPanel panelTabla = createTablePanel();
        add(panelTabla, BorderLayout.CENTER);

        // Panel inferior - Búsqueda
        JPanel panelBusqueda = createSearchPanel();
        add(panelBusqueda, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            "Información del Producto",
            0, 0, new Font("Arial", Font.BOLD, 14)
        ));

        // Panel de campos
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Código
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Código:"), gbc);

        gbc.gridx = 1;
        txtCodigo = new JTextField(10);
        txtCodigo.setEnabled(false);
        fieldsPanel.add(txtCodigo, gbc);

        // Nombre
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 3; gbc.gridwidth = 3;
        txtNombre = new JTextField(30);
        fieldsPanel.add(txtNombre, gbc);

        // Precio
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        fieldsPanel.add(new JLabel("Precio:"), gbc);

        gbc.gridx = 1;
        txtPrecio = new JTextField(10);
        fieldsPanel.add(txtPrecio, gbc);

        // Cantidad
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Cantidad:"), gbc);

        gbc.gridx = 3;
        txtCantidad = new JTextField(10);
        fieldsPanel.add(txtCantidad, gbc);

        // Categoría
        gbc.gridx = 4;
        fieldsPanel.add(new JLabel("Categoría:"), gbc);

        gbc.gridx = 5;
        String[] categorias = {"Maquillaje", "Cuidado Facial", "Cuidado Capilar", "Fragancias"};
        cmbCategoria = new JComboBox<>(categorias);
        fieldsPanel.add(cmbCategoria, gbc);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnNuevo = createButton("🆕 Nuevo", new Color(52, 152, 219));
        btnGuardar = createButton("💾 Guardar", new Color(46, 204, 113));
        btnActualizar = createButton("✏️ Actualizar", new Color(241, 196, 15));
        btnEliminar = createButton("🗑️ Eliminar", new Color(231, 76, 60));

//        btnNuevo.setForeground(Color.BLACK);
//        btnGuardar.setForeground(Color.BLACK);
//        btnActualizar.setForeground(Color.BLACK);
//

        btnNuevo.addActionListener(e -> limpiarCampos());
        btnGuardar.addActionListener(e -> guardarProducto());
        btnActualizar.addActionListener(e -> actualizarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());

        botonesPanel.add(btnNuevo);
        botonesPanel.add(btnGuardar);
        botonesPanel.add(btnActualizar);
        botonesPanel.add(btnEliminar);

        panel.add(botonesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
            "Listado de Productos",
            0, 0, new Font("Arial", Font.BOLD, 14)
        ));

        String[] columnas = {"Código", "Nombre", "Precio", "Stock", "Categoría", "Fecha Ingreso"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.setRowHeight(25);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(new Color(139, 69, 19));
        tablaProductos.getTableHeader().setForeground(Color.BLACK);

        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaProductos.getSelectedRow() != -1) {
                cargarProductoSeleccionado();
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Búsqueda"));

        panel.add(new JLabel("Buscar por nombre:"));

        txtBuscar = new JTextField(20);
        panel.add(txtBuscar);

        btnBuscar = createButton("🔍 Buscar", new Color(52, 152, 219));
        btnBuscar.addActionListener(e -> buscarProductos());
        panel.add(btnBuscar);

        JButton btnMostrarTodos = createButton("📋 Mostrar Todos", new Color(149, 165, 166));
        btnMostrarTodos.addActionListener(e -> cargarProductos());
        panel.add(btnMostrarTodos);

        JComboBox<String> cmbFiltroCategoria = new JComboBox<>(
            new String[]{"Todas", "Maquillaje", "Cuidado Facial", "Cuidado Capilar", "Fragancias"}
        );
        cmbFiltroCategoria.addActionListener(e -> {
            String categoria = (String) cmbFiltroCategoria.getSelectedItem();
            if ("Todas".equals(categoria)) {
                cargarProductos();
            } else {
                filtrarPorCategoria(categoria);
            }
        });

        panel.add(new JLabel("Categoría:"));
        panel.add(cmbFiltroCategoria);

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

    private void guardarProducto() {
        try {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es requerido", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nombre = txtNombre.getText().trim();
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String categoria = (String) cmbCategoria.getSelectedItem();
            LocalDate fecha = LocalDate.now();

            Producto producto = new Producto(nombre, precio, cantidad, categoria, fecha);

            if (productoService.crearProducto(producto)) {
                JOptionPane.showMessageDialog(this,
                    "✓ Producto guardado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "✗ Error al guardar el producto",
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

    private void actualizarProducto() {
        try {
            if (txtCodigo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Seleccione un producto de la tabla",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int codigo = Integer.parseInt(txtCodigo.getText().trim());
            Producto producto = productoService.obtenerProducto(codigo);

            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Producto no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            producto.setNombreProducto(txtNombre.getText().trim());
            producto.setPrecioUnitario(new BigDecimal(txtPrecio.getText().trim()));
            producto.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
            producto.setCategoria((String) cmbCategoria.getSelectedItem());

            if (productoService.actualizarProducto(producto)) {
                JOptionPane.showMessageDialog(this,
                    "✓ Producto actualizado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "✗ Error al actualizar el producto",
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

    private void eliminarProducto() {
        if (txtCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un producto de la tabla",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar este producto?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            int codigo = Integer.parseInt(txtCodigo.getText().trim());

            if (productoService.eliminarProducto(codigo)) {
                JOptionPane.showMessageDialog(this,
                    "✓ Producto eliminado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarProductos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "✗ Error al eliminar el producto",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoService.listarTodosLosProductos();

        for (Producto p : productos) {
            Object[] fila = {
                p.getCodigoProducto(),
                p.getNombreProducto(),
                String.format("$%.2f", p.getPrecioUnitario()),
                p.getCantidad(),
                p.getCategoria(),
                p.getFechaIngreso().format(dateFormatter)
            };
            modeloTabla.addRow(fila);
        }
    }

    private void buscarProductos() {
        String nombre = txtBuscar.getText().trim();
        if (nombre.isEmpty()) {
            cargarProductos();
            return;
        }

        modeloTabla.setRowCount(0);
        List<Producto> productos = productoService.buscarProductos(nombre);

        for (Producto p : productos) {
            Object[] fila = {
                p.getCodigoProducto(),
                p.getNombreProducto(),
                String.format("$%.2f", p.getPrecioUnitario()),
                p.getCantidad(),
                p.getCategoria(),
                p.getFechaIngreso().format(dateFormatter)
            };
            modeloTabla.addRow(fila);
        }

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No se encontraron productos",
                "Búsqueda",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void filtrarPorCategoria(String categoria) {
        modeloTabla.setRowCount(0);
        List<Producto> productos = productoService.listarProductosPorCategoria(categoria);

        for (Producto p : productos) {
            Object[] fila = {
                p.getCodigoProducto(),
                p.getNombreProducto(),
                String.format("$%.2f", p.getPrecioUnitario()),
                p.getCantidad(),
                p.getCategoria(),
                p.getFechaIngreso().format(dateFormatter)
            };
            modeloTabla.addRow(fila);
        }
    }

    private void cargarProductoSeleccionado() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada != -1) {
            txtCodigo.setText(modeloTabla.getValueAt(filaSeleccionada, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(filaSeleccionada, 1).toString());

            String precioStr = modeloTabla.getValueAt(filaSeleccionada, 2).toString();
            precioStr = precioStr.replace("$", "").trim();
            txtPrecio.setText(precioStr);

            txtCantidad.setText(modeloTabla.getValueAt(filaSeleccionada, 3).toString());
            cmbCategoria.setSelectedItem(modeloTabla.getValueAt(filaSeleccionada, 4).toString());
        }
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCantidad.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtBuscar.setText("");
        tablaProductos.clearSelection();
    }
}

