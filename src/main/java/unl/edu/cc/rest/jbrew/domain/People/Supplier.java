package unl.edu.cc.rest.jbrew.domain.People;

public class Supplier extends Person {
    private String razonSocial;
    private String contacto;

    public Supplier() {
        super(0, "", "", "", "", "");
        this.razonSocial = "";
        this.contacto = "";
    }

    public Supplier(int idSupplier, String identificationNumber, String name, String razonSocial, String contacto, String phone, String email, String address) {
        super(idSupplier, identificationNumber, name, phone, email, address);
        this.razonSocial = razonSocial;
        this.contacto = contacto;
    }

    // Getters específicos si se necesita mantener compatibilidad
    public int getIdSupplier() {
        return getId();
    }

    public void setIdSupplier(int idSupplier) {
        setId(idSupplier);
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
}
