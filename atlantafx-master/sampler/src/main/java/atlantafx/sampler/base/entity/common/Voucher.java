package atlantafx.sampler.base.entity.common;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Voucher {
  private int id;
  private String voucherCode;
  private String voucherName;
  private int voucherPercentage;
  private LocalDate startDate;
  private LocalDate endDate;
  private String status;

  public Voucher(int id, String voucherCode, String voucherName, int voucherPercentage, LocalDate startDate, LocalDate endDate, String status) {
    this.id = id;
    this.voucherCode = voucherCode;
    this.voucherName = voucherName;
    this.voucherPercentage = voucherPercentage;
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = status;
  }

  public Voucher() {
  }

  public Voucher(String code, String name, int percentage, LocalDate startDate, LocalDate endDate, String status) {
    this.voucherCode = code;
    this.voucherName = name;
    this.voucherPercentage = percentage;
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = status;
  }

  public Voucher(String code, String name, double v, LocalDate startDate, LocalDate endDate, String status) {
    this.voucherCode = code;
    this.voucherName = name;
    this.voucherPercentage = (int) (v * 100);
    this.startDate = startDate;
    this.endDate = endDate;
    this.status = status;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getVoucherCode() {
    return voucherCode;
  }

  public void setVoucherCode(String voucherCode) {
    this.voucherCode = voucherCode;
  }

  public String getVoucherName() {
    return voucherName;
  }

  public void setVoucherName(String voucherName) {
    this.voucherName = voucherName;
  }

  public int getVoucherPercentage() {
    return voucherPercentage;
  }

  public void setVoucherPercentage(int voucherPercentage) {
    this.voucherPercentage = voucherPercentage;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "Voucher{" +
            "id=" + id +
            ", voucherCode='" + voucherCode + '\'' +
            ", voucherName='" + voucherName + '\'' +
            ", voucherPercentage=" + voucherPercentage +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", status='" + status + '\'' +
            '}';
  }
}