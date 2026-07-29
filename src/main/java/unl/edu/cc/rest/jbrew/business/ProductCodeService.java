package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;

@Stateless
public class ProductCodeService {

    @Inject
    private InventoryService inventoryService;

    public String generateCode(Category category) {
        if (category == null || category.getName() == null) {
            return "GEN-0001";
        }

        String prefijo = obtenerPrefijo(category.getName());
        long cantidadExistente = inventoryService.getAllProducts().stream()
                .filter(p -> p.getCodigo() != null && p.getCodigo().startsWith(prefijo + "-"))
                .count();
        return prefijo + "-" + String.format("%04d", cantidadExistente + 1);
    }

    private String obtenerPrefijo(String nombreCategoria) {
        String nombreNormalizado = nombreCategoria.toUpperCase()
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("Ñ", "N");
        
        // Tomar las primeras 3 letras o menos si el nombre es más corto
        if (nombreNormalizado.length() >= 3) {
            return nombreNormalizado.substring(0, 3);
        }
        return String.format("%-3s", nombreNormalizado).replace(" ", "X");
    }
}
