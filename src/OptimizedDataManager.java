import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * Optimized data management class for the Student Portal system
 * Features:
 * - HashMap-based caching for O(1) student lookups
 * - Efficient batch file operations
 * - Proper data validation and error handling
 * - Organized data loading with lazy initialization
 * - Thread-safe operations using ConcurrentHashMap
 */
public class OptimizedDataManager {
    
    // File paths
    private static final String DATABASE_FILE = "Database.txt";
    private static final String USER_PASSWORD_FILE = "UserPasswordID.txt";
    private static final String PAYMENT_LOGS_FILE = "paymentLogs.txt";
    private static final String ATTENDANCE_FILE = "attendanceRecords.txt";
    private static final String GRADES_FILE = "gradeRecords.txt";
    private static final String SCHEDULES_FILE = "courseSchedules.txt";
    
    // Cache for fast data retrieval - Thread-safe
    private static final Map<String, StudentInfo> studentCache = new ConcurrentHashMap<>();
    private static final Map<String, List<AttendanceRecord>> attendanceCache = new ConcurrentHashMap<>();
    private static final Map<String, List<GradeRecord>> gradeCache = new ConcurrentHashMap<>();
    private static final Map<String, List<PaymentTransaction>> paymentCache = new ConcurrentHashMap<>();
    
    // Flags to track cache initialization
    private static volatile boolean studentCacheInitialized = false;
    private static volatile boolean attendanceCacheInitialized = false;
    private static volatile boolean gradeCacheInitialized = false;
    private static volatile boolean paymentCacheInitialized = false;
    
    // Data validation patterns
    private static final String STUDENT_ID_PATTERN = "^225\\d{4}$";
    private static final String DATE_PATTERN = "^\\d{2}/\\d{2}/\\d{4}$";
    
    /**
     * Resolve a data file by searching from the working directory and then walking up
     * from the compiled classes location. This makes file access robust regardless
     * of where the application is launched from.
     */
    private static File resolveFile(String filename) {
        // 1) Try working directory
        File direct = new File(filename);
        if (direct.exists()) {
            return direct.getAbsoluteFile();
        }

        // 2) Try walking up from the code source (e.g., out/production/...)
        try {
            URL codeSourceUrl = OptimizedDataManager.class.getProtectionDomain().getCodeSource().getLocation();
            File location = new File(codeSourceUrl.toURI());
            File dir = location.isFile() ? location.getParentFile() : location;

            for (int i = 0; i < 8 && dir != null; i++) {
                File candidate = new File(dir, filename);
                if (candidate.exists()) {
                    return candidate.getAbsoluteFile();
                }
                dir = dir.getParentFile();
            }
        } catch (URISyntaxException ignored) {
        }

        // 3) Fallback to working directory path (even if it does not exist yet)
        return direct.getAbsoluteFile();
    }

    // File resolver methods
    private static File getDatabaseFile() { return resolveFile(DATABASE_FILE); }
    private static File getUserPasswordFile() { return resolveFile(USER_PASSWORD_FILE); }
    private static File getPaymentLogsFile() { return resolveFile(PAYMENT_LOGS_FILE); }
    private static File getAttendanceFile() { return resolveFile(ATTENDANCE_FILE); }
    private static File getGradesFile() { return resolveFile(GRADES_FILE); }
    private static File getSchedulesFile() { return resolveFile(SCHEDULES_FILE); }

    /**
     * Validates student ID format
     */
    private static boolean isValidStudentID(String studentID) {
        return studentID != null && studentID.matches(STUDENT_ID_PATTERN);
    }
    
    /**
     * Validates date format
     */
    private static boolean isValidDateFormat(String date) {
        return date != null && date.matches(DATE_PATTERN);
    }
    
