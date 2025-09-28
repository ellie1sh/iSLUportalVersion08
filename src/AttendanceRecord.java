import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Data class to hold attendance record information
 */
public class AttendanceRecord {
    private String studentID;
    private String subjectCode;
    private String subjectName;
    private LocalDate date;
    private String status; // "Present", "Absent", "Late"
    private String remarks;
    
    public AttendanceRecord(String studentID, String subjectCode, String subjectName, LocalDate date, String status, String remarks) {
        this.studentID = studentID;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.date = date;
        this.status = status;
        this.remarks = remarks;
    }
    
    // Getters
    public String getStudentID() { return studentID; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public LocalDate getDate() { return date; }
    public String getStatus() { return status; }
    public String getRemarks() { return remarks; }
    
    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    /**
     * Converts the attendance record to CSV format for file storage
     */
    public String toCsvFormat() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return studentID + "," + subjectCode + "," + subjectName + "," + 
               date.format(formatter) + "," + status + "," + (remarks != null ? remarks : "");
    }
    
    /**
     * Creates an AttendanceRecord from CSV format
     */
    public static AttendanceRecord fromCsvFormat(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 5) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate date = LocalDate.parse(parts[3], formatter);
            String remarks = parts.length > 5 ? parts[5] : "";
            return new AttendanceRecord(parts[0], parts[1], parts[2], date, parts[4], remarks);
        }
        return null;
    }
}