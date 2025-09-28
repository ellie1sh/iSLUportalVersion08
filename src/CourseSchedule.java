import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Data class to hold course schedule information
 */
public class CourseSchedule {
    private String studentID;
    private String classCode;
    private String courseNumber;
    private String courseDescription;
    private int units;
    private LocalTime startTime;
    private LocalTime endTime;
    private String days;
    private String room;
    private String instructor;
    private String semester;
    
    public CourseSchedule(String studentID, String classCode, String courseNumber, 
                         String courseDescription, int units, LocalTime startTime, 
                         LocalTime endTime, String days, String room, String instructor, String semester) {
        this.studentID = studentID;
        this.classCode = classCode;
        this.courseNumber = courseNumber;
        this.courseDescription = courseDescription;
        this.units = units;
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = days;
        this.room = room;
        this.instructor = instructor;
        this.semester = semester;
    }
    
    // Getters
    public String getStudentID() { return studentID; }
    public String getClassCode() { return classCode; }
    public String getCourseNumber() { return courseNumber; }
    public String getCourseDescription() { return courseDescription; }
    public int getUnits() { return units; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getDays() { return days; }
    public String getRoom() { return room; }
    public String getInstructor() { return instructor; }
    public String getSemester() { return semester; }
    
    /**
     * Checks if this course occurs on a specific day
     */
    public boolean occursOn(String day) {
        Set<String> daySet = new HashSet<>(Arrays.asList(days.split("")));
        return daySet.contains(day);
    }
    
    /**
     * Gets the formatted time range
     */
    public String getTimeRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return startTime.format(formatter) + " - " + endTime.format(formatter);
    }
    
    /**
     * Converts the course schedule to CSV format for file storage
     */
    public String toCsvFormat() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return studentID + "," + classCode + "," + courseNumber + "," + 
               courseDescription + "," + units + "," + 
               startTime.format(timeFormatter) + "," + endTime.format(timeFormatter) + "," +
               days + "," + room + "," + instructor + "," + semester;
    }
    
    /**
     * Creates a CourseSchedule from CSV format
     */
    public static CourseSchedule fromCsvFormat(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length >= 11) {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startTime = LocalTime.parse(parts[5], timeFormatter);
            LocalTime endTime = LocalTime.parse(parts[6], timeFormatter);
            
            return new CourseSchedule(
                parts[0], // studentID
                parts[1], // classCode
                parts[2], // courseNumber
                parts[3], // courseDescription
                Integer.parseInt(parts[4]), // units
                startTime,
                endTime,
                parts[7], // days
                parts[8], // room
                parts[9], // instructor
                parts[10] // semester
            );
        }
        return null;
    }
    
    /**
     * Converts to table row format for display
     */
    public Object[] toTableRow() {
        return new Object[]{
            classCode,
            courseNumber,
            courseDescription,
            String.valueOf(units),
            getTimeRange(),
            days,
            room,
            instructor
        };
    }
}