package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryService;
import unl.edu.cc.rest.jbrew.business.VentaService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import unl.edu.cc.rest.jbrew.domain.Movements.ProductMovement;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@Named
@ViewScoped
public class ReporteBean implements Serializable {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private VentaService ventaService;

    @Inject
    private PurchaseService purchaseService;

    private String tipoReporte;
    private Date fechaInicio;
    private Date fechaFin;
    private Category category;

    private double totalVentas;
    private double ganancia;
    private int totalTransacciones;
    private double margenPromedio;

    private List<DatoReporte> datosReporte;

    private Object chartVentasCategoria;
    private Object chartTendenciaVentas;
    private Object chartMetodosPago;

    public ReporteBean() {
        this.tipoReporte = "ventas";
        this.datosReporte = new ArrayList<>();
    }

    public void generarReporte() {
        System.out.println("=== INICIANDO GENERACIÓN DE REPORTE ===");
        System.out.println("Tipo de reporte: " + tipoReporte);
        
        datosReporte = new ArrayList<>();
        totalVentas = 0;
        ganancia = 0;
        totalTransacciones = 0;
        margenPromedio = 0;

        if ("ventas".equals(tipoReporte)) {
            System.out.println("Generando reporte de ventas...");
            generarReporteVentas();
        } else if ("compras".equals(tipoReporte)) {
            System.out.println("Generando reporte de compras...");
            generarReporteCompras();
        } else if ("rotacion".equals(tipoReporte)) {
            System.out.println("Generando reporte de rotación...");
            generarReporteRotacion();
        } else if ("stockBajo".equals(tipoReporte)) {
            System.out.println("Generando reporte de stock bajo...");
            generarReporteStockBajo();
        } else if ("ganancias".equals(tipoReporte)) {
            System.out.println("Generando reporte de ganancias...");
            generarReporteGanancias();
        } else if ("ventasCliente".equals(tipoReporte)) {
            System.out.println("Generando reporte de ventas por cliente...");
            generarReporteVentasCliente();
        }

        System.out.println("Total ventas: " + totalVentas);
        System.out.println("Total transacciones: " + totalTransacciones);
        System.out.println("Datos reporte: " + datosReporte.size());
        
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Reporte Generado", "El reporte se ha generado correctamente"));
    }

    public void generar() {
        generarReporte();
    }

    private void generarReporteVentas() {
        List<SaleInvoice> facturas = ventaService.obtenerFacturas();
        System.out.println("Facturas encontradas: " + facturas.size());
        
        if (facturas.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Datos", "No hay facturas de ventas registradas. Realice ventas primero para generar este reporte."));
            return;
        }
        
        for (SaleInvoice factura : facturas) {
            String tercero = factura.getCustomer() != null ? factura.getCustomer().getName() : "Cliente Mostrador";

            if (factura.getMovement() == null) {
                continue; // factura sin movimiento asociado (no debería pasar, pero por seguridad)
            }

            for (ProductMovement linea : factura.getMovement().getProductMovementList()) {
                DatoReporte dato = new DatoReporte();
                dato.setFecha(factura.getInvoiceDate());
                dato.setDescripcion(linea.getProduct().getName());
                dato.setCantidad(linea.getQuantity());
                dato.setPrecioUnitario(linea.getUnitPrice());
                dato.setTotal(linea.getSubtotal());
                dato.setTercero(tercero);
                dato.setMetodo(factura.getPaymentMethod());
                dato.setGanancia(0); // TODO: requiere precio de costo en Product
                datosReporte.add(dato);

                totalVentas += linea.getSubtotal();
            }
            totalTransacciones++;
        }
    }

    private void generarReporteCompras() {
        List<PurchaseInvoice> facturas = purchaseService.getPurchaseInvoices();
        
        if (facturas.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Datos", "No hay facturas de compras registradas. Realice compras primero para generar este reporte."));
            return;
        }
        
        // TODO: idealmente esto también debería desglosarse por línea de
        // producto comprado, igual que se hizo en generarReporteVentas,
        // en cuanto se comparta la estructura de PurchaseInvoice/Movement.
        for (PurchaseInvoice factura : purchaseService.getPurchaseInvoices()) {
            DatoReporte dato = new DatoReporte();
            dato.setFecha(factura.getInvoiceDate());
            dato.setDescripcion(factura.getInvoiceNumber());
            dato.setCantidad(0);
            dato.setPrecioUnitario(0);
            dato.setTotal(factura.getTotal());
            dato.setTercero(factura.getSupplier() != null ? factura.getSupplier().getName() : "N/A");
            dato.setMetodo(factura.getPurchaseOrderNumber());
            dato.setGanancia(0);
            datosReporte.add(dato);

            totalTransacciones++;
        }
    }

