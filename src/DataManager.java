import java.io.*;
import java.util.*;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * Centralized data management class for the Student Portal system
 * Handles all file operations and data persistence
 */
public class DataManager {
    
    // File paths
    private static final String DATABASE_FILE = "Database.txt";
    private static final String USER_PASSWORD_FILE = "UserPasswordID.txt";
    private static final String PAYMENT_LOGS_FILE = "paymentLogs.txt";
    private static final String MEDICAL_RECORDS_FILE = "MedicalRecords.txt";
    
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
            URL codeSourceUrl = DataManager.class.getProtectionDomain().getCodeSource().getLocation();
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

    private static File getDatabaseFile() { return resolveFile(DATABASE_FILE); }
    private static File getUserPasswordFile() { return resolveFile(USER_PASSWORD_FILE); }
    private static File getPaymentLogsFile() { return resolveFile(PAYMENT_LOGS_FILE); }

    public static boolean databaseExists() {
        return getDatabaseFile().exists();
    }
    
    /**
     * Authenticates user credentials against the database
     * @param studentID The student ID to authenticate
     * @param password The password to authenticate
     * @return true if credentials are valid, false otherwise
     */
    public static boolean authenticateUser(String studentID, String password) {
        try {
            File databaseFile = getDatabaseFile();
            if (!databaseFile.exists()) {
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Handle lines with profile data (containing | separator)
                    String[] mainParts = line.split("\\|");
                    String basicInfo = mainParts[0]; // Everything before the |
                    
                    String[] parts = basicInfo.split(",");
                    if (parts.length >= 6) {
                        String storedID = parts[0].trim();
                        String storedPassword = parts[5].trim();
                        
                        if (studentID.equals(storedID) && password.equals(storedPassword)) {
                            return true;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading database: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Retrieves student information from the database
     * @param studentID The student ID to look up
     * @return StudentInfo object containing student details, or null if not found
     */
    public static StudentInfo getStudentInfo(String studentID) {
        try {
            File databaseFile = getDatabaseFile();
            if (!databaseFile.exists()) {
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Handle lines with profile data (containing | separator)
                    String[] mainParts = line.split("\\|");
                    String basicInfo = mainParts[0]; // Everything before the |
                    
                    String[] parts = basicInfo.split(",");
                    if (parts.length >= 6) {
                        String storedID = parts[0].trim();
                        
                        if (studentID.equals(storedID)) {
                            return new StudentInfo(
                                parts[0].trim(), // ID
                                parts[1].trim(), // Last Name
                                parts[2].trim(), // First Name
                                parts[3].trim(), // Middle Name
                                parts[4].trim(), // Date of Birth
                                parts[5].trim()  // Password
                            );
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading database: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Saves a new student account to the database
     * @param studentInfo The student information to save
     * @return true if successful, false otherwise
     */
    public static boolean saveStudentAccount(StudentInfo studentInfo) {
        try {
            // Save to Database.txt
            File dbFile = getDatabaseFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, true))) {
                String dbEntry = studentInfo.toDatabaseFormat();
                writer.write(dbEntry);
                writer.newLine();
                writer.flush(); // Ensure data is written immediately
            }
            
            // Save to UserPasswordID.txt
            File credsFile = getUserPasswordFile();
            try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(credsFile, true))) {
                String credsEntry = "ID: " + studentInfo.getId() + " | Password: " + studentInfo.getPassword();
                logWriter.write(credsEntry);
                logWriter.newLine();
                logWriter.flush(); // Ensure data is written immediately
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error saving student account: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generates a unique student ID
     * @return A unique 7-digit ID starting with "225"
     */
    public static String generateUniqueStudentID() {
        Set<String> usedIDs = new HashSet<>();
        
        try {
            File file = getDatabaseFile();
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length > 0) {
                            usedIDs.add(parts[0]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading existing IDs: " + e.getMessage());
        }
        
        Random rand = new Random();
        String newID;
        do {
            int lastFour = rand.nextInt(10000);
            newID = "225" + String.format("%04d", lastFour);
        } while (usedIDs.contains(newID));
        
        return newID;
    }
    
    /**
     * Logs a payment transaction
     * @param channelName The payment channel used
     * @param amount The amount paid
     * @param studentID The student ID making the payment
     */
    public static void logPaymentTransaction(String channelName, double amount, String studentID) {
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
        } catch (IOException e) {
            System.err.println("Error writing to payment log: " + e.getMessage());
        }
    }
    
    /**
     * Loads payment transactions for a specific student
     * @param studentID The student ID to load transactions for
     * @return List of payment transactions
     */
    public static List<PaymentTransaction> loadPaymentTransactions(String studentID) {
        List<PaymentTransaction> transactions = new ArrayList<>();
        
        try {
            File logFile = getPaymentLogsFile();
            if (logFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length >= 5) {
                            String transactionStudentID = parts[4].trim();
                            if (studentID.equals(transactionStudentID)) {
                                transactions.add(new PaymentTransaction(
                                    parts[0].trim(), // Date
                                    parts[1].trim(), // Channel
                                    parts[2].trim(), // Reference
                                    parts[3].trim()  // Amount
                                ));
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading payment logs: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Gets all students from the database
     * @return List of all student information
     */
    public static List<StudentInfo> getAllStudents() {
        List<StudentInfo> students = new ArrayList<>();
        
        try {
            File databaseFile = getDatabaseFile();
            if (!databaseFile.exists()) {
                return students;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(databaseFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip empty lines
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Handle lines with profile data (containing | separator)
                    String[] mainParts = line.split("\\|");
                    String basicInfo = mainParts[0]; // Everything before the |
                    
                    String[] parts = basicInfo.split(",");
                    if (parts.length >= 6) {
                        students.add(new StudentInfo(
                            parts[0].trim(), // ID
                            parts[1].trim(), // Last Name
                            parts[2].trim(), // First Name
                            parts[3].trim(), // Middle Name
                            parts[4].trim(), // Date of Birth
                            parts[5].trim()  // Password
                        ));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading all students: " + e.getMessage());
        }
        
        return students;
    }
    
    /**
     * Gets student profile information from Database.txt
     * @param studentID The student ID to get profile for
     * @return Profile data as a formatted string
     */
    public static String getStudentProfile(String studentID) {
        try {
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        
                        System.out.println("DEBUG: Checking line: " + line);
                        
                        // Handle lines with profile data (containing | separator)
                        String[] mainParts = line.split("\\|");
                        String basicInfo = mainParts[0]; // Everything before the |
                        
                        String[] parts = basicInfo.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            System.out.println("DEBUG: Found matching student ID: " + studentID);
                            // Check if profile data exists (after the | separator)
                            if (line.contains("|") && mainParts.length > 1) {
                                System.out.println("DEBUG: Returning profile data: " + mainParts[1]);
                                return mainParts[1]; // Return the profile data part
                            } else {
                                System.out.println("DEBUG: No profile data found for student " + studentID);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading student profile: " + e.getMessage());
        }
        System.out.println("DEBUG: No profile data found for student " + studentID);
        return null;
    }
    
    /**
     * Updates a student's profile information in Database.txt
     * @param studentID The student ID to update
     * @param profileData The profile data to save
     * @return true if successful, false otherwise
     */
    public static boolean updateStudentProfile(String studentID, String profileData) {
        try {
            // Update Database.txt with profile information
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            // Append profile data to the existing line
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
     * Updates a student's password in both Database.txt and UserPasswordID.txt
     * @param studentID The student ID to update
     * @param newPassword The new password
     * @return true if successful, false otherwise
     */
    public static boolean updateStudentPassword(String studentID, String newPassword) {
        try {
            // Update Database.txt
            File dbFile = getDatabaseFile();
            if (dbFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        
                        // Handle lines with profile data (containing | separator)
                        String[] mainParts = line.split("\\|");
                        String basicInfo = mainParts[0]; // Everything before the |
                        String profileData = mainParts.length > 1 ? mainParts[1] : "";
                        
                        String[] parts = basicInfo.split(",");
                        if (parts.length > 0 && parts[0].equals(studentID)) {
                            // Update the password (6th field, index 5)
                            if (parts.length >= 6) {
                                parts[5] = newPassword;
                                String updatedBasicInfo = String.join(",", parts);
                                line = profileData.isEmpty() ? updatedBasicInfo : updatedBasicInfo + "|" + profileData;
                            }
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
            
            // Update UserPasswordID.txt
            File credsFile = getUserPasswordFile();
            if (credsFile.exists()) {
                java.util.List<String> lines = new java.util.ArrayList<>();
                try (BufferedReader reader = new BufferedReader(new FileReader(credsFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        if (line.contains("ID: " + studentID)) {
                            line = "ID: " + studentID + " | Password: " + newPassword;
                        }
                        lines.add(line);
                    }
                }
                
                // Write back to file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(credsFile))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    private static File getMedicalRecordsFile() { return resolveFile(MEDICAL_RECORDS_FILE); }
    
    /**
     * Get medical record for a student
     */
    public static MedicalRecord getMedicalRecord(String studentId) {
        File file = getMedicalRecordsFile();
        if (!file.exists()) {
            // Return empty medical record if file doesn't exist
            return new MedicalRecord(studentId);
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split("\\|", -1);
                if (parts.length > 0 && parts[0].equals(studentId)) {
                    return MedicalRecord.fromDatabaseFormat(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading medical records: " + e.getMessage());
        }
        
        // Return new empty record if not found
        return new MedicalRecord(studentId);
    }
    
    /**
     * Save or update medical record
     */
    public static boolean saveMedicalRecord(MedicalRecord record) {
        File file = getMedicalRecordsFile();
        List<String> lines = new ArrayList<>();
        boolean recordFound = false;
        
        // Read existing records
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split("\\|", -1);
                    if (parts.length > 0 && parts[0].equals(record.getStudentId())) {
                        // Replace existing record
                        lines.add(record.toDatabaseFormat());
                        recordFound = true;
                    } else {
                        lines.add(line);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading medical records: " + e.getMessage());
                return false;
            }
        }
        
        // Add new record if not found
        if (!recordFound) {
            lines.add(record.toDatabaseFormat());
        }
        
        // Write back to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.println(line);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving medical record: " + e.getMessage());
            return false;
        }
    }
}

