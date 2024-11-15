package atlantafx.sampler.base.entity.common;

import java.time.LocalDate;

public class Discount {

    private int id; // Corresponds to the 'id' column
    private String productName; // Instead of 'productId', use 'productName' for user-friendly reference
    private String discountName; // Corresponds to the 'discount_name' column
    private double discountPercentage; // Corresponds to the 'discount_percentage' column
    private LocalDate startDate; // Corresponds to the 'start_date' column
    private LocalDate endDate; // Corresponds to the 'end_date' column

    // Constructor
    public Discount(String productName, String discountName, double discountPercentage,
                    LocalDate startDate, LocalDate endDate) {
        this.productName = productName;
        this.discountName = discountName;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Validation method to check if the discount is currently valid
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return (today.isEqual(startDate) || today.isAfter(startDate)) &&
                (today.isEqual(endDate) || today.isBefore(endDate));
    }
}
