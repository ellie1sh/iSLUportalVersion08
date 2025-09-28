/**
 * Data class to hold payment transaction information
 */
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentTransaction {
    private String date;
    private String channel;
    private String reference;
    private String amount;
    private LocalDateTime timestamp;
    private PaymentStatus status;
    private String paymentType; // ONLINE or ONSITE
    private String remarks;
    
    public PaymentTransaction(String date, String channel, String reference, String amount) {
        this.date = date;
        this.channel = channel;
        this.reference = reference;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = PaymentStatus.PROCESSING;
        this.paymentType = "ONLINE";
        this.remarks = "";
    }
    
    // Constructor with status
    public PaymentTransaction(String date, String channel, String reference, String amount, PaymentStatus status) {
        this(date, channel, reference, amount);
        this.status = status;
    }
    
    // Constructor that uses current date/time
    public PaymentTransaction(String channel, String reference, String amount) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        this.date = LocalDateTime.now().format(formatter);
        this.channel = channel;
        this.reference = reference;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = PaymentStatus.PROCESSING;
        this.paymentType = channel.toLowerCase().contains("cashier") || channel.toLowerCase().contains("onsite") ? "ONSITE" : "ONLINE";
        this.remarks = "";
    }
    
    // Getters
    public String getDate() { return date; }
    public String getChannel() { return channel; }
    public String getReference() { return reference; }
    public String getAmount() { return amount; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public Object[] toTableRow() {
        return new Object[]{date, channel, reference, amount, status.getDisplayName()};
    }
    
    public Object[] toTableRowWithoutStatus() {
        return new Object[]{date, channel, reference, amount};
    }
    
    /**
     * Updates status based on time elapsed (simulating processing)
     */
    public void updateStatusBasedOnTime() {
        long minutesElapsed = java.time.Duration.between(timestamp, LocalDateTime.now()).toMinutes();
        
        if (paymentType.equals("ONSITE")) {
            // Onsite payments need cashier posting
            if (minutesElapsed < 5) {
                status = PaymentStatus.FOR_POSTING;
            } else {
                status = PaymentStatus.POSTED;
            }
        } else {
            // Online payments process automatically
            if (minutesElapsed < 2) {
                status = PaymentStatus.PROCESSING;
            } else if (minutesElapsed < 5) {
                status = PaymentStatus.FOR_POSTING;
            } else {
                status = PaymentStatus.COMPLETED;
            }
        }
    }
}