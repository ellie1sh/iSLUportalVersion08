/**
 * Data class to hold attendance summary information for a subject
 */
public class AttendanceSummary {
    private String subjectName;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    
    public AttendanceSummary(String subjectName) {
        this.subjectName = subjectName;
        this.presentCount = 0;
        this.absentCount = 0;
        this.lateCount = 0;
    }
    
    // Getters
    public String getSubjectName() { return subjectName; }
    public int getPresentCount() { return presentCount; }
    public int getAbsentCount() { return absentCount; }
    public int getLateCount() { return lateCount; }
    
    // Increment methods
    public void incrementPresent() { presentCount++; }
    public void incrementAbsent() { absentCount++; }
    public void incrementLate() { lateCount++; }
    
    // Calculated properties
    public int getTotalSessions() {
        return presentCount + absentCount + lateCount;
    }
    
    public double getAttendancePercentage() {
        int total = getTotalSessions();
        if (total == 0) return 100.0;
        return ((double) (presentCount + lateCount) / total) * 100.0;
    }
    
    public String getFormattedPercentage() {
        return String.format("%.2f%%", getAttendancePercentage());
    }
    
    /**
     * Converts to table row format for display
     */
    public Object[] toTableRow() {
        return new Object[]{
            subjectName,
            String.valueOf(presentCount),
            String.valueOf(absentCount),
            String.valueOf(lateCount),
            getFormattedPercentage()
        };
    }
}