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
import unl.edu.cc.rest.jbrew.domain.Invoice.PurchaseInvoice;
import unl.edu.cc.rest.jbrew.domain.Invoice.SaleInvoice;
import unl.edu.cc.rest.jbrew.domain.Movements.Movement;
import unl.edu.cc.rest.jbrew.domain.Movements.MovementType;
import unl.edu.cc.rest.jbrew.domain.People.Customer;
import unl.edu.cc.rest.jbrew.domain.People.Supplier;

import java.util.Date;
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

            // Crear datos de prueba de compras
            Movement movementCompra1 = new Movement(1, MovementType.ENTRY, new Date(), "Compra de prueba - Café Americano");
            movementCompra1.addProductMovement(producto1, 20, 1.20);
            movementCompra1.processMovement();
            crudGenericService.create(movementCompra1);

            PurchaseInvoice purchaseInvoice1 = new PurchaseInvoice();
            purchaseInvoice1.setIdInvoice(1);
            purchaseInvoice1.setInvoiceDate(new Date());
            purchaseInvoice1.setInvoiceNumber("FAC-COMP-000001");
            purchaseInvoice1.setPurchaseOrderNumber("PO-000001");
            purchaseInvoice1.setSupplier(proveedor1);
            purchaseInvoice1.setMovement(movementCompra1);
            purchaseInvoice1.generateInvoice();
            crudGenericService.create(purchaseInvoice1);

            Movement movementCompra2 = new Movement(2, MovementType.ENTRY, new Date(), "Compra de prueba - Cappuccino");
            movementCompra2.addProductMovement(producto2, 15, 1.80);
            movementCompra2.processMovement();
            crudGenericService.create(movementCompra2);

            PurchaseInvoice purchaseInvoice2 = new PurchaseInvoice();
            purchaseInvoice2.setIdInvoice(2);
            purchaseInvoice2.setInvoiceDate(new Date());
            purchaseInvoice2.setInvoiceNumber("FAC-COMP-000002");
            purchaseInvoice2.setPurchaseOrderNumber("PO-000002");
            purchaseInvoice2.setSupplier(proveedor2);
            purchaseInvoice2.setMovement(movementCompra2);
            purchaseInvoice2.generateInvoice();
            crudGenericService.create(purchaseInvoice2);

            LOGGER.info("Compras de prueba creadas: 2");

            // Crear datos de prueba de ventas
            Movement movementVenta1 = new Movement(3, MovementType.EXIT, new Date(), "Venta de prueba - Café Americano");
            movementVenta1.addProductMovement(producto1, 5, 2.50);
            movementVenta1.processMovement();
            crudGenericService.create(movementVenta1);

            SaleInvoice saleInvoice1 = new SaleInvoice();
            saleInvoice1.setIdInvoice(1);
            saleInvoice1.setInvoiceDate(new Date());
            saleInvoice1.setInvoiceNumber("FAC-000001");
            saleInvoice1.setCustomer(cliente1);
            saleInvoice1.setPaymentMethod("efectivo");
            saleInvoice1.setMovement(movementVenta1);
            saleInvoice1.generateInvoice();
            saleInvoice1.setSubtotal(saleInvoice1.getTotal());
            saleInvoice1.setTax(saleInvoice1.getTotal() * 0.12);
            saleInvoice1.setDiscount(0);
            saleInvoice1.setTotal(saleInvoice1.getSubtotal() + saleInvoice1.getTax());
            crudGenericService.create(saleInvoice1);

            Movement movementVenta2 = new Movement(4, MovementType.EXIT, new Date(), "Venta de prueba - Cappuccino");
            movementVenta2.addProductMovement(producto2, 3, 3.50);
            movementVenta2.processMovement();
            crudGenericService.create(movementVenta2);

            SaleInvoice saleInvoice2 = new SaleInvoice();
            saleInvoice2.setIdInvoice(2);
            saleInvoice2.setInvoiceDate(new Date());
            saleInvoice2.setInvoiceNumber("FAC-000002");
            saleInvoice2.setCustomer(cliente2);
            saleInvoice2.setPaymentMethod("tarjeta");
            saleInvoice2.setMovement(movementVenta2);
            saleInvoice2.generateInvoice();
            saleInvoice2.setSubtotal(saleInvoice2.getTotal());
            saleInvoice2.setTax(saleInvoice2.getTotal() * 0.12);
            saleInvoice2.setDiscount(0);
            saleInvoice2.setTotal(saleInvoice2.getSubtotal() + saleInvoice2.getTax());
            crudGenericService.create(saleInvoice2);

            LOGGER.info("Ventas de prueba creadas: 2");

            LOGGER.info("Datos iniciales cargados exitosamente en la base de datos.");

        } catch (Exception e) {
            LOGGER.severe("Error al inicializar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
