import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages account statements for all students
 * Handles loading, saving, and updating account data
 */
public class AccountStatementManager {
    private static final String STATEMENTS_FILE = "accountStatements.txt";
    private static final String FEES_FILE = "feeSchedule.txt";
    private static Map<String, AccountStatement> statements = new HashMap<>();
    
    static {
        loadStatements();
        initializeDefaultFees();
    }
    
    /**
     * Gets or creates an account statement for a student
     */
    public static AccountStatement getStatement(String studentID) {
        if (!statements.containsKey(studentID)) {
            createNewStatement(studentID);
        }
        
        AccountStatement statement = statements.get(studentID);
        
        // Ensure all accounts have the default enrollment payment transaction
        ensureDefaultEnrollmentTransaction(statement);
        
        return statement;
    }
    
    /**
     * Creates a new account statement with default fees
     */
    private static AccountStatement createNewStatement(String studentID) {
        AccountStatement statement = new AccountStatement(
            studentID, 
            "FIRST SEMESTER", 
            "2025-2026"
        );
        
        // Add default fees for the semester
        addDefaultFees(statement);
        
        // Initialize user in DueBalance.txt database with default values
        UserBalanceDatabase.initializeUser(studentID);
        
        statements.put(studentID, statement);
        saveStatements();
        
        return statement;
    }
    
    /**
     * Ensures all accounts have the default enrollment payment transaction
     */
    private static void ensureDefaultEnrollmentTransaction(AccountStatement statement) {
        // Check if the default enrollment transaction already exists
        boolean hasEnrollmentTransaction = false;
        for (PaymentTransaction transaction : statement.getPaymentHistory()) {
            if (transaction.getChannel().equals("BPI ONLINE") && 
                transaction.getReference().equals("FIRST SEMESTER 2025-2026 Enrollment") &&
                transaction.getAmount().equals("P 21,177.00")) {
                hasEnrollmentTransaction = true;
                break;
            }
        }
        
        // If no enrollment transaction exists, add it
        if (!hasEnrollmentTransaction) {
            addDefaultEnrollmentTransaction(statement);
        }
    }
    
    /**
     * Adds the default enrollment payment transaction
     */
    private static void addDefaultEnrollmentTransaction(AccountStatement statement) {
        // Process the default enrollment payment
        statement.processPayment(21177.00, "BPI ONLINE", "FIRST SEMESTER 2025-2026 Enrollment");
        
        // Log to payment logs file
        DataManager.logPaymentTransaction("BPI ONLINE", 21177.00, statement.getStudentID());
    }
    
    /**
     * Adds default fees to a new statement
     */
    private static void addDefaultFees(AccountStatement statement) {
        LocalDate now = LocalDate.now();
        
        // Base tuition and fees for IT students
        statement.addFee(new FeeBreakdown("TF001", "Tuition Fee (21 units @ P1,500/unit)", 
            31500.00, FeeBreakdown.FeeType.TUITION, now));
        
        statement.addFee(new FeeBreakdown("LF001", "Computer Laboratory Fee", 
            3500.00, FeeBreakdown.FeeType.LABORATORY, now));
        
        statement.addFee(new FeeBreakdown("MF001", "Miscellaneous Fee", 
            2800.00, FeeBreakdown.FeeType.MISCELLANEOUS, now));
        
        statement.addFee(new FeeBreakdown("RF001", "Registration Fee", 
            500.00, FeeBreakdown.FeeType.REGISTRATION, now));
        
        statement.addFee(new FeeBreakdown("LB001", "Library Fee", 
            800.00, FeeBreakdown.FeeType.LIBRARY, now));
        
        statement.addFee(new FeeBreakdown("AT001", "Athletic Fee", 
            500.00, FeeBreakdown.FeeType.ATHLETIC, now));
        
        statement.addFee(new FeeBreakdown("MD001", "Medical/Dental Fee", 
            400.00, FeeBreakdown.FeeType.MEDICAL, now));
        
        statement.addFee(new FeeBreakdown("GD001", "Guidance Fee", 
            300.00, FeeBreakdown.FeeType.GUIDANCE, now));
        
        statement.addFee(new FeeBreakdown("PB001", "Student Publication Fee", 
            250.00, FeeBreakdown.FeeType.PUBLICATION, now));
        
        statement.addFee(new FeeBreakdown("IN001", "Internet and Technology Fee", 
            1500.00, FeeBreakdown.FeeType.INTERNET, now));
        
        statement.addFee(new FeeBreakdown("EN001", "Energy Fee", 
            1200.00, FeeBreakdown.FeeType.ENERGY, now));
        
        statement.addFee(new FeeBreakdown("IS001", "Student Insurance", 
            350.00, FeeBreakdown.FeeType.INSURANCE, now));
        
        statement.addFee(new FeeBreakdown("DV001", "Development Fund", 
            1000.00, FeeBreakdown.FeeType.DEVELOPMENT, now));
        
        statement.addFee(new FeeBreakdown("CL001", "Cultural Activities Fee", 
            300.00, FeeBreakdown.FeeType.CULTURAL, now));
        
        // Apply early enrollment discount if applicable
        LocalDate enrollmentDeadline = LocalDate.of(2025, 7, 15);
        if (LocalDate.now().isBefore(enrollmentDeadline)) {
            statement.addFee(new FeeBreakdown("DISC01", "Early Enrollment Discount (5%)", 
                -2225.00, FeeBreakdown.FeeType.DISCOUNT, now, "5% discount on total fees"));
        }
    }
    