    private void generarReporteRotacion() {
        List<Product> productos = inventoryService.getAllProducts();
        
        if (productos.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Datos", "No hay productos registrados. Agregue productos primero para generar este reporte."));
            return;
        }
        
        for (Product producto : productos) {
            DatoReporte dato = new DatoReporte();
            dato.setDescripcion(producto.getName());
            dato.setCantidad(producto.getStock());
            dato.setPrecioUnitario(producto.getSalePrice());
            dato.setTotal(producto.getStock() * producto.getSalePrice());
            dato.setTercero(producto.getCategory() != null ? producto.getCategory().getName() : "");
            dato.setMetodo("");
            dato.setGanancia(0);
            datosReporte.add(dato);
        }

        totalTransacciones = inventoryService.getAllProducts().size();
    }

    private void generarReporteStockBajo() {
        List<Product> productosCriticos = inventoryService.findProductsWithCriticalStock();
        
        for (Product producto : productosCriticos) {
            DatoReporte dato = new DatoReporte();
            dato.setDescripcion(producto.getName());
            dato.setCantidad(producto.getStock());
            dato.setPrecioUnitario(producto.getSalePrice());
            dato.setTotal(producto.getStock() * producto.getSalePrice());
            dato.setTercero(producto.getCategory() != null ? producto.getCategory().getName() : "");
            dato.setMetodo("Stock Mínimo: " + producto.getMinStock());
            dato.setGanancia(0);
            datosReporte.add(dato);
            
            totalVentas += producto.getStock() * producto.getSalePrice();
        }

        totalTransacciones = productosCriticos.size();
        
        if (productosCriticos.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Alertas", "No hay productos con stock bajo"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Alertas de Stock", 
                            "Se encontraron " + productosCriticos.size() + " productos con stock bajo"));
        }
    }

    private void generarReporteGanancias() {
        List<SaleInvoice> facturas = ventaService.obtenerFacturas();
        System.out.println("=== GENERANDO REPORTE DE GANANCIAS ===");
        System.out.println("Facturas encontradas: " + facturas.size());
        
        if (facturas.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Datos", "No hay facturas de ventas registradas. Realice ventas primero para generar este reporte."));
            return;
        }
        
        for (SaleInvoice factura : facturas) {
            String tercero = factura.getCustomer() != null ? factura.getCustomer().getName() : "Cliente Mostrador";

            if (factura.getMovement() == null) {
                System.out.println("Factura sin movimiento: " + factura.getInvoiceNumber());
                continue;
            }

            for (ProductMovement linea : factura.getMovement().getProductMovementList()) {
                DatoReporte dato = new DatoReporte();
                dato.setFecha(factura.getInvoiceDate());
                dato.setDescripcion(linea.getProduct().getName());
                dato.setCantidad(linea.getQuantity());
                dato.setPrecioUnitario(linea.getUnitPrice());
                dato.setTotal(linea.getSubtotal());
                dato.setTercero(tercero);
                dato.setMetodo(factura.getPaymentMethod());
                
                // Calcular ganancia: (precio venta - precio compra) * cantidad
                double precioCompra = linea.getProduct().getPurchasePrice();
                double precioVenta = linea.getUnitPrice();
                int cantidad = linea.getQuantity();
                
                System.out.println("Producto: " + linea.getProduct().getName() + " | PrecioCompra: " + precioCompra + " | PrecioVenta: " + precioVenta + " | Cantidad: " + cantidad);
                
                double gananciaLinea = (precioVenta - precioCompra) * cantidad;
                dato.setGanancia(gananciaLinea);
                datosReporte.add(dato);

                totalVentas += linea.getSubtotal();
                ganancia += gananciaLinea;
            }
            totalTransacciones++;
        }
        
        System.out.println("Ganancia total calculada: " + ganancia);
        
        // Calcular margen promedio
        if (totalVentas > 0) {
            margenPromedio = (ganancia / totalVentas) * 100;
        }
    }

    private void generarReporteVentasCliente() {
        List<SaleInvoice> facturas = ventaService.obtenerFacturas();
        
        if (facturas.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Sin Datos", "No hay facturas de ventas registradas. Realice ventas primero para generar este reporte."));
            return;
        }
        
        // Agrupar ventas por cliente
        java.util.Map<String, java.util.Map<String, Object>> ventasPorCliente = new java.util.HashMap<>();
        
        for (SaleInvoice factura : ventaService.obtenerFacturas()) {
            String nombreCliente = factura.getCustomer() != null ? factura.getCustomer().getName() : "Cliente Mostrador";
            
            if (!ventasPorCliente.containsKey(nombreCliente)) {
                ventasPorCliente.put(nombreCliente, new java.util.HashMap<>());
                ventasPorCliente.get(nombreCliente).put("total", 0.0);
                ventasPorCliente.get(nombreCliente).put("cantidad", 0);
                ventasPorCliente.get(nombreCliente).put("facturas", 0);
            }
            
            double totalFactura = factura.getTotal();
            ventasPorCliente.get(nombreCliente).put("total", 
                (Double) ventasPorCliente.get(nombreCliente).get("total") + totalFactura);
            ventasPorCliente.get(nombreCliente).put("facturas", 
                (Integer) ventasPorCliente.get(nombreCliente).get("facturas") + 1);
            
            if (factura.getMovement() != null) {
                for (ProductMovement linea : factura.getMovement().getProductMovementList()) {
                    ventasPorCliente.get(nombreCliente).put("cantidad", 
                        (Integer) ventasPorCliente.get(nombreCliente).get("cantidad") + linea.getQuantity());
                }
            }
        }
        
        // Crear datos del reporte
        for (java.util.Map.Entry<String, java.util.Map<String, Object>> entry : ventasPorCliente.entrySet()) {
            DatoReporte dato = new DatoReporte();
            dato.setDescripcion(entry.getKey());
            dato.setCantidad((Integer) entry.getValue().get("cantidad"));
            dato.setPrecioUnitario(0);
            dato.setTotal((Double) entry.getValue().get("total"));
            dato.setTercero((Integer) entry.getValue().get("facturas") + " facturas");
            dato.setMetodo("");
            dato.setGanancia(0);
            datosReporte.add(dato);
            
            totalVentas += (Double) entry.getValue().get("total");
        }
        
        totalTransacciones = ventasPorCliente.size();
    }

    public void exportarPDF() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_" + tipoReporte + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf");
            
            ServletOutputStream out = response.getOutputStream();
            com.itextpdf.kernel.pdf.PdfWriter pdfWriter = new com.itextpdf.kernel.pdf.PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(pdfWriter);
            Document document = new Document(pdfDoc);
            
            // Fuentes
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            
            // Título
            Paragraph title = new Paragraph("Reporte: " + getTituloReporte())
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);
            
            // Fecha de generación
            Paragraph date = new Paragraph("Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20);
            document.add(date);
            
            // Resumen KPIs
            Paragraph kpiTitle = new Paragraph("Resumen")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setMarginBottom(10);
            document.add(kpiTitle);
            
            document.add(new Paragraph("Total Ventas: $" + String.format("%.2f", totalVentas)).setFont(normalFont).setFontSize(10));
            document.add(new Paragraph("Ganancia Neta: $" + String.format("%.2f", ganancia)).setFont(normalFont).setFontSize(10));
            document.add(new Paragraph("Transacciones: " + totalTransacciones).setFont(normalFont).setFontSize(10));
            document.add(new Paragraph("Margen Promedio: " + String.format("%.2f", margenPromedio) + "%").setFont(normalFont).setFontSize(10));
            
            // Tabla de datos
            document.add(new Paragraph("\n"));
            Paragraph tableTitle = new Paragraph("Detalle del Reporte")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setMarginBottom(10);
            document.add(tableTitle);
            
            Table table = new Table(8).useAllAvailableWidth();
            
            // Headers
            String[] headers = {"Fecha", "Descripción", "Cantidad", "Precio Unit.", "Total", "Cliente/Prov.", "Método", "Ganancia"};
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setFont(boldFont).setFontSize(10).setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                        .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.DARK_GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
            }
            
            // Datos
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (DatoReporte dato : datosReporte) {
                table.addCell(new Cell().add(new Paragraph(dato.getFecha() != null ? sdf.format(dato.getFecha()) : "").setFont(normalFont).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(dato.getDescripcion()).setFont(normalFont).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(dato.getCantidad())).setFont(normalFont).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", dato.getPrecioUnitario())).setFont(normalFont).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", dato.getTotal())).setFont(normalFont).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(dato.getTercero()).setFont(normalFont).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(dato.getMetodo()).setFont(normalFont).setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph("$" + String.format("%.2f", dato.getGanancia())).setFont(normalFont).setFontSize(9)));
            }
            
            document.add(table);
            document.close();
            out.close();
            
            facesContext.responseComplete();
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exportación Exitosa", "El reporte PDF se ha generado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al exportar PDF", "Error: " + e.getMessage()));
        }
    }

    public void exportarExcel() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=reporte_" + tipoReporte + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx");
            
            ServletOutputStream out = response.getOutputStream();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Reporte");
            
            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(headerFont(workbook, true));
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setFont(headerFont(workbook, false));
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            
            // Título del reporte
            Row titleRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Reporte: " + getTituloReporte());
            titleCell.setCellStyle(headerStyle);
            
            // Fecha de generación
            Row dateRow = sheet.createRow(1);
            org.apache.poi.ss.usermodel.Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Generado: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            
            // Resumen KPIs
            int kpiRow = 3;
            sheet.createRow(kpiRow++).createCell(0).setCellValue("RESUMEN");
            sheet.getRow(kpiRow - 1).getCell(0).setCellStyle(headerStyle);
            
            sheet.createRow(kpiRow++).createCell(0).setCellValue("Total Ventas: $" + String.format("%.2f", totalVentas));
            sheet.createRow(kpiRow++).createCell(0).setCellValue("Ganancia Neta: $" + String.format("%.2f", ganancia));
            sheet.createRow(kpiRow++).createCell(0).setCellValue("Transacciones: " + totalTransacciones);
            sheet.createRow(kpiRow++).createCell(0).setCellValue("Margen Promedio: " + String.format("%.2f", margenPromedio) + "%");
            
            // Tabla de datos
            int tableRow = kpiRow + 2;
            Row headerRow = sheet.createRow(tableRow++);
            String[] headers = {"Fecha", "Descripción", "Cantidad", "Precio Unit.", "Total", "Cliente/Prov.", "Método", "Ganancia"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (DatoReporte dato : datosReporte) {
                Row row = sheet.createRow(tableRow++);
                row.createCell(0).setCellValue(dato.getFecha() != null ? sdf.format(dato.getFecha()) : "");
                row.createCell(1).setCellValue(dato.getDescripcion());
                row.createCell(2).setCellValue(dato.getCantidad());
                row.createCell(3).setCellValue(dato.getPrecioUnitario());
                row.createCell(4).setCellValue(dato.getTotal());
                row.createCell(5).setCellValue(dato.getTercero());
                row.createCell(6).setCellValue(dato.getMetodo());
                row.createCell(7).setCellValue(dato.getGanancia());
                
                for (org.apache.poi.ss.usermodel.Cell cell : row) {
                    cell.setCellStyle(dataStyle);
                }
            }
            
            // Auto-size columnas
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            workbook.close();
            out.close();
            
            facesContext.responseComplete();
            
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exportación Exitosa", "El reporte Excel se ha generado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al exportar Excel", "Error: " + e.getMessage()));
        }
    }
    
    private Font headerFont(Workbook workbook, boolean bold) {
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(bold);
        return font;
    }
    
    private String getTituloReporte() {
        switch (tipoReporte) {
            case "ventas": return "Ventas por Período";
            case "compras": return "Compras por Período";
            case "rotacion": return "Rotación de Inventario";
            case "stockBajo": return "Productos con Stock Bajo";
            case "ganancias": return "Ganancias y Pérdidas";
            case "ventasCliente": return "Ventas por Cliente";
            default: return "Reporte General";
        }
    }

    // Getters y Setters
    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCategoryName() {
        return category != null ? category.getName() : null;
    }

    public void setCategoryName(String categoryName) {
        if (categoryName != null) {
            this.category = inventoryService.getAllCategories().stream()
                    .filter(c -> c.getName().equals(categoryName))
                    .findFirst()
                    .orElse(null);
        } else {
            this.category = null;
        }
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public double getGanancia() {
        return ganancia;
    }

    public int getTotalTransacciones() {
        return totalTransacciones;
    }

    public double getMargenPromedio() {
        return margenPromedio;
    }

    public List<DatoReporte> getDatosReporte() {
        return datosReporte;
    }

    public Object getChartVentasCategoria() {
        return chartVentasCategoria;
    }

    public Object getChartTendenciaVentas() {
        return chartTendenciaVentas;
    }

    public Object getChartMetodosPago() {
        return chartMetodosPago;
    }

    public static class DatoReporte implements Serializable {
        private Date fecha;
        private String descripcion;
        private int cantidad;
        private double precioUnitario;
        private double total;
        private String tercero;
        private String metodo;
        private double ganancia;

        public Date getFecha() {
            return fecha;
        }

        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getPrecioUnitario() {
            return precioUnitario;
        }

        public void setPrecioUnitario(double precioUnitario) {
            this.precioUnitario = precioUnitario;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public String getTercero() {
            return tercero;
        }

        public void setTercero(String tercero) {
            this.tercero = tercero;
        }

        public String getMetodo() {
            return metodo;
        }

        public void setMetodo(String metodo) {
            this.metodo = metodo;
        }

        public double getGanancia() {
            return ganancia;
        }

        public void setGanancia(double ganancia) {
            this.ganancia = ganancia;
        }
    }
}