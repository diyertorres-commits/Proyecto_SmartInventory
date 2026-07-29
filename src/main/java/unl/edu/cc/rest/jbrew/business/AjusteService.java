package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.dao.AjusteDAO;
import unl.edu.cc.rest.jbrew.domain.Ajuste;
import unl.edu.cc.rest.jbrew.domain.AjusteRequest;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;

import java.util.List;
import java.util.Date;
import java.util.logging.Logger;

@Stateless
public class AjusteService {

    private static final Logger LOGGER = Logger.getLogger(AjusteService.class.getName());

    @Inject
    private AjusteDAO ajusteDAO;

    @Inject
    private InventoryService inventoryService;

    public Ajuste registrarAjuste(String productoNombre, String tipoAjuste, String operacion,
                                  int cantidad, int stockAnterior, int stockNuevo,
                                  String motivo, String responsable) {
        Ajuste ajuste = new Ajuste(
                getNextAjusteId(),
                new Date(),
                productoNombre,
                tipoAjuste,
                operacion,
                cantidad,
                stockAnterior,
                stockNuevo,
                motivo,
                responsable
        );
        ajusteDAO.save(ajuste);
        return ajuste;
    }

    public AjusteResult procesarAjuste(AjusteRequest request) {
        // Validar campos requeridos
        if (request.getProductoId() == null || request.getTipoAjuste() == null 
                || request.getTipoOperacion() == null || request.getCantidadAjuste() <= 0) {
            return AjusteResult.error("Por favor complete todos los campos requeridos");
        }

        // Buscar producto
        Product producto = inventoryService.findProductById(request.getProductoId()).orElse(null);
        if (producto == null) {
            return AjusteResult.error("El producto seleccionado ya no existe");
        }

        int stockAnterior = producto.getStock();
        int stockNuevo;

        // Calcular nuevo stock según operación
        if ("restar".equals(request.getTipoOperacion())) {
            if (stockAnterior < request.getCantidadAjuste()) {
                return AjusteResult.error("Stock insuficiente para restar");
            }
            stockNuevo = stockAnterior - request.getCantidadAjuste();
        } else {
            stockNuevo = stockAnterior + request.getCantidadAjuste();
        }

        // Actualizar stock del producto
        producto.setStock(stockNuevo);
        inventoryService.saveProduct(producto);

        // Registrar el ajuste
        Ajuste ajuste = registrarAjuste(
                producto.getName(),
                request.getTipoAjuste(),
                request.getTipoOperacion(),
                request.getCantidadAjuste(),
                stockAnterior,
                stockNuevo,
                request.getObservacion() != null ? request.getObservacion() : "",
                request.getResponsable() != null ? request.getResponsable() : "No especificado"
        );

        // Verificar si hay stock bajo
        boolean stockBajo = producto.verifyStockMinimo();
        String mensajeStockBajo = null;
        if (stockBajo) {
            mensajeStockBajo = "Advertencia: poco stock del producto \"" + producto.getName()
                    + "\" (quedan " + stockNuevo + " unidades, mínimo recomendado: " + producto.getMinStock() + ")";
        }

        return AjusteResult.success(ajuste, stockBajo, mensajeStockBajo);
    }

    public void revertirAjuste(Ajuste ajuste) {
        Product producto = inventoryService.findProductByName(ajuste.getProductoNombre()).orElse(null);
        if (producto != null) {
            producto.setStock(ajuste.getStockAnterior());
            inventoryService.saveProduct(producto);
            eliminarAjuste(ajuste);
        }
    }

    public void eliminarAjuste(Ajuste ajuste) {
        ajusteDAO.delete(ajuste);
    }

    public List<Ajuste> obtenerAjustes() {
        return ajusteDAO.findAllOrderByDateDesc();
    }

    private int getNextAjusteId() {
        // Obtener el último ID de ajuste de la base de datos
        Ajuste lastAjuste = ajusteDAO.findLastAjuste();
        if (lastAjuste == null) {
            return 1;
        }
        return lastAjuste.getIdAjuste() + 1;
    }

    public static class AjusteResult {
        private final boolean exitoso;
        private final String mensaje;
        private final Ajuste ajuste;
        private final boolean stockBajo;
        private final String mensajeStockBajo;

        private AjusteResult(boolean exitoso, String mensaje, Ajuste ajuste, boolean stockBajo, String mensajeStockBajo) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.ajuste = ajuste;
            this.stockBajo = stockBajo;
            this.mensajeStockBajo = mensajeStockBajo;
        }

        public static AjusteResult success(Ajuste ajuste, boolean stockBajo, String mensajeStockBajo) {
            return new AjusteResult(true, "Ajuste registrado correctamente", ajuste, stockBajo, mensajeStockBajo);
        }

        public static AjusteResult error(String mensaje) {
            return new AjusteResult(false, mensaje, null, false, null);
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public Ajuste getAjuste() {
            return ajuste;
        }

        public boolean isStockBajo() {
            return stockBajo;
        }

        public String getMensajeStockBajo() {
            return mensajeStockBajo;
        }
    }
}
