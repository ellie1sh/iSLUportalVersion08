# UML Diagrams for ISLU Student Portal System

## Overview
This document contains comprehensive UML diagrams for the enhanced ISLU Student Portal System, showing the system architecture, class relationships, and behavioral patterns.

## 1. Class Diagram

```mermaid
classDiagram
    %% Core Application Classes
    class ISLUStudentPortal {
        -mainPanel: JPanel
        -contentPanel: JPanel
        -sidebarPanel: JPanel
        -studentID: String
        -studentName: String
        -menu: EnhancedDoublyLinkedList~MenuItem~
        +ISLUStudentPortal(studentID: String)
        +initializeComponents(): void
        +setupLayout(sublist: EnhancedDoublyLinkedList~MenuItem~): void
        +loadAnnouncements(): void
        +startDatabaseMonitoring(): void
    }

    class Login {
        -usernameField: JTextField
        -passwordField: JPasswordField
        +Login()
        +authenticateUser(): boolean
        +showPortal(studentID: String): void
    }

    class reqAcc {
        -registrationForm: JPanel
        -studentInfoFields: Map~String, JTextField~
        +reqAcc()
        +validateInput(): ValidationResult
        +createAccount(): boolean
    }

    %% Enhanced Data Structures
    class EnhancedDoublyLinkedList~T~ {
        -head: DoublyLinkedNode~T~
        -tail: DoublyLinkedNode~T~
        -size: int
        -isSorted: boolean
        -lastUsedComparator: Comparator~T~
        +add(data: T): void
        +get(index: int): T
        +sort(comparator: Comparator~T~): void
        +binarySearch(key: T, comparator: Comparator~T~): int
        +reverse(): void
        +iterator(): Iterator~T~
        +reverseIterator(): Iterator~T~
    }

    class EnhancedSinglyLinkedList~T~ {
        -head: Node~T~
        -tail: Node~T~
        -size: int
        -isSorted: boolean
        +add(data: T): void
        +addFirst(data: T): void
        +addLast(data: T): void
        +get(index: int): T
        +sort(comparator: Comparator~T~): void
        +hasCycle(): boolean
        +reverse(): void
        +getMiddleElement(): T
    }

    class DoublyLinkedNode~T~ {
        -data: T
        -next: DoublyLinkedNode~T~
        -prev: DoublyLinkedNode~T~
        +DoublyLinkedNode(data: T)
        +getData(): T
        +setData(data: T): void
        +getNext(): DoublyLinkedNode~T~
        +getPrev(): DoublyLinkedNode~T~
    }

    class Node~T~ {
        -data: T
        -next: Node~T~
        +Node(data: T)
        +getData(): T
        +setData(data: T): void
        +getNext(): Node~T~
        +setNext(next: Node~T~): void
    }

    %% Data Management Layer
    class OptimizedDataManager {
        -studentCache: Map~String, StudentInfo~
        -attendanceCache: Map~String, List~AttendanceRecord~~
        -gradeCache: Map~String, List~GradeRecord~~
        -paymentCache: Map~String, List~PaymentTransaction~~
        +authenticateUser(studentID: String, password: String): boolean
        +getStudentInfo(studentID: String): StudentInfo
        +getAttendanceRecords(studentID: String): List~AttendanceRecord~
        +getGradeRecords(studentID: String): List~GradeRecord~
        +saveStudentAccount(studentInfo: StudentInfo): boolean
        +generateUniqueStudentID(): String
        +clearCaches(): void
    }

    class FileIOManager {
        -BUFFER_SIZE: int
        -lock: ReadWriteLock
        +readAllLines(file: File): List~String~
        +writeAllLines(file: File, lines: List~String~): void
        +appendLines(file: File, lines: List~String~): void
        +updateLines(file: File, updater: LineUpdater): boolean
        +batchUpdate(fileUpdates: Map~File, List~String~~): void
        +searchInFile(file: File, pattern: String, caseSensitive: boolean): List~SearchResult~
        +getFileStats(file: File): FileStats
        +createBackup(file: File): void
        +restoreFromBackup(file: File): boolean
    }

    class DataValidator {
        -STUDENT_ID_PATTERN: Pattern
        -NAME_PATTERN: Pattern
        -EMAIL_PATTERN: Pattern
        -PHONE_PATTERN: Pattern
        +validateStudentID(studentID: String): ValidationResult
        +validateName(name: String, fieldName: String): ValidationResult
        +validateDate(dateString: String, fieldName: String): ValidationResult
        +validateEmail(email: String): ValidationResult
        +validateGrade(grade: Double, fieldName: String): ValidationResult
        +validateStudentInfo(studentInfo: StudentInfo): ValidationResult
        +batchValidate(items: List~T~, validator: Validator~T~): List~ValidationResult~
    }

    %% Data Models
    class StudentInfo {
        -id: String
        -lastName: String
        -firstName: String
        -middleName: String
        -dateOfBirth: String
        -password: String
        +StudentInfo(id: String, lastName: String, firstName: String, middleName: String, dateOfBirth: String, password: String)
        +getId(): String
        +getFullName(): String
        +toDatabaseFormat(): String
    }

    class AttendanceRecord {
        -studentID: String
        -subjectCode: String
        -subjectName: String
        -date: LocalDate
        -status: String
        -remarks: String
        +AttendanceRecord(studentID: String, subjectCode: String, subjectName: String, date: LocalDate, status: String, remarks: String)
        +toCsvFormat(): String
        +fromCsvFormat(csvLine: String): AttendanceRecord
    }

    class GradeRecord {
        -studentID: String
        -subjectCode: String
        -subjectName: String
        -prelimGrade: Double
        -midtermGrade: Double
        -tentativeFinalGrade: Double
        -finalGrade: Double
        -semester: String
        -status: String
        +GradeRecord(...)
        +calculateOverallGrade(): Double
        +toCsvFormat(): String
        +fromCsvFormat(csvLine: String): GradeRecord
        +toTableRow(): Object[]
    }

    class PaymentTransaction {
        -date: String
        -channel: String
        -reference: String
        -amount: String
        +PaymentTransaction(date: String, channel: String, reference: String, amount: String)
        +getFormattedAmount(): String
    }

    class MenuItem {
        -text: String
        -icon: String
        -action: String
        +MenuItem(text: String, icon: String, action: String)
        +getText(): String
        +getIcon(): String
        +getAction(): String
    }

    %% Validation Classes
    class ValidationResult {
        -valid: boolean
        -errors: List~String~
        -warnings: List~String~
        +ValidationResult(valid: boolean)
        +isValid(): boolean
        +getErrors(): List~String~
        +getWarnings(): List~String~
        +addError(error: String): void
        +addWarning(warning: String): void
    }

    %% File I/O Helper Classes
    class SearchResult {
        -lineNumber: int
        -line: String
        +SearchResult(lineNumber: int, line: String)
        +getLineNumber(): int
        +getLine(): String
    }

    class FileStats {
        -sizeInBytes: long
        -lineCount: int
        -wordCount: int
        +FileStats(sizeInBytes: long, lineCount: int, wordCount: int)
        +getSizeInBytes(): long
        +getLineCount(): int
        +getWordCount(): int
    }

    %% Relationships
    ISLUStudentPortal --> EnhancedDoublyLinkedList : uses
    ISLUStudentPortal --> OptimizedDataManager : uses
    ISLUStudentPortal --> DataValidator : uses
    
    Login --> OptimizedDataManager : authenticates
    Login --> DataValidator : validates
    Login --> ISLUStudentPortal : creates
    
    reqAcc --> OptimizedDataManager : saves
    reqAcc --> DataValidator : validates
    reqAcc --> StudentInfo : creates
    
    OptimizedDataManager --> FileIOManager : uses
    OptimizedDataManager --> StudentInfo : manages
    OptimizedDataManager --> AttendanceRecord : manages
    OptimizedDataManager --> GradeRecord : manages
    OptimizedDataManager --> PaymentTransaction : manages
    
    DataValidator --> ValidationResult : returns
    DataValidator --> StudentInfo : validates
    DataValidator --> AttendanceRecord : validates
    DataValidator --> GradeRecord : validates
    
    FileIOManager --> SearchResult : returns
    FileIOManager --> FileStats : returns
    
    EnhancedDoublyLinkedList --> DoublyLinkedNode : contains
    EnhancedSinglyLinkedList --> Node : contains
    
    EnhancedDoublyLinkedList --> MenuItem : stores
```

