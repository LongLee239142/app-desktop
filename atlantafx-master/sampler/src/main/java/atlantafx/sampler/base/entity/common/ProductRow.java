package atlantafx.sampler.base.entity.common;

public class ProductRow {
    private final String name;
    private final double price;
    private final int quantity;
    private final double total;
    private final String discountName;
    private final double discountValue;

    public ProductRow(String name, double price, int quantity, double total, String discountName, double discountValue) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
        this.discountName = discountName;
        this.discountValue = discountValue;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }

    public String getDiscountName() {
        return discountName;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    @Override
    public String toString() {
        return "ProductRow{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", total=" + total +
                ", discountName='" + discountName + '\'' +
                ", discountValue=" + discountValue +
                '}';
    }
}