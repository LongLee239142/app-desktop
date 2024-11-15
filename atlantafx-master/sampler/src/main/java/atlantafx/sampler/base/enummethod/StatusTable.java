package atlantafx.sampler.base.enummethod;

public enum StatusTable {
  AVAILABLE("Có Chỗ"),
  CLEANING("Đang dọn"),
  OCCUPIED("Hết Chỗ");
  private final String status;

  StatusTable(String status) {
    this.status = status;
  }

  public String getRoleValueStatus() {
    return status;
  }

  // Phương thức để lấy enum từ tên hiển thị
  public static StatusTable fromDisplayName(String displayName) {
    for (StatusTable status : StatusTable.values()) {
      if (status.status.equals(displayName)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Không tìm thấy vai trò với tên hiển thị: " + displayName);
  }

}


