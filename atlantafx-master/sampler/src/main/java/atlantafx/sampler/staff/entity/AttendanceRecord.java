package atlantafx.sampler.staff.entity;

import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;

public class AttendanceRecord {
    private final SimpleStringProperty date;
    private final SimpleStringProperty checkIn;
    private final SimpleStringProperty checkOut;
    private final SimpleStringProperty status; // New property for attendance status

    // Constructor for basic attendance record
    public AttendanceRecord(String date, String checkIn, String checkOut) {
        this.date = new SimpleStringProperty(date);
        this.checkIn = new SimpleStringProperty(checkIn);
        this.checkOut = new SimpleStringProperty(checkOut);
        this.status = new SimpleStringProperty(""); // Default status
    }

    // Constructor for attendance record with LocalDateTime parameters
    public AttendanceRecord(int id, String staffId, LocalDateTime checkIn, LocalDateTime checkOut, String status) {
        this.date = new SimpleStringProperty(checkIn.toLocalDate().toString()); // Use checkIn date
        this.checkIn = new SimpleStringProperty(checkIn.toString());
        this.checkOut = new SimpleStringProperty(checkOut.toString());
        this.status = new SimpleStringProperty(status); // Set the status
    }

    // Constructor for attendance record with SimpleStringProperty parameters
    public AttendanceRecord(SimpleStringProperty date, SimpleStringProperty checkIn, SimpleStringProperty checkOut) {
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = new SimpleStringProperty(""); // Default status
    }

    // New constructor to include status
    public AttendanceRecord(String date, String status) { // Constructor for SalaryReportPage
        this.date = new SimpleStringProperty(date);
        this.checkIn = new SimpleStringProperty(""); // Default value
        this.checkOut = new SimpleStringProperty(""); // Default value
        this.status = new SimpleStringProperty(status);
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public SimpleStringProperty checkInProperty() {
        return checkIn;
    }

    public SimpleStringProperty checkOutProperty() {
        return checkOut;
    }

    public SimpleStringProperty statusProperty() { // New method for status property
        return status;
    }

    public String getStatus() { // Getter for status
        return status.get();
    }

    public void setStatus(String status) { // Setter for status
        this.status.set(status);
    }
}
