/**
 * Enum representing different payment statuses
 */
public enum PaymentStatus {
    PENDING("Pending", "Payment awaiting processing"),
    PROCESSING("Processing", "Payment is being processed"),
    FOR_POSTING("For Posting", "Payment awaiting cashier posting"),
    POSTED("Posted", "Payment has been posted"),
    COMPLETED("Completed", "Payment fully processed"),
    FAILED("Failed", "Payment failed"),
    CANCELLED("Cancelled", "Payment cancelled"),
    REFUNDED("Refunded", "Payment refunded");
    
    private final String displayName;
    private final String description;
    
    PaymentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Check if the payment is considered successful
     */
    public boolean isSuccessful() {
        return this == POSTED || this == COMPLETED;
    }
    
    /**
     * Check if the payment is in progress
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING || this == FOR_POSTING;
    }
}