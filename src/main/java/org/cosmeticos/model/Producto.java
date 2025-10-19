package org.cosmeticos.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Producto {
    private Integer codigoProducto;
    private String nombreProducto;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private String categoria;
    private LocalDate fechaIngreso;

    // Constructor vacío
    public Producto() {
    }

    // Constructor completo
    public Producto(Integer codigoProducto, String nombreProducto, BigDecimal precioUnitario,
                    Integer cantidad, String categoria, LocalDate fechaIngreso) {
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.fechaIngreso = fechaIngreso;
    }

    // Constructor sin ID (para inserciones)
    public Producto(String nombreProducto, BigDecimal precioUnitario, Integer cantidad,
                    String categoria, LocalDate fechaIngreso) {
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.fechaIngreso = fechaIngreso;
    }

    // Getters y Setters
    public Integer getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(Integer codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "codigoProducto=" + codigoProducto +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", precioUnitario=" + precioUnitario +
                ", cantidad=" + cantidad +
                ", categoria='" + categoria + '\'' +
                ", fechaIngreso=" + fechaIngreso +
                '}';
    }
}

