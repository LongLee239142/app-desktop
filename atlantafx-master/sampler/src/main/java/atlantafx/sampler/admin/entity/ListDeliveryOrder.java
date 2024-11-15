package atlantafx.sampler.admin.entity;

import java.time.LocalDateTime;

public class ListDeliveryOrder {
    private String orderNumber;
    private String supplierId;
    private LocalDateTime orderDate; // Use LocalDateTime for date and time
    private String status;

    // Constructor that takes String for orderDate
    public ListDeliveryOrder(String orderNumber, String supplierId, String orderDate, String status) {
        this.orderNumber = orderNumber;
        this.supplierId = supplierId;
        this.orderDate = LocalDateTime.parse(orderDate); // Parse String to LocalDateTime
        this.status = status;
    }

    // Constructor that takes LocalDateTime for orderDate
    public ListDeliveryOrder(String orderNumber, String supplierId, LocalDateTime orderDate, String status) {
        this.orderNumber = orderNumber;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters and Setters
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
