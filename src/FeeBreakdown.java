import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single fee or charge in the student's account
 */
public class FeeBreakdown {
    private String code;
    private String description;
    private double amount;
    private double amountPaid;
    private double balance;
    private FeeType type;
    private LocalDate datePosted;
    private boolean isPaid;
    private boolean isPartiallyPaid;
    private PaymentStatus paymentStatus;
    private String remarks;
    
    // Constructor
    public FeeBreakdown(String code, String description, double amount, FeeType type, LocalDate datePosted) {
        this.code = code;
        this.description = description;
        this.amount = amount;
        this.amountPaid = 0.0;
        this.balance = amount;
        this.type = type;
        this.datePosted = datePosted;
        this.isPaid = false;
        this.isPartiallyPaid = false;
        this.paymentStatus = null;
        this.remarks = "";
    }
    
    // Constructor with remarks
    public FeeBreakdown(String code, String description, double amount, FeeType type, LocalDate datePosted, String remarks) {
        this(code, description, amount, type, datePosted);
        this.remarks = remarks;
    }
    
    /**
     * Formats the fee for display in a table
     */
    public Object[] toTableRow() {
        String dateStr = datePosted != null ? 
            datePosted.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "";
        String amountStr = amount >= 0 ? 
            String.format("P %,.2f", amount) : 
            String.format("(P %,.2f)", Math.abs(amount));
        
        return new Object[]{dateStr, description, amountStr};
    }
    
    /**
     * Formats the fee for display in a table with payment status
     */
    public Object[] toTableRowWithStatus() {
        String dateStr = datePosted != null ? 
            datePosted.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "";
        
        String descriptionWithStatus = description;
        if (paymentStatus != null && paymentStatus.isInProgress()) {
            descriptionWithStatus += " [" + paymentStatus.getDisplayName() + "]";
        } else if (isPaid) {
            descriptionWithStatus += " [PAID]";
        } else if (isPartiallyPaid) {
            descriptionWithStatus += " [PARTIAL]";
        }
        
        String amountStr;
        if (isPaid) {
            amountStr = "P 0.00";
        } else if (isPartiallyPaid) {
            amountStr = String.format("P %,.2f", balance);
        } else {
            amountStr = amount >= 0 ? 
                String.format("P %,.2f", amount) : 
                String.format("(P %,.2f)", Math.abs(amount));
        }
        
        String statusStr = "";
        if (paymentStatus != null) {
            statusStr = paymentStatus.getDisplayName();
        } else if (isPaid) {
            statusStr = "Paid";
        } else if (isPartiallyPaid) {
            statusStr = "Partial";
        } else {
            statusStr = "Unpaid";
        }
        
        return new Object[]{dateStr, descriptionWithStatus, amountStr, statusStr};
    }
    
    /**
     * Apply payment to this fee
     */
    public void applyPayment(double paymentAmount) {
        if (paymentAmount <= 0) return;
        
        amountPaid += paymentAmount;
        balance = amount - amountPaid;
        
        if (balance <= 0) {
            balance = 0;
            isPaid = true;
            isPartiallyPaid = false;
        } else {
            isPartiallyPaid = true;
            isPaid = false;
        }
    }
    
    /**
     * Returns a formatted string representation of the fee
     */
    public String toFormattedString() {
        String dateStr = datePosted != null ? 
            datePosted.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "";
        String amountStr = amount >= 0 ? 
            String.format("P %,.2f", amount) : 
            String.format("(P %,.2f)", Math.abs(amount));
        
        return String.format("%s | %s | %s | %s", 
            code, dateStr, description, amountStr);
    }
    
    /**
     * Creates a fee from a CSV string
     */
    public static FeeBreakdown fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 5) {
            return null;
        }
        
        try {
            String code = parts[0].trim();
            String description = parts[1].trim();
            double amount = Double.parseDouble(parts[2].trim());
            FeeType type = FeeType.valueOf(parts[3].trim());
            LocalDate date = LocalDate.parse(parts[4].trim());
            String remarks = parts.length > 5 ? parts[5].trim() : "";
            
            return new FeeBreakdown(code, description, amount, type, date, remarks);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Converts the fee to CSV format for storage
     */
    public String toCSV() {
        return String.format("%s,%s,%.2f,%s,%s,%s",
            code, description, amount, type.name(), 
            datePosted.toString(), remarks);
    }
    
    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public FeeType getType() { return type; }
    public void setType(FeeType type) { this.type = type; }
    
    public LocalDate getDatePosted() { return datePosted; }
    public void setDatePosted(LocalDate datePosted) { this.datePosted = datePosted; }
    
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
    
    public boolean isPartiallyPaid() { return isPartiallyPaid; }
    public void setPartiallyPaid(boolean partiallyPaid) { isPartiallyPaid = partiallyPaid; }
    
    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    // Enum for fee types
    public enum FeeType {
        TUITION("Tuition Fee"),
        LABORATORY("Laboratory Fee"),
        MISCELLANEOUS("Miscellaneous Fee"),
        LIBRARY("Library Fee"),
        REGISTRATION("Registration Fee"),
        ATHLETIC("Athletic Fee"),
        MEDICAL("Medical/Dental Fee"),
        GUIDANCE("Guidance Fee"),
        PUBLICATION("Publication Fee"),
        INTERNET("Internet Fee"),
        ENERGY("Energy Fee"),
        INSURANCE("Insurance Fee"),
        DEVELOPMENT("Development Fee"),
        CULTURAL("Cultural Fee"),
        DISCOUNT("Discount/Scholarship"),
        PENALTY("Late Payment Penalty"),
        OTHER("Other Fees");
        
        private final String displayName;
        
        FeeType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}