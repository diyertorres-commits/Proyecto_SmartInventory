package unl.edu.cc.rest.jbrew.domain.Inventory;

import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductNameException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductPriceException;
import unl.edu.cc.rest.jbrew.domain.Exception.InvalidProductStockException;
import unl.edu.cc.rest.jbrew.domain.Reports.StockAlert;
import unl.edu.cc.rest.jbrew.domain.Kardex.Kardex;
import java.util.ArrayList;
import java.util.List;

public class Product {

    private int idProduct;
    private String name;
    private String description;
    private double salePrice;
    private double purchasePrice;
    private int stock;
    private int minStock;
    private List<Kardex> kardexList; // Relación 1 → 0..* con Kardex

    public Product() {
        salePrice = 0;
        purchasePrice = 0;
        this.kardexList = new ArrayList<>();
    }

    public Product(int idProduct, String name, String description, double salePrice, double purchasePrice, int stock, int minStock) throws InvalidProductNameException, InvalidProductPriceException, InvalidProductStockException {
        this.idProduct = idProduct;
        setName(name);
        this.description = description;
        setSalePrice(salePrice);
        this.purchasePrice = purchasePrice;
        setStock(stock);
        this.minStock = minStock;
        this.kardexList = new ArrayList<>();
    }
    public void updateProduct(String name, Double salePrice) throws InvalidProductNameException, InvalidProductPriceException {
        setName(name);
        setSalePrice(salePrice);
    }

    public void modifyStock(int quantity) {
        stock += quantity;
    }

    public boolean verifyStockMinimo() {
        return stock <= minStock;
    }

    public StockAlert generateStockAlert(int idAlert) { // Asociación con StockAlert
        if (verifyStockMinimo()) {
            return new StockAlert(idAlert, this, stock, minStock, new java.util.Date()); // Composición con StockAlert
        }
        return null;
    }

    private void validateName(String name) throws InvalidProductNameException{
            if(name == null || name.trim().isEmpty()){
                throw new InvalidProductNameException("El nombre del producto es inválido");
            }
        if(!name.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")){
            throw new InvalidProductNameException("El nombre del producto solo puede contener letras y espacios");
        }

    }
    private void validateStock(int stock) throws InvalidProductStockException{
            if(stock == 0 || stock < 0){
                throw new InvalidProductStockException("El stock no puede ser negativo ni cero");
            }
    }
    private void validatePrice(double salePrice) throws InvalidProductPriceException{
            if(salePrice <= 0 || salePrice >=100){
                throw new InvalidProductPriceException("El precio está fuera del rango establecido");
            }
    }
    // Getters y Setters
    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidProductNameException{
        validateName(name);
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) throws InvalidProductPriceException{
        validatePrice(salePrice);
        this.salePrice = salePrice;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) throws InvalidProductPriceException {
        if (purchasePrice <= 0) {
            throw new InvalidProductPriceException("El precio de compra debe ser mayor que cero");
        }
        this.purchasePrice = purchasePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) throws InvalidProductStockException{
        validateStock(stock);
        this.stock = stock;
    }
    public int getMinStock() {
        return minStock;
    }


    public void setMinStock(int minStock) {
        if (minStock < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo");
        }
        this.minStock = minStock;
    }

    public List<Kardex> getKardexList() {
        return kardexList;
    }

    public void setKardexList(List<Kardex> kardexList) {
        this.kardexList = kardexList;
    }

    public void addKardex(Kardex kardex) {
        kardexList.add(kardex);
    }

}
