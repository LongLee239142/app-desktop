package atlantafx.sampler.base.entity.common;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class BillOrder {
    private int id;
    private int tableId;
    private double totalAmount;
    private int paymentMethodId;
    private Timestamp createdAt;
    private List<BillDetail> billDetails;

    public BillOrder(int id, int tableId, double totalAmount, int paymentMethodId, Timestamp createdAt, List<BillDetail> billDetails) {
        this.id = id;
        this.tableId = tableId;
        this.totalAmount = totalAmount;
        this.paymentMethodId = paymentMethodId;
        this.createdAt = createdAt;
        this.billDetails = billDetails;
    }
    public BillOrder(int id, int tableId, double totalAmount, int paymentMethodId, Timestamp createdAt) {
        this.id = id;
        this.tableId = tableId;
        this.totalAmount = totalAmount;
        this.paymentMethodId = paymentMethodId;
        this.createdAt = createdAt;
    }

    public BillOrder(int tableId, double totalAmount, int paymentMethodId) {
        this.tableId = tableId;
        this.totalAmount = totalAmount;
        this.paymentMethodId = paymentMethodId;
    }

    public BillOrder() {
    }

    public BillOrder(int id, int tableId, double totalAmount, int paymentMethodId, LocalDateTime createdAt) {
        this.id = id;
        this.tableId = tableId;
        this.totalAmount = totalAmount;
        this.paymentMethodId = paymentMethodId;
        this.createdAt = Timestamp.valueOf(createdAt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<BillDetail> getBillDetails() {
        return billDetails;
    }

    public void setBillDetails(List<BillDetail> billDetails) {
        this.billDetails = billDetails;
    }
}