    /**
     * Processes a payment for a student
     */
    public static AccountStatement.PaymentResult processPayment(
            String studentID, double amount, String channel, String reference) {
        AccountStatement statement = getStatement(studentID);
        AccountStatement.PaymentResult result = statement.processPayment(amount, channel, reference);
        
        if (result.success) {
            saveStatements();
            // Also save to payment logs
            DataManager.logPaymentTransaction(channel, amount, studentID);
        }
        
        return result;
    }
    
    /**
     * Applies a scholarship to a student's account
     */
    public static void applyScholarship(String studentID, double percentage, String scholarshipName) {
        AccountStatement statement = getStatement(studentID);
        statement.applyScholarship(percentage, scholarshipName);
        saveStatements();
    }
    
    /**
     * Adds a custom fee to a student's account
     */
    public static void addCustomFee(String studentID, FeeBreakdown fee) {
        AccountStatement statement = getStatement(studentID);
        statement.addFee(fee);
        saveStatements();
    }
    
    /**
     * Gets payment status for exams
     */
    public static Map<String, Boolean> getExamPaymentStatus(String studentID) {
        AccountStatement statement = getStatement(studentID);
        Map<String, Boolean> status = new HashMap<>();
        status.put("prelim", statement.isPrelimPaid());
        status.put("midterm", statement.isMidtermPaid());
        status.put("finals", statement.isFinalsPaid());
        return status;
    }
    
    /**
     * Generates a detailed statement report
     */
    public static String generateStatementReport(String studentID) {
        AccountStatement statement = getStatement(studentID);
        StringBuilder report = new StringBuilder();
        
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String currentDate = LocalDate.now().format(dateFormatter);
        
        report.append("STATEMENT OF ACCOUNT\n");
        report.append("===========================================\n");
        report.append("Student ID: ").append(statement.getStudentID()).append("\n");
        report.append("Semester: ").append(statement.getSemester()).append(" ").append(statement.getAcademicYear()).append("\n");
        report.append("Date Generated: ").append(currentDate).append("\n");
        report.append("As of: ").append(currentDate).append("\n\n");
        
        report.append("FEES BREAKDOWN:\n");
        report.append("-------------------------------------------\n");
        for (FeeBreakdown fee : statement.getFeeBreakdowns()) {
            report.append(fee.toFormattedString()).append("\n");
        }
        
        report.append("\n-------------------------------------------\n");
        report.append(String.format("Total Tuition: P %,.2f\n", statement.getTotalTuition()));
        report.append(String.format("Total Fees: P %,.2f\n", statement.getTotalFees()));
        report.append(String.format("TOTAL AMOUNT: P %,.2f\n", statement.getTotalAmount()));
        report.append(String.format("Amount Paid: P %,.2f\n", statement.getAmountPaid()));
        report.append(String.format("BALANCE: P %,.2f\n", statement.getBalance()));
        
        if (statement.getOverpayment() > 0) {
            report.append(String.format("Overpayment: P %,.2f\n", statement.getOverpayment()));
        }
        
        report.append("\nEXAM PAYMENT STATUS:\n");
        report.append("-------------------------------------------\n");
        report.append("Prelim Exams: ").append(statement.isPrelimPaid() ? "✓ PAID" : "✗ UNPAID").append("\n");
        report.append("Midterm Exams: ").append(statement.isMidtermPaid() ? "✓ PAID" : "✗ UNPAID").append("\n");
        report.append("Finals Exams: ").append(statement.isFinalsPaid() ? "✓ PAID" : "✗ UNPAID").append("\n");
        
        if (!statement.getPaymentHistory().isEmpty()) {
            report.append("\nPAYMENT HISTORY:\n");
            report.append("-------------------------------------------\n");
            for (PaymentTransaction payment : statement.getPaymentHistory()) {
                report.append(String.format("%s | %s | %s | %s\n",
                    payment.getDate(), payment.getChannel(), 
                    payment.getReference(), payment.getAmount()));
            }
        }
        
        return report.toString();
    }
    
