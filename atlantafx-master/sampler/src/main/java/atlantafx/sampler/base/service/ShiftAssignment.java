package atlantafx.sampler.base.service;

import java.time.LocalDate;

public class ShiftAssignment {
    private String staffId;
    private int shiftId; // 1: Morning, 2: Afternoon, 3: Administrative, 4: Off
    private LocalDate assignedDate;

    public ShiftAssignment(String staffId, int shiftId, LocalDate assignedDate) {
        this.staffId = staffId;
        this.shiftId = shiftId;
        this.assignedDate = assignedDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public int getShiftId() {
        return shiftId;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public ShiftType getShiftType() {
        switch (shiftId) {
            case 1:
                return ShiftType.MORNING;
            case 2:
                return ShiftType.AFTERNOON;
            case 3:
                return ShiftType.ADMINISTRATIVE;
            case 4:
                return ShiftType.OFF;
            default:
                throw new IllegalArgumentException("Unknown shift ID: " + shiftId);
        }
    }

    public enum ShiftType {
        MORNING,
        AFTERNOON,
        ADMINISTRATIVE,
        OFF
    }
}
