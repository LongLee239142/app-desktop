package atlantafx.sampler.admin.entity;

import java.math.BigDecimal;

public class DeliveryOrderItem {
    private String supplyCode;
    private BigDecimal quantity;
    private BigDecimal price; // Thêm trường giá
    private BigDecimal totalValue; // Thêm trường tổng giá trị

    public DeliveryOrderItem(String supplyCode, BigDecimal quantity, BigDecimal price) {
        this.supplyCode = supplyCode;
        this.quantity = quantity;
        this.price = price;
        this.totalValue = price.multiply(quantity); // Tính toán tổng giá trị
    }

    public String getSupplyCode() {
        return supplyCode;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }
}
