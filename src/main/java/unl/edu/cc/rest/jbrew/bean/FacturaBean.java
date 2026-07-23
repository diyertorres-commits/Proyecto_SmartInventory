package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.SalesService;
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
    private SalesService salesService;
    
    @Inject
    private PurchaseService purchaseService;
    
    private String invoiceType;
    private List<InvoiceInfo> filteredInvoices;
    private InvoiceInfo selectedInvoice;
    
    public FacturaBean() {
        this.invoiceType = "venta";
        this.filteredInvoices = new ArrayList<>();
        this.selectedInvoice = new InvoiceInfo();
    }
    
    public void filterInvoices() {
        filteredInvoices = new ArrayList<>();
        
        if ("venta".equals(invoiceType)) {
            for (SaleInvoice invoice : salesService.getSaleInvoices()) {
                InvoiceInfo info = new InvoiceInfo();
                info.setNumber(invoice.getInvoiceNumber());
                info.setDate(invoice.getInvoiceDate());
                info.setType("VENTA");
                info.setThirdParty(invoice.getCustomer() != null ? 
                    invoice.getCustomer().getName() + " " + invoice.getCustomer().getApellido() : "Cliente Mostrador");
                info.setMethod(invoice.getPaymentMethod());
                info.setTotal(0);
                filteredInvoices.add(info);
            }
        } else {
            for (PurchaseInvoice invoice : purchaseService.getPurchaseInvoices()) {
                InvoiceInfo info = new InvoiceInfo();
                info.setNumber(invoice.getInvoiceNumber());
                info.setDate(invoice.getInvoiceDate());
                info.setType("COMPRA");
                info.setThirdParty(invoice.getSupplier() != null ? invoice.getSupplier().getName() : "N/A");
                info.setMethod(invoice.getPurchaseOrderNumber());
                info.setTotal(0);
                filteredInvoices.add(info);
            }
        }
    }
    
    public void viewDetail(InvoiceInfo invoice) {
        this.selectedInvoice = invoice;
    }
    
    public void printInvoice(InvoiceInfo invoice) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Impresión", "Factura #" + invoice.getNumber() + " enviada a impresión"));
    }
    
    // Getters and Setters
    public String getInvoiceType() {
        return invoiceType;
    }
    
    public String getTipoFactura() {
        return getInvoiceType();
    }
    
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }
    
    public void setTipoFactura(String invoiceType) {
        setInvoiceType(invoiceType);
    }
    
    public void filtrar() {
        filterInvoices();
    }
    
    public List<InvoiceInfo> getFilteredInvoices() {
        if (filteredInvoices.isEmpty()) {
            filterInvoices();
        }
        return filteredInvoices;
    }
    
    public List<InvoiceInfo> getFacturasFiltradas() {
        return getFilteredInvoices();
    }
    
    public List<InvoiceInfo> getFacturas() {
        return getFilteredInvoices();
    }
    
    public void setFilteredInvoices(List<InvoiceInfo> filteredInvoices) {
        this.filteredInvoices = filteredInvoices;
    }
    
    public InvoiceInfo getSelectedInvoice() {
        return selectedInvoice;
    }
    
    public InvoiceInfo getFacturaSeleccionada() {
        return getSelectedInvoice();
    }
    
    public void setSelectedInvoice(InvoiceInfo selectedInvoice) {
        this.selectedInvoice = selectedInvoice;
    }
    
    public void setFacturaSeleccionada(InvoiceInfo selectedInvoice) {
        setSelectedInvoice(selectedInvoice);
    }
    
    // Inner class for invoice information
    public static class InvoiceInfo implements Serializable {
        private String number;
        private Date date;
        private String type;
        private String thirdParty;
        private String method;
        private double total;
        
        public String getNumber() {
            return number;
        }
        
        public String getNumero() {
            return getNumber();
        }
        
        public void setNumber(String number) {
            this.number = number;
        }
        
        public void setNumero(String number) {
            setNumber(number);
        }
        
        public Date getDate() {
            return date;
        }
        
        public void setDate(Date date) {
            this.date = date;
        }
        
        public String getType() {
            return type;
        }
        
        public String getTipo() {
            return getType();
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getThirdParty() {
            return thirdParty;
        }
        
        public String getTercero() {
            return getThirdParty();
        }
        
        public void setThirdParty(String thirdParty) {
            this.thirdParty = thirdParty;
        }
        
        public String getMethod() {
            return method;
        }
        
        public String getMetodo() {
            return getMethod();
        }
        
        public void setMethod(String method) {
            this.method = method;
        }
        
        public void setMetodo(String method) {
            setMethod(method);
        }
        
        public double getTotal() {
            return total;
        }
        
        public double getMonto() {
            return getTotal();
        }
        
        public void setTotal(double total) {
            this.total = total;
        }
        
        public void setMonto(double total) {
            setTotal(total);
        }
        
        public String getFormattedDate() {
            if (date != null) {
                return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
            }
            return "";
        }
        
        public String getFechaTexto() {
            return getFormattedDate();
        }
    }
}
