package unl.edu.cc.rest.jbrew.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import unl.edu.cc.rest.jbrew.domain.Movements.ProductMovement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ReporteService {

    @Inject
    private InventoryService inventoryService;

    @Inject
    private VentaService ventaService;

    @Inject
    private PurchaseService purchaseService;

    public ReporteData generarReporteVentas() {
        List<SaleInvoice> facturas = ventaService.obtenerFacturas();
        
        if (facturas.isEmpty()) {
            return ReporteData.error("No hay facturas de ventas registradas. Realice ventas primero para generar este reporte.");
        }
        
        List<DatoReporte> datos = new ArrayList<>();
        double totalVentas = 0;
        int totalTransacciones = 0;
        
        for (SaleInvoice factura : facturas) {
            String tercero = factura.getCustomer() != null ? factura.getCustomer().getName() : "Cliente Mostrador";

            if (factura.getMovement() == null) {
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
                dato.setGanancia(0);
                datos.add(dato);

                totalVentas += linea.getSubtotal();
            }
            totalTransacciones++;
        }
        
        return ReporteData.success(datos, totalVentas, 0, totalTransacciones, 0);
    }

    public ReporteData generarReporteCompras() {
        List<PurchaseInvoice> facturas = purchaseService.getPurchaseInvoices();
        
        if (facturas.isEmpty()) {
            return ReporteData.error("No hay facturas de compras registradas. Realice compras primero para generar este reporte.");
        }
        
        List<DatoReporte> datos = new ArrayList<>();
        double totalVentas = 0;
        int totalTransacciones = 0;
        
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
            datos.add(dato);

            totalVentas += factura.getTotal();
            totalTransacciones++;
        }
        
        return ReporteData.success(datos, totalVentas, 0, totalTransacciones, 0);
    }

    public ReporteData generarReporteRotacion() {
        List<Product> productos = inventoryService.getAllProducts();
        
        if (productos.isEmpty()) {
            return ReporteData.error("No hay productos registrados. Agregue productos primero para generar este reporte.");
        }
        
        List<DatoReporte> datos = new ArrayList<>();
        double totalVentas = 0;
        int totalTransacciones = 0;
        
        for (Product producto : productos) {
            DatoReporte dato = new DatoReporte();
            dato.setDescripcion(producto.getName());
            dato.setCantidad(producto.getStock());
            dato.setPrecioUnitario(producto.getSalePrice());
            dato.setTotal(producto.getStock() * producto.getSalePrice());
            dato.setTercero(producto.getCategory() != null ? producto.getCategory().getName() : "");
            dato.setMetodo("");
            dato.setGanancia(0);
            datos.add(dato);

            totalVentas += producto.getStock() * producto.getSalePrice();
        }

        totalTransacciones = inventoryService.getAllProducts().size();
        
        return ReporteData.success(datos, totalVentas, 0, totalTransacciones, 0);
    }

    public ReporteData generarReporteStockBajo() {
        List<Product> productosCriticos = inventoryService.findProductsWithCriticalStock();
        
        List<DatoReporte> datos = new ArrayList<>();
        double totalVentas = 0;
        int totalTransacciones = 0;
        
        for (Product producto : productosCriticos) {
            DatoReporte dato = new DatoReporte();
            dato.setDescripcion(producto.getName());
            dato.setCantidad(producto.getStock());
            dato.setPrecioUnitario(producto.getSalePrice());
            dato.setTotal(producto.getStock() * producto.getSalePrice());
            dato.setTercero(producto.getCategory() != null ? producto.getCategory().getName() : "");
            dato.setMetodo("Stock Mínimo: " + producto.getMinStock());
            dato.setGanancia(0);
            datos.add(dato);
            
            totalVentas += producto.getStock() * producto.getSalePrice();
        }

        totalTransacciones = productosCriticos.size();
        
        String mensaje = productosCriticos.isEmpty() 
            ? "No hay productos con stock bajo" 
            : "Se encontraron " + productosCriticos.size() + " productos con stock bajo";
        
        return ReporteData.success(datos, totalVentas, 0, totalTransacciones, 0, mensaje);
    }

    public ReporteData generarReporteGanancias() {
        List<SaleInvoice> facturas = ventaService.obtenerFacturas();
        
        if (facturas.isEmpty()) {
            return ReporteData.error("No hay facturas de ventas registradas. Realice ventas primero para generar este reporte.");
        }
        
        List<DatoReporte> datos = new ArrayList<>();
        double totalVentas = 0;
        double ganancia = 0;
        int totalTransacciones = 0;
        int productosSinPrecioCompra = 0;
        
        for (SaleInvoice factura : facturas) {
            String tercero = factura.getCustomer() != null ? factura.getCustomer().getName() : "Cliente Mostrador";

            if (factura.getMovement() == null) {
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
                
                // Usar el precio de compra del producto actual (no del movimiento)
                double precioCompra = linea.getProduct().getPurchasePrice();
                double precioVenta = linea.getUnitPrice();
                int cantidad = linea.getQuantity();
                
                double gananciaLinea;
                if (precioCompra <= 0) {
                    gananciaLinea = 0;
                    productosSinPrecioCompra++;
                } else {
                    gananciaLinea = (precioVenta - precioCompra) * cantidad;
                }
                dato.setGanancia(gananciaLinea);
                datos.add(dato);

                totalVentas += linea.getSubtotal();
                ganancia += gananciaLinea;
            }
            totalTransacciones++;
        }
        
        double margenPromedio = totalVentas > 0 ? (ganancia / totalVentas) * 100 : 0;
        
        String mensaje = "Reporte generado correctamente";
        if (productosSinPrecioCompra > 0) {
            mensaje += " (Advertencia: " + productosSinPrecioCompra + " productos sin precio de compra, ganancia no calculada para ellos)";
        }
        
        return ReporteData.success(datos, totalVentas, ganancia, totalTransacciones, margenPromedio, mensaje);
    }

    public ReporteData generarReporteVentasCliente() {
        List<SaleInvoice> facturas = ventaService.obtenerFacturas();
        
        if (facturas.isEmpty()) {
            return ReporteData.error("No hay facturas de ventas registradas. Realice ventas primero para generar este reporte.");
        }
        
        Map<String, Map<String, Object>> ventasPorCliente = new java.util.HashMap<>();
        
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
        
        List<DatoReporte> datos = new ArrayList<>();
        double totalVentas = 0;
        int totalTransacciones = 0;
        
        for (Map.Entry<String, Map<String, Object>> entry : ventasPorCliente.entrySet()) {
            DatoReporte dato = new DatoReporte();
            dato.setDescripcion(entry.getKey());
            dato.setCantidad((Integer) entry.getValue().get("cantidad"));
            dato.setPrecioUnitario(0);
            dato.setTotal((Double) entry.getValue().get("total"));
            dato.setTercero((Integer) entry.getValue().get("facturas") + " facturas");
            dato.setMetodo("");
            dato.setGanancia(0);
            datos.add(dato);
            
            totalVentas += (Double) entry.getValue().get("total");
        }
        
        totalTransacciones = ventasPorCliente.size();
        
        return ReporteData.success(datos, totalVentas, 0, totalTransacciones, 0);
    }

    public static class ReporteData {
        private final boolean exitoso;
        private final String mensaje;
        private final List<DatoReporte> datos;
        private final double totalVentas;
        private final double ganancia;
        private final int totalTransacciones;
        private final double margenPromedio;

        private ReporteData(boolean exitoso, String mensaje, List<DatoReporte> datos, 
                          double totalVentas, double ganancia, int totalTransacciones, double margenPromedio) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.datos = datos;
            this.totalVentas = totalVentas;
            this.ganancia = ganancia;
            this.totalTransacciones = totalTransacciones;
            this.margenPromedio = margenPromedio;
        }

        public static ReporteData success(List<DatoReporte> datos, double totalVentas, double ganancia, 
                                         int totalTransacciones, double margenPromedio) {
            return new ReporteData(true, "Reporte generado correctamente", datos, totalVentas, ganancia, totalTransacciones, margenPromedio);
        }

        public static ReporteData success(List<DatoReporte> datos, double totalVentas, double ganancia, 
                                         int totalTransacciones, double margenPromedio, String mensaje) {
            return new ReporteData(true, mensaje, datos, totalVentas, ganancia, totalTransacciones, margenPromedio);
        }

        public static ReporteData error(String mensaje) {
            return new ReporteData(false, mensaje, new ArrayList<>(), 0, 0, 0, 0);
        }

        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        public List<DatoReporte> getDatos() { return datos; }
        public double getTotalVentas() { return totalVentas; }
        public double getGanancia() { return ganancia; }
        public int getTotalTransacciones() { return totalTransacciones; }
        public double getMargenPromedio() { return margenPromedio; }
    }

    public static class DatoReporte implements java.io.Serializable {
        private Date fecha;
        private String descripcion;
        private int cantidad;
        private double precioUnitario;
        private double total;
        private String tercero;
        private String metodo;
        private double ganancia;

        public Date getFecha() { return fecha; }
        public void setFecha(Date fecha) { this.fecha = fecha; }

        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }

        public double getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }

        public String getTercero() { return tercero; }
        public void setTercero(String tercero) { this.tercero = tercero; }

        public String getMetodo() { return metodo; }
        public void setMetodo(String metodo) { this.metodo = metodo; }

        public double getGanancia() { return ganancia; }
        public void setGanancia(double ganancia) { this.ganancia = ganancia; }
    }
}
