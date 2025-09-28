import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

/**
 * Comprehensive data validation utility for the Student Portal system
 * Features:
 * - Student ID validation with format checking
 * - Name validation with proper formatting
 * - Date validation with multiple format support
 * - Email and phone number validation
 * - Grade validation with range checking
 * - Password strength validation
 * - File format validation
 * - Batch validation for multiple records
 */
public class DataValidator {
    
    // Validation patterns
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^225\\d{4}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s\\-\\.]{1,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s\\(\\)]{7,20}$");
    private static final Pattern SUBJECT_CODE_PATTERN = Pattern.compile("^[A-Z]{2,6}\\d{2,3}[A-Z]?$");
    
    // Date formatters
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM-dd-yyyy")
    };
    
    // Grade ranges
    private static final double MIN_GRADE = 60.0;
    private static final double MAX_GRADE = 100.0;
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        
        public ValidationResult(boolean valid) {
            this.valid = valid;
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
        }
        
        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
        
        public void addError(String error) { errors.add(error); }
        public void addWarning(String warning) { warnings.add(warning); }
        
        public boolean hasErrors() { return !errors.isEmpty(); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Valid: ").append(valid);
            if (hasErrors()) {
                sb.append("\nErrors: ").append(String.join(", ", errors));
            }
            if (hasWarnings()) {
                sb.append("\nWarnings: ").append(String.join(", ", warnings));
            }
            return sb.toString();
        }
    }
    
    /**
     * Validate student ID format
     */
    public static ValidationResult validateStudentID(String studentID) {
        ValidationResult result = new ValidationResult(true);
        
        if (studentID == null || studentID.trim().isEmpty()) {
            result.addError("Student ID cannot be null or empty");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        String trimmedID = studentID.trim();
        
        if (!STUDENT_ID_PATTERN.matcher(trimmedID).matches()) {
            result.addError("Student ID must be in format '225XXXX' where X is a digit");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        return result;
    }
    
    /**
     * Validate name fields (first, middle, last name)
     */
    public static ValidationResult validateName(String name, String fieldName) {
        ValidationResult result = new ValidationResult(true);
        
        if (name == null || name.trim().isEmpty()) {
            result.addError(fieldName + " cannot be null or empty");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        String trimmedName = name.trim();
        
        if (trimmedName.length() < 2) {
            result.addError(fieldName + " must be at least 2 characters long");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            result.addError(fieldName + " contains invalid characters or is too long (max 50 chars)");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        // Check for proper capitalization
        if (!isProperlyCapitalized(trimmedName)) {
            result.addWarning(fieldName + " should be properly capitalized");
        }
        
        return result;
    }
    
    /**
     * Validate date with multiple format support
     */
    public static ValidationResult validateDate(String dateString, String fieldName) {
        ValidationResult result = new ValidationResult(true);
        
        if (dateString == null || dateString.trim().isEmpty()) {
            result.addError(fieldName + " cannot be null or empty");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        String trimmedDate = dateString.trim();
        LocalDate parsedDate = null;
        
        // Try different date formats
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                parsedDate = LocalDate.parse(trimmedDate, formatter);
                break;
            } catch (DateTimeParseException e) {
                // Continue trying other formats
            }
        }
        
        if (parsedDate == null) {
            result.addError(fieldName + " has invalid date format. Expected formats: MM/dd/yyyy, yyyy-MM-dd, dd/MM/yyyy, MM-dd-yyyy");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        // Check if date is reasonable (not too far in past or future)
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(150); // 150 years ago
        LocalDate maxDate = now.plusYears(50);   // 50 years in future
        
        if (parsedDate.isBefore(minDate)) {
            result.addError(fieldName + " is too far in the past");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        if (parsedDate.isAfter(maxDate)) {
            result.addError(fieldName + " is too far in the future");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        // Warning for birth dates that seem unusual
        if (fieldName.toLowerCase().contains("birth")) {
            LocalDate minBirthDate = now.minusYears(100);
            LocalDate maxBirthDate = now.minusYears(10);
            
            if (parsedDate.isBefore(minBirthDate)) {
                result.addWarning("Birth date seems very old");
            } else if (parsedDate.isAfter(maxBirthDate)) {
                result.addWarning("Birth date seems very recent");
            }
        }
        
        return result;
    }
    
    /**
     * Validate email address
     */
    public static ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult(true);
        
        if (email == null || email.trim().isEmpty()) {
            result.addError("Email cannot be null or empty");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            result.addError("Email format is invalid");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        return result;
    }
    
    /**
     * Validate phone number
     */
    public static ValidationResult validatePhoneNumber(String phone) {
        ValidationResult result = new ValidationResult(true);
        
        if (phone == null || phone.trim().isEmpty()) {
            result.addError("Phone number cannot be null or empty");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        String trimmedPhone = phone.trim();
        
        if (!PHONE_PATTERN.matcher(trimmedPhone).matches()) {
            result.addError("Phone number format is invalid");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        return result;
    }
    
    /**
     * Validate grade value
     */
    public static ValidationResult validateGrade(Double grade, String fieldName) {
        ValidationResult result = new ValidationResult(true);
        
        if (grade == null) {
            // Null grades are allowed (not yet assigned)
            return result;
        }
        
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            result.addError(fieldName + " must be between " + MIN_GRADE + " and " + MAX_GRADE);
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        // Warning for very low grades
        if (grade < 70.0) {
            result.addWarning(fieldName + " is quite low");
        }
        
        return result;
    }
    
    /**
     * Validate subject code
     */
    public static ValidationResult validateSubjectCode(String subjectCode) {
        ValidationResult result = new ValidationResult(true);
        
        if (subjectCode == null || subjectCode.trim().isEmpty()) {
            result.addError("Subject code cannot be null or empty");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        String trimmedCode = subjectCode.trim().toUpperCase();
        
        if (!SUBJECT_CODE_PATTERN.matcher(trimmedCode).matches()) {
            result.addError("Subject code format is invalid. Expected format: 2-6 letters followed by 2-3 digits, optionally followed by a letter");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        return result;
    }
    
    /**
     * Validate password strength
     */
    public static ValidationResult validatePassword(String password) {
        ValidationResult result = new ValidationResult(true);
        
        if (password == null || password.isEmpty()) {
            result.addError("Password cannot be null or empty");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        if (password.length() < 6) {
            result.addError("Password must be at least 6 characters long");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        if (password.length() > 50) {
            result.addError("Password must be at most 50 characters long");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        // Check for common weak passwords
        String lowerPassword = password.toLowerCase();
        String[] weakPasswords = {"password", "123456", "qwerty", "abc123", "password123"};
        for (String weak : weakPasswords) {
            if (lowerPassword.contains(weak)) {
                result.addWarning("Password contains common weak patterns");
                break;
            }
        }
        
        // Check password strength
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        
        if (!hasLetter) {
            result.addWarning("Password should contain at least one letter");
        }
        if (!hasDigit) {
            result.addWarning("Password should contain at least one digit");
        }
        if (!hasSpecial && password.length() < 8) {
            result.addWarning("Short passwords should contain special characters");
        }
        
        return result;
    }
    
    /**
     * Validate student information comprehensively
     */
    public static ValidationResult validateStudentInfo(StudentInfo studentInfo) {
        ValidationResult result = new ValidationResult(true);
        
        if (studentInfo == null) {
            result.addError("Student information cannot be null");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        // Validate each field
        ValidationResult idResult = validateStudentID(studentInfo.getId());
        ValidationResult firstNameResult = validateName(studentInfo.getFirstName(), "First name");
        ValidationResult lastNameResult = validateName(studentInfo.getLastName(), "Last name");
        ValidationResult middleNameResult = validateName(studentInfo.getMiddleName(), "Middle name");
        ValidationResult dobResult = validateDate(studentInfo.getDateOfBirth(), "Date of birth");
        ValidationResult passwordResult = validatePassword(studentInfo.getPassword());
        
        // Combine results
        if (!idResult.isValid()) {
            result.getErrors().addAll(idResult.getErrors());
        }
        if (!firstNameResult.isValid()) {
            result.getErrors().addAll(firstNameResult.getErrors());
        }
        if (!lastNameResult.isValid()) {
            result.getErrors().addAll(lastNameResult.getErrors());
        }
        if (!middleNameResult.isValid()) {
            result.getErrors().addAll(middleNameResult.getErrors());
        }
        if (!dobResult.isValid()) {
            result.getErrors().addAll(dobResult.getErrors());
        }
        if (!passwordResult.isValid()) {
            result.getErrors().addAll(passwordResult.getErrors());
        }
        
        // Collect warnings
        result.getWarnings().addAll(idResult.getWarnings());
        result.getWarnings().addAll(firstNameResult.getWarnings());
        result.getWarnings().addAll(lastNameResult.getWarnings());
        result.getWarnings().addAll(middleNameResult.getWarnings());
        result.getWarnings().addAll(dobResult.getWarnings());
        result.getWarnings().addAll(passwordResult.getWarnings());
        
        boolean isValid = result.getErrors().isEmpty();
        return new ValidationResult(isValid, result.getErrors(), result.getWarnings());
    }
    
    /**
     * Validate attendance record
     */
    public static ValidationResult validateAttendanceRecord(AttendanceRecord record) {
        ValidationResult result = new ValidationResult(true);
        
        if (record == null) {
            result.addError("Attendance record cannot be null");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        ValidationResult idResult = validateStudentID(record.getStudentID());
        ValidationResult subjectResult = validateSubjectCode(record.getSubjectCode());
        
        if (!idResult.isValid()) {
            result.getErrors().addAll(idResult.getErrors());
        }
        if (!subjectResult.isValid()) {
            result.getErrors().addAll(subjectResult.getErrors());
        }
        
        // Validate status
        String status = record.getStatus();
        if (status == null || status.trim().isEmpty()) {
            result.addError("Attendance status cannot be null or empty");
        } else {
            String[] validStatuses = {"Present", "Absent", "Late", "Excused"};
            boolean validStatus = false;
            for (String valid : validStatuses) {
                if (valid.equalsIgnoreCase(status.trim())) {
                    validStatus = true;
                    break;
                }
            }
            if (!validStatus) {
                result.addError("Invalid attendance status: " + status);
            }
        }
        
        boolean isValid = result.getErrors().isEmpty();
        return new ValidationResult(isValid, result.getErrors(), result.getWarnings());
    }
    
    /**
     * Validate grade record
     */
    public static ValidationResult validateGradeRecord(GradeRecord record) {
        ValidationResult result = new ValidationResult(true);
        
        if (record == null) {
            result.addError("Grade record cannot be null");
            return new ValidationResult(false, result.getErrors(), result.getWarnings());
        }
        
        ValidationResult idResult = validateStudentID(record.getStudentID());
        ValidationResult subjectResult = validateSubjectCode(record.getSubjectCode());
        ValidationResult prelimResult = validateGrade(record.getPrelimGrade(), "Prelim grade");
        ValidationResult midtermResult = validateGrade(record.getMidtermGrade(), "Midterm grade");
        ValidationResult tentativeResult = validateGrade(record.getTentativeFinalGrade(), "Tentative final grade");
        ValidationResult finalResult = validateGrade(record.getFinalGrade(), "Final grade");
        
        // Combine results
        if (!idResult.isValid()) result.getErrors().addAll(idResult.getErrors());
        if (!subjectResult.isValid()) result.getErrors().addAll(subjectResult.getErrors());
        if (!prelimResult.isValid()) result.getErrors().addAll(prelimResult.getErrors());
        if (!midtermResult.isValid()) result.getErrors().addAll(midtermResult.getErrors());
        if (!tentativeResult.isValid()) result.getErrors().addAll(tentativeResult.getErrors());
        if (!finalResult.isValid()) result.getErrors().addAll(finalResult.getErrors());
        
        // Collect warnings
        result.getWarnings().addAll(prelimResult.getWarnings());
        result.getWarnings().addAll(midtermResult.getWarnings());
        result.getWarnings().addAll(tentativeResult.getWarnings());
        result.getWarnings().addAll(finalResult.getWarnings());
        
        boolean isValid = result.getErrors().isEmpty();
        return new ValidationResult(isValid, result.getErrors(), result.getWarnings());
    }
    
    /**
     * Batch validation for multiple records
     */
    public static <T> List<ValidationResult> batchValidate(List<T> items, Validator<T> validator) {
        List<ValidationResult> results = new ArrayList<>();
        for (T item : items) {
            results.add(validator.validate(item));
        }
        return results;
    }
    
    /**
     * Check if name is properly capitalized
     */
    private static boolean isProperlyCapitalized(String name) {
        String[] words = name.split("\\s+");
        for (String word : words) {
            if (word.length() > 0) {
                char firstChar = word.charAt(0);
                if (!Character.isUpperCase(firstChar)) {
                    return false;
                }
                // Check if rest of the word is lowercase (except for special cases like McDonald)
                for (int i = 1; i < word.length(); i++) {
                    char c = word.charAt(i);
                    if (Character.isUpperCase(c) && i > 0 && word.charAt(i-1) != '-' && word.charAt(i-1) != '\'') {
                        // Allow for names like McDonald, O'Connor
                        continue;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Generic validator interface
     */
    @FunctionalInterface
    public interface Validator<T> {
        ValidationResult validate(T item);
    }
}