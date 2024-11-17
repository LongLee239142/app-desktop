package atlantafx.sampler.base.entity.common;

public class BillDetail {
    private int id;
    private int billId;
    private Integer productId;
    private int quantity;
    private double price;
    private int voucherId;
    private String productName;
    public BillDetail( int billId, Integer productId, int quantity, double price, int voucherId) {

        this.billId = billId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.voucherId = voucherId;
    }

    public BillDetail() {
    }

    public BillDetail(int id, int billId, String productName, int quantity, double price) {
        this.id = id;
        this.billId = billId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
}