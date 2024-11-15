package atlantafx.sampler.admin.entity;

public class OrderDetail {
    private String productName;
    private int quantity;
    private double price;
    private String supplyCode;

    public OrderDetail(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderDetail(String supplyCode, double quantity) {
        this.supplyCode = supplyCode;
        this.quantity = (int) quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSupplyCode() {
        return supplyCode;
    }

    public void setSupplyCode(String supplyCode) {
        this.supplyCode = supplyCode;
    }
}
