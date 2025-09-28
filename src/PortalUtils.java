import java.util.LinkedList;
import java.util.List;

/**
 * Utility class that demonstrates integration of all classes in the Student Portal system
 * This class shows how MyDoublyLinkedList, MenuItem, DataManager, and other classes work together
 */
public class PortalUtils {
    
    /**
     * Creates a comprehensive menu system using MyDoublyLinkedList and MenuItem
     * @return A doubly linked list containing all menu items
     */
    public static MyDoublyLinkedList<MenuItem> createIntegratedMenuSystem() {
        MyDoublyLinkedList<MenuItem> menu = new MyDoublyLinkedList<>();
        
        // Create sub-items for each menu category
        MySinglyLinkedList<String> homeSubList = createHomeSublist();
        MySinglyLinkedList<String> attendanceSubList = createAttendanceSubList();
        MySinglyLinkedList<String> scheduleSubList = createScheduleSubList();
        MySinglyLinkedList<String> gradesSubList = createGradeSubList();
        MySinglyLinkedList<String> soaSubList = createSOASubList();
        MySinglyLinkedList<String> torSubList = createTORSubList();
        MySinglyLinkedList<String> personalDetailsSubList = createPersonalDetailsSubList();
        MySinglyLinkedList<String> curriculumChecklistSubList = createCurriculumChecklistSubList();
        MySinglyLinkedList<String> medicalSubList = createMedicalSubList();
        MySinglyLinkedList<String> journalSubList = createJournalSubList();
        MySinglyLinkedList<String> downloadableSubList = createDownloadableSubList();

        
        // Add menu items to the doubly linked list
        menu.add(new MenuItem("🏠 Home", homeSubList));
        menu.add(new MenuItem("📌 Attendance", attendanceSubList));
        menu.add(new MenuItem("📅 Schedule", scheduleSubList));
        menu.add(new MenuItem("🧮 Statement of Accounts", soaSubList));
        menu.add(new MenuItem("📊 Grades", gradesSubList));
        menu.add(new MenuItem("📋 Transcript of Records", torSubList));
        menu.add(new MenuItem("✅ Curriculum Checklist", curriculumChecklistSubList));
        menu.add(new MenuItem("🏥 Medical Record", medicalSubList));
        menu.add(new MenuItem("📚 Journal/Periodical", journalSubList));
        menu.add(new MenuItem("👤 Personal Details", personalDetailsSubList));
        menu.add(new MenuItem("ℹ️ Downloadable/ About iSLU", downloadableSubList));
        
        return menu;
    }
    
    /**
     * Demonstrates data integration by creating a student management system
     * @return A doubly linked list of student information
     */
    public static MyDoublyLinkedList<StudentInfo> createStudentManagementSystem() {
        MyDoublyLinkedList<StudentInfo> students = new MyDoublyLinkedList<>();
        
        // Get all students from DataManager
        List<StudentInfo> allStudents = DataManager.getAllStudents();
        
        // Add students to the doubly linked list
        for (StudentInfo student : allStudents) {
            students.add(student);
        }
        
        return students;
    }
    
    /**
     * Demonstrates menu navigation using the doubly linked list
     * @param menu The menu system
     * @param currentIndex Current menu index
     * @param direction Direction to navigate (1 for next, -1 for previous)
     * @return The menu item at the new position
     */
    public static MenuItem navigateMenu(MyDoublyLinkedList<MenuItem> menu, int currentIndex, int direction) {
        int newIndex = currentIndex + direction;
        
        if (newIndex < 0) {
            newIndex = menu.getSize() - 1; // Wrap to last item
        } else if (newIndex >= menu.getSize()) {
            newIndex = 0; // Wrap to first item
        }
        
        return menu.get(newIndex);
    }
    
