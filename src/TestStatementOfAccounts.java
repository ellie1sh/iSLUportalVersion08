import java.time.LocalDate;
import java.util.Map;

/**
 * Test class to demonstrate Statement of Accounts functionality
 */
public class TestStatementOfAccounts {
    
    public static void main(String[] args) {
        System.out.println("=== Statement of Accounts Test ===\n");
        
        // Test student ID from the images
        String studentID = "2255146";
        
        // Get or create account statement
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        
        System.out.println("Student ID: " + statement.getStudentID());
        System.out.println("Semester: " + statement.getSemester() + " " + statement.getAcademicYear());
        System.out.println("Total Amount: P " + String.format("%,.2f", statement.getTotalAmount()));
        System.out.println("Amount Paid: P " + String.format("%,.2f", statement.getAmountPaid()));
        System.out.println("Balance: P " + String.format("%,.2f", statement.getBalance()));
        
        if (statement.getOverpayment() > 0) {
            System.out.println("Overpayment: P " + String.format("%,.2f", statement.getOverpayment()));
        }
        
        System.out.println("\n=== Exam Payment Status ===");
        System.out.println("Prelim: " + (statement.isPrelimPaid() ? "PAID" : "NOT PAID"));
        System.out.println("Midterm: " + (statement.isMidtermPaid() ? "PAID" : "NOT PAID"));
        System.out.println("Finals: " + (statement.isFinalsPaid() ? "PAID" : "NOT PAID"));
        
        // Test payment processing
        System.out.println("\n=== Processing Payment ===");
        double paymentAmount = 6500.00; // Amount from the image
        AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
            studentID, paymentAmount, "DragonPay (GCash)", "DP" + System.currentTimeMillis());
        
        System.out.println("Payment Result: " + (result.success ? "SUCCESS" : "FAILED"));
        System.out.println("Message: " + result.message);
        System.out.println("New Balance: P " + String.format("%,.2f", result.newBalance));
        
        if (result.newOverpayment > 0) {
            System.out.println("New Overpayment: P " + String.format("%,.2f", result.newOverpayment));
        }
        
        // Display updated statement
        System.out.println("\n=== Updated Statement ===");
        statement = AccountStatementManager.getStatement(studentID);
        System.out.println("Total Amount: P " + String.format("%,.2f", statement.getTotalAmount()));
        System.out.println("Amount Paid: P " + String.format("%,.2f", statement.getAmountPaid()));
        System.out.println("Balance: P " + String.format("%,.2f", statement.getBalance()));
        
        System.out.println("\n=== Updated Exam Status ===");
        System.out.println("Prelim: " + (statement.isPrelimPaid() ? "PAID" : "NOT PAID"));
        System.out.println("Midterm: " + (statement.isMidtermPaid() ? "PAID" : "NOT PAID"));
        System.out.println("Finals: " + (statement.isFinalsPaid() ? "PAID" : "NOT PAID"));
        
        // Display fee breakdown
        System.out.println("\n=== Fee Breakdown ===");
        for (FeeBreakdown fee : statement.getFeeBreakdowns()) {
            System.out.println(fee.toFormattedString());
        }
        
        // Display payment history
        System.out.println("\n=== Payment History ===");
        for (PaymentTransaction payment : statement.getPaymentHistory()) {
            System.out.println(String.format("%s | %s | %s | %s",
                payment.getDate(), payment.getChannel(), 
                payment.getReference(), payment.getAmount()));
        }
        
        // Test another student (Ashel John D. Bimmuyag from second image)
        System.out.println("\n\n=== Testing Second Student ===");
        String studentID2 = "2250605";
        AccountStatement statement2 = AccountStatementManager.getStatement(studentID2);
        
        System.out.println("Student ID: " + statement2.getStudentID());
        System.out.println("Total Amount: P " + String.format("%,.2f", statement2.getTotalAmount()));
        System.out.println("Balance: P " + String.format("%,.2f", statement2.getBalance()));
        
        // Simulate the payment scenario from image 2 (P 6,500.00 due, P 20,383.00 balance)
        // This means they need to add more fees or adjust the existing ones
        
        System.out.println("\n=== Exam Period Due Amounts ===");
        System.out.println("Prelim Due: P " + String.format("%,.2f", 
            statement2.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM)));
        System.out.println("Midterm Due: P " + String.format("%,.2f", 
            statement2.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM)));
        System.out.println("Finals Due: P " + String.format("%,.2f", 
            statement2.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS)));
        
        System.out.println("\n=== Test Completed ===");
    }
}