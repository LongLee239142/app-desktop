package atlantafx.sampler.base.entity.staff;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Role {
    private final IntegerProperty id;
    private final StringProperty roleName;
    private final DoubleProperty basicSalary;
    private final DoubleProperty allowance;

    public Role(int id, String roleName, double basicSalary, double allowance) {
        this.id = new SimpleIntegerProperty(id);
        this.roleName = new SimpleStringProperty(roleName);
        this.basicSalary = new SimpleDoubleProperty(basicSalary);
        this.allowance = new SimpleDoubleProperty(allowance);
    }

    public int getId() {
        return id.get();
    }

    public String getRoleName() {
        return roleName.get();
    }

    public double getBasicSalary() {
        return basicSalary.get();
    }

    public double getAllowance() {
        return allowance.get();
    }

    // Optionally, you can provide property getters if you want to bind to the TableView directly
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    public DoubleProperty basicSalaryProperty() {
        return basicSalary;
    }

    public DoubleProperty allowanceProperty() {
        return allowance;
    }
}
