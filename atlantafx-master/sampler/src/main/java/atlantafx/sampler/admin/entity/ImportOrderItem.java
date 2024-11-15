package atlantafx.sampler.admin.entity;

import java.math.BigDecimal;

public class ImportOrderItem {
    private int supplyId;
    private BigDecimal orderedQuantity;
    private BigDecimal quantity;
    private BigDecimal price; // Add price attribute

    public ImportOrderItem(int supplyId, BigDecimal orderedQuantity, BigDecimal quantity, BigDecimal price) {
        this.supplyId = supplyId;
        this.orderedQuantity = orderedQuantity;
        this.quantity = quantity;
        this.price = price; // Initialize price
    }

    public int getSupplyId() {
        return supplyId;
    }

    public BigDecimal getOrderedQuantity() {
        return orderedQuantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() { // Add a getter for price
        return price;
    }
}
