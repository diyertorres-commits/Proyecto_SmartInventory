package unl.edu.cc.rest.jbrew.domain.People;

public abstract class Person {
    private int id;
    private String identificationNumber; // DNI/RUC
    private String name;
    private String phone;
    private String email;
    private String address;

    public Person(int id, String identificationNumber, String name, String phone, String email, String address) {
        this.id = id;
        this.identificationNumber = identificationNumber;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
