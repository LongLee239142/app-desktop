package atlantafx.sampler.admin.entity;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ImportOrderDetail {
    private final IntegerProperty id;                  // Corresponds to `id`
    private final IntegerProperty importOrderId;      // Corresponds to `import_order_id`
    private final IntegerProperty supplyId;            // Corresponds to `supply_id`
    private final DoubleProperty orderedQuantity;      // Corresponds to `ordered_quantity`
    private final DoubleProperty receivedQuantity;      // Corresponds to `received_quantity`

    public ImportOrderDetail(int id, int importOrderId, int supplyId, double orderedQuantity, double receivedQuantity) {
        this.id = new SimpleIntegerProperty(id);
        this.importOrderId = new SimpleIntegerProperty(importOrderId);
        this.supplyId = new SimpleIntegerProperty(supplyId);
        this.orderedQuantity = new SimpleDoubleProperty(orderedQuantity);
        this.receivedQuantity = new SimpleDoubleProperty(receivedQuantity);
    }

    // Getters and Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getImportOrderId() {
        return importOrderId.get();
    }

    public void setImportOrderId(int importOrderId) {
        this.importOrderId.set(importOrderId);
    }

    public int getSupplyId() {
        return supplyId.get();
    }

    public void setSupplyId(int supplyId) {
        this.supplyId.set(supplyId);
    }

    public double getOrderedQuantity() {
        return orderedQuantity.get();
    }

    public void setOrderedQuantity(double orderedQuantity) {
        this.orderedQuantity.set(orderedQuantity);
    }

    public double getReceivedQuantity() {
        return receivedQuantity.get();
    }

    public void setReceivedQuantity(double receivedQuantity) {
        this.receivedQuantity.set(receivedQuantity);
    }
}
