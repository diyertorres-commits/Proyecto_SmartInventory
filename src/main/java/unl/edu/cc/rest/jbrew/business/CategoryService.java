package unl.edu.cc.rest.jbrew.business;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import unl.edu.cc.rest.jbrew.dao.CategoryDAO;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;

import java.util.List;
import java.util.Objects;

@Stateless
public class CategoryService {

    @Inject
    private CategoryDAO categoryDAO;

    public CategoryResult guardarCategoria(Category categoria) {
        String nombre = categoria.getName();
        if (nombre == null || nombre.isBlank()) {
            return CategoryResult.error("El nombre de la categoría no puede estar vacío");
        }

        if (existeNombre(nombre.trim(), null)) {
            return CategoryResult.error("Ya existe una categoría con ese nombre");
        }

        categoria.setName(nombre.trim());
        categoryDAO.save(categoria);

        return CategoryResult.success("Categoría creada: \"" + nombre.trim() + "\"");
    }

    public CategoryResult actualizarCategoria(Category categoria) {
        String nombre = categoria.getName();
        if (nombre == null || nombre.isBlank()) {
            return CategoryResult.error("El nombre de la categoría no puede estar vacío");
        }

        if (existeNombre(nombre.trim(), categoria.getId())) {
            return CategoryResult.error("Ya existe otra categoría con ese nombre");
        }

        categoria.setName(nombre.trim());
        categoryDAO.save(categoria);

        return CategoryResult.success("Categoría actualizada correctamente");
    }

    public List<Category> obtenerTodasCategorias() {
        return categoryDAO.findAll();
    }

    private boolean existeNombre(String nombre, Long idExcluir) {
        List<Category> categorias = categoryDAO.findAll();
        if (categorias == null) {
            return false;
        }
        return categorias.stream()
                .filter(c -> idExcluir == null || !Objects.equals(c.getId(), idExcluir))
                .anyMatch(c -> c.getName().equalsIgnoreCase(nombre));
    }

    public static class CategoryResult {
        private final boolean exitoso;
        private final String mensaje;

        private CategoryResult(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }

        public static CategoryResult success(String mensaje) {
            return new CategoryResult(true, mensaje);
        }

        public static CategoryResult error(String mensaje) {
            return new CategoryResult(false, mensaje);
        }

        public boolean isExitoso() {
            return exitoso;
        }

        public String getMensaje() {
            return mensaje;
        }
    }
}