    /**
     * Initialize student cache with all student data for fast O(1) lookups
     */
    private static synchronized void initializeStudentCache() {
        if (studentCacheInitialized) return;
        
        try {
            File databaseFile = getDatabaseFile();
            if (!databaseFile.exists()) {
                studentCacheInitialized = true;
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip header lines and empty lines
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:") || line.startsWith("ProfileData")) {
                        continue;
                    }
                    
                    // Handle lines with profile data (containing | separator)
                    String[] mainParts = line.split("\\|");
                    String basicInfo = mainParts[0]; // Everything before the |
                    
                    String[] parts = basicInfo.split(",");
                    if (parts.length >= 6) {
                        String studentID = parts[0].trim();
                        if (isValidStudentID(studentID)) {
                            StudentInfo student = new StudentInfo(
                                studentID,
                                parts[1].trim(), // Last Name
                                parts[2].trim(), // First Name
                                parts[3].trim(), // Middle Name
                                parts[4].trim(), // Date of Birth
                                parts[5].trim()  // Password
                            );
                            studentCache.put(studentID, student);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing student cache: " + e.getMessage());
        }
        
        studentCacheInitialized = true;
    }
    
    /**
     * Initialize attendance cache for fast attendance data retrieval
     */
    private static synchronized void initializeAttendanceCache() {
        if (attendanceCacheInitialized) return;
        
        try {
            File attendanceFile = getAttendanceFile();
            if (!attendanceFile.exists()) {
                attendanceCacheInitialized = true;
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(attendanceFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip header lines and empty lines
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:")) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String studentID = parts[0].trim();
                        if (isValidStudentID(studentID)) {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                LocalDate date = LocalDate.parse(parts[3].trim(), formatter);
                                String remarks = parts.length > 5 ? parts[5].trim() : "";
                                
                                AttendanceRecord record = new AttendanceRecord(
                                    studentID,
                                    parts[1].trim(), // Subject Code
                                    parts[2].trim(), // Subject Name
                                    date,
                                    parts[4].trim(), // Status
                                    remarks
                                );
                                
                                attendanceCache.computeIfAbsent(studentID, k -> new ArrayList<>()).add(record);
                            } catch (Exception e) {
                                System.err.println("Error parsing attendance record: " + line);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing attendance cache: " + e.getMessage());
        }
        
        attendanceCacheInitialized = true;
    }
    
    /**
     * Initialize grade cache for fast grade data retrieval
     */
    private static synchronized void initializeGradeCache() {
        if (gradeCacheInitialized) return;
        
        try {
            File gradesFile = getGradesFile();
            if (!gradesFile.exists()) {
                gradeCacheInitialized = true;
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(gradesFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip header lines and empty lines
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:") || line.startsWith("#")) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 9) {
                        String studentID = parts[0].trim();
                        if (isValidStudentID(studentID)) {
                            try {
                                Double prelimGrade = parts[3].isEmpty() ? null : Double.parseDouble(parts[3]);
                                Double midtermGrade = parts[4].isEmpty() ? null : Double.parseDouble(parts[4]);
                                Double tentativeFinalGrade = parts[5].isEmpty() ? null : Double.parseDouble(parts[5]);
                                Double finalGrade = parts[6].isEmpty() ? null : Double.parseDouble(parts[6]);
                                
                                GradeRecord record = new GradeRecord(
                                    studentID,
                                    parts[1].trim(), // Subject Code
                                    parts[2].trim(), // Subject Name
                                    prelimGrade,
                                    midtermGrade,
                                    tentativeFinalGrade,
                                    finalGrade,
                                    parts[7].trim(), // Semester
                                    parts[8].trim()  // Status
                                );
                                
                                gradeCache.computeIfAbsent(studentID, k -> new ArrayList<>()).add(record);
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing grade record: " + line);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing grade cache: " + e.getMessage());
        }
        
        gradeCacheInitialized = true;
    }
    
    /**
     * Initialize payment cache for fast payment data retrieval
     */
    private static synchronized void initializePaymentCache() {
        if (paymentCacheInitialized) return;
        
        try {
            File paymentFile = getPaymentLogsFile();
            if (!paymentFile.exists()) {
                paymentCacheInitialized = true;
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(paymentFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip header lines and empty lines
                    if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:") || line.startsWith("Description:")) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String studentID = parts[4].trim();
                        if (isValidStudentID(studentID)) {
                            PaymentTransaction transaction = new PaymentTransaction(
                                parts[0].trim(), // Date
                                parts[1].trim(), // Channel
                                parts[2].trim(), // Reference
                                parts[3].trim()  // Amount
                            );
                            
                            paymentCache.computeIfAbsent(studentID, k -> new ArrayList<>()).add(transaction);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error initializing payment cache: " + e.getMessage());
        }
        
        paymentCacheInitialized = true;
    }
    
    /**
     * Check if database exists
     */
    public static boolean databaseExists() {
        return getDatabaseFile().exists();
    }
    
    /**
     * Authenticates user credentials with O(1) lookup after cache initialization
     */
    public static boolean authenticateUser(String studentID, String password) {
        if (!isValidStudentID(studentID) || password == null || password.trim().isEmpty()) {
            return false;
        }
        
        initializeStudentCache();
        
        StudentInfo student = studentCache.get(studentID);
        return student != null && password.equals(student.getPassword());
    }
    
    /**
     * Retrieves student information with O(1) lookup after cache initialization
     */
    public static StudentInfo getStudentInfo(String studentID) {
        if (!isValidStudentID(studentID)) {
            return null;
        }
        
        initializeStudentCache();
        return studentCache.get(studentID);
    }
    
    /**
     * Get all students with efficient cache-based retrieval
     */
    public static List<StudentInfo> getAllStudents() {
        initializeStudentCache();
        return new ArrayList<>(studentCache.values());
    }
    
    /**
     * Get attendance records for a student with O(1) lookup
     */
    public static List<AttendanceRecord> getAttendanceRecords(String studentID) {
        if (!isValidStudentID(studentID)) {
            return new ArrayList<>();
        }
        
        initializeAttendanceCache();
        return attendanceCache.getOrDefault(studentID, new ArrayList<>());
    }
    
    /**
     * Get grade records for a student with O(1) lookup
     */
    public static List<GradeRecord> getGradeRecords(String studentID) {
        if (!isValidStudentID(studentID)) {
            return new ArrayList<>();
        }
        
        initializeGradeCache();
        return gradeCache.getOrDefault(studentID, new ArrayList<>());
    }
    
    /**
     * Get payment transactions for a student with O(1) lookup
     */
    public static List<PaymentTransaction> getPaymentTransactions(String studentID) {
        if (!isValidStudentID(studentID)) {
            return new ArrayList<>();
        }
        
        initializePaymentCache();
        return paymentCache.getOrDefault(studentID, new ArrayList<>());
    }
    
    /**
     * Saves a new student account to the database with cache update
     */
    public static boolean saveStudentAccount(StudentInfo studentInfo) {
        if (studentInfo == null || !isValidStudentID(studentInfo.getId())) {
            return false;
        }
        
        try {
            // Save to Database.txt
            File dbFile = getDatabaseFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, true))) {
                String dbEntry = studentInfo.toDatabaseFormat();
                writer.write(dbEntry);
                writer.newLine();
                writer.flush();
            }
            
            // Save to UserPasswordID.txt
            File credsFile = getUserPasswordFile();
            try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(credsFile, true))) {
                String credsEntry = "ID: " + studentInfo.getId() + " | Password: " + studentInfo.getPassword();
                logWriter.write(credsEntry);
                logWriter.newLine();
                logWriter.flush();
            }
            
            // Update cache
            studentCache.put(studentInfo.getId(), studentInfo);
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving student account: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates a unique student ID with collision detection
     */
    public static String generateUniqueStudentID() {
        initializeStudentCache();
        
        Random rand = new Random();
        String newID;
        int attempts = 0;
        final int maxAttempts = 1000; // Prevent infinite loop
        
        do {
            int lastFour = rand.nextInt(10000);
            newID = "225" + String.format("%04d", lastFour);
            attempts++;
        } while (studentCache.containsKey(newID) && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            throw new RuntimeException("Unable to generate unique student ID after " + maxAttempts + " attempts");
        }
        
        return newID;
    }
    
    /**
     * Logs a payment transaction with cache update
     */
    public static void logPaymentTransaction(String channelName, double amount, String studentID) {
        if (!isValidStudentID(studentID) || channelName == null || amount <= 0) {
            return;
        }
        
        try {
            File logFile = getPaymentLogsFile();
            
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
            String currentDateTime = dateFormat.format(new java.util.Date());
            
            String reference = "FIRST SEMESTER 2025-2026 Enrollme.";
            String formattedAmount = String.format("P %,.2f", amount);
            
            String logEntry = currentDateTime + "," + channelName + "," + reference + "," + formattedAmount + "," + studentID;
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(logEntry);
                writer.newLine();
            }
            
            // Update cache
            PaymentTransaction transaction = new PaymentTransaction(
                currentDateTime, channelName, reference, formattedAmount
            );
            paymentCache.computeIfAbsent(studentID, k -> new ArrayList<>()).add(transaction);
            
        } catch (IOException e) {
            System.err.println("Error writing to payment log: " + e.getMessage());
        }
    }
    
    /**
     * Gets student profile information with caching
     */
    public static String getStudentProfile(String studentID) {
        if (!isValidStudentID(studentID)) {
            return null;
        }
        
        try {
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:") || line.startsWith("ProfileData")) {
                            continue;
                        }
                        
                        String[] mainParts = line.split("\\|");
                        String basicInfo = mainParts[0];
                        
                        String[] parts = basicInfo.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            if (line.contains("|") && mainParts.length > 1) {
                                return mainParts[1];
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading student profile: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Updates a student's profile information with validation
     */
    public static boolean updateStudentProfile(String studentID, String profileData) {
        if (!isValidStudentID(studentID) || profileData == null) {
            return false;
        }
        
        try {
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:") || line.startsWith("ProfileData")) {
                            lines.add(line);
                            continue;
                        }
                        
                        String[] parts = line.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            line = line + "|" + profileData;
                        }
                        lines.add(line);
                    }
                }
                
                // Write back to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error updating profile: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates a student's password with validation and cache update
     */
    public static boolean updateStudentPassword(String studentID, String newPassword) {
        if (!isValidStudentID(studentID) || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }
        
        try {
            // Update Database.txt
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty() || line.startsWith("===") || line.startsWith("Format:") || line.startsWith("ProfileData")) {
                            lines.add(line);
                            continue;
                        }
                        
                        String[] mainParts = line.split("\\|");
                        String basicInfo = mainParts[0];
                        String profileData = mainParts.length > 1 ? mainParts[1] : "";
                        
                        String[] parts = basicInfo.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            if (parts.length >= 6) {
                                parts[5] = newPassword;
                                String updatedBasicInfo = String.join(",", parts);
                                line = profileData.isEmpty() ? updatedBasicInfo : updatedBasicInfo + "|" + profileData;
                            }
                        }
                        lines.add(line);
                    }
                }
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            // Update UserPasswordID.txt
            File credsFile = getUserPasswordFile();
            if (credsFile.exists()) {
                List<String> lines = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(credsFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                            lines.add(line);
                            continue;
                        }
                        if (line.contains("ID: " + studentID)) {
                            line = "ID: " + studentID + " | Password: " + newPassword;
                        }
                        lines.add(line);
                    }
                }
                
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(credsFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            // Update cache
            StudentInfo cachedStudent = studentCache.get(studentID);
            if (cachedStudent != null) {
                // Create updated student info (assuming StudentInfo is immutable or has setter)
                // This depends on your StudentInfo implementation
                studentCache.put(studentID, new StudentInfo(
                    cachedStudent.getId(),
                    cachedStudent.getLastName(),
                    cachedStudent.getFirstName(),
                    cachedStudent.getMiddleName(),
                    cachedStudent.getDateOfBirth(),
                    newPassword
                ));
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Clear all caches - useful for testing or when data files are externally modified
     */
    public static void clearCaches() {
        studentCache.clear();
        attendanceCache.clear();
        gradeCache.clear();
        paymentCache.clear();
        
        studentCacheInitialized = false;
        attendanceCacheInitialized = false;
        gradeCacheInitialized = false;
        paymentCacheInitialized = false;
    }
    
    /**
     * Get cache statistics for monitoring
     */
    public static String getCacheStatistics() {
        return String.format(
            "Cache Statistics:\n" +
            "Students: %d entries\n" +
            "Attendance: %d students with records\n" +
            "Grades: %d students with records\n" +
            "Payments: %d students with transactions",
            studentCache.size(),
            attendanceCache.size(),
            gradeCache.size(),
            paymentCache.size()
        );
    }
}