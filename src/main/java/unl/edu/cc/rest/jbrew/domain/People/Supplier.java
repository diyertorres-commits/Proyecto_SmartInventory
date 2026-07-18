package unl.edu.cc.rest.jbrew.domain.People;

public class Supplier extends Person {

    public Supplier(int idSupplier, String name, String phone, String email, String address) {
        super(idSupplier, name, phone, email, address);
    }

    // Getters específicos si se necesita mantener compatibilidad
    public int getIdSupplier() {
        return getId();
    }

    public void setIdSupplier(int idSupplier) {
        setId(idSupplier);
    }
}