## 2. Use Case Diagram

```mermaid
graph TB
    Student((Student))
    Admin((Administrator))
    System((System))
    
    subgraph "Student Portal System"
        UC1[Login to System]
        UC2[View Profile]
        UC3[Update Profile]
        UC4[View Grades]
        UC5[View Attendance]
        UC6[View Schedule]
        UC7[Make Payment]
        UC8[View Payment History]
        UC9[Register Account]
        UC10[Change Password]
        
        UC11[Manage Students]
        UC12[Generate Reports]
        UC13[Backup Data]
        UC14[Restore Data]
        UC15[Monitor System]
    end
    
    Student --> UC1
    Student --> UC2
    Student --> UC3
    Student --> UC4
    Student --> UC5
    Student --> UC6
    Student --> UC7
    Student --> UC8
    Student --> UC9
    Student --> UC10
    
    Admin --> UC11
    Admin --> UC12
    Admin --> UC13
    Admin --> UC14
    Admin --> UC15
    
    UC1 --> System
    UC2 --> System
    UC3 --> System
    UC4 --> System
    UC5 --> System
    UC6 --> System
    UC7 --> System
    UC8 --> System
    UC9 --> System
    UC10 --> System
    UC11 --> System
    UC12 --> System
    UC13 --> System
    UC14 --> System
    UC15 --> System
```

