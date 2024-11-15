package atlantafx.sampler.admin.entity;

public class Supplier {
    private String suppliersId;
    private String name;
    private String contactNumber;
    private String email;

    public Supplier() {
    }

    public Supplier(String suppliersId, String name, String contactNumber, String email) {
        this.suppliersId = suppliersId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    public String getSuppliersId() {
        return suppliersId;
    }

    public void setSuppliersId(String suppliersId) {
        this.suppliersId = suppliersId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
