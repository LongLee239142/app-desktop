package atlantafx.sampler.base.entity.common;


import atlantafx.sampler.base.enummethod.StatusTable;

public class TableStatus {
  private int tableId;
  private StatusTable status;

  public TableStatus(int tableId, StatusTable satatus) {
    this.tableId = tableId;
    this.status = satatus;
  }

  public StatusTable getSatatus() {
    return status;
  }

  public void setSatatus(StatusTable satatus) {
    this.status = satatus;
  }

  public int getTableId() {
    return tableId;
  }

  public void setTableId(int tableId) {
    this.tableId = tableId;
  }
}
