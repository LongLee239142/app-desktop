package atlantafx.sampler.base.enummethod;
public enum Payment {
  CASH ("Tiền Mặt"),   // tiền mặt
  QR("Quét Mã"),     // quét mã
  CARD("Cà thẻ"),
  ;   // thẻ
  private final String status;

  Payment(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }
  // Phương thức để lấy enum từ tên hiển thị
  public static Payment fromDisplayName(String displayName) {
    for (Payment status : Payment.values()) {
      if (status.status.equals(displayName)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Không tìm thấy vai trò với tên hiển thị: " + displayName);
  }
  public static void main(String[] args) {
    Payment method = null;
    System.out.println(method.CASH.getStatus());
  }
}
