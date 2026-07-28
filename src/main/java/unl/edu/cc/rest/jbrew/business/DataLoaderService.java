package unl.edu.cc.rest.jbrew.business;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import unl.edu.cc.rest.jbrew.business.service.CrudGenericService;
import unl.edu.cc.rest.jbrew.domain.Inventory.Category;
import unl.edu.cc.rest.jbrew.domain.Inventory.Product;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.logging.Logger;

@Singleton
@Startup
public class DataLoaderService {

    private static final Logger LOGGER = Logger.getLogger(DataLoaderService.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    private CrudGenericService crudGenericService;

    @PostConstruct
    public void initializeData() {
        LOGGER.info("Inicializando datos de la base de datos...");
        
        // Verificar si ya hay datos
        Long categoryCount = (Long) em.createQuery("SELECT COUNT(c) FROM Category c").getSingleResult();
        if (categoryCount > 0) {
            LOGGER.info("Los datos ya están inicializados. Saltando carga inicial.");
            return;
        }

        try {
            // Crear categorías
            Category bebidas = new Category(1, "Bebidas");
            Category alimentos = new Category(2, "Alimentos");
            Category postres = new Category(3, "Postres");
            Category snacks = new Category(4, "Snacks");

            crudGenericService.create(bebidas);
            crudGenericService.create(alimentos);
            crudGenericService.create(postres);
            crudGenericService.create(snacks);

            LOGGER.info("Categorías creadas: " + categoryCount);

            // Crear productos
            Product producto1 = new Product(1, "CAF-001", "Café Americano", "Café americano tradicional", bebidas, "cafe_americano.jpg", 2.50, 1.20, 50, 10);
            Product producto2 = new Product(2, "CAF-002", "Cappuccino", "Cappuccino italiano", bebidas, "cappuccino.jpg", 3.50, 1.80, 30, 8);
            Product producto3 = new Product(3, "POS-001", "Sandwich de Pollo", "Sandwich de pollo con vegetales", alimentos, "sandwich_pollo.jpg", 5.00, 2.50, 25, 5);
            Product producto4 = new Product(4, "POS-002", "Croissant", "Croissant de mantequilla", snacks, "croissant.jpg", 2.00, 0.80, 40, 15);
            Product producto5 = new Product(5, "DES-001", "Cheesecake", "Cheesecake de frutos rojos", postres, "cheesecake.jpg", 4.50, 2.00, 15, 5);

            crudGenericService.create(producto1);
            crudGenericService.create(producto2);
            crudGenericService.create(producto3);
            crudGenericService.create(producto4);
            crudGenericService.create(producto5);

            LOGGER.info("Productos creados: 5");

            // Crear clientes
            Customer cliente1 = new Customer(1L, "0912345678", "Juan", "Pérez", "0991234567", "juan.perez@email.com", "Av. Principal 123", "Empresa ABC", 1000.00);
            Customer cliente2 = new Customer(2L, "0923456789", "María", "García", "0992345678", "maria.garcia@email.com", "Calle Secundaria 456", "Comercial XYZ", 500.00);

            crudGenericService.create(cliente1);
            crudGenericService.create(cliente2);

            LOGGER.info("Clientes creados: 2");

            // Crear proveedores
            Supplier proveedor1 = new Supplier(1L, "1712345678001", "Distribuidora Central", "Distribuidora Central S.A.", "Carlos Rodríguez", "022345678", "central@distribuidora.com", "Av. Industrial 789");
            Supplier proveedor2 = new Supplier(2L, "1723456789001", "Proveedores del Sur", "Proveedores del Sur Ltda.", "Ana Martínez", "022345679", "sur@proveedores.com", "Calle Comercial 321");

            crudGenericService.create(proveedor1);
            crudGenericService.create(proveedor2);

            LOGGER.info("Proveedores creados: 2");

            LOGGER.info("Datos iniciales cargados exitosamente en la base de datos.");

        } catch (Exception e) {
            LOGGER.severe("Error al inicializar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
