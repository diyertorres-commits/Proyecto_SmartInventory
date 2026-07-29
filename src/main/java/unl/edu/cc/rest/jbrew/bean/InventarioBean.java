package unl.edu.cc.rest.jbrew.bean;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import unl.edu.cc.rest.jbrew.business.InventoryFacade;
import unl.edu.cc.rest.jbrew.business.CategoryService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.List;

@Named
@ViewScoped
public class InventarioBean implements java.io.Serializable {

    @Inject
    private InventoryFacade inventoryFacade;

    @Inject
    private CategoryService categoryService;

    private List<Category> categorias;
    private List<Product> productos;
    private List<Customer> clientes;
    private List<Supplier> proveedores;

    private Category nuevaCategoria = new Category();
    private Category categoriaEnEdicion = new Category();

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    public void cargarDatos() {
        this.categorias = inventoryFacade.getAllCategories();
        this.productos = inventoryFacade.getAllProducts();
        this.clientes = inventoryFacade.getAllCustomers();
        this.proveedores = inventoryFacade.getAllSuppliers();
    }

    // ---------- Crear categoría ----------

    public void prepararNuevaCategoria() {
        this.nuevaCategoria = new Category();
    }

    public void guardarCategoria() {
        CategoryService.CategoryResult resultado = categoryService.guardarCategoria(nuevaCategoria);
        
        if (!resultado.isExitoso()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", resultado.getMensaje()));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Categoría creada", resultado.getMensaje()));

        cargarDatos();
        this.nuevaCategoria = new Category();
    }

    // ---------- Editar categoría ----------

    public void prepararEdicionCategoria(Category categoria) {
        Category copia = new Category();
        copia.setId(categoria.getId());
        copia.setName(categoria.getName());
        this.categoriaEnEdicion = copia;
    }

    public void actualizarCategoria() {
        CategoryService.CategoryResult resultado = categoryService.actualizarCategoria(categoriaEnEdicion);
        
        if (!resultado.isExitoso()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", resultado.getMensaje()));
            return;
        }

        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Categoría actualizada", resultado.getMensaje()));

        cargarDatos();
    }


    // ---------- Getters ----------

    public List<Product> getProductos() {
        return productos;
    }

    public List<Category> getCategorias() {
        return categorias;
    }

    public List<Customer> getClientes() {
        return clientes;
    }

    public List<Supplier> getProveedores() {
        return proveedores;
    }

    public Category getNuevaCategoria() {
        return nuevaCategoria;
    }

    public void setNuevaCategoria(Category nuevaCategoria) {
        this.nuevaCategoria = nuevaCategoria;
    }

    public Category getCategoriaEnEdicion() {
        return categoriaEnEdicion;
    }

    public void setCategoriaEnEdicion(Category categoriaEnEdicion) {
        this.categoriaEnEdicion = categoriaEnEdicion;
    }

    // ---------- Métodos auxiliares ----------

    public List<Product> getProductosPorCategoria(Category categoria) {
        if (productos == null || categoria == null || categoria.getName() == null) {
            return List.of();
        }
        return productos.stream()
                .filter(p -> p.getCategory() != null && categoria.getName().equals(p.getCategory().getName()))
                .toList();
    }

    public List<Product> getProductosPorCategoria(String nombreCategoria) {
        return inventoryFacade.findProductsByCategory(nombreCategoria);
    }

    public List<Product> getProductosStockCritico() {
        if (productos == null)
            return List.of();
        return productos.stream()
                .filter(p -> p.getStock() <= p.getMinStock())
                .toList();
    }

    public Product buscarProductoPorId(int id) {
        return inventoryFacade.findProductById(id).orElse(null);
    }

    public Product buscarProductoPorNombre(String nombre) {
        return inventoryFacade.findProductByName(nombre).orElse(null);
    }

    public Customer buscarClientePorId(int id) {
        if (clientes == null)
            return null;
        return clientes.stream()
                .filter(c -> c.getIdCustomer() == id)
                .findFirst()
                .orElse(null);
    }

    public Supplier buscarProveedorPorId(int id) {
        if (proveedores == null)
            return null;
        return proveedores.stream()
                .filter(s -> s.getIdSupplier() == id)
                .findFirst()
                .orElse(null);
    }
}