    /**
     * Validates student data using integrated systems
     * @param studentID The student ID to validate
     * @param password The password to validate
     * @return true if valid, false otherwise
     */
    public static boolean validateStudentCredentials(String studentID, String password) {
        // Use DataManager for authentication
        boolean isValid = DataManager.authenticateUser(studentID, password);
        
        if (isValid) {
            // Get student info for additional validation
            StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
            if (studentInfo != null) {
                System.out.println("Welcome, " + studentInfo.getFullName() + "!");
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Creates a comprehensive student portal session
     * @param studentID The student ID
     * @return A portal session object containing all integrated data
     */
    public static PortalSession createPortalSession(String studentID) {
        StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
        MyDoublyLinkedList<MenuItem> menu = createIntegratedMenuSystem();
        List<PaymentTransaction> transactions = DataManager.loadPaymentTransactions(studentID);
        
        return new PortalSession(studentInfo, menu, transactions);
    }
    
    // Helper methods for creating sub-lists
    static MySinglyLinkedList<String> createHomeSublist() {
        MySinglyLinkedList<String> homeSubList = new MySinglyLinkedList<>();
        homeSubList.add("📰 Events, News & Announcements");
        homeSubList.add("📌 Student Status");
        return homeSubList;
    }
    
    private static MySinglyLinkedList<String> createAttendanceSubList() {
        MySinglyLinkedList<String> attendanceSubList = new MySinglyLinkedList<>();
        attendanceSubList.add("Attendance Record");
        return attendanceSubList;
    }
    
    private static MySinglyLinkedList<String> createScheduleSubList() {
        MySinglyLinkedList<String> scheduleSubList = new MySinglyLinkedList<>();
        scheduleSubList.add("Class Schedule");
        return scheduleSubList;
    }

    private static MySinglyLinkedList<String> createGradeSubList() {
        MySinglyLinkedList<String> gradesSublist = new MySinglyLinkedList<>();
        gradesSublist.add("Grades");
        return gradesSublist;
    }
    
    private static MySinglyLinkedList<String> createSOASubList() {
        MySinglyLinkedList<String> sOASubList = new MySinglyLinkedList<>();
        sOASubList.add("Statement of Accounts (SHORT TERM, 2025)");
        sOASubList.add("Online Payment Channels");
        return sOASubList;
    }
    
    private static MySinglyLinkedList<String> createTORSubList() {
        MySinglyLinkedList<String> TORSubList = new MySinglyLinkedList<>();
        TORSubList.add("📋 Transcript of Records");
        return TORSubList;
    }
    
    private static MySinglyLinkedList<String> createPersonalDetailsSubList() {
        MySinglyLinkedList<String> personalDetailsSubList = new MySinglyLinkedList<>();
        personalDetailsSubList.add("User Profile");
        return personalDetailsSubList;
    }

    private static MySinglyLinkedList<String> createCurriculumChecklistSubList(){
        MySinglyLinkedList<String> CurriculumChecklistSubList = new MySinglyLinkedList<>();
        CurriculumChecklistSubList.add("✅ BACHELOR OF SCIENCE IN INFORMATION TECHNOLOGY FIRST SEMESTER, 2018-2019");
        return CurriculumChecklistSubList;
    }

    private static MySinglyLinkedList<String> createMedicalSubList(){
        MySinglyLinkedList<String> medicalSubList = new MySinglyLinkedList<>();
        return medicalSubList;
    }



    private static MySinglyLinkedList<String> createJournalSubList(){
        MySinglyLinkedList<String> journalSubList = new MySinglyLinkedList<>();
        journalSubList.add("WHAT ARE JOURNAL INDEXES?");
        journalSubList.add("THE SLU LIBRARIES' PERIODICAL ARTICLE INDEXES");
        journalSubList.add("STEPS IN ACCESSING THE PERIODICAL ARTICLE INDEXES");
        return journalSubList;
    }

    private static MySinglyLinkedList<String> createDownloadableSubList(){
        MySinglyLinkedList<String> downloadableSubList = new MySinglyLinkedList<>();
        downloadableSubList.add("Downloadables");
        downloadableSubList.add("About iSLU");
        return downloadableSubList;
    }
}

