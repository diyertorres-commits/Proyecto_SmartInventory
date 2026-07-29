package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.VentaService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class FacturaBean implements Serializable {

    @Inject
    private VentaService ventaService;

    @Inject
    private PurchaseService purchaseService;

    private List<InvoiceInfo> facturasFiltradas = new ArrayList<>();
    private InvoiceInfo facturaSeleccionada = new InvoiceInfo();

    // Filtros de búsqueda
    private String filtroTipo;
    private String filtroCodigo;
    private Date filtroFechaDesde;
    private Date filtroFechaHasta;
    private Integer filtroMes;

    public void filtrar() {
        List<InvoiceInfo> todas = new ArrayList<>();
        todas.addAll(mapearVentas(ventaService.obtenerFacturas()));
        todas.addAll(mapearCompras(purchaseService.getPurchaseInvoices()));

        facturasFiltradas = todas.stream()
                .filter(this::coincideTipo)
                .filter(this::coincideCodigo)
                .filter(this::coincideRangoFechas)
                .filter(this::coincideMes)
                .sorted(Comparator.comparing(InvoiceInfo::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public void limpiarFiltros() {
        filtroTipo = null;
        filtroCodigo = null;
        filtroFechaDesde = null;
        filtroFechaHasta = null;
        filtroMes = null;
        filtrar();
    }

    private boolean coincideTipo(InvoiceInfo factura) {
        return filtroTipo == null || filtroTipo.isBlank() || filtroTipo.equals(factura.getTipo());
    }

    private boolean coincideCodigo(InvoiceInfo factura) {
        if (filtroCodigo == null || filtroCodigo.isBlank()) {
            return true;
        }
        return factura.getNumero() != null
                && factura.getNumero().toLowerCase().contains(filtroCodigo.toLowerCase());
    }

    private boolean coincideRangoFechas(InvoiceInfo factura) {
        if (factura.getFecha() == null) {
            return filtroFechaDesde == null && filtroFechaHasta == null;
        }
        if (filtroFechaDesde != null && factura.getFecha().before(inicioDia(filtroFechaDesde))) {
            return false;
        }
        if (filtroFechaHasta != null && factura.getFecha().after(finDia(filtroFechaHasta))) {
            return false;
        }
        return true;
    }

    private boolean coincideMes(InvoiceInfo factura) {
        if (filtroMes == null) {
            return true;
        }
        if (factura.getFecha() == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(factura.getFecha());
        return (cal.get(Calendar.MONTH) + 1) == filtroMes;
    }

    private Date inicioDia(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date finDia(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public void verDetalle(InvoiceInfo factura) {
        this.facturaSeleccionada = factura;
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

    // Propiedades usadas por facturas.xhtml

    public String getFiltroTipo() {
        return filtroTipo;
    }

    public void setFiltroTipo(String filtroTipo) {
        this.filtroTipo = filtroTipo;
    }

    public String getFiltroCodigo() {
        return filtroCodigo;
    }

    public void setFiltroCodigo(String filtroCodigo) {
        this.filtroCodigo = filtroCodigo;
    }

    public Date getFiltroFechaDesde() {
        return filtroFechaDesde;
    }

    public void setFiltroFechaDesde(Date filtroFechaDesde) {
        this.filtroFechaDesde = filtroFechaDesde;
    }

    public Date getFiltroFechaHasta() {
        return filtroFechaHasta;
    }

    public void setFiltroFechaHasta(Date filtroFechaHasta) {
        this.filtroFechaHasta = filtroFechaHasta;
    }

    public Integer getFiltroMes() {
        return filtroMes;
    }

    public void setFiltroMes(Integer filtroMes) {
        this.filtroMes = filtroMes;
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

        // Datos adicionales del cliente/proveedor
        private String identificacion;
        private String telefono;
        private String email;
        private String direccion;
        private String empresaORazonSocial; // "empresa" en Customer, "razonSocial" en Supplier

        // Referencia a la factura original, para el detalle de ítems al imprimir
        private SaleInvoice ventaOriginal;
        private PurchaseInvoice compraOriginal;

        public static InvoiceInfo desdeVenta(SaleInvoice factura) {
            InvoiceInfo info = new InvoiceInfo();
            info.numero = factura.getInvoiceNumber();
            info.fecha = factura.getInvoiceDate();
            info.tipo = "VENTA";
            info.metodo = factura.getPaymentMethod();
            info.total = factura.getTotal();
            info.ventaOriginal = factura;

            if (factura.getCustomer() != null) {
                var cliente = factura.getCustomer();
                info.tercero = cliente.getName() + " " + cliente.getApellido();
                info.identificacion = cliente.getIdentificationNumber();
                info.telefono = cliente.getPhone();
                info.email = cliente.getEmail();
                info.direccion = cliente.getAddress();
                info.empresaORazonSocial = cliente.getEmpresa();
            } else {
                info.tercero = "Cliente Mostrador";
            }
            return info;
        }

        public static InvoiceInfo desdeCompra(PurchaseInvoice factura) {
            InvoiceInfo info = new InvoiceInfo();
            info.numero = factura.getInvoiceNumber();
            info.fecha = factura.getInvoiceDate();
            info.tipo = "COMPRA";
            info.metodo = factura.getPurchaseOrderNumber();
            info.total = factura.getTotal();
            info.compraOriginal = factura;

            if (factura.getSupplier() != null) {
                var proveedor = factura.getSupplier();
                info.tercero = proveedor.getName();
                info.identificacion = proveedor.getIdentificationNumber();
                info.telefono = proveedor.getPhone();
                info.email = proveedor.getEmail();
                info.direccion = proveedor.getAddress();
                info.empresaORazonSocial = proveedor.getRazonSocial();
            } else {
                info.tercero = "N/A";
            }
            return info;
        }

        public String getNumero() {
            return numero;
        }

        public Date getFecha() {
            return fecha;
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

        public String getIdentificacion() {
            return identificacion;
        }

        public String getTelefono() {
            return telefono;
        }

        public String getEmail() {
            return email;
        }

        public String getDireccion() {
            return direccion;
        }

        public String getEmpresaORazonSocial() {
            return empresaORazonSocial;
        }

        public SaleInvoice getVentaOriginal() {
            return ventaOriginal;
        }

        public PurchaseInvoice getCompraOriginal() {
            return compraOriginal;
        }
    }
}