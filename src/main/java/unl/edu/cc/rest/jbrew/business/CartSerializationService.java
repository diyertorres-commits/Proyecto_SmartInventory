package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.Sales.Carrito;
import unl.edu.cc.rest.jbrew.domain.Sales.VentaDTO;

import java.util.Optional;
import java.util.logging.Logger;

@Stateless
public class CartSerializationService {

    private static final Logger LOGGER = Logger.getLogger(CartSerializationService.class.getName());

    @Inject
    private InventoryFacade inventoryFacade;

    public CartRestoreResult restoreFromJson(String carritoJson, Carrito carrito, VentaDTO ventaDTO) {
        try {
            if (carritoJson == null || carritoJson.isEmpty()) {
                return new CartRestoreResult(false, "JSON vacío", 0);
            }

            String[] parts = carritoJson.split("\"items\":\\[");
            if (parts.length > 1) {
                String itemsPart = parts[1].split("\\],\"descuento\"")[0];
                String[] itemStrings = itemsPart.split("\\},\\{");

                carrito.vaciar();
                int itemsRestored = 0;

                for (String itemStr : itemStrings) {
                    String nombre = extraerValor(itemStr, "productoNombre");
                    int cantidad = Integer.parseInt(extraerValor(itemStr, "cantidad"));
                    double precio = Double.parseDouble(extraerValor(itemStr, "precio"));

                    Optional<Product> productoOpt = inventoryFacade.findProductByName(nombre);
                    if (productoOpt.isPresent()) {
                        carrito.agregarItem(productoOpt.get(), cantidad);
                        itemsRestored++;
                    }
                }

                String descuentoStr = carritoJson.split("\"descuento\":")[1].split("}")[0];
                ventaDTO.setDescuento(Double.parseDouble(descuentoStr));

                return new CartRestoreResult(true, "Carrito restaurado con " + itemsRestored + " productos", itemsRestored);
            }

            return new CartRestoreResult(false, "Formato JSON inválido", 0);
        } catch (Exception e) {
            LOGGER.warning("Error al restaurar carrito desde JSON: " + e.getMessage());
            return new CartRestoreResult(false, "No se pudo restaurar el carrito: " + e.getMessage(), 0);
        }
    }

    private String extraerValor(String json, String clave) {
        String pattern = "\"" + clave + "\":\"?([^,}\\\"]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    public static class CartRestoreResult {
        private final boolean exitoso;
        private final String mensaje;
        private final int itemsRestored;

        public CartRestoreResult(boolean exitoso, String mensaje, int itemsRestored) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.itemsRestored = itemsRestored;
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }

        public int getItemsRestored() {
            return itemsRestored;
        }
    }
}
