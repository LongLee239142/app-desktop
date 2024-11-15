package atlantafx.sampler.base.entity.common;

public class StoreInfo {
    private String storeName;
    private String storeEmail;
    private String phone;
    private String storeAddress;
    private String taxCode;

    public StoreInfo(String storeName, String storeEmail, String phone, String storeAddress, String taxCode) {
        this.storeName = storeName;
        this.storeEmail = storeEmail;
        this.phone = phone;
        this.storeAddress = storeAddress;
        this.taxCode = taxCode;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public String getPhone() {
        return phone;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public String getTaxCode() {
        return taxCode;
    }
}
