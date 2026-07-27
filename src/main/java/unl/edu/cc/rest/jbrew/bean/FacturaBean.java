package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.VentaService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Named
@ViewScoped
public class FacturaBean implements Serializable {

    @Inject
    private VentaService ventaService;

    @Inject
    private PurchaseService purchaseService;

    private String tipoFactura = "venta";
    private List<InvoiceInfo> facturasFiltradas = new ArrayList<>();
    private InvoiceInfo facturaSeleccionada = new InvoiceInfo();

    public void filtrar() {
        facturasFiltradas = "venta".equals(tipoFactura)
                ? mapearVentas(ventaService.obtenerFacturas())
                : mapearCompras(purchaseService.getPurchaseInvoices());
    }

    public void verDetalle(InvoiceInfo factura) {
        this.facturaSeleccionada = factura;
    }

    public void imprimir(InvoiceInfo factura) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Impresión",
                        "Factura #" + factura.getNumero() + " enviada a impresión"));
    }

    private List<InvoiceInfo> mapearVentas(List<SaleInvoice> facturas) {
        List<InvoiceInfo> resultado = new ArrayList<>();
        for (SaleInvoice factura : facturas) {
            resultado.add(InvoiceInfo.desdeVenta(factura));
        }
        return resultado;
    }

    private List<InvoiceInfo> mapearCompras(List<PurchaseInvoice> facturas) {
        List<InvoiceInfo> resultado = new ArrayList<>();
        for (PurchaseInvoice factura : facturas) {
            resultado.add(InvoiceInfo.desdeCompra(factura));
        }
        return resultado;
    }

    // ===== Propiedades usadas por facturas.xhtml =====

    public String getTipoFactura() {
        return tipoFactura;
    }

    public void setTipoFactura(String tipoFactura) {
        this.tipoFactura = tipoFactura;
    }

    public List<InvoiceInfo> getFacturasFiltradas() {
        if (facturasFiltradas.isEmpty()) {
            filtrar();
        }
        return facturasFiltradas;
    }

    public InvoiceInfo getFacturaSeleccionada() {
        return facturaSeleccionada;
    }

    public static class InvoiceInfo implements Serializable {
        private String numero;
        private Date fecha;
        private String tipo;
        private String tercero;
        private String metodo;
        private double total;

        public static InvoiceInfo desdeVenta(SaleInvoice factura) {
            InvoiceInfo info = new InvoiceInfo();
            info.numero = factura.getInvoiceNumber();
            info.fecha = factura.getInvoiceDate();
            info.tipo = "VENTA";
            info.tercero = factura.getCustomer() != null
                    ? factura.getCustomer().getName() + " " + factura.getCustomer().getApellido()
                    : "Cliente Mostrador";
            info.metodo = factura.getPaymentMethod();
            // FIX: antes se hardcodeaba en 0; ahora usa el total real de
            // la factura (ver corrección previa en VentaService).
            info.total = factura.getTotal();
            return info;
        }

        public static InvoiceInfo desdeCompra(PurchaseInvoice factura) {
            InvoiceInfo info = new InvoiceInfo();
            info.numero = factura.getInvoiceNumber();
            info.fecha = factura.getInvoiceDate();
            info.tipo = "COMPRA";
            info.tercero = factura.getSupplier() != null ? factura.getSupplier().getName() : "N/A";
            info.metodo = factura.getPurchaseOrderNumber();
            info.total = factura.getTotal();
            return info;
        }

        public String getNumero() {
            return numero;
        }

        public String getTipo() {
            return tipo;
        }

        public String getTercero() {
            return tercero;
        }

        public String getMetodo() {
            return metodo;
        }

        public double getTotal() {
            return total;
        }

        public String getFechaTexto() {
            return fecha != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha) : "";
        }
    }
}