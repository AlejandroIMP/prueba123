package org.cosmeticos.util;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportExporter {
    
    private static final String EMPRESA = "COSMÉTICOS MERCY";
    private static final String DIRECCION = "Av. Principal #123, Ciudad";
    private static final String TELEFONO = "Tel: (123) 456-7890";
    private static final String EMAIL = "info@cosmeticosmercy.com";
    private static final String WEB = "www.cosmeticosmercy.com";
    
    // Colores corporativos
    private static final DeviceRgb COLOR_PRIMARY = new DeviceRgb(139, 69, 19); // Café
    private static final DeviceRgb COLOR_SECONDARY = new DeviceRgb(255, 192, 203); // Rosa
    private static final DeviceRgb COLOR_ACCENT = new DeviceRgb(218, 165, 32); // Dorado
    
    /**
     * Exporta un reporte completo a PDF con diseño profesional
     */
    public static void exportarPDF(String rutaArchivo, String tituloReporte, 
                                    String contenidoTexto, String[][] tablaInventario,
                                    String[][] tablaVentas, String[][] tablaBajoStock,
                                    String[][] tablaCategoria) throws IOException {
        
        PdfWriter writer = new PdfWriter(rutaArchivo);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Configurar márgenes
        document.setMargins(40, 40, 40, 40);
        
        // ENCABEZADO CORPORATIVO
        agregarEncabezadoPDF(document, tituloReporte);
        
        // LÍNEA SEPARADORA
        agregarLineaSeparadora(document);
        
        // INFORMACIÓN GENERAL
        agregarSeccion(document, "RESUMEN EJECUTIVO", contenidoTexto);
        
        // ESTADÍSTICAS DE INVENTARIO
        if (tablaInventario != null && tablaInventario.length > 0) {
            agregarSeccion(document, "📦 ESTADÍSTICAS DE INVENTARIO", null);
            agregarTablaPDF(document, tablaInventario, COLOR_PRIMARY);
        }
        
        // ESTADÍSTICAS DE VENTAS
        if (tablaVentas != null && tablaVentas.length > 0) {
            agregarSeccion(document, "💰 ESTADÍSTICAS DE VENTAS", null);
            agregarTablaPDF(document, tablaVentas, new DeviceRgb(231, 76, 60));
        }
        
        // PRODUCTOS CON BAJO STOCK
        if (tablaBajoStock != null && tablaBajoStock.length > 0) {
            agregarSeccion(document, "⚠️ PRODUCTOS CON BAJO STOCK", null);
            agregarTablaPDF(document, tablaBajoStock, new DeviceRgb(241, 196, 15));
        }
        
        // RESUMEN POR CATEGORÍA
        if (tablaCategoria != null && tablaCategoria.length > 0) {
            agregarSeccion(document, "📊 RESUMEN POR CATEGORÍA", null);
            agregarTablaPDF(document, tablaCategoria, new DeviceRgb(155, 89, 182));
        }
        
        // PIE DE PÁGINA
        agregarPieDePagina(document);
        
        document.close();
    }
    
    /**
     * Agrega el encabezado corporativo al PDF
     */
    private static void agregarEncabezadoPDF(Document document, String titulo) {
        // Nombre de la empresa
        Paragraph empresa = new Paragraph(EMPRESA)
            .setFontSize(24)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(empresa);
        
        // Información de contacto
        Paragraph contacto = new Paragraph(
            DIRECCION + " | " + TELEFONO + "\n" +
            EMAIL + " | " + WEB
        )
            .setFontSize(9)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.DARK_GRAY);
        document.add(contacto);
        
        // Título del reporte
        Paragraph tituloDoc = new Paragraph(titulo)
            .setFontSize(18)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(COLOR_ACCENT)
            .setMarginTop(10);
        document.add(tituloDoc);
        
        // Fecha y hora de generación
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Paragraph fecha = new Paragraph("Generado el: " + LocalDateTime.now().format(formatter))
            .setFontSize(10)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
            .setMarginBottom(15);
        document.add(fecha);
    }
    
    /**
     * Agrega una línea separadora decorativa
     */
    private static void agregarLineaSeparadora(Document document) {
        Table separador = new Table(1);
        separador.setWidth(UnitValue.createPercentValue(100));
        Cell cell = new Cell()
            .setHeight(2)
            .setBackgroundColor(COLOR_ACCENT)
            .setBorder(null);
        separador.addCell(cell);
        document.add(separador);
        document.add(new Paragraph("\n"));
    }
    
    /**
     * Agrega una sección con título y contenido opcional
     */
    private static void agregarSeccion(Document document, String titulo, String contenido) {
        // Título de sección
        Paragraph tituloSeccion = new Paragraph(titulo)
            .setFontSize(14)
            .setBold()
            .setFontColor(COLOR_PRIMARY)
            .setMarginTop(10)
            .setMarginBottom(5);
        document.add(tituloSeccion);
        
        // Contenido si existe
        if (contenido != null && !contenido.isEmpty()) {
            Paragraph parrafo = new Paragraph(contenido)
                .setFontSize(11)
                .setMarginBottom(10);
            document.add(parrafo);
        }
    }
    
    /**
     * Agrega una tabla formateada al PDF
     */
    private static void agregarTablaPDF(Document document, String[][] datos, DeviceRgb colorEncabezado) {
        if (datos.length == 0) return;
        
        // Crear tabla con el número de columnas de la primera fila
        int numColumnas = datos[0].length;
        Table table = new Table(numColumnas);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(15);
        
        // Encabezados
        for (String encabezado : datos[0]) {
            Cell headerCell = new Cell()
                .add(new Paragraph(encabezado).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(colorEncabezado)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
            table.addHeaderCell(headerCell);
        }
        
        // Datos (alternando colores de fila)
        for (int i = 1; i < datos.length; i++) {
            DeviceRgb bgColor = (i % 2 == 0) ? new DeviceRgb(245, 245, 245) : (DeviceRgb) ColorConstants.WHITE;
            
            for (String dato : datos[i]) {
                Cell cell = new Cell()
                    .add(new Paragraph(dato != null ? dato : ""))
                    .setBackgroundColor(bgColor)
                    .setPadding(6)
                    .setFontSize(10);
                table.addCell(cell);
            }
        }
        
        document.add(table);
    }
    
    /**
     * Agrega el pie de página
     */
    private static void agregarPieDePagina(Document document) {
        agregarLineaSeparadora(document);
        
        Paragraph footer = new Paragraph(
            "Este documento es generado automáticamente por el Sistema de Gestión de " + EMPRESA + "\n" +
            "Documento confidencial - Solo para uso interno\n" +
            "© 2025 " + EMPRESA + ". Todos los derechos reservados."
        )
            .setFontSize(8)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(ColorConstants.GRAY)
            .setMarginTop(20);
        document.add(footer);
    }
    
    /**
     * Exporta datos a formato CSV profesional
     */
    public static void exportarCSV(String rutaArchivo, String tituloReporte,
                                    String[][] tablaInventario, String[][] tablaVentas,
                                    String[][] tablaBajoStock, String[][] tablaCategoria) throws IOException {
        
        try (FileWriter writer = new FileWriter(rutaArchivo);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withDelimiter(';'))) {
            
            // ENCABEZADO
            csvPrinter.printRecord(EMPRESA);
            csvPrinter.printRecord(DIRECCION);
            csvPrinter.printRecord(TELEFONO + " | " + EMAIL);
            csvPrinter.printRecord(WEB);
            csvPrinter.printRecord("");
            csvPrinter.printRecord(tituloReporte);
            csvPrinter.printRecord("Generado el: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
            ));
            csvPrinter.printRecord("");
            csvPrinter.printRecord("=".repeat(80));
            csvPrinter.printRecord("");
            
            // ESTADÍSTICAS DE INVENTARIO
            if (tablaInventario != null && tablaInventario.length > 0) {
                csvPrinter.printRecord("ESTADÍSTICAS DE INVENTARIO");
                csvPrinter.printRecord("");
                for (String[] fila : tablaInventario) {
                    csvPrinter.printRecord((Object[]) fila);
                }
                csvPrinter.printRecord("");
            }
            
            // ESTADÍSTICAS DE VENTAS
            if (tablaVentas != null && tablaVentas.length > 0) {
                csvPrinter.printRecord("ESTADÍSTICAS DE VENTAS");
                csvPrinter.printRecord("");
                for (String[] fila : tablaVentas) {
                    csvPrinter.printRecord((Object[]) fila);
                }
                csvPrinter.printRecord("");
            }
            
            // PRODUCTOS CON BAJO STOCK
            if (tablaBajoStock != null && tablaBajoStock.length > 0) {
                csvPrinter.printRecord("PRODUCTOS CON BAJO STOCK");
                csvPrinter.printRecord("");
                for (String[] fila : tablaBajoStock) {
                    csvPrinter.printRecord((Object[]) fila);
                }
                csvPrinter.printRecord("");
            }
            
            // RESUMEN POR CATEGORÍA
            if (tablaCategoria != null && tablaCategoria.length > 0) {
                csvPrinter.printRecord("RESUMEN POR CATEGORÍA");
                csvPrinter.printRecord("");
                for (String[] fila : tablaCategoria) {
                    csvPrinter.printRecord((Object[]) fila);
                }
                csvPrinter.printRecord("");
            }
            
            // PIE DE PÁGINA
            csvPrinter.printRecord("=".repeat(80));
            csvPrinter.printRecord("Documento generado automáticamente por " + EMPRESA);
            csvPrinter.printRecord("© 2025 Todos los derechos reservados");
            
            csvPrinter.flush();
        }
    }
}

