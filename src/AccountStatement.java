import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages student account statement information including
 * balances, fees, payments, and transaction history
 */
public class AccountStatement {
    private String studentID;
    private String semester;
    private String academicYear;
    private double totalTuition;
    private double totalFees;
    private double totalAmount;
    private double amountPaid;
    private double balance;
    private double overpayment;
    private List<FeeBreakdown> feeBreakdowns;
    private List<PaymentTransaction> paymentHistory;
    private boolean isPrelimPaid;
    private boolean isMidtermPaid;
    private boolean isFinalsPaid;
    private LocalDate lastUpdateDate;
    
    // Constructor for new account statement
    public AccountStatement(String studentID, String semester, String academicYear) {
        this.studentID = studentID;
        this.semester = semester;
        this.academicYear = academicYear;
        this.feeBreakdowns = new ArrayList<>();
        this.paymentHistory = new ArrayList<>();
        this.lastUpdateDate = LocalDate.now();
        this.totalTuition = 0.0;
        this.totalFees = 0.0;
        this.totalAmount = 0.0;
        this.amountPaid = 0.0;
        this.balance = 23813.0; // Initialize remaining balance to P 23,813
        this.overpayment = 0.0;
        this.isPrelimPaid = false;
        this.isMidtermPaid = false;
        this.isFinalsPaid = false;
    }
    
    /**
     * Adds a fee item to the statement
     */
    public void addFee(FeeBreakdown fee) {
        feeBreakdowns.add(fee);
        recalculateTotals();
    }
    
    /**
     * Removes a fee item from the statement
     */
    public void removeFee(String feeCode) {
        feeBreakdowns.removeIf(fee -> fee.getCode().equals(feeCode));
        recalculateTotals();
    }
    
    /**
     * Processes a payment and updates balances
     */
    public PaymentResult processPayment(double amount, String paymentChannel, String reference) {
        PaymentResult result = new PaymentResult();
        
        if (amount <= 0) {
            result.success = false;
            result.message = "Payment amount must be greater than zero.";
            return result;
        }
        
        // Create payment transaction with current date and time
        String dateTime = java.time.LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
        String formattedAmount = String.format("P %,.2f", amount);
        
        // Determine payment type and initial status
        PaymentStatus initialStatus = paymentChannel.toLowerCase().contains("cashier") || 
                                     paymentChannel.toLowerCase().contains("onsite") ? 
                                     PaymentStatus.FOR_POSTING : PaymentStatus.PROCESSING;
        
        PaymentTransaction transaction = new PaymentTransaction(dateTime, paymentChannel, reference, formattedAmount, initialStatus);
        paymentHistory.add(transaction);
        
        // Apply payment to fees
        applyPaymentToFees(amount, transaction);
        
        // Update amounts
        amountPaid += amount;
        
        // Calculate new balance
        if (balance > 0) {
            // Has outstanding balance
            if (amount >= balance) {
                // Payment exceeds balance - create overpayment
                overpayment += (amount - balance);
                balance = 0;
                
                // Mark all fees as paid or processing
                for (FeeBreakdown fee : feeBreakdowns) {
                    if (!fee.isPaid() && fee.getAmount() > 0) {
                        fee.setPaymentStatus(initialStatus);
                    }
                }
                
                result.message = String.format("Payment %s! Amount: P %.2f\nBalance fully paid. Overpayment: P %.2f", 
                                             initialStatus.isInProgress() ? "processing" : "successful",
                                             amount, overpayment);
            } else {
                // Partial payment
                balance -= amount;
                result.message = String.format("Payment %s! Amount: P %.2f\nRemaining balance: P %.2f", 
                                             initialStatus.isInProgress() ? "processing" : "successful",
                                             amount, balance);
            }
        } else {
            // No balance - add to overpayment
            overpayment += amount;
            result.message = String.format("Payment successful! Amount: P %.2f\nAdded to overpayment. Total overpayment: P %.2f", 
                                         amount, overpayment);
        }
        
        // Update exam payment status
        updateExamPaymentStatus();
        
        result.success = true;
        result.transaction = transaction;
        result.newBalance = balance;
        result.newOverpayment = overpayment;
        
        lastUpdateDate = LocalDate.now();
        
        return result;
    }
    
