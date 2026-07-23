package unl.edu.cc.rest.jbrew.bean;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.SalesService;
import unl.edu.cc.rest.jbrew.business.PurchaseService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class DashboardBean implements Serializable {
    
    @Inject
    private InventoryFacade inventoryFacade;
    
    @Inject
    private SalesService salesService;
    
    @Inject
    private PurchaseService purchaseService;
    
    public DashboardBean() {
    }
    
    public int getTotalProductos() {
        return inventoryFacade.getAllProducts().size();
    }
    
    public double getValorTotalInventario() {
        return inventoryFacade.getAllProducts().stream()
                .mapToDouble(p -> p.getStock() * p.getPurchasePrice())
                .sum();
    }
    
    public double getTotalVentas() {
        return salesService.getSaleInvoices().stream()
                .mapToDouble(invoice -> 0) // TODO: Calculate actual total from invoice items
                .sum();
    }
    
    public int getProductosBajoStock() {
        return (int) inventoryFacade.getAllProducts().stream()
                .filter(p -> p.getStock() <= p.getMinStock())
                .count();
    }
    
    // Chart placeholders - these would need actual chart model implementations
    public Object getChartValorPorCategoria() {
        return null;
    }
    
    public Object getChartVentasPorProducto() {
        return null;
    }
    
    public Object getChartEstadoStock() {
        return null;
    }
    
    public List<Product> getProductosStockBajo() {
        return inventoryFacade.getAllProducts().stream()
                .filter(p -> p.getStock() <= p.getMinStock())
                .toList();
    }
}
