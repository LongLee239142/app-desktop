package atlantafx.sampler.admin.entity;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OrderItem {
    private SimpleStringProperty supplyCode; // Changed to non-static
    private SimpleStringProperty itemName;
    private SimpleStringProperty supplierName;
    private SimpleIntegerProperty quantity; // Change to SimpleIntegerProperty
    private SimpleStringProperty unit; // Make sure to initialize this
    private SimpleDoubleProperty price; // Change to SimpleDoubleProperty
    private SimpleDoubleProperty totalValue; // Change to SimpleDoubleProperty

    // Default constructor
    public OrderItem() {
        this.supplyCode = new SimpleStringProperty();
        this.itemName = new SimpleStringProperty();
        this.supplierName = new SimpleStringProperty();
        this.quantity = new SimpleIntegerProperty(1); // Initialize with default quantity of 1
        this.unit = new SimpleStringProperty(); // Initialize unit
        this.price = new SimpleDoubleProperty();
        this.totalValue = new SimpleDoubleProperty();
    }

    // Constructor with parameters
    public OrderItem(String supplyCode, double price) {
        this();
        setSupplyCode(supplyCode);
        setPrice(price);
    }

    // Getter and Setter for properties
    public String getSupplyCode() {
        return supplyCode.get();
    }

    public void setSupplyCode(String supplyCode) {
        this.supplyCode.set(supplyCode);
    }

    public String getItemName() {
        return itemName.get();
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    public String getSupplierName() {
        return supplierName.get();
    }

    public void setSupplierName(String supplierName) {
        this.supplierName.set(supplierName);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
        updateTotalValue(); // Update total value
    }

    public String getUnit() {
        return unit.get();
    }

    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public double getPrice() {
        return price.get();
    }

    public void setPrice(double price) {
        this.price.set(price);
        updateTotalValue(); // Update total value when price changes
    }

    public double getTotalValue() {
        return totalValue.get();
    }

    public void setTotalValue(double totalValue) {
        this.totalValue.set(totalValue); // Optional setter if needed
    }

    private void updateTotalValue() {
        this.totalValue.set(price.get() * quantity.get()); // Update total value
    }
}