    /**
     * Updates exam payment status based on current balance
     */
    public void updateExamPaymentStatus() {
        // Calculate required payments for each exam period
        double prelimRequirement = 6830.0; // Fixed prelim requirement of P 6,830
        double midtermRequirement = totalAmount * 0.6666;
        double finalsRequirement = totalAmount;
        
        // Check if payments are posted (not just processing)
        boolean hasPostedPayments = paymentHistory.stream()
            .anyMatch(p -> p.getStatus() != null && p.getStatus().isSuccessful());
        
        // Update payment status based on amount paid
        if (hasPostedPayments || amountPaid > 0) {
            // Prelim is paid if amount paid >= 6830
            isPrelimPaid = amountPaid >= prelimRequirement;
            isMidtermPaid = amountPaid >= midtermRequirement;
            isFinalsPaid = amountPaid >= finalsRequirement;
        } else {
            // If no payments, mark as unpaid
            isPrelimPaid = false;
            isMidtermPaid = false;
            isFinalsPaid = false;
        }
    }
    
    /**
     * Apply payment to individual fees
     */
    private void applyPaymentToFees(double paymentAmount, PaymentTransaction transaction) {
        double remainingPayment = paymentAmount;
        
        // Apply payment to unpaid fees in order
        for (FeeBreakdown fee : feeBreakdowns) {
            if (remainingPayment <= 0) break;
            
            if (!fee.isPaid() && fee.getAmount() > 0) {
                double feeBalance = fee.getBalance();
                if (feeBalance > 0) {
                    double paymentForThisFee = Math.min(remainingPayment, feeBalance);
                    fee.applyPayment(paymentForThisFee);
                    fee.setPaymentStatus(transaction.getStatus());
                    remainingPayment -= paymentForThisFee;
                }
            }
        }
    }
    
    /**
     * Updates payment statuses (simulates processing time)
     */
    public void updatePaymentStatuses() {
        for (PaymentTransaction payment : paymentHistory) {
            payment.updateStatusBasedOnTime();
        }
        
        // Update fee payment statuses
        for (FeeBreakdown fee : feeBreakdowns) {
            if (fee.getPaymentStatus() != null) {
                // Find the latest payment and use its status
                if (!paymentHistory.isEmpty()) {
                    PaymentTransaction latestPayment = paymentHistory.get(paymentHistory.size() - 1);
                    if (latestPayment.getStatus().isSuccessful()) {
                        fee.setPaymentStatus(null); // Clear status once posted
                    } else {
                        fee.setPaymentStatus(latestPayment.getStatus());
                    }
                }
            }
        }
        
        updateExamPaymentStatus();
    }
    
    /**
     * Recalculates all totals based on current fees
     */
    private void recalculateTotals() {
        totalTuition = 0;
        totalFees = 0;
        
        for (FeeBreakdown fee : feeBreakdowns) {
            if (fee.getType() == FeeBreakdown.FeeType.TUITION) {
                totalTuition += fee.getAmount();
            } else {
                totalFees += fee.getAmount();
            }
        }
        
        totalAmount = totalTuition + totalFees;
        
        // Preserve the initial balance of 23,813 unless payments have been made
        if (amountPaid == 0.0) {
            balance = 23813.0; // Keep initial balance of P 23,813
        } else {
            balance = totalAmount - amountPaid;
        }
        
        if (balance < 0) {
            overpayment = Math.abs(balance);
            balance = 0;
        }
        
        updateExamPaymentStatus();
    }
    
