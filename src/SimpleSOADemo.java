import java.time.LocalDate;
import java.util.Scanner;

/**
 * Simple demonstration of Statement of Accounts functionality without servlet dependencies
 */
public class SimpleSOADemo {
    
    public static void main(String[] args) {
        System.out.println("=== iSLU Student Portal - Statement of Accounts Demo ===\n");
        
        // Demo data based on the images provided
        demonstrateStudent1();
        System.out.println("\n" + "=".repeat(60) + "\n");
        demonstrateStudent2();
        System.out.println("\n" + "=".repeat(60) + "\n");
        interactiveDemo();
    }
    
    private static void demonstrateStudent1() {
        System.out.println("=== Student 1: Sherlie O. Rivera (2255146) ===");
        String studentID = "2255146";
        
        // Get account statement
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        
        // Display basic info
        System.out.println("Student ID: " + statement.getStudentID());
        System.out.println("Program: BSIT 2");
        System.out.println("Semester: " + statement.getSemester() + " " + statement.getAcademicYear());
        System.out.println();
        
        // Display financial summary
        System.out.println("FINANCIAL SUMMARY:");
        System.out.println("Total Amount: P " + String.format("%,.2f", statement.getTotalAmount()));
        System.out.println("Amount Paid: P " + String.format("%,.2f", statement.getAmountPaid()));
        System.out.println("Current Balance: P " + String.format("%,.2f", statement.getBalance()));
        
        if (statement.getOverpayment() > 0) {
            System.out.println("Overpayment: P " + String.format("%,.2f", statement.getOverpayment()));
        }
        
        // Display exam payment status
        System.out.println("\nEXAM PAYMENT STATUS:");
        System.out.println("Prelim: " + (statement.isPrelimPaid() ? "✓ PAID" : "✗ NOT PAID") + 
                          " (Due: P " + String.format("%,.2f", statement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM)) + ")");
        System.out.println("Midterm: " + (statement.isMidtermPaid() ? "✓ PAID" : "✗ NOT PAID") + 
                          " (Due: P " + String.format("%,.2f", statement.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM)) + ")");
        System.out.println("Finals: " + (statement.isFinalsPaid() ? "✓ PAID" : "✗ NOT PAID") + 
                          " (Due: P " + String.format("%,.2f", statement.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS)) + ")");
        
        // Current exam period status
        System.out.println("\nCURRENT EXAM PERIOD STATUS:");
        ExamPeriodInfo examInfo = getCurrentExamPeriodInfo(statement);
        System.out.println("Current Period: " + examInfo.currentPeriod);
        System.out.println("Amount Due: P " + String.format("%,.2f", examInfo.amountDue));
        System.out.println("Status: " + (examInfo.isPaid ? "PAID - Permitted to take exams" : "NOT PAID - Payment required"));
    }
    
    private static void demonstrateStudent2() {
        System.out.println("=== Student 2: Ashel John D. Bimmuyag (2250605) ===");
        String studentID = "2250605";
        
        // Get account statement
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        
        // Add additional fees to match the second image scenario (P 6,500 due, P 20,383 balance)
        // First, let's add some payments to create the scenario
        AccountStatementManager.processPayment(studentID, 21500.00, "Payment Received", "00453312H");
        AccountStatementManager.processPayment(studentID, 7500.00, "Payment Received", "00453335C");
        
        // Add more fees to increase balance
        statement.addFee(new FeeBreakdown("TF002", "TUITION FEE @820.00/u", 9020.00, FeeBreakdown.FeeType.TUITION, LocalDate.of(2025, 9, 15)));
        statement.addFee(new FeeBreakdown("TF003", "TUITION FEE @1167.00/u", 10503.00, FeeBreakdown.FeeType.TUITION, LocalDate.of(2025, 9, 15)));
        statement.addFee(new FeeBreakdown("TF004", "TUITION FEE @434.00/u", 1302.00, FeeBreakdown.FeeType.TUITION, LocalDate.of(2025, 9, 15)));
        statement.addFee(new FeeBreakdown("OF001", "OTHER FEES", 6784.00, FeeBreakdown.FeeType.MISCELLANEOUS, LocalDate.of(2025, 9, 15)));
        statement.addFee(new FeeBreakdown("LF002", "OTHER/LAB.FEE(S)", 14064.00, FeeBreakdown.FeeType.LABORATORY, LocalDate.of(2025, 9, 15)));
        statement.addFee(new FeeBreakdown("PMS001", "PMS WaterDrinkingSystem (JV100486)", 60.00, FeeBreakdown.FeeType.OTHER, LocalDate.of(2025, 9, 15)));
        statement.addFee(new FeeBreakdown("INT001", "Internationalization Fee (JV100487)", 150.00, FeeBreakdown.FeeType.OTHER, LocalDate.of(2025, 9, 15)));
        
        // Refresh statement
        statement = AccountStatementManager.getStatement(studentID);
        
        System.out.println("Student ID: " + statement.getStudentID());
        System.out.println("Program: BSIT 2");
        System.out.println("Semester: " + statement.getSemester() + " " + statement.getAcademicYear());
        System.out.println();
        
        // Display financial summary
        System.out.println("FINANCIAL SUMMARY:");
        System.out.println("Total Amount: P " + String.format("%,.2f", statement.getTotalAmount()));
        System.out.println("Amount Paid: P " + String.format("%,.2f", statement.getAmountPaid()));
        System.out.println("Current Balance: P " + String.format("%,.2f", statement.getBalance()));
        
        // Current exam period status
        ExamPeriodInfo examInfo = getCurrentExamPeriodInfo(statement);
        System.out.println("\nCURRENT EXAM PERIOD STATUS:");
        System.out.println("Current Period: " + examInfo.currentPeriod);
        System.out.println("Amount Due: P " + String.format("%,.2f", examInfo.amountDue));
        System.out.println("Status: " + (examInfo.isPaid ? "PAID - Permitted to take exams" : "NOT PAID - Payment required"));
        
        // Display fee breakdown (similar to the table in the images)
        System.out.println("\nFEE BREAKDOWN:");
        System.out.println("Date         | Description                              | Amount");
        System.out.println("-------------|------------------------------------------|-------------");
        System.out.println("             | BEGINNING BALANCE                        |        0.00");
        
        for (PaymentTransaction payment : statement.getPaymentHistory()) {
            String amount = payment.getAmount().replace("P ", "").replace(",", "");
            System.out.printf("%-12s | %-40s | (%10s)\n", 
                payment.getDate().split(" ")[0], 
                "PAYMENT RECEIVED (" + payment.getReference() + ")",
                String.format("%,.2f", Double.parseDouble(amount)));
        }
        
        for (FeeBreakdown fee : statement.getFeeBreakdowns()) {
            if (fee.getAmount() > 0) { // Only show positive fees
                System.out.printf("%-12s | %-40s | %11s\n", 
                    fee.getDatePosted().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    fee.getDescription(),
                    String.format("%,.2f", fee.getAmount()));
            }
        }
    }
    
    private static void interactiveDemo() {
        System.out.println("=== Interactive Payment Demo ===");
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter Student ID (or press Enter for 2255146): ");
        String studentID = scanner.nextLine().trim();
        if (studentID.isEmpty()) {
            studentID = "2255146";
        }
        
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        ExamPeriodInfo examInfo = getCurrentExamPeriodInfo(statement);
        
        System.out.println("\nStudent: " + studentID);
        System.out.println("Current Balance: P " + String.format("%,.2f", statement.getBalance()));
        System.out.println("Amount Due for " + examInfo.currentPeriod + ": P " + String.format("%,.2f", examInfo.amountDue));
        
        System.out.print("\nEnter payment amount (or 0 to skip): P ");
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            
            if (amount > 0) {
                System.out.println("\nSelect Payment Method:");
                System.out.println("1. DragonPay (GCash)");
                System.out.println("2. UPay by UnionBank");
                System.out.println("3. BDO Online");
                System.out.println("4. BPI Online");
                System.out.print("Choice (1-4): ");
                
                int choice = Integer.parseInt(scanner.nextLine().trim());
                String[] methods = {"DragonPay (GCash)", "UPay by UnionBank", "BDO Online", "BPI Online"};
                String[] prefixes = {"DP", "UP", "BDO", "BPI"};
                
                if (choice >= 1 && choice <= 4) {
                    String method = methods[choice - 1];
                    String reference = prefixes[choice - 1] + System.currentTimeMillis();
                    
                    System.out.println("\nProcessing payment...");
                    AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                        studentID, amount, method, reference);
                    
                    System.out.println("\n" + "=".repeat(50));
                    System.out.println("PAYMENT RESULT");
                    System.out.println("=".repeat(50));
                    System.out.println("Status: " + (result.success ? "SUCCESS" : "FAILED"));
                    System.out.println("Message: " + result.message);
                    System.out.println("Reference: " + reference);
                    System.out.println("New Balance: P " + String.format("%,.2f", result.newBalance));
                    
                    if (result.newOverpayment > 0) {
                        System.out.println("Overpayment: P " + String.format("%,.2f", result.newOverpayment));
                    }
                    
                    // Show updated exam status
                    statement = AccountStatementManager.getStatement(studentID);
                    System.out.println("\nUpdated Exam Status:");
                    System.out.println("Prelim: " + (statement.isPrelimPaid() ? "✓ PAID" : "✗ NOT PAID"));
                    System.out.println("Midterm: " + (statement.isMidtermPaid() ? "✓ PAID" : "✗ NOT PAID"));
                    System.out.println("Finals: " + (statement.isFinalsPaid() ? "✓ PAID" : "✗ NOT PAID"));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
        }
        
        scanner.close();
    }
    
    private static ExamPeriodInfo getCurrentExamPeriodInfo(AccountStatement statement) {
        ExamPeriodInfo info = new ExamPeriodInfo();
        LocalDate currentDate = LocalDate.now();
        
        // Determine current exam period based on date
        if (currentDate.isBefore(LocalDate.of(2025, 10, 15))) {
            info.currentPeriod = "PRELIM";
            info.amountDue = statement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
            info.isPaid = statement.isPrelimPaid();
        } else if (currentDate.isBefore(LocalDate.of(2025, 11, 30))) {
            info.currentPeriod = "MIDTERM";
            info.amountDue = statement.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM);
            info.isPaid = statement.isMidtermPaid();
        } else {
            info.currentPeriod = "FINALS";
            info.amountDue = statement.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS);
            info.isPaid = statement.isFinalsPaid();
        }
        
        return info;
    }
    
    // Simple class for exam period information
    public static class ExamPeriodInfo {
        public String currentPeriod;
        public double amountDue;
        public boolean isPaid;
    }
}