    /**
     * Loads all statements from file
     */
    private static void loadStatements() {
        File file = new File(STATEMENTS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            AccountStatement currentStatement = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("STUDENT:")) {
                    String[] parts = line.substring(8).split(",");
                    if (parts.length >= 3) {
                        currentStatement = new AccountStatement(
                            parts[0].trim(), parts[1].trim(), parts[2].trim()
                        );
                        statements.put(parts[0].trim(), currentStatement);
                    }
                } else if (line.startsWith("FEE:") && currentStatement != null) {
                    FeeBreakdown fee = FeeBreakdown.fromCSV(line.substring(4));
                    if (fee != null) {
                        currentStatement.addFee(fee);
                    }
                } else if (line.startsWith("PAYMENT:") && currentStatement != null) {
                    String[] parts = line.substring(8).split(",");
                    if (parts.length >= 3) {
                        currentStatement.processPayment(
                            Double.parseDouble(parts[2].trim()),
                            parts[1].trim(),
                            parts.length > 3 ? parts[3].trim() : "Payment"
                        );
                        // Ensure exam payment status is updated after loading payment
                        currentStatement.updateExamPaymentStatus();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading statements: " + e.getMessage());
        }
        
        // Ensure all loaded statements have their exam payment status updated
        for (AccountStatement statement : statements.values()) {
            statement.updateExamPaymentStatus();
        }
    }
    
    /**
     * Saves all statements to file
     */
    private static void saveStatements() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STATEMENTS_FILE))) {
            for (AccountStatement statement : statements.values()) {
                writer.write(String.format("STUDENT:%s,%s,%s\n",
                    statement.getStudentID(),
                    statement.getSemester(),
                    statement.getAcademicYear()));
                
                for (FeeBreakdown fee : statement.getFeeBreakdowns()) {
                    writer.write("FEE:" + fee.toCSV() + "\n");
                }
                
                for (PaymentTransaction payment : statement.getPaymentHistory()) {
                    writer.write(String.format("PAYMENT:%s,%s,%s,%s\n",
                        payment.getDate(),
                        payment.getChannel(),
                        payment.getAmount().replace("P ", "").replace(",", ""),
                        payment.getReference()));
                }
                
                writer.write("\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving statements: " + e.getMessage());
        }
    }
    
    /**
     * Initialize default fee schedule if file doesn't exist
     */
    private static void initializeDefaultFees() {
        File file = new File(FEES_FILE);
        if (file.exists()) {
            return;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("# Fee Schedule for 2025-2026\n");
            writer.write("# Format: CODE,DESCRIPTION,AMOUNT,TYPE\n");
            writer.write("TF001,Tuition Fee per Unit,1500.00,TUITION\n");
            writer.write("LF001,Computer Laboratory Fee,3500.00,LABORATORY\n");
            writer.write("MF001,Miscellaneous Fee,2800.00,MISCELLANEOUS\n");
            writer.write("RF001,Registration Fee,500.00,REGISTRATION\n");
            writer.write("LB001,Library Fee,800.00,LIBRARY\n");
            writer.write("AT001,Athletic Fee,500.00,ATHLETIC\n");
            writer.write("MD001,Medical/Dental Fee,400.00,MEDICAL\n");
            writer.write("GD001,Guidance Fee,300.00,GUIDANCE\n");
            writer.write("PB001,Student Publication Fee,250.00,PUBLICATION\n");
            writer.write("IN001,Internet and Technology Fee,1500.00,INTERNET\n");
            writer.write("EN001,Energy Fee,1200.00,ENERGY\n");
            writer.write("IS001,Student Insurance,350.00,INSURANCE\n");
            writer.write("DV001,Development Fund,1000.00,DEVELOPMENT\n");
            writer.write("CL001,Cultural Activities Fee,300.00,CULTURAL\n");
        } catch (IOException e) {
            System.err.println("Error creating fee schedule: " + e.getMessage());
        }
    }
    
    /**
     * Clears all cached statements (for testing)
     */
    public static void clearCache() {
        statements.clear();
        loadStatements();
    }
}