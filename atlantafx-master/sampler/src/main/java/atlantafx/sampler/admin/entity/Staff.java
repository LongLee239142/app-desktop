package atlantafx.sampler.admin.entity;

public class Staff {
    private String staffId;
    private String name;
    private String contactNumber;
    private String email;
    private String gender;
    private String role;   // Role of the staff
    private String status; // Status of the staff (Active, Dropout, Fire)

    public Staff(String staffId, String name, String contactNumber, String email, String gender, String role, String status) {
        this.staffId = staffId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
        this.gender = gender;
        this.role = role;
        this.status = status;
    }

    // Getters
    public String getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    // Setters for updating status if needed
    public void setStatus(String status) {
        this.status = status;
    }
}
