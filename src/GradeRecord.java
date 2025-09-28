/**
 * Data class to hold grade record information
 */
public class GradeRecord {
    private String studentID;
    private String subjectCode;
    private String subjectName;
    private Double prelimGrade;
    private Double midtermGrade;
    private Double tentativeFinalGrade;
    private Double finalGrade;
    private String semester;
    private String status; // "Ongoing", "Completed"
    
    public GradeRecord(String studentID, String subjectCode, String subjectName, 
                      Double prelimGrade, Double midtermGrade, Double tentativeFinalGrade, 
                      Double finalGrade, String semester, String status) {
        this.studentID = studentID;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.prelimGrade = prelimGrade;
        this.midtermGrade = midtermGrade;
        this.tentativeFinalGrade = tentativeFinalGrade;
        this.finalGrade = finalGrade;
        this.semester = semester;
        this.status = status;
    }
    
    // Getters
    public String getStudentID() { return studentID; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public Double getPrelimGrade() { return prelimGrade; }
    public Double getMidtermGrade() { return midtermGrade; }
    public Double getTentativeFinalGrade() { return tentativeFinalGrade; }
    public Double getFinalGrade() { return finalGrade; }
    public String getSemester() { return semester; }
    public String getStatus() { return status; }
    
    // Setters
    public void setPrelimGrade(Double prelimGrade) { this.prelimGrade = prelimGrade; }
    public void setMidtermGrade(Double midtermGrade) { this.midtermGrade = midtermGrade; }
    public void setTentativeFinalGrade(Double tentativeFinalGrade) { this.tentativeFinalGrade = tentativeFinalGrade; }
    public void setFinalGrade(Double finalGrade) { this.finalGrade = finalGrade; }
    public void setStatus(String status) { this.status = status; }
    
    /**
     * Calculates the overall grade based on available grades
     */
    public Double calculateOverallGrade() {
        if (finalGrade != null) {
            return finalGrade;
        }
        
        int count = 0;
        double total = 0.0;
        
        if (prelimGrade != null) {
            total += prelimGrade;
            count++;
        }
        if (midtermGrade != null) {
            total += midtermGrade;
            count++;
        }
        if (tentativeFinalGrade != null) {
            total += tentativeFinalGrade;
            count++;
        }
        
        return count > 0 ? total / count : null;
    }
    
    /**
     * Formats a grade for display
     */
    private String formatGrade(Double grade) {
        return grade != null ? String.format("%.2f", grade) : "-";
    }
    
    /**
     * Converts the grade record to CSV format for file storage
     */
    public String toCsvFormat() {
        return studentID + "," + subjectCode + "," + subjectName + "," + 
               (prelimGrade != null ? prelimGrade : "") + "," +
               (midtermGrade != null ? midtermGrade : "") + "," +
               (tentativeFinalGrade != null ? tentativeFinalGrade : "") + "," +
               (finalGrade != null ? finalGrade : "") + "," +
               semester + "," + status;
    }
    
    /**
     * Creates a GradeRecord from CSV format
     */
    public static GradeRecord fromCsvFormat(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 9) {
            Double prelimGrade = parts[3].isEmpty() ? null : Double.parseDouble(parts[3]);
            Double midtermGrade = parts[4].isEmpty() ? null : Double.parseDouble(parts[4]);
            Double tentativeFinalGrade = parts[5].isEmpty() ? null : Double.parseDouble(parts[5]);
            Double finalGrade = parts[6].isEmpty() ? null : Double.parseDouble(parts[6]);
            
            return new GradeRecord(
                parts[0], // studentID
                parts[1], // subjectCode
                parts[2], // subjectName
                prelimGrade,
                midtermGrade,
                tentativeFinalGrade,
                finalGrade,
                parts[7], // semester
                parts[8]  // status
            );
        }
        return null;
    }
    
    /**
     * Converts to table row format for display
     */
    public Object[] toTableRow() {
        return new Object[]{
            subjectName,
            formatGrade(prelimGrade),
            formatGrade(midtermGrade),
            formatGrade(tentativeFinalGrade),
            formatGrade(finalGrade != null ? finalGrade : calculateOverallGrade())
        };
    }
}