    /**
     * Gets the amount due for a specific exam period
     */
    public double getExamPeriodDue(ExamPeriod period) {
        switch (period) {
            case PRELIM:
                // If prelim is already paid, always return 0.00
                if (isPrelimPaid) {
                    return 0.0;
                }
                // Calculate amount due: initial 6830 minus amount paid
                double prelimDue = Math.max(0, 6830.0 - amountPaid);
                return prelimDue;
            case MIDTERM:
                double midtermRequirement = totalAmount * 0.6666;
                return Math.max(0, midtermRequirement - amountPaid);
            case FINALS:
                double finalsRequirement = totalAmount;
                return Math.max(0, finalsRequirement - amountPaid);
            default:
                return balance;
        }
    }
    
    /**
     * Gets exam eligibility message
     */
    public String getExamEligibilityMessage(ExamPeriod period) {
        boolean hasProcessingPayments = paymentHistory.stream()
            .anyMatch(p -> p.getStatus() != null && p.getStatus().isInProgress());
        
        double amountDue = getExamPeriodDue(period);
        
        if (amountDue <= 0) {
            if (hasProcessingPayments) {
                return "Payment processing - Eligibility pending";
            } else {
                return "✓ Eligible to take " + period.toString().toLowerCase() + " examination";
            }
        } else {
            return "✗ Payment required (P " + String.format("%,.2f", amountDue) + ") to take " + period.toString().toLowerCase() + " examination";
        }
    }
    
    /**
     * Applies a scholarship discount
     */
    public void applyScholarship(double discountPercentage, String scholarshipName) {
        // Remove existing scholarship if any
        feeBreakdowns.removeIf(fee -> fee.getType() == FeeBreakdown.FeeType.DISCOUNT);
        
        // Calculate discount amount
        double discountAmount = totalTuition * (discountPercentage / 100.0);
        
        // Add scholarship as negative fee
        FeeBreakdown scholarship = new FeeBreakdown(
            "SCHOLARSHIP",
            scholarshipName,
            -discountAmount,
            FeeBreakdown.FeeType.DISCOUNT,
            LocalDate.now()
        );
        
        addFee(scholarship);
    }
    
    /**
     * Gets a summary of the account statement
     */
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("studentID", studentID);
        summary.put("semester", semester);
        summary.put("academicYear", academicYear);
        summary.put("totalTuition", totalTuition);
        summary.put("totalFees", totalFees);
        summary.put("totalAmount", totalAmount);
        summary.put("amountPaid", amountPaid);
        summary.put("balance", balance);
        summary.put("overpayment", overpayment);
        summary.put("isPrelimPaid", isPrelimPaid);
        summary.put("isMidtermPaid", isMidtermPaid);
        summary.put("isFinalsPaid", isFinalsPaid);
        summary.put("lastUpdate", LocalDate.now().toString()); // Always use current date
        summary.put("feeCount", feeBreakdowns.size());
        summary.put("paymentCount", paymentHistory.size());
        summary.put("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        return summary;
    }
    
    // Getters
    public String getStudentID() { return studentID; }
    public String getSemester() { return semester; }
    public String getAcademicYear() { return academicYear; }
    public double getTotalTuition() { return totalTuition; }
    public double getTotalFees() { return totalFees; }
    public double getTotalAmount() { return totalAmount; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalance() { return balance; }
    public double getOverpayment() { return overpayment; }
    public List<FeeBreakdown> getFeeBreakdowns() { return new ArrayList<>(feeBreakdowns); }
    public List<PaymentTransaction> getPaymentHistory() { return new ArrayList<>(paymentHistory); }
    public boolean isPrelimPaid() { return isPrelimPaid; }
    public boolean isMidtermPaid() { return isMidtermPaid; }
    public boolean isFinalsPaid() { return isFinalsPaid; }
    public LocalDate getLastUpdateDate() { return lastUpdateDate; }
    
    // Enums
    public enum ExamPeriod {
        PRELIM, MIDTERM, FINALS
    }
    
    // Inner class for payment results
    public static class PaymentResult {
        public boolean success;
        public String message;
        public PaymentTransaction transaction;
        public double newBalance;
        public double newOverpayment;
    }
}