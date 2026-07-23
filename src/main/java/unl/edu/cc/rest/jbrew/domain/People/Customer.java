package unl.edu.cc.rest.jbrew.domain.People;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer extends Person {
    
    @Column(name = "apellido")
    private String apellido;
    
    @Column(name = "empresa")
    private String empresa;
    
    @Column(name = "limite_credito")
    private double limiteCredito;

    public Customer() {
        super(0L, "", "", "", "", "");
        this.apellido = "";
        this.empresa = "";
        this.limiteCredito = 0;
    }

    public Customer(Long idCustomer, String identificationNumber, String name, String apellido, String phone, String email, String address, String empresa, double limiteCredito) {
        super(idCustomer, identificationNumber, name, phone, email, address);
        this.apellido = apellido;
        this.empresa = empresa;
        this.limiteCredito = limiteCredito;
    }

    // Getters específicos si se necesita mantener compatibilidad
    public Long getIdCustomer() {
        return getId();
    }

    public void setIdCustomer(Long idCustomer) {
        setId(idCustomer);
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }
}