## 3. Sequence Diagram - Student Login Process

```mermaid
sequenceDiagram
    participant U as User
    participant L as Login
    participant DV as DataValidator
    participant ODM as OptimizedDataManager
    participant FIM as FileIOManager
    participant P as ISLUStudentPortal
    
    U->>L: Enter credentials
    L->>DV: validateStudentID(studentID)
    DV-->>L: ValidationResult
    
    alt Valid Student ID
        L->>ODM: authenticateUser(studentID, password)
        ODM->>FIM: readAllLines(Database.txt)
        FIM-->>ODM: List<String>
        ODM-->>L: boolean (authenticated)
        
        alt Authentication Success
            L->>P: new ISLUStudentPortal(studentID)
            P->>ODM: getStudentInfo(studentID)
            ODM-->>P: StudentInfo
            P->>P: initializeComponents()
            P->>P: setupLayout()
            P-->>U: Show Portal Dashboard
        else Authentication Failed
            L-->>U: Show error message
        end
    else Invalid Student ID
        L-->>U: Show validation error
    end
```

## 4. Sequence Diagram - Data Retrieval with Caching

```mermaid
sequenceDiagram
    participant P as Portal
    participant ODM as OptimizedDataManager
    participant Cache as StudentCache
    participant FIM as FileIOManager
    participant DB as Database.txt
    
    P->>ODM: getStudentInfo(studentID)
    ODM->>Cache: get(studentID)
    
    alt Cache Hit
        Cache-->>ODM: StudentInfo
        ODM-->>P: StudentInfo
    else Cache Miss
        ODM->>FIM: readAllLines(Database.txt)
        FIM->>DB: Read file
        DB-->>FIM: File contents
        FIM-->>ODM: List<String>
        ODM->>ODM: parseStudentData()
        ODM->>Cache: put(studentID, studentInfo)
        ODM-->>P: StudentInfo
    end
```

## 5. Component Diagram

```mermaid
graph TB
    subgraph "Presentation Layer"
        GUI[GUI Components]
        Login[Login Module]
        Portal[Portal Module]
        Registration[Registration Module]
    end
    
    subgraph "Business Logic Layer"
        DataManager[Optimized Data Manager]
        Validator[Data Validator]
        Utilities[Portal Utilities]
    end
    
    subgraph "Data Access Layer"
        FileManager[File I/O Manager]
        Cache[Caching System]
    end
    
    subgraph "Data Structures Layer"
        EnhancedDLL[Enhanced Doubly Linked List]
        EnhancedSLL[Enhanced Singly Linked List]
        DataModels[Data Models]
    end
    
    subgraph "Data Storage Layer"
        DatabaseTxt[(Database.txt)]
        AttendanceTxt[(attendanceRecords.txt)]
        GradesTxt[(gradeRecords.txt)]
        SchedulesTxt[(courseSchedules.txt)]
        PaymentsTxt[(paymentLogs.txt)]
    end
    
    GUI --> DataManager
    Login --> DataManager
    Portal --> DataManager
    Registration --> DataManager
    
    DataManager --> Validator
    DataManager --> FileManager
    DataManager --> Cache
    DataManager --> EnhancedDLL
    DataManager --> DataModels
    
    FileManager --> DatabaseTxt
    FileManager --> AttendanceTxt
    FileManager --> GradesTxt
    FileManager --> SchedulesTxt
    FileManager --> PaymentsTxt
    
    Cache --> DataModels
    EnhancedDLL --> DataModels
    EnhancedSLL --> DataModels
```

## 6. Activity Diagram - Student Registration Process

```mermaid
flowchart TD
    Start([Start Registration]) --> Input[Enter Student Information]
    Input --> Validate{Validate Input}
    
    Validate -->|Invalid| ShowError[Show Validation Errors]
    ShowError --> Input
    
    Validate -->|Valid| CheckDuplicate{Check Duplicate ID}
    CheckDuplicate -->|Exists| ShowDuplicateError[Show Duplicate ID Error]
    ShowDuplicateError --> GenerateID[Generate New Unique ID]
    GenerateID --> CreateAccount[Create Student Account]
    
    CheckDuplicate -->|Not Exists| CreateAccount
    CreateAccount --> SaveToDatabase[Save to Database]
    SaveToDatabase --> UpdateCache[Update Cache]
    UpdateCache --> SendConfirmation[Send Confirmation]
    SendConfirmation --> End([Registration Complete])
```

