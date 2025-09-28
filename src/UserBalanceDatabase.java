import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Database for tracking user balances and amount due
 * Format: StudentID,RemainingBalance,AmountDue,LastUpdated
 */
public class UserBalanceDatabase {
    private static final String DATABASE_FILE = "DueBalance.txt";
    private static Map<String, UserBalance> balances = new HashMap<>();
    
    static {
        loadBalances();
    }
    
    /**
     * User balance record
     */
    public static class UserBalance {
        private String studentID;
        private double remainingBalance;
        private double amountDue;
        private LocalDateTime lastUpdated;
        
        public UserBalance(String studentID, double remainingBalance, double amountDue) {
            this.studentID = studentID;
            this.remainingBalance = remainingBalance;
            this.amountDue = amountDue;
            this.lastUpdated = LocalDateTime.now();
        }
        
        // Getters and setters
        public String getStudentID() { return studentID; }
        public double getRemainingBalance() { return remainingBalance; }
        public double getAmountDue() { return amountDue; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        
        public void setRemainingBalance(double balance) { 
            this.remainingBalance = balance; 
            this.lastUpdated = LocalDateTime.now();
        }
        
        public void setAmountDue(double amount) { 
            this.amountDue = amount; 
            this.lastUpdated = LocalDateTime.now();
        }
        
        public void deductPayment(double paymentAmount) {
            // Deduct from both remaining balance and amount due
            this.remainingBalance = Math.max(0, this.remainingBalance - paymentAmount);
            this.amountDue = Math.max(0, this.amountDue - paymentAmount);
            this.lastUpdated = LocalDateTime.now();
        }
    }
    
    /**
     * Initialize a new user with default values
     * All accounts start with:
     * - P 6,830 due amount (prelim payment still needed)
     * - P 23,813 remaining balance (after P 21,177 enrollment payment)
     * - P 21,177 already paid (enrollment payment)
     */
    public static void initializeUser(String studentID) {
        if (!balances.containsKey(studentID)) {
            // User has paid P 21,177 (enrollment) and owes P 6,830 (prelim)
            // Remaining balance after enrollment payment: P 23,813
            UserBalance balance = new UserBalance(studentID, 23813.0, 6830.0);
            balances.put(studentID, balance);
            saveBalances();
            System.out.println("Initialized account " + studentID + ": P 6,830 due, P 23,813 balance, P 21,177 already paid (enrollment)");
        }
    }
    
    /**
     * Get user balance record
     */
    public static UserBalance getUserBalance(String studentID) {
        initializeUser(studentID); // Ensure user exists
        return balances.get(studentID);
    }
    
    /**
     * Update user balance after payment
     */
    public static void processPayment(String studentID, double paymentAmount) {
        UserBalance balance = getUserBalance(studentID);
        balance.deductPayment(paymentAmount);
        saveBalances();
    }
    
    /**
     * Get remaining balance for a user
     */
    public static double getRemainingBalance(String studentID) {
        return getUserBalance(studentID).getRemainingBalance();
    }
    
    /**
     * Get amount due for a user
     */
    public static double getAmountDue(String studentID) {
        return getUserBalance(studentID).getAmountDue();
    }
    
    /**
     * Check if user has paid (amount due = 0)
     */
    public static boolean isPaid(String studentID) {
        return getAmountDue(studentID) <= 0;
    }
    
    /**
     * Load balances from file
     */
    private static void loadBalances() {
        try {
            File file = new File(DATABASE_FILE);
            if (!file.exists()) {
                // Create file with header
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("=== USER BALANCE DATABASE ===");
                    writer.println("Format: StudentID,RemainingBalance,AmountDue,LastUpdated");
                    writer.println();
                }
                return;
            }
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || 
                        line.startsWith("===") || 
                        line.startsWith("Format:") || 
                        line.startsWith("Description:") ||
                        line.startsWith("Note:")) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        try {
                            String studentID = parts[0].trim();
                            double amountDue = Double.parseDouble(parts[1].trim());        // 2nd element: AmountDue
                            double remainingBalance = Double.parseDouble(parts[2].trim()); // 3rd element: RemainingBalance
                            
                            UserBalance balance = new UserBalance(studentID, remainingBalance, amountDue);
                            balances.put(studentID, balance);
                        } catch (NumberFormatException e) {
                            // Skip lines that can't be parsed as numbers
                            System.err.println("Skipping invalid line: " + line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading user balances: " + e.getMessage());
        }
    }
    
    /**
     * Save balances to file
     */
    private static void saveBalances() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATABASE_FILE))) {
            writer.println("=== USER BALANCE DATABASE ===");
            writer.println("Format: StudentID,AmountDue,RemainingBalance,PaidAmount");
            writer.println("Description: Records all user balances and amount due for each account");
            writer.println();
            
            for (UserBalance balance : balances.values()) {
                // Calculate paid amount: 21177 (enrollment) + any additional payments
                double paidAmount = 21177.0; // Default enrollment payment
                if (balance.getAmountDue() < 6830.0) {
                    paidAmount += (6830.0 - balance.getAmountDue()); // Add any prelim payments
                }
                
                writer.printf("%s,%.0f,%.0f,%.2f%n", 
                    balance.getStudentID(),
                    balance.getAmountDue(),
                    balance.getRemainingBalance(),
                    paidAmount
                );
            }
        } catch (IOException e) {
            System.err.println("Error saving user balances: " + e.getMessage());
        }
    }
    
    /**
     * Get all user balances (for admin purposes)
     */
    public static Map<String, UserBalance> getAllBalances() {
        return new HashMap<>(balances);
    }
    
    /**
     * Reset user balance to default values
     */
    public static void resetUserBalance(String studentID) {
        UserBalance balance = new UserBalance(studentID, 23813.0, 6830.0);
        balances.put(studentID, balance);
        saveBalances();
    }
}
