package unl.edu.cc.rest.jbrew.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@SessionScoped
public class FacturaBean implements Serializable {
    
    @Inject
    private VentaBean ventaBean;
    
    @Inject
    private CompraBean compraBean;
    
    private String tipoFactura;
    private List<FacturaInfo> facturasFiltradas;
    private FacturaInfo facturaSeleccionada;
    
    public FacturaBean() {
        this.tipoFactura = "venta";
        this.facturasFiltradas = new ArrayList<>();
        this.facturaSeleccionada = new FacturaInfo();
    }
    
    public void filtrar() {
        facturasFiltradas = new ArrayList<>();
        
        if ("venta".equals(tipoFactura)) {
            for (SaleInvoice factura : ventaBean.getFacturas()) {
                FacturaInfo info = new FacturaInfo();
                info.setNumero(factura.getInvoiceNumber());
                info.setFecha(factura.getInvoiceDate());
                info.setTipo("VENTA");
                info.setTercero(factura.getCustomer() != null ? 
                    factura.getCustomer().getName() + " " + factura.getCustomer().getApellido() : "Cliente Mostrador");
                info.setMetodo(factura.getPaymentMethod());
                info.setTotal(0); // Se puede calcular si se agregan items
                facturasFiltradas.add(info);
            }
        } else {
            for (PurchaseInvoice factura : compraBean.getFacturas()) {
                FacturaInfo info = new FacturaInfo();
                info.setNumero(factura.getInvoiceNumber());
                info.setFecha(factura.getInvoiceDate());
                info.setTipo("COMPRA");
                info.setTercero(factura.getSupplier() != null ? factura.getSupplier().getName() : "N/A");
                info.setMetodo(factura.getPurchaseOrderNumber());
                info.setTotal(0); // Se puede calcular si se agregan items
                facturasFiltradas.add(info);
            }
        }
    }
    
    public void verDetalle(FacturaInfo factura) {
        this.facturaSeleccionada = factura;
    }
    
    public void imprimir(FacturaInfo factura) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Impresión", "Factura #" + factura.getNumero() + " enviada a impresión"));
        // Aquí se podría agregar lógica real de impresión
    }
    
    // Getters y Setters
    public String getTipoFactura() {
        return tipoFactura;
    }
    
    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
    }
    
    public List<FacturaInfo> getFacturasFiltradas() {
        if (facturasFiltradas.isEmpty()) {
            filtrar();
        }
        return facturasFiltradas;
    }
    
    public void setFacturasFiltradas(List<FacturaInfo> facturasFiltradas) {
        this.facturasFiltradas = facturasFiltradas;
    }
    
    public FacturaInfo getFacturaSeleccionada() {
        return facturaSeleccionada;
    }
    
    public void setFacturaSeleccionada(FacturaInfo facturaSeleccionada) {
        this.facturaSeleccionada = facturaSeleccionada;
    }
    
    // Clase interna para información de factura
    public static class FacturaInfo implements Serializable {
        private String numero;
        private Date fecha;
        private String tipo;
        private String tercero;
        private String metodo;
        private double total;
        
        public String getNumero() {
            return numero;
        }
        
        public void setNumero(String numero) {
            this.numero = numero;
        }
        
        public Date getFecha() {
            return fecha;
        }
        
        public void setFecha(Date fecha) {
            this.fecha = fecha;
        }
        
        public String getTipo() {
            return tipo;
        }
        
        public void setTipo(String tipo) {
            this.tipo = tipo;
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
        
        public double getTotal() {
            return total;
        }
        
        public void setTotal(double total) {
            this.total = total;
        }
        
        public String getFechaTexto() {
            if (fecha != null) {
                return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
            }
            return "";
        }
    }
}
