package unl.edu.cc.rest.jbrew.domain.Inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidCategoryNameException;
import java.util.ArrayList;
import java.util.List;
@Entity
public class Category {

    @Id
    private Long id;
    private int idCategory;
    private String name;
    private List<Product> productList; // Composición con Product

    public Category(int idCategory, String name) throws InvalidCategoryNameException {
        this.idCategory = idCategory;
        setName(name);
        this.productList = new ArrayList<>();
    }

    public Category() {

    }

    private void validateName(String name) throws InvalidCategoryNameException {
        if(name == null || name.trim().isEmpty()){
            throw new InvalidCategoryNameException("El nombre de la categoría no puede estar vacío");
        }
        if(name.matches(".*\\d.*")){
            throw new InvalidCategoryNameException("El nombre de la categoría no puede contener números");
        }
    }
    public void addProduct(Product product) { // Asociación con Product
        productList.add(product);
    }

    public void removeProduct(Product product) { // Asociación con Product
        productList.remove(product);
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getters y Setters
    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidCategoryNameException {
        validateName(name);
        this.name = name;
    }
}
