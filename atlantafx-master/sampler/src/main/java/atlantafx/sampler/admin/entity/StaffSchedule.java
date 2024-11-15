package atlantafx.sampler.admin.entity;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.util.HashMap;
import java.util.Map;

public class StaffSchedule {
    private final StringProperty name;
    private final String staffId;
    private final Map<String, SimpleIntegerProperty> shifts;

    public StaffSchedule(String staffId, String name) {
        this.staffId = staffId;
        this.name = new SimpleStringProperty(name);
        shifts = new HashMap<>();
        // Khởi tạo các giá trị ca cho mỗi ngày trong tuần
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : daysOfWeek) {
            shifts.put(day, new SimpleIntegerProperty(0)); // Mặc định là không có ca
        }
    }

    public String getStaffId() {
        return staffId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setShift(String day, Integer shiftId) {
        if (shifts.containsKey(day)) {
            shifts.get(day).set(shiftId);
        }
    }

    public Integer getShift(String day) {
        return shifts.get(day).get();
    }

    public ObservableValue<Integer> getShiftProperty(String day) {
        // Trả về giá trị ObservableValue<Integer>
        return shifts.get(day).asObject(); // Sử dụng asObject() để chuyển đổi
    }

    public String[] getDays() {
        return shifts.keySet().toArray(new String[0]);
    }
}