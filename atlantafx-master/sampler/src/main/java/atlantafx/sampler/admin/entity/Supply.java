package atlantafx.sampler.admin.entity;

public class Supply {
    private String supplyCode;  // Code for the supply
    private String name;        // Name of the supply
    private String unit;        // Unit of measure (e.g., kg, piece)
    private double price;       // Price of the supply
    private double quantity;    // Available quantity
    private String suppliersId; // ID for the supplier
    private String supplierName; // Name of the supplier
    private double totalValue;   // Total value calculated as price * quantity

    // Constructor with all attributes
    public Supply(String supplyCode, String name, String unit, double price, double quantity, String suppliersId, String supplierName) {
        this.supplyCode = supplyCode;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.suppliersId = suppliersId;
        this.supplierName = supplierName;
        this.totalValue = price * quantity; // Calculate totalValue
    }


    // Constructor without suppliersId
    public Supply(String supplyCode, String name, String unit, double price, double quantity, String supplierName) {
        this(supplyCode, name, unit, price, quantity, null, supplierName); // suppliersId set to null
    }




    // Getters
    public String getSupplyCode() {
        return supplyCode;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getSuppliersId() {
        return suppliersId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public double getTotalValue() {
        return totalValue;
    }

    // Setters
    public void setSupplyCode(String supplyCode) {
        this.supplyCode = supplyCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(double price) {
        this.price = price;
        this.totalValue = price * this.quantity; // Update totalValue when price changes
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
        this.totalValue = this.price * quantity; // Update totalValue when quantity changes
    }

    public void setSuppliersId(String suppliersId) {
        this.suppliersId = suppliersId;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return "Supply{" +
                "supplyCode='" + supplyCode + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", suppliersId='" + suppliersId + '\'' +
                ", supplierName='" + supplierName + '\'' +
                ", totalValue=" + totalValue +
                '}';
    }
}