## 7. Activity Diagram - Grade Retrieval Process

```mermaid
flowchart TD
    Start([Request Grades]) --> CheckCache{Check Cache}
    
    CheckCache -->|Hit| ReturnCached[Return Cached Grades]
    ReturnCached --> Display[Display Grades]
    Display --> End([End])
    
    CheckCache -->|Miss| ReadFile[Read Grade Records File]
    ReadFile --> ParseData[Parse Grade Data]
    ParseData --> FilterByStudent[Filter by Student ID]
    FilterByStudent --> ValidateGrades[Validate Grade Records]
    ValidateGrades --> UpdateCache[Update Cache]
    UpdateCache --> Display
```

## 8. State Diagram - Student Portal Session

```mermaid
stateDiagram-v2
    [*] --> LoginScreen
    
    LoginScreen --> Authenticating: Enter Credentials
    Authenticating --> LoginScreen: Invalid Credentials
    Authenticating --> Dashboard: Valid Credentials
    
    Dashboard --> ViewingGrades: Select Grades
    Dashboard --> ViewingAttendance: Select Attendance
    Dashboard --> ViewingSchedule: Select Schedule
    Dashboard --> ViewingProfile: Select Profile
    Dashboard --> MakingPayment: Select Payment
    
    ViewingGrades --> Dashboard: Back
    ViewingAttendance --> Dashboard: Back
    ViewingSchedule --> Dashboard: Back
    ViewingProfile --> EditingProfile: Edit Profile
    ViewingProfile --> Dashboard: Back
    
    EditingProfile --> ViewingProfile: Save Changes
    EditingProfile --> ViewingProfile: Cancel
    
    MakingPayment --> PaymentProcessing: Submit Payment
    PaymentProcessing --> PaymentComplete: Success
    PaymentProcessing --> MakingPayment: Failure
    PaymentComplete --> Dashboard: Continue
    
    Dashboard --> [*]: Logout
```

## 9. Deployment Diagram

```mermaid
graph TB
    subgraph "Client Machine"
        ClientJVM[Java Virtual Machine]
        subgraph "Application"
            GUI[GUI Application]
            BusinessLogic[Business Logic]
            DataStructures[Data Structures]
        end
    end
    
    subgraph "File System"
        DatabaseFiles[(Database Files)]
        BackupFiles[(Backup Files)]
        ConfigFiles[(Configuration Files)]
    end
    
    ClientJVM --> GUI
    GUI --> BusinessLogic
    BusinessLogic --> DataStructures
    BusinessLogic --> DatabaseFiles
    BusinessLogic --> BackupFiles
    BusinessLogic --> ConfigFiles
```

## 10. Package Diagram

```mermaid
graph TB
    subgraph "com.islu.portal"
        subgraph "core"
            ISLUPortal[ISLUStudentPortal]
            LoginClass[Login]
            Registration[reqAcc]
        end
        
        subgraph "data"
            DataManager[OptimizedDataManager]
            FileManager[FileIOManager]
            Models[Data Models]
        end
        
        subgraph "structures"
            EnhancedDLL[EnhancedDoublyLinkedList]
            EnhancedSLL[EnhancedSinglyLinkedList]
            Nodes[Node Classes]
        end
        
        subgraph "validation"
            Validator[DataValidator]
            ValidationResult[ValidationResult]
        end
        
        subgraph "utils"
            PortalUtils[PortalUtils]
            PortalSession[PortalSession]
        end
    end
    
    core --> data
    core --> structures
    core --> validation
    core --> utils
    
    data --> structures
    data --> validation
    validation --> Models
```

## Summary

These UML diagrams provide a comprehensive view of the enhanced ISLU Student Portal System:

1. **Class Diagram**: Shows all classes and their relationships
2. **Use Case Diagram**: Illustrates system functionality from user perspective
3. **Sequence Diagrams**: Detail interaction flows for key processes
4. **Component Diagram**: Shows system architecture and dependencies
5. **Activity Diagrams**: Model business processes and workflows
6. **State Diagram**: Shows system states and transitions
7. **Deployment Diagram**: Illustrates system deployment structure
8. **Package Diagram**: Shows code organization and dependencies

These diagrams help visualize:
- **System Architecture**: Clear separation of concerns
- **Data Flow**: How information moves through the system
- **Performance Optimizations**: Caching and efficient data structures
- **Error Handling**: Validation and exception management
- **User Interactions**: Complete user journey mapping