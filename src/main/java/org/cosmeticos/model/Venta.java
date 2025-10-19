package org.cosmeticos.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Venta {
    private Integer idVenta;
    private Integer codigoProducto;
    private Integer cantidadVendida;
    private LocalDate fechaVenta;
    private BigDecimal total;

    // Constructor vacío
    public Venta() {
    }

    // Constructor completo
    public Venta(Integer idVenta, Integer codigoProducto, Integer cantidadVendida,
                 LocalDate fechaVenta, BigDecimal total) {
        this.idVenta = idVenta;
        this.codigoProducto = codigoProducto;
        this.cantidadVendida = cantidadVendida;
        this.fechaVenta = fechaVenta;
        this.total = total;
    }

    // Constructor sin ID (para inserciones)
    public Venta(Integer codigoProducto, Integer cantidadVendida, LocalDate fechaVenta, BigDecimal total) {
        this.codigoProducto = codigoProducto;
        this.cantidadVendida = cantidadVendida;
        this.fechaVenta = fechaVenta;
        this.total = total;
    }

    // Getters y Setters
    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Integer getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(Integer codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", codigoProducto=" + codigoProducto +
                ", cantidadVendida=" + cantidadVendida +
                ", fechaVenta=" + fechaVenta +
                ", total=" + total +
                '}';
    }
}

