import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Data class to store medical record information for students
 */
public class MedicalRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String allergies;
    private String pastMedicalHistory;
    private String childhoodDiseases;
    private String gynecologicHistory;
    private String personalSocialHistory;
    private String immunization;
    private String others;
    private String consultationRecord;
    
    // Emergency contact information
    private String emergencyContactName;
    private String emergencyContactAddress;
    private String emergencyContactNumber;
    
    // Constructor
    public MedicalRecord(String studentId) {
        this.studentId = studentId;
        this.allergies = "";
        this.pastMedicalHistory = "";
        this.childhoodDiseases = "";
        this.gynecologicHistory = "";
        this.personalSocialHistory = "";
        this.immunization = "";
        this.others = "";
        this.consultationRecord = "";
        this.emergencyContactName = "";
        this.emergencyContactAddress = "";
        this.emergencyContactNumber = "";
    }
    
    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    
    public String getPastMedicalHistory() { return pastMedicalHistory; }
    public void setPastMedicalHistory(String pastMedicalHistory) { this.pastMedicalHistory = pastMedicalHistory; }
    
    public String getChildhoodDiseases() { return childhoodDiseases; }
    public void setChildhoodDiseases(String childhoodDiseases) { this.childhoodDiseases = childhoodDiseases; }
    
    public String getGynecologicHistory() { return gynecologicHistory; }
    public void setGynecologicHistory(String gynecologicHistory) { this.gynecologicHistory = gynecologicHistory; }
    
    public String getPersonalSocialHistory() { return personalSocialHistory; }
    public void setPersonalSocialHistory(String personalSocialHistory) { this.personalSocialHistory = personalSocialHistory; }
    
    public String getImmunization() { return immunization; }
    public void setImmunization(String immunization) { this.immunization = immunization; }
    
    public String getOthers() { return others; }
    public void setOthers(String others) { this.others = others; }
    
    public String getConsultationRecord() { return consultationRecord; }
    public void setConsultationRecord(String consultationRecord) { this.consultationRecord = consultationRecord; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactAddress() { return emergencyContactAddress; }
    public void setEmergencyContactAddress(String emergencyContactAddress) { this.emergencyContactAddress = emergencyContactAddress; }
    
    public String getEmergencyContactNumber() { return emergencyContactNumber; }
    public void setEmergencyContactNumber(String emergencyContactNumber) { this.emergencyContactNumber = emergencyContactNumber; }
    
    /**
     * Convert to database format for storage
     */
    public String toDatabaseFormat() {
        return studentId + "|" +
               allergies + "|" +
               pastMedicalHistory + "|" +
               childhoodDiseases + "|" +
               gynecologicHistory + "|" +
               personalSocialHistory + "|" +
               immunization + "|" +
               others + "|" +
               consultationRecord + "|" +
               emergencyContactName + "|" +
               emergencyContactAddress + "|" +
               emergencyContactNumber;
    }
    
    /**
     * Create MedicalRecord from database format string
     */
    public static MedicalRecord fromDatabaseFormat(String data) {
        String[] parts = data.split("\\|", -1);
        if (parts.length < 12) {
            return null;
        }
        
        MedicalRecord record = new MedicalRecord(parts[0]);
        record.setAllergies(parts[1]);
        record.setPastMedicalHistory(parts[2]);
        record.setChildhoodDiseases(parts[3]);
        record.setGynecologicHistory(parts[4]);
        record.setPersonalSocialHistory(parts[5]);
        record.setImmunization(parts[6]);
        record.setOthers(parts[7]);
        record.setConsultationRecord(parts[8]);
        record.setEmergencyContactName(parts[9]);
        record.setEmergencyContactAddress(parts[10]);
        record.setEmergencyContactNumber(parts[11]);
        
        return record;
    }
}