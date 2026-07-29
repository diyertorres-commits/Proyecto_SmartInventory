package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class InvoiceService {

    @Inject
    private VentaService ventaService;

    @Inject
    private PurchaseService purchaseService;

    public List<InvoiceInfo> filtrarFacturas(InvoiceFilter filter) {
        List<InvoiceInfo> todas = new ArrayList<>();
        todas.addAll(mapearVentas(ventaService.obtenerFacturas()));
        todas.addAll(mapearCompras(purchaseService.getPurchaseInvoices()));

        return todas.stream()
                .filter(f -> coincideTipo(f, filter.getTipo()))
                .filter(f -> coincideCodigo(f, filter.getCodigo()))
                .filter(f -> coincideRangoFechas(f, filter.getFechaDesde(), filter.getFechaHasta()))
                .filter(f -> coincideMes(f, filter.getMes()))
                .sorted(Comparator.comparing(InvoiceInfo::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    private boolean coincideTipo(InvoiceInfo factura, String filtroTipo) {
        return filtroTipo == null || filtroTipo.isBlank() || filtroTipo.equals(factura.getTipo());
    }

    private boolean coincideCodigo(InvoiceInfo factura, String filtroCodigo) {
        if (filtroCodigo == null || filtroCodigo.isBlank()) {
            return true;
        }
        return factura.getNumero() != null
                && factura.getNumero().toLowerCase().contains(filtroCodigo.toLowerCase());
    }

    private boolean coincideRangoFechas(InvoiceInfo factura, Date fechaDesde, Date fechaHasta) {
        if (factura.getFecha() == null) {
            return fechaDesde == null && fechaHasta == null;
        }
        if (fechaDesde != null && factura.getFecha().before(inicioDia(fechaDesde))) {
            return false;
        }
        if (fechaHasta != null && factura.getFecha().after(finDia(fechaHasta))) {
            return false;
        }
        return true;
    }

    private boolean coincideMes(InvoiceInfo factura, Integer filtroMes) {
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

    public static class InvoiceFilter {
        private String tipo;
        private String codigo;
        private Date fechaDesde;
        private Date fechaHasta;
        private Integer mes;

        public InvoiceFilter() {
            this.tipo = null;
            this.codigo = null;
            this.fechaDesde = null;
            this.fechaHasta = null;
            this.mes = null;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public Date getFechaDesde() {
            return fechaDesde;
        }

        public void setFechaDesde(Date fechaDesde) {
            this.fechaDesde = fechaDesde;
        }

        public Date getFechaHasta() {
            return fechaHasta;
        }

        public void setFechaHasta(Date fechaHasta) {
            this.fechaHasta = fechaHasta;
        }

        public Integer getMes() {
            return mes;
        }

        public void setMes(Integer mes) {
            this.mes = mes;
        }
    }

    public static class InvoiceInfo implements java.io.Serializable {
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
        private String empresaORazonSocial;

        // Referencia a la factura original
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
            return fecha != null ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha) : "";
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
