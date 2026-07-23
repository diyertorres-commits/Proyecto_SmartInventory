package unl.edu.cc.rest.jbrew.domain.People;

import jakarta.persistence.*;

@Entity
@Table(name = "suppliers")
public class Supplier extends Person {
    
    @Column(name = "razon_social")
    private String razonSocial;
    
    @Column(name = "contacto")
    private String contacto;

    public Supplier() {
        super(0L, "", "", "", "", "");
        this.razonSocial = "";
        this.contacto = "";
    }

    public Supplier(Long idSupplier, String identificationNumber, String name, String razonSocial, String contacto, String phone, String email, String address) {
        super(idSupplier, identificationNumber, name, phone, email, address);
        this.razonSocial = razonSocial;
        this.contacto = contacto;
    }

    // Getters específicos si se necesita mantener compatibilidad
    public Long getIdSupplier() {
        return getId();
    }

    public void setIdSupplier(Long idSupplier) {
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
