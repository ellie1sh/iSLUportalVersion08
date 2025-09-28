import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.awt.Desktop;
import javax.swing.Timer;
import java.awt.print.PrinterException;
import java.util.List;

public class ISLUStudentPortal extends JFrame {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JPanel footbarPanel;
    private JLabel userNameLabel;
    private long lastDatabaseModified = 0; // Track database file modification time
    private Timer databaseCheckTimer; // Timer for checking database changes
    private JLabel semesterLabel;
    private JTextArea announcementsArea;
    private JTextArea statusArea;
    private JPanel mainCardHolder;
    private CardLayout mainCardLayout;
    private MyDoublyLinkedList<MenuItem> menu;

    // Student data
    private String studentID;
    private String studentName;
    private String semester = "FIRST SEMESTER, 2025-2026";
    private String status = "CURRENTLY ENROLLED THIS FIRST SEMESTER, 2025-2026 IN BSIT 2.";

    public ISLUStudentPortal(String studentID) {
        this.studentID = studentID;
        this.studentName = getStudentNameFromDatabase(studentID);
        
        // Initialize random amounts for each account
        // Initialize account statement for the student
        this.accountStatement = AccountStatementManager.getStatement(studentID);
        
        initializeComponents();
        setupLayout(PortalUtils.createHomeSublist());
        loadAnnouncements();
        
        // Start database monitoring
        startDatabaseMonitoring();
        
        // Add window listener to stop monitoring when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopDatabaseMonitoring();
                System.exit(0);
            }
        });
        loadStudentStatus();
    }


    /**
     * Generates random grade between 76-99 using optimized data structures
     */
    private int generateRandomGrade() {
        return 76 + (int) (Math.random() * 24); // 76 to 99
    }

    /**
     * Generates transcript data with all semesters and random grades
     */
    private Object[][] generateTranscriptData() {
        java.util.List<Object[]> data = new java.util.ArrayList<>();
        
        // FIRST SEMESTER, 2024-2025
        data.add(new Object[]{"FIRST SEMESTER, 2024-2025", "", "", ""});
        data.add(new Object[]{"CFE 101", "GOD'S JOURNEY WITH HIS PEOPLE", generateRandomGrade(), 3});
        data.add(new Object[]{"FIT HW", "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (HEALTH AND WELLNESS)", generateRandomGrade(), 2});
        data.add(new Object[]{"GART", "ART APPRECIATION", generateRandomGrade(), 3});
        data.add(new Object[]{"GHIST", "READINGS IN PHILIPPINE HISTORY", generateRandomGrade(), 3});
        data.add(new Object[]{"GSELF", "UNDERSTANDING THE SELF", generateRandomGrade(), 3});
        data.add(new Object[]{"IT 111", "INTRODUCTION TO COMPUTING (LEC)", generateRandomGrade(), 2});
        data.add(new Object[]{"IT 111L", "INTRODUCTION TO COMPUTING (LAB)", generateRandomGrade(), 1});
        data.add(new Object[]{"IT 112", "COMPUTER PROGRAMMING 1 (LEC)", generateRandomGrade(), 2});
        data.add(new Object[]{"IT 112L", "COMPUTER PROGRAMMING 1 (LAB)", generateRandomGrade(), 1});
        data.add(new Object[]{"IT 113", "DISCRETE MATHEMATICS", generateRandomGrade(), 3});
        
        // SECOND SEMESTER, 2024-2025
        data.add(new Object[]{"SECOND SEMESTER, 2024-2025", "", "", ""});
        data.add(new Object[]{"CFE 102", "CHRISTIAN MORALITY IN OUR TIMES", generateRandomGrade(), 3});
        data.add(new Object[]{"FIT CS", "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (COMBATIVE SPORTS)", generateRandomGrade(), 2});
        data.add(new Object[]{"GCWORLD", "THE CONTEMPORARY WORLD", generateRandomGrade(), 3});
        data.add(new Object[]{"GMATH", "MATHEMATICS IN THE MODERN WORLD", generateRandomGrade(), 3});
        data.add(new Object[]{"GPCOM", "PURPOSIVE COMMUNICATION", generateRandomGrade(), 3});
        data.add(new Object[]{"IT 121", "INFORMATION SYSTEM FUNDAMENTALS", generateRandomGrade(), 3});
        data.add(new Object[]{"IT 122", "COMPUTER PROGRAMMING 2", generateRandomGrade(), 2});
        data.add(new Object[]{"IT 122L", "COMPUTER PROGRAMMING 2 (LAB)", generateRandomGrade(), 1});
        data.add(new Object[]{"IT 123", "PLATFORM TECHNOLOGIES", generateRandomGrade(), 2});
        data.add(new Object[]{"IT 123L", "PLATFORM TECHNOLOGIES (LAB)", generateRandomGrade(), 1});
        
        // SHORT TERM, 2025
        data.add(new Object[]{"SHORT TERM, 2025", "", "", ""});
        data.add(new Object[]{"GRIZAL", "THE LIFE AND WORKS OF RIZAL", generateRandomGrade(), 3});
        data.add(new Object[]{"IT 131", "COMPUTER ARCHITECTURE", generateRandomGrade(), 2});
        data.add(new Object[]{"IT 131L", "COMPUTER ARCHITECTURE (LAB)", generateRandomGrade(), 1});
        
        return data.toArray(new Object[data.size()][4]);
    }

    private void initializeComponents() {
        setTitle("iSLU Student Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Create main panel
        mainPanel = new JPanel(new BorderLayout());

        // Create header
        JPanel headerPanel = createHeader();

        // Create sidebar
        sidebarPanel = createSidebar();

        footbarPanel = createFooter();
        // Create content panel

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        mainContentPanel.add(footbarPanel, BorderLayout.SOUTH);


        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(13, 37, 73));
        headerPanel.setPreferredSize(new Dimension(0, 55));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JSeparator headerSeparator = new JSeparator();
        headerSeparator.setForeground(new Color(70, 130, 180)); // Blue separator color
        headerSeparator.setBackground(new Color(70, 130, 180));
        headerSeparator.setPreferredSize(new Dimension(0, 2));

        // Logo section
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoPanel.setBackground(new Color(52, 73, 94));
        
        // Load InsideLogo.png image
        JLabel logoLabel = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/photos/InsideLogo.png");
            if (logoUrl != null) {
                ImageIcon originalIcon = new ImageIcon(logoUrl);
                Image originalImage = originalIcon.getImage();
                // Scale the image to appropriate size (height of about 40px to fit in header)
                Image scaledImage = originalImage.getScaledInstance(-1, 40, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                logoLabel.setIcon(scaledIcon);
            } else {
                // Fallback to text if image not found
                logoLabel.setText("iSLU");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
                logoLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            // Fallback to text if any error occurs
            logoLabel.setText("iSLU");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
            logoLabel.setForeground(Color.WHITE);
        }
        
        logoPanel.add(logoLabel);

        // User info section
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(31, 47, 57));

        userNameLabel = new JLabel(studentName);
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        userNameLabel.setForeground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.addActionListener(e -> {
            // Dispose current student portal window
            dispose();
            // Open login page
            new Login().setVisible(true);
        });

        userPanel.add(userNameLabel);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        JPanel headerWithSeparator = new JPanel(new BorderLayout());
        headerWithSeparator.add(headerSeparator, BorderLayout.SOUTH);
        headerWithSeparator.add(headerPanel, BorderLayout.CENTER);

        return headerWithSeparator;
    }

    private JPanel createFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);// Match sidebar color
        footerPanel.setPreferredSize(new Dimension(0, 50));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Add separator line at the top of footer
        JSeparator footerSeparator = new JSeparator();
        footerSeparator.setForeground(new Color(70, 130, 180)); // Blue separator color
        footerSeparator.setBackground(new Color(70, 130, 180));
        footerSeparator.setPreferredSize(new Dimension(0, 2));

        // Footer content
        JLabel copyrightLabel = new JLabel("Copyright © 2021 TMDD - Software Development. All rights reserved.");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        copyrightLabel.setForeground(Color.BLACK);
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel footerWithSeparator = new JPanel(new BorderLayout());
        footerWithSeparator.add(footerSeparator, BorderLayout.NORTH);
        footerWithSeparator.add(footerPanel, BorderLayout.CENTER);

        footerPanel.add(copyrightLabel, BorderLayout.CENTER);

        return footerWithSeparator;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(13, 37, 73));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // Semester info
        semesterLabel = new JLabel(semester);
        semesterLabel.setForeground(Color.WHITE);
        semesterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        semesterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        semesterLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        sidebar.add(semesterLabel);

        // Navigation menu items
        JFrame frame = new JFrame("Student Portal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);


        menu = PortalUtils.createIntegratedMenuSystem();
        JPanel mainSideButtonPanel = new JPanel();
        mainSideButtonPanel.setBackground(new Color(13, 37, 73));
        mainSideButtonPanel.setLayout(new GridLayout(0, 1, 0, 0));
        mainSideButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        mainCardLayout = new CardLayout();
        mainCardHolder = new JPanel(mainCardLayout);

        for (MenuItem option : menu) {
            JPanel buttonPanel = getButtonPanel(option);
            mainSideButtonPanel.add(buttonPanel);


            buttonPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mainCardLayout.show(mainCardHolder, option.getName());
                    showContent(option);
                }
            });
        }
        sidebar.add(mainSideButtonPanel, BorderLayout.WEST);

        return sidebar;
    }

    private static JPanel getButtonPanel(MenuItem text) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout()); // Use BorderLayout to align the label

        // Set the panel's background color
        buttonPanel.setBackground(new Color(13, 37, 73));
        buttonPanel.setOpaque(true);

        // Create the top/bottom border
        Border topBottomBorder = BorderFactory.createMatteBorder(1, 0, 1, 0, Color.black);

        // Create the padding border (this goes inside the top/bottom border)
        Border padding = new EmptyBorder(10, 10, 10, 10);

        // Combine the borders
        Border finalBorder = BorderFactory.createCompoundBorder(topBottomBorder, padding);
        buttonPanel.setBorder(finalBorder);

        // Create a JLabel for the text
        JLabel buttonLabel = new JLabel(text.getName());
        buttonLabel.setForeground(Color.WHITE);
        buttonLabel.setHorizontalAlignment(SwingConstants.LEFT);
        buttonLabel.setOpaque(false); // Make the label transparent

        // Add the label to the panel. Use BorderLayout.WEST to align it to the left.
        buttonPanel.add(buttonLabel, BorderLayout.WEST);

        return buttonPanel;
    }

    private void setupLayout(MySinglyLinkedList<String> subItem) {
        // Create announcements panel
        JPanel announcementsPanel = createAnnouncementsPanel(subItem);
        loadAnnouncements();
        // Create student status panel
        JPanel statusPanel = createStatusPanel(subItem);
        loadStudentStatus();

        contentPanel.add(announcementsPanel);
        contentPanel.add(statusPanel);
    }

    // Method for the "Grade" sub-panels
    private JPanel createGradesPanel(MySinglyLinkedList<String> subItems) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header with semester info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94)); // Dark blue background
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("📊 Grades (FIRST SEMESTER, 2025-2026)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setIcon(null); // Remove any existing icon

        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Table with exact columns from image
        String[] columnNames = {"Class Code", "Course Number", "Units", "Prelim Grade", "Midterm Grade", "Tentative Final Grade", "Final Grade", "Weights"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setFillsViewportHeight(false);

        // Add the exact data from the image
        tableModel.addRow(new Object[]{"7024", "NCTP-CWTS 1", "3", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9454", "GSTS", "3", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9465", "GEN1", "3", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9458", "LYE 103", "3", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9457", "IT 211", "3", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9458A", "IT 212", "2", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9458B", "IT 212L", "1", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9459A", "IT 213", "2", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9459B", "IT 213L", "1", "", "", "", "Not Yet Submitted", ""});
        tableModel.addRow(new Object[]{"9547", "FIT OA", "1", "", "", "", "Not Yet Submitted", ""});

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        scrollPane.setBackground(Color.WHITE);

        int rowCount = table.getRowCount();
        int rowHeight = table.getRowHeight();
        int headerHeight = table.getTableHeader().getPreferredSize().height;
        int totalTableHeight = (rowCount * rowHeight) + headerHeight;

        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, totalTableHeight));


        // Deadline note section
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notePanel.setBackground(Color.WHITE);
        notePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel noteLabel = new JLabel("<html><b>NOTE:</b> Deadline of submission for completion of Students is <b>February 04, 2026</b>. NC due to NFE/INC if not completed, the final grades shall become permanent.</html>");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        noteLabel.setForeground(new Color(200, 0, 0)); // Red color for note
        notePanel.add(noteLabel);

        // Legend section
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        JLabel legendTitle = new JLabel("LEGEND:");
        legendTitle.setFont(new Font("Arial", Font.BOLD, 12));
        legendPanel.add(legendTitle);

        // Create legend items in columns as shown in image
        JPanel legendContent = new JPanel(new GridLayout(0, 2, 20, 2));
        legendContent.setBackground(Color.WHITE);

        // Left column
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);

        leftColumn.add(createLegendItem("P", "Passed", "HP", "High Pass"));
        leftColumn.add(createLegendItem("INC", "Incomplete", "WP", "Withdrawal w/ Permission"));
        leftColumn.add(createLegendItem("D", "Dropped", "F", "Failure"));
        leftColumn.add(createLegendItem("NC", "No Credit", "NFE", "No Final Examination"));

        JLabel undergrad = new JLabel("FOR UNDERGRADUATE");
        undergrad.setFont(new Font("Arial", Font.BOLD, 11));
        leftColumn.add(undergrad);

        leftColumn.add(createLegendItem("Passing Grade", "75%"));
        leftColumn.add(createLegendItem("Failure", "Below 75%"));

        // Right column
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);

        JLabel gradSchool = new JLabel("FOR GRADUATE SCHOOL");
        gradSchool.setFont(new Font("Arial", Font.BOLD, 11));
        rightColumn.add(gradSchool);

        rightColumn.add(createLegendItem("Passing Grade", "85%"));
        rightColumn.add(createLegendItem("Failure", "Below 85%"));

        legendContent.add(leftColumn);
        legendContent.add(rightColumn);
        legendPanel.add(legendContent);

        JPanel scrollWrapper = new JPanel(new BorderLayout()); // Use BorderLayout for clean top-alignment
        scrollWrapper.add(scrollPane, BorderLayout.NORTH); // Anchor the limited scrollPane to the NORTH
        scrollWrapper.setBackground(Color.WHITE); // Ensure the background is white
        // Assemble the main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollWrapper, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(notePanel, BorderLayout.NORTH);
        bottomPanel.add(legendPanel, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }
    
    // Helper method to create legend items
    private JPanel createLegendItem(String abbrev, String meaning) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        item.setBackground(Color.WHITE);
        
        JLabel abbrevLabel = new JLabel(abbrev);
        abbrevLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        JLabel dots = new JLabel("........... ");
        dots.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel meaningLabel = new JLabel(meaning);
        meaningLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        item.add(abbrevLabel);
        item.add(dots);
        item.add(meaningLabel);
        
        return item;
    }
    
    // Overloaded helper method for legend items with two abbreviations
    private JPanel createLegendItem(String abbrev1, String meaning1, String abbrev2, String meaning2) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        item.setBackground(Color.WHITE);
        
        JLabel abbrevLabel1 = new JLabel(abbrev1);
        abbrevLabel1.setFont(new Font("Arial", Font.BOLD, 11));
        
        JLabel dots1 = new JLabel("........... ");
        dots1.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel meaningLabel1 = new JLabel(meaning1 + "    ");
        meaningLabel1.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel abbrevLabel2 = new JLabel(abbrev2);
        abbrevLabel2.setFont(new Font("Arial", Font.BOLD, 11));
        
        JLabel dots2 = new JLabel("........... ");
        dots2.setFont(new Font("Arial", Font.PLAIN, 11));
        
        JLabel meaningLabel2 = new JLabel(meaning2);
        meaningLabel2.setFont(new Font("Arial", Font.PLAIN, 11));
        
        item.add(abbrevLabel1);
        item.add(dots1);
        item.add(meaningLabel1);
        item.add(abbrevLabel2);
        item.add(dots2);
        item.add(meaningLabel2);
        
        return item;
    }

    // Method for the Announcements sub-panels

    private JPanel createAnnouncementsPanel(MySinglyLinkedList<String> subItem) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                subItem.getFirst(),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setBackground(Color.WHITE);

        announcementsArea = new JTextArea();
        announcementsArea.setEditable(false);
        announcementsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        announcementsArea.setLineWrap(true);
        announcementsArea.setWrapStyleWord(true);
        announcementsArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(announcementsArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 400));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    // Method for the Status sub-panels
    private JPanel createStatusPanel(MySinglyLinkedList<String> subItem) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                subItem.get(1),
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        panel.setBackground(Color.WHITE);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Arial", Font.PLAIN, 12));
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(0, 400));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    // Content for announcements
    private void loadAnnouncements() {

        StringBuilder announcements = new StringBuilder();

        announcements.append("[INVITATION FOR FRESHMEN] 29th PSQ - University Elimination Round\n\n");
        announcements.append("Good day, freshmen Louisians!\n\n");
        announcements.append("We are inviting all of you to join and participate in the 29th Philippine Statistics Quiz - SLU Qualifiers, Elimination Round! The competition will be conducted online on September 12, 2025. The top 30 qualifiers from the elimination round will battle it out in the final round in October 2025. The top 2 placers will represent Saint Louis University in the Regional Finals tentatively scheduled in November!\n\n");
        announcements.append("For those currently enrolled in a mathematics course, you may consult with your respective instructors for more details.\n\n");
        announcements.append("For any inquiries, do not hesitate to contact Mr. Clarenz Magsakay via cbmagsakay@slu.edu.ph.\n\n");
        announcements.append("Have a wonderful day!\n");
        announcements.append("─────────────────────────────────────────────────────\n\n");

        announcements.append("[CALL FOR RESPONDENTS] A Research Survey on Assessing the Coffee Production potential of SLU Maryheights Coffee Plantation\n\n");
        announcements.append("We are conducting a research survey to evaluate the coffee production potential of the SLU Maryheights Coffee Plantation located in Bakakeng Norte. This survey focuses on assessing the operational readiness of the plantation and exploring marketing opportunities for its coffee products.\n\n");
        announcements.append("The primary objective of this study is to identify opportunities for developing single-origin coffee from the area, which will promote a sustainable coffee industry in Baguio City.\n\n");
        announcements.append("Sincerely yours,\nMA. ARACELI D. TAMBOL\nSLUHTM Faculty Researcher\n");
        announcements.append("─────────────────────────────────────────────────────\n\n");

        announcements.append("AppliedHE Public & Private University Ranking Survey\n\n");
        announcements.append("Kindly answer the survey. This will take approximately 5 minutes of your time. Thank You!\n");
        announcements.append("─────────────────────────────────────────────────────\n\n");

        announcements.append("ACADEMIC CALENDAR 2025-2026\n");
        announcements.append("View the complete academic calendar for detailed information about important dates and deadlines.");

        announcementsArea.setText(announcements.toString());
        announcementsArea.setCaretPosition(0);
    }
    // Content for student status
    private void loadStudentStatus() {

        String status = "Status:\n" +
                "- " + this.status + "\n\n\n" +
                "Announcement from instructor:\n\n" +
                "Class: 7024-NSTP-CWTS 1\n\n" +
                "Google classroom invite link:\n" +
                "https://classroom.google.com/c/NzkxOTgxNDQ3NTcy?cjc=3hnunus2\n\n" +
                "Instructor: Bullong, Doris K.\n\n" +
                "─────────────────────────────────────────────────────\n\n" +
                "Additional Information:\n" +
                "• Make sure to check your class schedule regularly\n" +
                "• Join the Google Classroom for important updates\n" +
                "• Contact your instructor for any class-related queries\n" +
                "• Keep track of assignment deadlines and exam schedules";

        statusArea.setText(status);
        statusArea.setCaretPosition(0);
    }

    // method for showing different contents
    private void showContent(MenuItem item) {
// Clear current content
        contentPanel.removeAll();
        switch (item.getName()) {
            case "🏠 Home":
                // Reset layout to GridLayout for home content
                contentPanel.setLayout(new GridLayout(1, 2, 10, 10));
                contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
                contentPanel.setBackground(Color.WHITE);
                setupLayout(item.getSubItems());
                break;
            case "📚 Journal/Periodical":
                contentPanel.add(createJournalPeriodicalPanel(item.getSubItems()));
                break;
            case "📅 Schedule":
                contentPanel.add(createClassChecklistPanel());
                break;
            case "📌 Attendance":
                contentPanel.add(showAttendanceContent(item.getSubItems()));
                break;
            case "📊 Grades":
                contentPanel.add(createGradesPanel(item.getSubItems()));
                break;
            case "👤 Personal Details":
                showPersonalDetailsContent(item.getSubItems());
                break;
            case "🧮 Statement of Accounts":
                contentPanel.add(createStatementOfAccountsPanel(item.getSubItems()));
                break;
            case "📋 Transcript of Records":
                contentPanel.add(createTranscriptOfRecordsPanel(item.getSubItems()));
                break;
            case "✅ Curriculum Checklist":
                contentPanel.add(createCurriculumChecklistPanel(item.getSubItems()));
                break;
            case "ℹ️ Downloadable/ About iSLU":
                contentPanel.add(createAboutISLUPanel(item.getSubItems()));
                break;
            case "🏥 Medical Record":
                contentPanel.add(createMedicalRecordPanel(item.getSubItems()));
                break;
            default:
                // Fallback for any other menu item with a sublist
                showGenericContent(item.getName());
        }
        contentPanel.revalidate();
        contentPanel.repaint();

    }
    // Journal/Periodical Panel with Enhanced Search Functionality
    private JPanel createJournalPeriodicalPanel(MySinglyLinkedList<String> subItems) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Create the main content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        // Blue header with title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 51, 153)); // SLU Blue
        titlePanel.setLayout(new GridBagLayout());
        titlePanel.setPreferredSize(new Dimension(0, 80));
        
        // Title text
        JPanel titleTextPanel = new JPanel();
        titleTextPanel.setLayout(new BoxLayout(titleTextPanel, BoxLayout.Y_AXIS));
        titleTextPanel.setBackground(new Color(0, 51, 153));
        
        JLabel titleLine1 = new JLabel("SAINT LOUIS UNIVERSITY LIBRARIES");
        titleLine1.setFont(new Font("Arial", Font.BOLD, 24));
        titleLine1.setForeground(Color.WHITE);
        titleLine1.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLine2 = new JLabel("PERIODICAL ARTICLE INDEXES");
        titleLine2.setFont(new Font("Arial", Font.BOLD, 18));
        titleLine2.setForeground(Color.WHITE);
        titleLine2.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        titleTextPanel.add(titleLine1);
        titleTextPanel.add(Box.createVerticalStrut(5));
        titleTextPanel.add(titleLine2);
        
        titlePanel.add(titleTextPanel);
        contentWrapper.add(titlePanel, BorderLayout.NORTH);

        // Get sample articles
        List<JournalArticle> allArticles = JournalArticle.getSampleArticles();

        // Create CardLayout for switching between default view and search results
        CardLayout cardLayout = new CardLayout();
        JPanel cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.WHITE);

        // Default content panel (instructions)
        JPanel defaultPanel = createDefaultJournalContent();
        
        // Search results panel
        JPanel searchResultsPanel = new JPanel(new BorderLayout());
        searchResultsPanel.setBackground(Color.WHITE);
        
        cardPanel.add(defaultPanel, "default");
        cardPanel.add(searchResultsPanel, "results");

        // Search section
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(480, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = new JButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.setBackground(new Color(240, 240, 240));
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton advancedButton = new JButton("Advance Search");
        advancedButton.setPreferredSize(new Dimension(140, 35));
        advancedButton.setBackground(new Color(240, 240, 240));
        advancedButton.setFocusPainted(false);
        advancedButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        advancedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Print button (initially hidden)
        JButton printButton = new JButton("🖨 Print");
        printButton.setPreferredSize(new Dimension(80, 35));
        printButton.setBackground(new Color(240, 240, 240));
        printButton.setFocusPainted(false);
        printButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printButton.setVisible(false);

        // Search again button (initially hidden)
        JButton searchAgainButton = new JButton("Search again");
        searchAgainButton.setPreferredSize(new Dimension(110, 35));
        searchAgainButton.setBackground(new Color(240, 240, 240));
        searchAgainButton.setFocusPainted(false);
        searchAgainButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        searchAgainButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchAgainButton.setVisible(false);

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(advancedButton);
        searchPanel.add(searchAgainButton);
        searchPanel.add(printButton);

        // Store current search results for printing
        final List<JournalArticle>[] currentSearchResults = new List[]{new ArrayList<>()};
        final String[] currentSearchTerm = new String[]{""};        

        // Search functionality
        Runnable performSearch = () -> {
            String searchTerm = searchField.getText().trim();
            
            if (searchTerm.isEmpty()) {
                cardLayout.show(cardPanel, "default");
                searchButton.setVisible(true);
                advancedButton.setVisible(true);
                searchAgainButton.setVisible(false);
                printButton.setVisible(false);
                return;
            }

            // Filter articles based on search term (case-insensitive)
            List<JournalArticle> searchResults = new ArrayList<>();
            for (JournalArticle article : allArticles) {
                if (article.matchesSearch(searchTerm)) {
                    searchResults.add(article);
                }
            }
            
            // Store results for printing
            currentSearchResults[0] = searchResults;
            currentSearchTerm[0] = searchTerm;

            // Update search results panel
            searchResultsPanel.removeAll();
            searchResultsPanel.add(createSearchResultsContent(searchTerm, searchResults), BorderLayout.CENTER);
            searchResultsPanel.revalidate();
            searchResultsPanel.repaint();

            // Show results and update buttons - Search Again should remain visible
            cardLayout.show(cardPanel, "results");
            searchButton.setVisible(false);
            advancedButton.setVisible(false);
            searchAgainButton.setVisible(true);  // Always visible after search
            printButton.setVisible(true);
        };

        // Add action listeners
        searchButton.addActionListener(e -> performSearch.run());
        searchField.addActionListener(e -> performSearch.run());

        searchAgainButton.addActionListener(e -> {
            searchField.setText("");
            cardLayout.show(cardPanel, "default");
            searchButton.setVisible(true);
            advancedButton.setVisible(true);
            searchAgainButton.setVisible(false);
            printButton.setVisible(false);
            searchField.requestFocus();
        });

        advancedButton.addActionListener(e -> {
            // Create Advanced Search Dialog
            JDialog advancedDialog = new JDialog(this, "Advanced Search", true);
            advancedDialog.setSize(500, 400);
            advancedDialog.setLocationRelativeTo(this);
            
            JPanel advPanel = new JPanel(new GridBagLayout());
            advPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Title field
            gbc.gridx = 0; gbc.gridy = 0;
            advPanel.add(new JLabel("Title Contains:"), gbc);
            gbc.gridx = 1;
            JTextField titleField = new JTextField(20);
            advPanel.add(titleField, gbc);
            
            // Author field
            gbc.gridx = 0; gbc.gridy = 1;
            advPanel.add(new JLabel("Author Contains:"), gbc);
            gbc.gridx = 1;
            JTextField authorField = new JTextField(20);
            advPanel.add(authorField, gbc);
            
            // Journal field
            gbc.gridx = 0; gbc.gridy = 2;
            advPanel.add(new JLabel("Journal Name:"), gbc);
            gbc.gridx = 1;
            JTextField journalField = new JTextField(20);
            advPanel.add(journalField, gbc);
            
            // Publication Type dropdown
            gbc.gridx = 0; gbc.gridy = 3;
            advPanel.add(new JLabel("Publication Type:"), gbc);
            gbc.gridx = 1;
            String[] types = {"All", "BIOGRAPHY", "TOURISM RESEARCH", "TRAVEL DECISION MAKING", 
                            "CULTURAL HERITAGE", "DIGITAL TRANSFORMATION", "PANDEMIC"};
            JComboBox<String> typeCombo = new JComboBox<>(types);
            advPanel.add(typeCombo, gbc);
            
            // Year range
            gbc.gridx = 0; gbc.gridy = 4;
            advPanel.add(new JLabel("Year From:"), gbc);
            gbc.gridx = 1;
            JTextField yearFromField = new JTextField(10);
            advPanel.add(yearFromField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 5;
            advPanel.add(new JLabel("Year To:"), gbc);
            gbc.gridx = 1;
            JTextField yearToField = new JTextField(10);
            advPanel.add(yearToField, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel();
            JButton searchAdvButton = new JButton("Search");
            JButton cancelButton = new JButton("Cancel");
            
            searchAdvButton.addActionListener(evt -> {
                // Perform advanced search
                List<JournalArticle> searchResults = new ArrayList<>();
                for (JournalArticle article : allArticles) {
                    if (article.matchesAdvancedSearch(
                            titleField.getText(),
                            authorField.getText(),
                            journalField.getText(),
                            yearFromField.getText(),
                            yearToField.getText(),
                            (String) typeCombo.getSelectedItem())) {
                        searchResults.add(article);
                    }
                }
                
                // Update results
                String searchDesc = "Advanced Search";
                currentSearchResults[0] = searchResults;
                currentSearchTerm[0] = searchDesc;
                
                searchResultsPanel.removeAll();
                searchResultsPanel.add(createSearchResultsContent(searchDesc, searchResults), BorderLayout.CENTER);
                searchResultsPanel.revalidate();
                searchResultsPanel.repaint();
                
                cardLayout.show(cardPanel, "results");
                searchButton.setVisible(false);
                advancedButton.setVisible(false);
                searchAgainButton.setVisible(true);
                printButton.setVisible(true);
                
                advancedDialog.dispose();
            });
            
            cancelButton.addActionListener(evt -> advancedDialog.dispose());
            
            buttonPanel.add(searchAdvButton);
            buttonPanel.add(cancelButton);
            
            gbc.gridx = 0; gbc.gridy = 6;
            gbc.gridwidth = 2;
            advPanel.add(buttonPanel, gbc);
            
            advancedDialog.add(advPanel);
            advancedDialog.setVisible(true);
        });

        printButton.addActionListener(e -> {
            // Implement actual printing
            if (currentSearchResults[0] == null || currentSearchResults[0].isEmpty()) {
                JOptionPane.showMessageDialog(this, "No results to print.", "Print", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                // Create a printable text representation
                StringBuilder printContent = new StringBuilder();
                printContent.append("SAINT LOUIS UNIVERSITY LIBRARIES\n");
                printContent.append("PERIODICAL ARTICLE INDEXES\n");
                printContent.append("================================\n\n");
                printContent.append("Search Results for: ").append(currentSearchTerm[0]).append("\n");
                printContent.append("Date: ").append(new java.util.Date()).append("\n");
                printContent.append("Total Results: ").append(currentSearchResults[0].size()).append("\n\n");
                
                int count = 1;
                for (JournalArticle article : currentSearchResults[0]) {
                    printContent.append(count++).append(". ").append(article.getTitle()).append("\n");
                    if (article.getPublicationType() != null) {
                        printContent.append("   Type: ").append(article.getPublicationType()).append("\n");
                    }
                    printContent.append("   Authors: ").append(article.getAuthors()).append("\n");
                    printContent.append("   Journal: ").append(article.getJournalName()).append("\n");
                    printContent.append("   Volume: ").append(article.getVolume())
                                .append(", Issue: ").append(article.getIssue())
                                .append(", Pages: ").append(article.getPages()).append("\n");
                    printContent.append("   Date: ").append(article.getDate()).append("\n\n");
                }
                
                // Create a text area with the content
                JTextArea printArea = new JTextArea(printContent.toString());
                printArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
                
                // Use Java's print service
                try {
                    boolean printed = printArea.print();
                    if (printed) {
                        JOptionPane.showMessageDialog(this, "Print job sent successfully!", 
                                                    "Print", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (java.awt.print.PrinterException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error printing: " + ex.getMessage(), 
                        "Print Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error preparing print: " + ex.getMessage(), 
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Content body wrapper
        JPanel bodyWrapper = new JPanel(new BorderLayout());
        bodyWrapper.setBackground(Color.WHITE);
        bodyWrapper.add(searchPanel, BorderLayout.NORTH);
        bodyWrapper.add(cardPanel, BorderLayout.CENTER);
        
        contentWrapper.add(bodyWrapper, BorderLayout.CENTER);
        
        // Add the content wrapper to main panel with padding
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        return mainPanel;
    }

    // Helper method to create default journal content
    private JPanel createDefaultJournalContent() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

        // WHAT ARE JOURNAL INDEXES?
        JLabel title1 = new JLabel("WHAT ARE JOURNAL INDEXES?");
        title1.setFont(new Font("Arial", Font.BOLD, 13));
        title1.setForeground(new Color(50, 50, 50));
        title1.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(title1);
        body.add(Box.createVerticalStrut(8));

        JTextArea text1 = new JTextArea("An index is a list of items pulled together for a purpose. Journal indexes (also called bibliographic indexes or bibliographic databases) are lists of journals, organized by discipline, subject, or type of publication.");
        text1.setWrapStyleWord(true);
        text1.setLineWrap(true);
        text1.setEditable(false);
        text1.setBackground(Color.WHITE);
        text1.setFont(new Font("Arial", Font.PLAIN, 12));
        text1.setForeground(new Color(70, 70, 70));
        text1.setBorder(null);
        text1.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(text1);
        body.add(Box.createVerticalStrut(20));

        // THE SLU LIBRARIES' PERIODICAL ARTICLE INDEXES
        JLabel title2 = new JLabel("THE SLU LIBRARIES' PERIODICAL ARTICLE INDEXES");
        title2.setFont(new Font("Arial", Font.BOLD, 13));
        title2.setForeground(new Color(50, 50, 50));
        title2.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(title2);
        body.add(Box.createVerticalStrut(8));

        JTextArea text2 = new JTextArea("One of the Home Library Services that the University Libraries offer is the Periodical Article Indexes where the subscribed print journals are being indexed and can be accessed through an online bibliographic database.\n\nThe Periodical Article Indexes database provides access to periodical articles by subject or author and it can help you find articles about a specific topic.");
        text2.setWrapStyleWord(true);
        text2.setLineWrap(true);
        text2.setEditable(false);
        text2.setBackground(Color.WHITE);
        text2.setFont(new Font("Arial", Font.PLAIN, 12));
        text2.setForeground(new Color(70, 70, 70));
        text2.setBorder(null);
        text2.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(text2);
        body.add(Box.createVerticalStrut(20));

        // STEPS IN ACCESSING THE PERIODICAL ARTICLE INDEXES
        JLabel title3 = new JLabel("STEPS IN ACCESSING THE PERIODICAL ARTICLE INDEXES");
        title3.setFont(new Font("Arial", Font.BOLD, 13));
        title3.setForeground(new Color(50, 50, 50));
        title3.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(title3);
        body.add(Box.createVerticalStrut(8));

        // Create formatted steps with proper indentation
        JPanel stepsPanel = new JPanel();
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
        stepsPanel.setBackground(Color.WHITE);
        stepsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] steps = {
            "1.  Enter your topic on the search box and click Search",
            "2.  You will see the various bibliographic details (i.e. title of the journal, the specific date, volume and issue, and page numbers for the article) that contain your topic.",
            "3.  Should you opt to read the full text of the article, you may request it by sending an email to uldir@slu.edu.ph"
        };
        
        for (String step : steps) {
            JTextArea stepText = new JTextArea(step);
            stepText.setWrapStyleWord(true);
            stepText.setLineWrap(true);
            stepText.setEditable(false);
            stepText.setBackground(Color.WHITE);
            stepText.setFont(new Font("Arial", Font.PLAIN, 12));
            stepText.setForeground(new Color(70, 70, 70));
            stepText.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 0));
            stepText.setAlignmentX(Component.LEFT_ALIGNMENT);
            stepsPanel.add(stepText);
        }
        
        body.add(stepsPanel);

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(scroll, BorderLayout.CENTER);
        
        return wrapper;
    }

    // Helper method to create search results content
    private JPanel createSearchResultsContent(String searchTerm, List<JournalArticle> results) {
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(245, 245, 245));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Results header
        JLabel resultsLabel = new JLabel("Journals/Periodicals Search Result:");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultsPanel.add(resultsLabel);
        resultsPanel.add(Box.createVerticalStrut(15));

        if (results.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No results found for \"" + searchTerm + "\"");
            noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noResultsLabel.setForeground(new Color(100, 100, 100));
            noResultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultsPanel.add(noResultsLabel);
        } else {
            // Display each result
            for (JournalArticle article : results) {
                JPanel articlePanel = createArticlePanel(article);
                articlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultsPanel.add(articlePanel);
                resultsPanel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        
        return wrapper;
    }

    // Helper method to create individual article panel
    private JPanel createArticlePanel(JournalArticle article) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Title and keywords
        JLabel titleLabel = new JLabel(article.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        titleLabel.setForeground(new Color(0, 51, 153));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        if (article.getPublicationType() != null && !article.getPublicationType().isEmpty()) {
            JLabel typeLabel = new JLabel(article.getPublicationType());
            typeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            typeLabel.setForeground(new Color(100, 100, 100));
            typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(typeLabel);
        }

        panel.add(Box.createVerticalStrut(5));

        // Authors
        JLabel authorsLabel = new JLabel(article.getAuthors());
        authorsLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        authorsLabel.setForeground(new Color(50, 50, 50));
        authorsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(authorsLabel);

        panel.add(Box.createVerticalStrut(3));

        // Journal details
        String journalDetails = article.getJournalName();
        if (article.getVolume() != null && article.getIssue() != null) {
            journalDetails += " / " + article.getAuthors() + ". ";
        }
        
        JLabel journalLabel = new JLabel(journalDetails);
        journalLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        journalLabel.setForeground(new Color(70, 70, 70));
        journalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(journalLabel);

        // Current Issues link
        if (article.isCurrentIssue()) {
            JLabel currentIssueLabel = new JLabel("<html><u>Current Issues in Tourism</u></html>");
            currentIssueLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            currentIssueLabel.setForeground(new Color(0, 51, 153));
            currentIssueLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            currentIssueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(currentIssueLabel);
        }

        // Volume and page info
        String volumeInfo = "v." + article.getVolume() + ", no." + article.getIssue() + 
                           " (" + article.getDate() + ") : pp. " + article.getPages();
        JLabel volumeLabel = new JLabel(volumeInfo);
        volumeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        volumeLabel.setForeground(new Color(70, 70, 70));
        volumeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(volumeLabel);

        return panel;
    }
    /**
     * Creates the Class Checklist panel which contains:
     *  - Current Load table (top)
     *  - Weekly View (bottom) auto-generated from the table (non-interactive)
     *
     * The mock class data is provided by getMockClasses()
     */
    private JPanel createClassChecklistPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10));
        wrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("CLASS SCHEDULE", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        wrapper.add(title, BorderLayout.NORTH);

        // Fetch mock classes (editable in one place)
        List<ClassInfo> classes = getMockClasses();
        JPanel tablePanel = createClassScheduleTable();


        // Weekly View panel (custom painter)
        WeeklyViewPanel weeklyView = new WeeklyViewPanel(classes);

        // Combine table (top) and weekly view (below) in vertical layout with scroll
        JPanel combined = new JPanel();
        combined.setLayout(new BoxLayout(combined, BoxLayout.Y_AXIS));
        combined.add(new JScrollPane(tablePanel));
        combined.add(Box.createRigidArea(new Dimension(0, 12)));
        combined.add(weeklyView);

        // Wrap with a scroll pane so both are scrollable together if height exceeds window
        JScrollPane combinedScroll = new JScrollPane(combined,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        combinedScroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(combinedScroll, BorderLayout.CENTER);

        return wrapper;
    }

    /**
     * Mock class data center (edit this method to add/remove mock classes)
     *
     * Format per ClassInfo:
     *   ClassInfo(classCode, courseNo, description, units, startTimeStr, endTimeStr, daysList, room, studentCount)
     *
     * Days are strings: "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
     */
    private List<ClassInfo> getMockClasses() {
        List<ClassInfo> list = new ArrayList<>();

        // ===== MOCK CLASS DATA (edit here) =====
        // Note: times are in 12-hour format with AM/PM, days are standard names.
        list.add(new ClassInfo("7024", "NSTP-CWTS 1",
                "TFOUNDATIONS OF SERVICE", 3,
                "1:30 PM", "2:30 PM",
                Arrays.asList("Mon","Wed","Fri"), "D906"));

        list.add(new ClassInfo("9454", "GSTS",
                "SCIENCE, TECHNOLOGY, AND SOCIETY", 3,
                "9:30 AM", "10:30 AM",
                Arrays.asList("Tue","Thu","Sat"), "D504"));

        list.add(new ClassInfo("9455", "GENVI",
                "ENVIRONMENTAL SCIENCE", 3,
                "9:30 AM", "10:30 AM",
                Arrays.asList("Mon","Wed","Fri"), "D503"));

        list.add(new ClassInfo("9456", "CFE 103",
                "CATHOLIC FOUNDATION OF MISSION", 3,
                "1:30 PM", "2:30 PM",
                Arrays.asList("Tue","Thu","Sat"), "D503"));

        list.add(new ClassInfo("9457", "IT 211",
                "REQUIREMENTS ANALYSIS AND MODELING", 3,
                "10:30 AM", "11:30 AM",
                Arrays.asList("Mon","Wed","Fri"), "D511"));

        list.add(new ClassInfo("9458A", "IT 212",
                "DATA STRUCTURES (LEC)", 2,
                "2:30 PM", "3:30 PM",
                Arrays.asList("Tue","Fri"), "D513"));

        list.add(new ClassInfo("9458B", "IT 212L",
                "DATA STRUCTURES (LAB)", 1,
                "4:00 PM", "5:30 PM",
                Arrays.asList("Tue","Fri"), "D522"));

        list.add(new ClassInfo("9459A", "IT 213",
                "NETWORK FUNDAMENTALS (LEC)", 2,
                "8:30 AM", "9:30 AM",
                Arrays.asList("Tue","Fri"), "D513"));

        list.add(new ClassInfo("9459B", "IT 213L",
                "NETWORK FUNDAMENTALS (LAB)", 1,
                "11:30 AM", "1:00 PM",
                Arrays.asList("Tue","Fri"), "D528"));

        list.add(new ClassInfo("9547", "FIT OA",
                "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (OUTDOOR AND ADVENTURE ACTIVITIES)", 2,
                "3:30 PM", "5:30 PM",
                Arrays.asList("Thu"), "D221"));
        // ===== MOCK CLASS DATA END =====

        return list;
    }

    /**
     * Helper class to store class information (small structured object)
     */
    private static class ClassInfo {
        String classCode;
        String courseNo;
        String description;
        int units;
        String startTime; // "7:30 AM"
        String endTime;   // "9:00 PM"
        List<String> days; // e.g., ["Mon","Wed","Fri"]
        String room;
        int studentCount;

        ClassInfo(String classCode, String courseNo, String description, int units,
                  String startTime, String endTime, List<String> days, String room) {
            this.classCode = classCode;
            this.courseNo = courseNo;
            this.description = description;
            this.units = units;
            this.startTime = startTime;
            this.endTime = endTime;
            this.days = days;
            this.room = room;
        }
    }

    /**
     * Custom panel that paints the weekly grid and class blocks.
     * Non-interactive, auto-generated from the list of ClassInfo.
     */
    private static class WeeklyViewPanel extends JPanel {

        private final java.util.List<ClassInfo> classes;
        private final SimpleDateFormat parseFormat = new SimpleDateFormat("h:mm a");
        private final String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        // Grid parameters
        private final int startHour = 7;   // 7:00 AM
        private final int endHour = 21;    // 9:00 PM
        private final int slotMinutes = 30; // 30-minute increments

        // Visual parameters
        private final int timeColWidth = 80;
        private final int headerHeight = 30;
        private final int cellHeight = 28;
        private final int leftPadding = 10;
        private final int rightPadding = 10;

        WeeklyViewPanel(List<ClassInfo> classes) {
            this.classes = classes;
            setPreferredSize(new Dimension(900, computePreferredHeight()));
            setBackground(Color.WHITE);

        }

        private int computePreferredHeight() {
            int rows = ((endHour - startHour) * 60) / slotMinutes;
            // header + rows * cellHeight + some bottom margin
            return headerHeight + rows * cellHeight + 40;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Compute grid metrics
            int days = 7;
            int gridX = timeColWidth + leftPadding;
            int gridWidth = width - gridX - rightPadding;
            if (gridWidth < 100) gridWidth = 700; // fallback
            int colWidth = gridWidth / days;

            int rows = ((endHour - startHour) * 60) / slotMinutes;

            // Draw day headers
            g2.setColor(new Color(245, 245, 245));
            g2.fillRect(gridX, 0, gridWidth, headerHeight);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            for (int d = 0; d < days; d++) {
                int x = gridX + d * colWidth;
                String day = dayNames[d];
                FontMetrics fm = g2.getFontMetrics();
                int fx = x + (colWidth - fm.stringWidth(day)) / 2;
                int fy = (headerHeight + fm.getAscent()) / 2 - 2;
                g2.drawString(day, fx, fy);
            }

            // Vertical line separating time column and grid
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(gridX - 6, 0, gridX - 6, headerHeight + rows * cellHeight + 2);

            // Draw time labels and horizontal grid lines
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            for (int r = 0; r <= rows; r++) {
                int y = headerHeight + r * cellHeight;
                // horizontal grid line
                g2.setColor(new Color(230, 230, 230));
                g2.drawLine(gridX, y, gridX + days * colWidth, y);

                if (r < rows) {
                    // time label for this row (every slotMinutes)
                    int minutesFromStart = r * slotMinutes;
                    int hour = startHour + (minutesFromStart / 60);
                    int minute = minutesFromStart % 60;

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, minute);
                    String label = formatTo12Hour(cal.get(Calendar.HOUR_OF_DAY), minute);

                    g2.setColor(Color.DARK_GRAY);
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = leftPadding + 4;
                    int ty = y + (cellHeight + fm.getAscent()) / 2 - 3;
                    g2.drawString(label, tx, ty);
                }
            }

            // Draw vertical separators between days
            g2.setColor(new Color(235, 235, 235));
            for (int d = 0; d <= days; d++) {
                int x = gridX + d * colWidth;
                g2.drawLine(x, headerHeight, x, headerHeight + rows * cellHeight);
            }

            // Draw each class as a rounded blue rectangle spanning appropriate rows and days
            for (ClassInfo ci : classes) {
                // for each day this class occurs on
                for (String day : ci.days) {
                    int dayIndex = dayNameToIndex(day);
                    if (dayIndex < 0) continue; // skip unknown day labels

                    int startMinutes = parseTimeToMinutes(ci.startTime);
                    int endMinutes = parseTimeToMinutes(ci.endTime);

                    int gridStart = Math.max(0, (startMinutes - startHour * 60) / slotMinutes);
                    int gridEnd = Math.min(rows, (endMinutes - startHour * 60) / slotMinutes);

                    if (gridEnd <= 0 || gridStart >= rows) {
                        continue; // out of visible range
                    }

                    int x = gridX + dayIndex * colWidth + 6;
                    int y = headerHeight + gridStart * cellHeight + 4;
                    int w = colWidth - 12;
                    int h = Math.max(16, (gridEnd - gridStart) * cellHeight - 8);

                    // Draw rounded rectangle
                    RoundRectangle2D.Double rrect = new RoundRectangle2D.Double(x, y, w, h, 12, 12);
                    g2.setColor(new Color(38, 112, 201)); // blue
                    g2.fill(rrect);

                    // Draw text centered
                    String display = ci.courseNo + " (" + ci.room + ")";
                    g2.setColor(Color.WHITE);
                    Font font = new Font("Arial", Font.BOLD, 12);
                    g2.setFont(font);
                    drawStringInRect(g2, display, new Rectangle(x + 6, y + 6, w - 12, h - 12));
                }
            }

            g2.dispose();
        }

        private String formatTo12Hour(int hour24, int minute) {
            String ampm = (hour24 >= 12) ? "PM" : "AM";
            int hour = hour24 % 12;
            if (hour == 0) hour = 12;
            return String.format("%d:%02d %s", hour, minute, ampm);
        }

        private int dayNameToIndex(String day) {
            // Accept common variants
            String d = day.trim().toLowerCase();
            switch (d) {
                case "sun":
                case "sunday":
                    return 0;
                case "mon":
                case "monday":
                    return 1;
                case "tue":
                case "tues":
                case "tuesday":
                    return 2;
                case "wed":
                case "wednesday":
                    return 3;
                case "thu":
                case "thur":
                case "thurs":
                case "thursday":
                    return 4;
                case "fri":
                case "friday":
                    return 5;
                case "sat":
                case "saturday":
                    return 6;
                default:
                    return -1;
            }
        }

        private int parseTimeToMinutes(String timeStr) {
            // Parse "h:mm a" with SimpleDateFormat
            try {
                Date d = parseFormat.parse(timeStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                int hour = cal.get(Calendar.HOUR);
                // NOTE: SimpleDateFormat parse gives HOUR in 12-hour clock; need AM/PM separately
                // A safer approach: parse using a dedicated Calendar with parse, then get HOUR_OF_DAY
                // We'll re-parse with another format to get HOUR_OF_DAY
                SimpleDateFormat sdf24 = new SimpleDateFormat("H:mm");
                String s24 = new SimpleDateFormat("H:mm").format(d);
                String[] parts = s24.split(":");
                int h = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                return h * 60 + m;
            } catch (ParseException e) {
                // fallback: attempt manual parse
                try {
                    return manualParseTimeToMinutes(timeStr);
                } catch (Exception ex) {
                    return 0;
                }
            }
        }

        private int manualParseTimeToMinutes(String timeStr) {
            // Expected "h:mm AM" or "hh:mm PM"
            String s = timeStr.trim().toUpperCase();
            boolean pm = s.endsWith("PM");
            boolean am = s.endsWith("AM");
            s = s.replace("AM", "").replace("PM", "").trim();
            String[] parts = s.split(":");
            int h = Integer.parseInt(parts[0]);
            int m = (parts.length > 1) ? Integer.parseInt(parts[1]) : 0;
            if (pm && h < 12) h += 12;
            if (am && h == 12) h = 0;
            return h * 60 + m;
        }

        private void drawStringInRect(Graphics2D g2, String text, Rectangle rect) {
            FontMetrics fm = g2.getFontMetrics();
            // If text too wide, try to wrap on space
            if (fm.stringWidth(text) <= rect.width) {
                int tx = rect.x + (rect.width - fm.stringWidth(text)) / 2;
                int ty = rect.y + (rect.height + fm.getAscent()) / 2 - 2;
                g2.drawString(text, tx, ty);
                return;
            }

            // Try splitting into multiple lines if spaces exist
            String[] words = text.split(" ");
            java.util.List<String> lines = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            for (String w : words) {
                String trial = cur.length() == 0 ? w : cur + " " + w;
                if (fm.stringWidth(trial) <= rect.width) {
                    cur = new StringBuilder(trial);
                } else {
                    if (cur.length() > 0) {
                        lines.add(cur.toString());
                        cur = new StringBuilder(w);
                    } else {
                        // single word too long -> truncate
                        lines.add(truncateToFit(w, fm, rect.width));
                        cur = new StringBuilder();
                    }
                }
            }
            if (cur.length() > 0) lines.add(cur.toString());

            int totalHeight = lines.size() * fm.getHeight();
            int startY = rect.y + (rect.height - totalHeight) / 2 + fm.getAscent() - 2;
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int tx = rect.x + (rect.width - fm.stringWidth(line)) / 2;
                int ty = startY + i * fm.getHeight();
                g2.drawString(line, tx, ty);
            }
        }

        private String truncateToFit(String word, FontMetrics fm, int width) {
            String ell = "...";
            for (int len = word.length(); len > 0; len--) {
                String t = word.substring(0, len) + ell;
                if (fm.stringWidth(t) <= width) return t;
            }
            return word;
        }
    }
    //method for Schedule Content
    private JPanel showScheduleContent(MySinglyLinkedList<String> subItems) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Create main content panel with vertical layout for stacked tables
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // CLASS SCHEDULE Section
        JPanel classScheduleSection = createClassScheduleSection();
        contentPanel.add(classScheduleSection);
        
        // Add minimal spacing between sections
        contentPanel.add(Box.createVerticalStrut(5));
        
        // WEEKLY VIEW Section
        JPanel weeklyViewSection = createWeeklyViewSection();
        contentPanel.add(weeklyViewSection);
        
        // Wrap in scroll pane for better usability
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createClassScheduleSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        
        // CLASS SCHEDULE Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(13, 37, 73));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel titleLabel = new JLabel("CLASS SCHEDULE");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        section.add(headerPanel, BorderLayout.NORTH);
        
        // Class Schedule Table
        JPanel tablePanel = createClassScheduleTable();
        section.add(tablePanel, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createWeeklyViewSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        
        // WEEKLY VIEW Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(13, 37, 73));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel titleLabel = new JLabel("WEEKLY VIEW");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        section.add(headerPanel, BorderLayout.NORTH);
        
        // Weekly View Table
        JPanel tablePanel = createWeeklyViewTable();
        section.add(tablePanel, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createClassScheduleTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Get course data
        List<CourseScheduleItem> courses = getSampleCourses();
        int totalUnits = courses.stream().mapToInt(c -> c.units).sum();
        
        // Create table with proper columns matching the image
        String[] columnNames = {"Class Code", "Course Number", "Course Description", "Units", "Schedule", "Days", "Room", "Module"};
        
        // Build table data
        Object[][] data = new Object[courses.size()][columnNames.length];
        for (int i = 0; i < courses.size(); i++) {
            CourseScheduleItem course = courses.get(i);
            data[i][0] = course.classCode;
            data[i][1] = course.courseNumber;
            data[i][2] = course.courseDescription;
            data[i][3] = course.units;
            data[i][4] = formatTime(course.startTime) + " - " + formatTime(course.endTime);
            data[i][5] = course.days;
            data[i][6] = course.room;
            data[i][7] = getModuleFromRoom(course.room); // Extract module from room
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setSelectionBackground(new Color(230, 240, 255));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Class Code
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Course Number
        table.getColumnModel().getColumn(2).setPreferredWidth(300); // Course Description
        table.getColumnModel().getColumn(3).setPreferredWidth(50);  // Units
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Schedule
        table.getColumnModel().getColumn(5).setPreferredWidth(80);  // Days
        table.getColumnModel().getColumn(6).setPreferredWidth(80);  // Room
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Module
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        int tableHeight = table.getPreferredSize().height;
        int headerHeight = table.getTableHeader().getPreferredSize().height;
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, tableHeight + headerHeight));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer with total units and block info
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JLabel unitsLabel = new JLabel("Total Units: " + totalUnits);
        unitsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        footerPanel.add(unitsLabel, BorderLayout.WEST);
        
        JLabel blockLabel = new JLabel("BLOCK: BSIT 2-3");
        blockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        blockLabel.setForeground(new Color(100, 100, 100));
        footerPanel.add(blockLabel, BorderLayout.EAST);
        
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createWeeklyViewTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Get course data
        List<CourseScheduleItem> courses = getSampleCourses();
        
        // Time slots (30-minute increments) based on min/max course times
        LocalTime minStart = courses.stream().map(c -> c.startTime).min(LocalTime::compareTo).orElse(LocalTime.of(7, 0));
        LocalTime maxEnd = courses.stream().map(c -> c.endTime).max(LocalTime::compareTo).orElse(LocalTime.of(18, 0));
        minStart = roundDownToHalfHour(minStart);
        maxEnd = roundUpToHalfHour(maxEnd);
        
        // Create weekly view table
        String[] columnNames = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        
        // Build data rows for weekly view
        List<Object[]> rows = new ArrayList<>();
        
        // Add "all-day" row
        Object[] allDayRow = new Object[columnNames.length];
        allDayRow[0] = "all-day";
        for (int i = 1; i < columnNames.length; i++) {
            allDayRow[i] = "";
        }
        rows.add(allDayRow);
        
        // Add time slots
        for (LocalTime slot = minStart; slot.isBefore(maxEnd); slot = slot.plusMinutes(60)) { // 1-hour increments for better visibility
            Object[] row = new Object[columnNames.length];
            row[0] = formatTime(slot);
            row[1] = ""; // Sunday
            row[2] = getWeeklyCourseLabelAtTime(courses, slot, "M");  // Monday
            row[3] = getWeeklyCourseLabelAtTime(courses, slot, "T");  // Tuesday
            row[4] = getWeeklyCourseLabelAtTime(courses, slot, "W");  // Wednesday
            row[5] = getWeeklyCourseLabelAtTime(courses, slot, "TH"); // Thursday
            row[6] = getWeeklyCourseLabelAtTime(courses, slot, "F");  // Friday
            row[7] = getWeeklyCourseLabelAtTime(courses, slot, "S"); // Saturday
            rows.add(row);
        }
        
        DefaultTableModel weeklyModel = new DefaultTableModel(rows.toArray(new Object[0][]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable weeklyTable = new JTable(weeklyModel);
        weeklyTable.setRowHeight(40);
        weeklyTable.setFont(new Font("Arial", Font.PLAIN, 11));
        weeklyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        weeklyTable.getTableHeader().setBackground(new Color(240, 240, 240));
        weeklyTable.getTableHeader().setReorderingAllowed(false);
        weeklyTable.setAutoCreateRowSorter(false);
        weeklyTable.setGridColor(new Color(220, 220, 220));
        weeklyTable.setShowGrid(true);
        
        // Set column widths for weekly view
        weeklyTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Time
        for (int i = 1; i < columnNames.length; i++) {
            weeklyTable.getColumnModel().getColumn(i).setPreferredWidth(120); // Days
        }
        
        // Custom cell renderer for course blocks
        weeklyTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null && !value.toString().isEmpty() && column > 0) {
                    // Color course blocks
                    c.setBackground(new Color(70, 130, 180, 100)); // Semi-transparent blue
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else if (column == 0) {
                    // Time column
                    c.setBackground(new Color(245, 245, 245));
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    c.setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        JScrollPane weeklyScrollPane = new JScrollPane(weeklyTable);
        weeklyScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(weeklyScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String getWeeklyCourseLabelAtTime(List<CourseScheduleItem> courses, LocalTime slot, String day) {
        for (CourseScheduleItem c : courses) {
            if (c.occursOn(day) && !slot.isBefore(c.startTime) && slot.isBefore(c.endTime)) {
                return "<html><center>" + c.courseNumber + "<br>(" + c.room + ")</center></html>";
            }
        }
        return "";
    }
    
    private String getModuleFromRoom(String room) {
        if (room == null || room.isEmpty()) return "";
        // Extract first character/letter from room as module
        return room.substring(0, 1);
    }

    private String courseLabelAtTime(List<CourseScheduleItem> courses, LocalTime slot, String day) {
        for (CourseScheduleItem c : courses) {
            if (c.occursOn(day) && !slot.isBefore(c.startTime) && slot.isBefore(c.endTime)) {
                return c.courseNumber + " (" + c.room + ")";
            }
        }
        return "";
    }

    private static LocalTime roundDownToHalfHour(LocalTime time) {
        int minute = time.getMinute();
        return time.withMinute(minute < 30 ? 0 : 30).withSecond(0).withNano(0);
    }

    private static LocalTime roundUpToHalfHour(LocalTime time) {
        int minute = time.getMinute();
        if (minute == 0 || minute == 30) {
            return time.withSecond(0).withNano(0);
        }
        LocalTime base = time.withSecond(0).withNano(0);
        return minute < 30 ? base.withMinute(30) : base.plusHours(1).withMinute(0);
    }

    private static String formatTimeRange(LocalTime start, LocalTime end) {
        return formatTime(start) + "-" + formatTime(end);
    }

    private static String formatTime(LocalTime t) {
        int hour = t.getHour();
        int minute = t.getMinute();
        String ampm = hour < 12 ? "AM" : "PM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;
        return String.format("%d:%02d %s", displayHour, minute, ampm);
    }

    // Lightweight course model aligned with provided fields
    private static class CourseScheduleItem {
        final String classCode;
        final String courseNumber;
        final String courseDescription;
        final int units;
        final LocalTime startTime;
        final LocalTime endTime;
        final String days;
        final String room;
        final Set<String> daySet;

        CourseScheduleItem(String classCode, String courseNumber, String courseDescription,
                            int units, LocalTime startTime, LocalTime endTime, String days, String room) {
            this.classCode = classCode;
            this.courseNumber = courseNumber;
            this.courseDescription = courseDescription;
            this.units = units;
            this.startTime = startTime;
            this.endTime = endTime;
            this.days = days;
            this.room = room;
            this.daySet = parseDays(days);
        }

        boolean occursOn(String day) {
            return daySet.contains(day);
        }

        private static Set<String> parseDays(String s) {
            Set<String> set = new HashSet<>();
            if (s == null) return set;
            String str = s.trim().toUpperCase();
            // Handle common multi-letter tokens first
            if (str.contains("TH")) {
                set.add("TH");
                str = str.replace("TH", "");
            }
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                switch (ch) {
                    case 'M': set.add("M"); break;
                    case 'T': set.add("T"); break;
                    case 'W': set.add("W"); break;
                    case 'F': set.add("F"); break;
                    case 'S': set.add("S"); break;
                    default: break;
                }
            }
            return set;
        }
    }

    private List<CourseScheduleItem> sampleCourses;

    private List<CourseScheduleItem> getSampleCourses() {
        if (sampleCourses == null) {
            sampleCourses = new ArrayList<>();
            // Sample data matching the image provided
            sampleCourses.add(new CourseScheduleItem("7024", "NSTP-CWTS 1", "FOUNDATIONS OF SERVICE", 3,
                    LocalTime.of(13, 30), LocalTime.of(14, 30), "MWF", "D906"));
            sampleCourses.add(new CourseScheduleItem("9454", "GSTS", "SCIENCE, TECHNOLOGY, AND SOCIETY", 3,
                    LocalTime.of(9, 30), LocalTime.of(10, 30), "TThS", "D504"));
            sampleCourses.add(new CourseScheduleItem("9455", "GENVI", "ENVIRONMENTAL SCIENCE", 3,
                    LocalTime.of(9, 30), LocalTime.of(10, 30), "MWF", "D503"));
            sampleCourses.add(new CourseScheduleItem("9456", "CFE 103", "CATHOLIC FOUNDATION OF MISSION", 3,
                    LocalTime.of(13, 30), LocalTime.of(14, 30), "TThS", "D503"));
            sampleCourses.add(new CourseScheduleItem("9457", "IT 211", "REQUIREMENTS ANALYSIS AND MODELING", 3,
                    LocalTime.of(10, 30), LocalTime.of(11, 30), "MWF", "D511"));
            sampleCourses.add(new CourseScheduleItem("9458A", "IT 212", "DATA STRUCTURES (LEC)", 2,
                    LocalTime.of(14, 30), LocalTime.of(15, 30), "TF", "D513"));
            sampleCourses.add(new CourseScheduleItem("9458B", "IT 212L", "DATA STRUCTURES (LAB)", 1,
                    LocalTime.of(16, 0), LocalTime.of(17, 30), "TF", "D522"));
            sampleCourses.add(new CourseScheduleItem("9459A", "IT 213", "NETWORK FUNDAMENTALS (LEC)", 2,
                    LocalTime.of(8, 30), LocalTime.of(9, 30), "TF", "D513"));
            sampleCourses.add(new CourseScheduleItem("9459B", "IT 213L", "NETWORK FUNDAMENTALS (LAB)", 1,
                    LocalTime.of(11, 30), LocalTime.of(13, 0), "TF", "D528"));
            sampleCourses.add(new CourseScheduleItem("9547", "FIT OA", "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (OUTDOOR AND ADVENTURE ACTIVITIES)", 2,
                    LocalTime.of(15, 30), LocalTime.of(17, 30), "TH", "D221"));
        }
        return sampleCourses;
    }
    // method for attendance Content
    private Component showAttendanceContent(MySinglyLinkedList<String> subItems) {
        JPanel attendancePanel = new JPanel(new BorderLayout());
        attendancePanel.setBackground(new Color(240, 240, 240));
        
        // Get attendance records for current student
        MySinglyLinkedList<AttendanceRecord> attendanceRecords = loadAttendanceRecords();
        MySinglyLinkedList<AttendanceRecord> absencesAndTardies = getAbsencesAndTardies(attendanceRecords);
        
        if (absencesAndTardies.getSize() == 0) {
            // Show "Great! No Absences/Tardiness were found" message
            attendancePanel.add(createNoAbsencesPanel(), BorderLayout.CENTER);
        } else {
            // Show detailed absence/tardy records
            attendancePanel.add(createAbsenceTardyPanel(absencesAndTardies), BorderLayout.CENTER);
        }
        
        return attendancePanel;
    }
    
    // Create the "Great!" panel when no absences/tardiness found
    private JPanel createNoAbsencesPanel() {
        JPanel noAbsencesPanel = new JPanel(new BorderLayout());
        noAbsencesPanel.setBackground(Color.WHITE);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));
        
        // "Great!" title with emoji
        JLabel titleLabel = new JLabel("Great!👍");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(80, 80, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Message
        JLabel messageLabel = new JLabel("No Absences/Tardiness were found.");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        messageLabel.setForeground(new Color(80, 80, 80));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // "Click here to go back home" link
        JLabel linkLabel = new JLabel("<html><u>click here to go back home</u></html>");
        linkLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        linkLabel.setForeground(new Color(51, 122, 183));
        linkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Navigate back to home - you can implement this navigation logic
                showHomeContent();
            }
        });
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(linkLabel);
        
        noAbsencesPanel.add(contentPanel, BorderLayout.CENTER);
        return noAbsencesPanel;
    }
    
    // Create the detailed absence/tardy panel
    private JPanel createAbsenceTardyPanel(MySinglyLinkedList<AttendanceRecord> absencesAndTardies) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Create scroll pane for the content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add readmission records section
        contentPanel.add(createReadmissionRecordsSection(absencesAndTardies));
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Group records by subject and add subject sections
        MySinglyLinkedList<String> subjects = getUniqueSubjects(absencesAndTardies);
        for (int i = 0; i < subjects.getSize(); i++) {
            String subject = subjects.get(i);
            MySinglyLinkedList<AttendanceRecord> subjectRecords = getRecordsBySubject(absencesAndTardies, subject);
            contentPanel.add(createSubjectSection(subject, subjectRecords));
            contentPanel.add(Box.createVerticalStrut(15));
        }
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    // Create readmission records section
    private JPanel createReadmissionRecordsSection(MySinglyLinkedList<AttendanceRecord> records) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(51, 122, 183));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel headerIcon = new JLabel("📄");
        headerIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        headerIcon.setForeground(Color.WHITE);
        
        JLabel headerTitle = new JLabel("READMISSION RECORDS");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setForeground(Color.WHITE);
        
        headerPanel.add(headerIcon);
        headerPanel.add(Box.createHorizontalStrut(5));
        headerPanel.add(headerTitle);
        
        // Table for readmission records
        String[] columnNames = {"Date Readmitted", "Date Absent", "Status", "Reason / Detail", "Remarks"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add sample readmission record (you can populate this with actual data)
        // For now, adding the example from the image
        tableModel.addRow(new Object[]{
            "SEP-04-2025",
            "SEP-03-2025", 
            "Excused",
            "SICKNESS - LBM/ STOMACH ACHE/ACUTE GASTROENTERITIS",
            "W/ MEDCERT ACUTE GASTRO, ALL SUBJECTS"
        });
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setGridColor(new Color(200, 200, 200));
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(0, 80));
        
        sectionPanel.add(headerPanel, BorderLayout.NORTH);
        sectionPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return sectionPanel;
    }
    
    // Create subject section with absence/tardy records
    private JPanel createSubjectSection(String subjectName, MySinglyLinkedList<AttendanceRecord> records) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Header with subject info
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(13, 37, 73));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel headerIcon = new JLabel("📚");
        headerIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        headerIcon.setForeground(Color.WHITE);
        
        // Format subject name with code (you can customize this based on your data)
        String displayName = getSubjectDisplayName(subjectName);
        JLabel headerTitle = new JLabel(displayName);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setForeground(Color.WHITE);
        
        headerPanel.add(headerIcon);
        headerPanel.add(Box.createHorizontalStrut(5));
        headerPanel.add(headerTitle);
        
        // Content panel with table and button
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table for absences/tardies
        String[] columnNames = {"Date of absence/tardy", "Date Dropped", "Date Claimed", "Remarks", "Type"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Populate table with records
        for (int i = 0; i < records.getSize(); i++) {
            AttendanceRecord record = records.get(i);
            String type = record.getStatus().equals("Absent") ? "Absent" : "Tardy";
            tableModel.addRow(new Object[]{
                record.getDate().format(DateTimeFormatter.ofPattern("MMM-dd-yyyy")),
                "", // Date Dropped - empty for now
                "", // Date Claimed - empty for now
                record.getRemarks() != null ? record.getRemarks() : "",
                type
            });
        }
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(230, 230, 230));
        table.setGridColor(new Color(200, 200, 200));
        
        // Set specific column widths
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(0, Math.min(100, (records.getSize() + 1) * 25 + 5)));
        
        // Apply reason button
        JButton applyReasonButton = new JButton("✏️ Apply Reason of absence/tardy from the selected date/s");
        applyReasonButton.setFont(new Font("Arial", Font.PLAIN, 12));
        applyReasonButton.setBackground(new Color(240, 173, 78));
        applyReasonButton.setForeground(Color.BLACK);
        applyReasonButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        applyReasonButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyReasonButton.addActionListener(e -> showReasonDialog(table, records));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(applyReasonButton);
        
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        sectionPanel.add(headerPanel, BorderLayout.NORTH);
        sectionPanel.add(contentPanel, BorderLayout.CENTER);
        
        return sectionPanel;
    }
    
    // Show reason input dialog
    private void showReasonDialog(JTable table, MySinglyLinkedList<AttendanceRecord> records) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Ooops!\n\nPlease select date/s of absence/tardy.", 
                "Reason/s of absences/tardiness in class: (9457)", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create reason input dialog
        JDialog reasonDialog = new JDialog(this, "Reason/s of absences/tardiness in class: (9457)", true);
        reasonDialog.setSize(500, 400);
        reasonDialog.setLocationRelativeTo(this);
        reasonDialog.setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(13, 37, 73));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel headerIcon = new JLabel("📅");
        headerIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        headerIcon.setForeground(Color.WHITE);
        
        JLabel headerTitle = new JLabel("Selected Date of absences/tardiness");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        headerTitle.setForeground(Color.WHITE);
        
        headerPanel.add(headerIcon);
        headerPanel.add(Box.createHorizontalStrut(5));
        headerPanel.add(headerTitle);
        
        // Date display panel
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        datePanel.setBackground(Color.WHITE);
        
        JLabel dateLabel = new JLabel("Date");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        AttendanceRecord selectedRecord = records.get(selectedRow);
        JTextField dateField = new JTextField(selectedRecord.getDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy, EEEE")));
        dateField.setEditable(false);
        dateField.setBackground(new Color(245, 245, 245));
        dateField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        datePanel.add(dateLabel, BorderLayout.NORTH);
        datePanel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        datePanel.add(dateField, BorderLayout.SOUTH);
        
        // Reason input panel
        JPanel reasonPanel = new JPanel(new BorderLayout());
        reasonPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        reasonPanel.setBackground(Color.WHITE);
        
        JPanel reasonHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reasonHeaderPanel.setBackground(new Color(13, 37, 73));
        reasonHeaderPanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel reasonIcon = new JLabel("💭");
        reasonIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        reasonIcon.setForeground(Color.WHITE);
        
        JLabel reasonTitle = new JLabel("Enter your reason");
        reasonTitle.setFont(new Font("Arial", Font.BOLD, 14));
        reasonTitle.setForeground(Color.WHITE);
        
        reasonHeaderPanel.add(reasonIcon);
        reasonHeaderPanel.add(Box.createHorizontalStrut(5));
        reasonHeaderPanel.add(reasonTitle);
        
        JTextArea reasonTextArea = new JTextArea(5, 30);
        reasonTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        reasonTextArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        reasonTextArea.setText(selectedRecord.getRemarks());
        
        JScrollPane reasonScrollPane = new JScrollPane(reasonTextArea);
        reasonScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        reasonPanel.add(reasonHeaderPanel, BorderLayout.NORTH);
        reasonPanel.add(reasonScrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton submitButton = new JButton("📝 Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.setBackground(new Color(51, 122, 183));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> {
            // Update the reason in the record
            String newReason = reasonTextArea.getText().trim();
            selectedRecord.setRemarks(newReason);
            
            // Update the table display
            table.setValueAt(newReason, selectedRow, 3);
            
            // TODO: Save to file - implement this when adding backend functionality
            // saveAttendanceRecords(records);
            
            reasonDialog.dispose();
            JOptionPane.showMessageDialog(ISLUStudentPortal.this, "Reason updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(submitButton);
        
        // Fix layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(datePanel, BorderLayout.NORTH);
        centerPanel.add(reasonPanel, BorderLayout.CENTER);
        
        reasonDialog.add(headerPanel, BorderLayout.NORTH);
        reasonDialog.add(centerPanel, BorderLayout.CENTER);
        reasonDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        reasonDialog.setVisible(true);
    }
    
    // Helper method to get subject display name with code
    private String getSubjectDisplayName(String subjectName) {
        // Map subject names to codes - you can customize this based on your data
        switch (subjectName) {
            case "NSTP-CWTS 1": return "9455 - GENVI (ENVIRONMENTAL SCIENCE)";
            case "Programming 2": return "9457 - IT 211 (REQUIREMENTS ANALYSIS AND MODELING)";
            case "Data Structures": return "9458A - IT 212 (DATA STRUCTURES (LEC))";
            case "Database Systems": return "9458B - IT 212L (DATA STRUCTURES (LAB))";
            case "Web Development": return "9459 - WD 101 (WEB DEVELOPMENT)";
            default: return subjectName;
        }
    }
    
    // Load attendance records from file
    private MySinglyLinkedList<AttendanceRecord> loadAttendanceRecords() {
        MySinglyLinkedList<AttendanceRecord> records = new MySinglyLinkedList<>();
        
        /* TODO: Faculty Backend Integration
         * This method should load attendance records from the database/file
         * Faculty will mark students as Present/Absent/Late through their interface
         * The data should be stored in attendanceRecords.txt or database
         * Format: StudentID,SubjectCode,SubjectName,Date,Status,Remarks
         * 
         * For now, we'll simulate loading from the existing file structure
         */
        
        try {
            // Read from attendanceRecords.txt - implement actual file reading here
            // This is a placeholder - you'll need to implement the actual file reading
            String currentStudentId = "2250493"; // Get from current session
            
            // Sample data for demonstration - replace with actual file reading
            records.add(new AttendanceRecord("2250493", "NSTP101", "NSTP-CWTS 1", 
                java.time.LocalDate.of(2025, 9, 24), "Absent", "Acute Gastroenteritis"));
            records.add(new AttendanceRecord("2250493", "IT122", "Programming 2", 
                java.time.LocalDate.of(2025, 9, 24), "Absent", "Family emergency"));
            records.add(new AttendanceRecord("2250493", "IT122", "Programming 2", 
                java.time.LocalDate.of(2025, 9, 26), "Absent", "Sick"));
            records.add(new AttendanceRecord("2250493", "IT122", "Programming 2", 
                java.time.LocalDate.of(2025, 9, 29), "Late", "Traffic"));
            records.add(new AttendanceRecord("2250493", "IT211", "Data Structures", 
                java.time.LocalDate.of(2025, 10, 6), "Late", "Overslept"));
            records.add(new AttendanceRecord("2250493", "IT311", "Database Systems", 
                java.time.LocalDate.of(2025, 9, 24), "Absent", "Doctor appointment"));
            records.add(new AttendanceRecord("2250493", "WD101", "Web Development", 
                java.time.LocalDate.of(2025, 9, 24), "Absent", "Personal matter"));
            records.add(new AttendanceRecord("2250493", "WD101", "Web Development", 
                java.time.LocalDate.of(2025, 9, 26), "Absent", "Sick"));
            records.add(new AttendanceRecord("2250493", "WD101", "Web Development", 
                java.time.LocalDate.of(2025, 9, 29), "Late", "Bus delay"));
            records.add(new AttendanceRecord("2250493", "WD101", "Web Development", 
                java.time.LocalDate.of(2025, 10, 1), "Late", "Traffic"));
            
        } catch (Exception e) {
            System.err.println("Error loading attendance records: " + e.getMessage());
        }
        
        return records;
    }
    
    // Filter records to get only absences and tardies
    private MySinglyLinkedList<AttendanceRecord> getAbsencesAndTardies(MySinglyLinkedList<AttendanceRecord> allRecords) {
        MySinglyLinkedList<AttendanceRecord> filtered = new MySinglyLinkedList<>();
        
        for (int i = 0; i < allRecords.getSize(); i++) {
            AttendanceRecord record = allRecords.get(i);
            if ("Absent".equals(record.getStatus()) || "Late".equals(record.getStatus())) {
                filtered.add(record);
            }
        }
        
        return filtered;
    }
    
    // Get unique subjects from records
    private MySinglyLinkedList<String> getUniqueSubjects(MySinglyLinkedList<AttendanceRecord> records) {
        MySinglyLinkedList<String> subjects = new MySinglyLinkedList<>();
        
        for (int i = 0; i < records.getSize(); i++) {
            String subject = records.get(i).getSubjectName();
            boolean found = false;
            for (int j = 0; j < subjects.getSize(); j++) {
                if (subjects.get(j).equals(subject)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                subjects.add(subject);
            }
        }
        
        return subjects;
    }
    
    // Get records for a specific subject
    private MySinglyLinkedList<AttendanceRecord> getRecordsBySubject(MySinglyLinkedList<AttendanceRecord> allRecords, String subject) {
        MySinglyLinkedList<AttendanceRecord> subjectRecords = new MySinglyLinkedList<>();
        
        for (int i = 0; i < allRecords.getSize(); i++) {
            AttendanceRecord record = allRecords.get(i);
            if (record.getSubjectName().equals(subject)) {
                subjectRecords.add(record);
            }
        }
        
        return subjectRecords;
    }
    
    // Navigate to home content
    private void showHomeContent() {
        // Clear current content and reset layout for home
        contentPanel.removeAll();
        contentPanel.setLayout(new GridLayout(1, 2, 10, 10));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Recreate home content
        setupLayout(null); // Pass null since home doesn't need subItems
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // method for Personal Details Content
    private void showPersonalDetailsContent(MySinglyLinkedList<String> subItems) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(240, 240, 240));
        
        // Main profile panel
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header with "User Profile" title - improved layout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(13, 37, 73));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Left side with icon and title
        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftHeaderPanel.setBackground(new Color(13, 37, 73));
        
        JLabel headerIcon = new JLabel("👤");
        headerIcon.setForeground(Color.WHITE);
        headerIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        leftHeaderPanel.add(headerIcon);
        
        JLabel headerLabel = new JLabel("User Profile");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        leftHeaderPanel.add(headerLabel);
        
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        profilePanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content area with two columns
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Left side - Profile picture and action buttons (Sidebar Panel)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(248, 248, 248)); // Light gray background to match image
        leftPanel.setPreferredSize(new Dimension(220, 0));
        leftPanel.setMinimumSize(new Dimension(220, 400));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        leftPanel.setOpaque(true); // Ensure the panel is visible
        leftPanel.setVisible(true); // Explicitly set visible
        
        // Profile picture placeholder with better styling
        JLabel profilePicture = new JLabel();
        ImageIcon profileIcon = createProfilePictureIcon();
        if (profileIcon != null) {
            profilePicture.setIcon(profileIcon);
        } else {
            // Fallback if icon creation fails
            profilePicture.setText("👤");
            profilePicture.setFont(new Font("Arial", Font.PLAIN, 48));
            profilePicture.setForeground(new Color(180, 180, 180));
        }
        profilePicture.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePicture.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        profilePicture.setPreferredSize(new Dimension(140, 140));
        profilePicture.setMaximumSize(new Dimension(140, 140));
        profilePicture.setMinimumSize(new Dimension(140, 140));
        profilePicture.setOpaque(true);
        profilePicture.setBackground(Color.WHITE);
        profilePicture.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicture.setVerticalAlignment(SwingConstants.CENTER);
        profilePicture.setVisible(true); // Ensure profile picture is visible
        
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(profilePicture);
        leftPanel.add(Box.createVerticalStrut(10));
        
        // Student name label under profile picture
        JLabel studentNameLabel = new JLabel(studentName);
        studentNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        studentNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        studentNameLabel.setForeground(new Color(60, 60, 60));
        studentNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        studentNameLabel.setVisible(true); // Ensure name label is visible
        leftPanel.add(studentNameLabel);
        
        // Student ID label
        JLabel studentIDLabel = new JLabel("ID: " + studentID);
        studentIDLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        studentIDLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        studentIDLabel.setForeground(new Color(100, 100, 100));
        studentIDLabel.setHorizontalAlignment(SwingConstants.CENTER);
        studentIDLabel.setVisible(true); // Ensure ID label is visible
        leftPanel.add(studentIDLabel);
        leftPanel.add(Box.createVerticalStrut(20));
        
        // Action buttons - styled to match the design with icons
        JButton personalDetailsBtn = createSidebarButton("📋 Personal Details");
        JButton accountInfoBtn = createSidebarButton("🔐 Account Info");  
        JButton changePasswordBtn = createSidebarButton("🔑 Change Password");
        
        // Add action listeners
        personalDetailsBtn.addActionListener(e -> {
            // Reset all button styles
            resetButtonStyles(personalDetailsBtn, accountInfoBtn, changePasswordBtn);
            // Highlight current button
            setButtonSelectedStyle(personalDetailsBtn);
            // Remove existing right panel if it exists (should be at BorderLayout.CENTER)
            Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent != null) {
                mainContentPanel.remove(centerComponent);
            }
            // Add new right panel
            Component rightPanel = showPersonalDetailsInRightPanel();
            mainContentPanel.add(rightPanel, BorderLayout.CENTER);
            
            // Ensure left panel remains visible
            Component westComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (westComponent != null) {
                westComponent.setVisible(true);
            }
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        
        accountInfoBtn.addActionListener(e -> {
            // Reset all button styles
            resetButtonStyles(personalDetailsBtn, accountInfoBtn, changePasswordBtn);
            // Highlight current button
            setButtonSelectedStyle(accountInfoBtn);
            // Remove existing right panel if it exists (should be at BorderLayout.CENTER)
            Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent != null) {
                mainContentPanel.remove(centerComponent);
            }
            // Add new right panel
            Component rightPanel = showAccountInfo();
            mainContentPanel.add(rightPanel, BorderLayout.CENTER);
            
            // Ensure left panel remains visible
            Component westComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (westComponent != null) {
                westComponent.setVisible(true);
            }
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        
        changePasswordBtn.addActionListener(e -> {
            // Reset all button styles
            resetButtonStyles(personalDetailsBtn, accountInfoBtn, changePasswordBtn);
            // Highlight current button
            setButtonSelectedStyle(changePasswordBtn);
            // Remove existing right panel if it exists (should be at BorderLayout.CENTER)
            Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent != null) {
                mainContentPanel.remove(centerComponent);
            }
            // Add new right panel
            Component rightPanel = showPasswordChangeInRightPanel();
            mainContentPanel.add(rightPanel, BorderLayout.CENTER);
            
            // Ensure left panel remains visible
            Component westComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (westComponent != null) {
                westComponent.setVisible(true);
            }
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        
        // Ensure buttons are visible and properly added
        personalDetailsBtn.setVisible(true);
        accountInfoBtn.setVisible(true);
        changePasswordBtn.setVisible(true);
        
        leftPanel.add(personalDetailsBtn);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(accountInfoBtn);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(changePasswordBtn);
        leftPanel.add(Box.createVerticalGlue());
        
        // Set Personal Details as the default selected button
        setButtonSelectedStyle(personalDetailsBtn);
        
        mainContentPanel.add(leftPanel, BorderLayout.WEST);
        
        // Show Personal Details by default
        SwingUtilities.invokeLater(() -> {
            // Remove existing right panel if it exists (should be at BorderLayout.CENTER)
            Component centerComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent != null) {
                mainContentPanel.remove(centerComponent);
            }
            Component rightPanel = showPersonalDetailsInRightPanel();
            mainContentPanel.add(rightPanel, BorderLayout.CENTER);
            
            // Ensure left panel remains visible
            Component westComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (westComponent != null) {
                westComponent.setVisible(true);
            }
            
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        });
        
        profilePanel.add(mainContentPanel, BorderLayout.CENTER);
        contentPanel.add(profilePanel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Creates a styled sidebar button
     */
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(170, 40));
        button.setMinimumSize(new Dimension(170, 40));
        button.setMaximumSize(new Dimension(170, 40));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(60, 60, 60));
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Check if button is selected (has thick dark border)
                if (button.getBorder() instanceof CompoundBorder) {
                    CompoundBorder border = (CompoundBorder) button.getBorder();
                    if (border.getOutsideBorder() instanceof LineBorder) {
                        LineBorder lineBorder = (LineBorder) border.getOutsideBorder();
                        // Don't change hover for selected button (thick border = 2)
                        if (lineBorder.getThickness() != 2) {
                            button.setBackground(new Color(240, 240, 240));
                        }
                    }
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Check if button is selected (has thick dark border)
                if (button.getBorder() instanceof CompoundBorder) {
                    CompoundBorder border = (CompoundBorder) button.getBorder();
                    if (border.getOutsideBorder() instanceof LineBorder) {
                        LineBorder lineBorder = (LineBorder) border.getOutsideBorder();
                        // Don't change hover for selected button (thick border = 2)
                        if (lineBorder.getThickness() != 2) {
                            button.setBackground(Color.WHITE);
                        }
                    }
                }
            }
        });
        
        return button;
    }
    
    /**
     * Creates a profile picture icon (gray silhouette)
     */
    private ImageIcon createProfilePictureIcon() {
        try {
            // Create a profile picture placeholder matching the reference images
            int size = 140;
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill background with white
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, size, size);
            
            // Draw border
            g2d.setColor(new Color(220, 220, 220));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(1, 1, size - 2, size - 2);
            
            // Draw person silhouette similar to reference images
            g2d.setColor(new Color(180, 180, 180));
            
            // Head (circle)
            int headSize = size / 3;
            int headX = (size - headSize) / 2;
            int headY = size / 4;
            g2d.fillOval(headX, headY, headSize, headSize);
            
            // Body (rounded rectangle)
            int bodyWidth = size * 2 / 3;
            int bodyHeight = size / 2;
            int bodyX = (size - bodyWidth) / 2;
            int bodyY = headY + headSize + 5;
            g2d.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 20, 20);
            
            g2d.dispose();
            return new ImageIcon(image);
        } catch (Exception e) {
            // Return null if image creation fails - fallback will be used
            System.err.println("Failed to create profile picture icon: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Shows the password change form
     */
    private void showPasswordChangeForm() {
        JDialog passwordDialog = new JDialog(this, "Change Password", true);
        passwordDialog.setSize(400, 300);
        passwordDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Old Password
        JPanel oldPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        oldPassPanel.add(new JLabel("Old Password:"));
        JPasswordField oldPasswordField = new JPasswordField(20);
        oldPassPanel.add(oldPasswordField);
        formPanel.add(oldPassPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // New Password
        JPanel newPassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        newPassPanel.add(new JLabel("New Password:"));
        JPasswordField newPasswordField = new JPasswordField(20);
        newPassPanel.add(newPasswordField);
        formPanel.add(newPassPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Retype Password
        JPanel retypePassPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        retypePassPanel.add(new JLabel("Retype Password:"));
        JPasswordField retypePasswordField = new JPasswordField(20);
        retypePassPanel.add(retypePasswordField);
        formPanel.add(retypePassPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("💾 Save");
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setForeground(Color.WHITE);
        
        JButton cancelButton = new JButton("❌ Cancel");
        cancelButton.setBackground(new Color(220, 220, 220));
        
        saveButton.addActionListener(e -> {
            String oldPass = new String(oldPasswordField.getPassword());
            String newPass = new String(newPasswordField.getPassword());
            String retypePass = new String(retypePasswordField.getPassword());
            
            if (oldPass.isEmpty() || newPass.isEmpty() || retypePass.isEmpty()) {
                JOptionPane.showMessageDialog(passwordDialog, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPass.equals(retypePass)) {
                JOptionPane.showMessageDialog(passwordDialog, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password using DataManager
            if (DataManager.updateStudentPassword(studentID, newPass)) {
                JOptionPane.showMessageDialog(passwordDialog, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                passwordDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(passwordDialog, "Failed to update password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> passwordDialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        formPanel.add(buttonPanel);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        passwordDialog.add(mainPanel);
        passwordDialog.setVisible(true);
    }
    
    /**
     * Shows the Account Information in the right panel
     *
     * @return
     */
    private Component showAccountInfo() {
        // Create new right panel with account information
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create account information section with image styling
        JPanel accountInfoPanel = createImageStyledSectionPanel("ACCOUNT INFORMATION");
        
        // Get student info from database
        StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
        String accountName = studentInfo != null ? studentInfo.getFullName() : "N/A";
        
        // Get registration date from database or use a generic message
        String registrationDate = "N/A"; // Default if no data available
        if (studentInfo != null) {
            // You could add a registration date field to StudentInfo class if needed
            registrationDate = "Available in Database"; // Placeholder - can be enhanced
        }
        
        // Add account details using actual database data
        addPersonalDetailRow(accountInfoPanel, "User ID/Login ID:", studentID);
        addPersonalDetailRow(accountInfoPanel, "Account Name:", accountName);
        addPersonalDetailRow(accountInfoPanel, "Date Registered:", registrationDate);
        addPersonalDetailRow(accountInfoPanel, "Account Type:", "Student");
        
        rightPanel.add(accountInfoPanel);

        return rightPanel;
    }
    
    /**
     * Resets all button styles to default
     */
    private void resetButtonStyles(JButton btn1, JButton btn2, JButton btn3) {
        JButton[] buttons = {btn1, btn2, btn3};
        for (JButton btn : buttons) {
            setButtonDefaultStyle(btn);
        }
    }
    
    /**
     * Sets a button to the default unselected style
     */
    private void setButtonDefaultStyle(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }
    
    /**
     * Sets a button to the selected style
     */
    private void setButtonSelectedStyle(JButton button) {
        button.setBackground(new Color(13, 37, 73));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(13, 37, 73), 2),
            BorderFactory.createEmptyBorder(8, 13, 8, 13)
        ));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }
    
    /**
     * Shows the Password Change form in the right panel
     *
     * @return
     */
    private Component showPasswordChangeInRightPanel() {
        // Create new right panel with password change form
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create password change section with image styling
        JPanel passwordFormPanel = new JPanel();
        passwordFormPanel.setLayout(new BoxLayout(passwordFormPanel, BoxLayout.Y_AXIS));
        passwordFormPanel.setBackground(Color.WHITE);
        passwordFormPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Section header with dark blue background
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(13, 37, 73));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel titleLabel = new JLabel("CHANGE PASSWORD");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        passwordFormPanel.add(headerPanel);
        
        // Add password requirements information
        JPanel requirementsPanel = new JPanel();
        requirementsPanel.setLayout(new BoxLayout(requirementsPanel, BoxLayout.Y_AXIS));
        requirementsPanel.setBackground(Color.WHITE);
        requirementsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        
        JLabel requirementsTitle = new JLabel("Password Requirements:");
        requirementsTitle.setFont(new Font("Arial", Font.BOLD, 12));
        requirementsTitle.setForeground(new Color(60, 60, 60));
        requirementsPanel.add(requirementsTitle);
        
        String[] requirements = {
            "• At least 8 characters long",
            "• At least one uppercase letter (A-Z)",
            "• At least one lowercase letter (a-z)",
            "• At least one number (0-9)",
            "• At least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)"
        };
        
        for (String requirement : requirements) {
            JLabel reqLabel = new JLabel(requirement);
            reqLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            reqLabel.setForeground(new Color(80, 80, 80));
            reqLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));
            requirementsPanel.add(reqLabel);
        }
        
        passwordFormPanel.add(requirementsPanel);
        
        // Store password fields for later access
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField retypePasswordField = new JPasswordField();
        
        // Add password fields with proper styling
        addEditablePasswordField(passwordFormPanel, "Old Password:", oldPasswordField);
        addEditablePasswordField(passwordFormPanel, "New Password:", newPasswordField);
        addEditablePasswordField(passwordFormPanel, "Retype Password:", retypePasswordField);
        
        // Add buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(13, 37, 73));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setPreferredSize(new Dimension(80, 30));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add action listeners for the buttons
        saveButton.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String retypePassword = new String(retypePasswordField.getPassword());
            
            // Validate passwords
            if (oldPassword.isEmpty() || newPassword.isEmpty() || retypePassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all password fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(retypePassword)) {
                JOptionPane.showMessageDialog(this, "New password and retype password do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate old password against database
            if (!DataManager.authenticateUser(studentID, oldPassword)) {
                JOptionPane.showMessageDialog(this, "Old password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validate new password requirements
            String passwordValidationError = validatePasswordRequirements(newPassword);
            if (passwordValidationError != null) {
                JOptionPane.showMessageDialog(this, passwordValidationError, "Password Requirements", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update password in database
            if (DataManager.updateStudentPassword(studentID, newPassword)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear the fields
                oldPasswordField.setText("");
                newPasswordField.setText("");
                retypePasswordField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> {
            // Clear the fields
            oldPasswordField.setText("");
            newPasswordField.setText("");
            retypePasswordField.setText("");
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        passwordFormPanel.add(buttonPanel);
        rightPanel.add(passwordFormPanel);

        return rightPanel;
    }
    
    /**
     * Adds an editable password field to the panel with proper styling
     */
    private void addEditablePasswordField(JPanel panel, String label, JPasswordField passwordField) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        labelComponent.setForeground(new Color(60, 60, 60));
        labelComponent.setPreferredSize(new Dimension(150, 25));
        
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setPreferredSize(new Dimension(300, 25));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        fieldPanel.add(labelComponent, BorderLayout.WEST);
        fieldPanel.add(passwordField, BorderLayout.CENTER);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        
        panel.add(fieldPanel);
        panel.add(separator);
    }

    /**
     * Adds a password field to the panel
     */
    private void addPasswordField(JPanel panel, String label) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setForeground(new Color(60, 60, 60));
        labelComponent.setPreferredSize(new Dimension(150, 25));
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        passwordField.setPreferredSize(new Dimension(200, 25));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        fieldPanel.add(labelComponent, BorderLayout.WEST);
        fieldPanel.add(passwordField, BorderLayout.CENTER);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        
        panel.add(fieldPanel);
        panel.add(separator);
    }
    
    /**
     * Shows the Personal Details in the right panel while keeping left panel intact
     *
     * @return
     */
    private Component showPersonalDetailsInRightPanel() {
        // Create new right panel with personal details
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create scrollable content for personal details
        JPanel personalDetailsContent = new JPanel();
        personalDetailsContent.setLayout(new BoxLayout(personalDetailsContent, BoxLayout.Y_AXIS));
        personalDetailsContent.setBackground(Color.WHITE);
        
        // Add all the personal details sections with updated styling
        addPersonalDetailsSections(personalDetailsContent);
        
        // Create scroll pane for the content
        JScrollPane rightScrollPane = new JScrollPane(personalDetailsContent);
        rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rightScrollPane.setBorder(null);
        rightScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Add the scroll pane to the right panel
        rightPanel.add(rightScrollPane);

        return rightPanel;
    }
    
    /**
     * Adds all personal details sections to the panel
     */
    private void addPersonalDetailsSections(JPanel parentPanel) {
        // Get student info and profile data from database
        StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
        String profileString = DataManager.getStudentProfile(studentID);
        ProfileData profileData = parseProfileData(profileString);
        
        // Get basic student information
        String birthday = studentInfo != null ? studentInfo.getDateOfBirth() : "N/A";
        String email = studentID + "@slu.edu.ph";
        
        // General Information section
        JPanel generalInfoPanel = createImageStyledSectionPanel("GENERAL INFORMATION");
        addPersonalDetailRow(generalInfoPanel, "Gender:", profileData != null ? profileData.getGender() : "N/A");
        addPersonalDetailRow(generalInfoPanel, "Birthday:", birthday);
        addPersonalDetailRow(generalInfoPanel, "Citizenship:", profileData != null ? profileData.getCitizenship() : "N/A");
        addPersonalDetailRow(generalInfoPanel, "Religion:", profileData != null ? profileData.getReligion() : "N/A");
        addPersonalDetailRow(generalInfoPanel, "Civil Status:", profileData != null ? profileData.getCivilStatus() : "N/A");
        addPersonalDetailRow(generalInfoPanel, "Birthplace:", profileData != null ? profileData.getBirthplace() : "N/A");
        addPersonalDetailRow(generalInfoPanel, "Nationality:", profileData != null ? profileData.getNationality() : "N/A");
        parentPanel.add(generalInfoPanel);
        parentPanel.add(Box.createVerticalStrut(20));
        
        // Contact Information section
        JPanel contactInfoPanel = createImageStyledSectionPanel("CONTACT INFORMATION");
        addPersonalDetailRow(contactInfoPanel, "Home Address:", profileData != null ? profileData.getHomeAddress() : "N/A");
        addPersonalDetailRow(contactInfoPanel, "Home Telephone No:", profileData != null ? profileData.getHomeTel() : "N/A");
        addPersonalDetailRow(contactInfoPanel, "Baguio Address:", profileData != null ? profileData.getBaguioAddress() : "N/A");
        addPersonalDetailRow(contactInfoPanel, "Baguio Telephone No:", profileData != null ? profileData.getBaguioTel() : "N/A");
        addPersonalDetailRow(contactInfoPanel, "Cellphone No:", profileData != null ? profileData.getCellphone() : "N/A");
        addPersonalDetailRow(contactInfoPanel, "Email Address:", email);
        parentPanel.add(contactInfoPanel);
        parentPanel.add(Box.createVerticalStrut(20));
        
        // Contact Persons section  
        JPanel contactPersonsPanel = createImageStyledSectionPanel("CONTACT PERSONS");
        // Parents subsection
        JLabel parentsLabel = new JLabel("Parents");
        parentsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        parentsLabel.setForeground(new Color(60, 60, 60));
        parentsLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
        contactPersonsPanel.add(parentsLabel);
        
        addPersonalDetailRow(contactPersonsPanel, "Father's Name:", profileData != null ? profileData.getFatherName() : "N/A");
        addPersonalDetailRow(contactPersonsPanel, "Occupation:", profileData != null ? profileData.getFatherOcc() : "N/A");
        addPersonalDetailRow(contactPersonsPanel, "Mother's Maiden Name:", profileData != null ? profileData.getMotherName() : "N/A");
        addPersonalDetailRow(contactPersonsPanel, "Occupation:", profileData != null ? profileData.getMotherOcc() : "N/A");
        
        parentPanel.add(contactPersonsPanel);
        
        // Note at the bottom
        JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        notePanel.setBackground(Color.WHITE);
        JLabel noteLabel = new JLabel("NOTE: For corrections please email records@slu.edu.ph");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        noteLabel.setForeground(new Color(100, 100, 100));
        notePanel.add(noteLabel);
        parentPanel.add(Box.createVerticalStrut(20));
        parentPanel.add(notePanel);
    }
    
    /**
     * Creates a section panel styled to match the image design
     */
    private JPanel createImageStyledSectionPanel(String title) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Section header with dark blue background
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(13, 37, 73)); // Dark blue background like in image
        headerPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE); // White text on dark background
        headerPanel.add(titleLabel);
        
        sectionPanel.add(headerPanel);
        
        return sectionPanel;
    }
    
    /**
     * Adds a personal detail row to the panel with proper styling
     */
    private void addPersonalDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        labelComponent.setForeground(new Color(60, 60, 60));
        labelComponent.setPreferredSize(new Dimension(180, 20));
        
        JLabel valueComponent = new JLabel(value != null ? value.toUpperCase() : "N/A");
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        valueComponent.setForeground(new Color(40, 40, 40));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(240, 240, 240));
        
        panel.add(rowPanel);
        panel.add(separator);
    }

    /**
     * Adds an account information row to the panel
     */
    private void addAccountInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setForeground(new Color(60, 60, 60));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        valueComponent.setForeground(new Color(40, 40, 40));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.EAST);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        
        panel.add(rowPanel);
        panel.add(separator);
    }
    
    /**
     * Adds a compact account information row to the panel
     */
    private void addCompactAccountInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8)); // Very small padding
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 8)); // Very small font
        labelComponent.setForeground(new Color(60, 60, 60));
        labelComponent.setPreferredSize(new Dimension(80, 12)); // Very small fixed width and height
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 8)); // Very small font
        valueComponent.setForeground(new Color(40, 40, 40));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        
        panel.add(rowPanel);
        panel.add(separator);
    }
    
    /**
     * Adds a clean account information row to the panel (matches image format)
     */
    private void addCleanAccountInfoRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20)); // Proper padding
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 12)); // Regular font, not bold
        labelComponent.setForeground(new Color(60, 60, 60));
        labelComponent.setPreferredSize(new Dimension(150, 20)); // Fixed width for alignment
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12)); // Regular font
        valueComponent.setForeground(new Color(40, 40, 40));
        valueComponent.setHorizontalAlignment(SwingConstants.RIGHT); // Right align values
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.EAST);
        
        // Add separator line
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(220, 220, 220));
        
        panel.add(rowPanel);
        panel.add(separator);
    }
    

    private JPanel createSectionPanel(String title, Object[][] data) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(10, 45, 90));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        sectionPanel.add(titleLabel, BorderLayout.NORTH);

        // Data panel
        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < data.length; i++) {
            String label = (String) data[i][0];
            String value = (String) data[i][1];
            boolean editable = (Boolean) data[i][2];
            String fieldType = (String) data[i][3];
            String[] options = (String[]) data[i][4];

            // Label
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3;
            JLabel labelComponent = new JLabel(label);
            labelComponent.setFont(new Font("Arial", Font.PLAIN, 12));
            labelComponent.setPreferredSize(new Dimension(200, 25));
            dataPanel.add(labelComponent, gbc);

            // Value component
            gbc.gridx = 1;
            gbc.weightx = 0.7;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JPanel valuePanel = new JPanel(new BorderLayout());
            valuePanel.setBackground(Color.WHITE);
            
            Component valueComponent;
            if (fieldType.equals("combo") && editable) {
                JComboBox<String> comboBox = new JComboBox<>(options);
                comboBox.setSelectedItem(value.equals("None") ? null : value);
                comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
                comboBox.setPreferredSize(new Dimension(250, 25));
                valueComponent = comboBox;
            } else {
                JTextField textField = new JTextField(value);
                textField.setFont(new Font("Arial", Font.PLAIN, 12));
                textField.setPreferredSize(new Dimension(250, 25));
                textField.setEditable(editable);
                if (!editable) {
                    textField.setBackground(new Color(240, 240, 240));
                }
                valueComponent = textField;
            }
            
            valuePanel.add(valueComponent, BorderLayout.WEST);
            
            // Edit button (pen icon)
            if (editable) {
                JButton editButton = new JButton("✏️");
                editButton.setFont(new Font("Arial", Font.PLAIN, 12));
                editButton.setPreferredSize(new Dimension(30, 25));
                editButton.setToolTipText("Edit " + label);
                editButton.addActionListener(e -> showEditDialog(label, valueComponent, fieldType, options));
                valuePanel.add(editButton, BorderLayout.EAST);
            }
            
            dataPanel.add(valuePanel, gbc);
        }

        sectionPanel.add(dataPanel, BorderLayout.CENTER);
        return sectionPanel;
    }

    private void showEditDialog(String fieldName, Component component, String fieldType, String[] options) {
        JDialog editDialog = new JDialog(this, "Edit " + fieldName, true);
        editDialog.setSize(400, 200);
        editDialog.setLocationRelativeTo(this);
        editDialog.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel("Edit " + fieldName);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        
        editDialog.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        JLabel instructionLabel = new JLabel("Enter new value for " + fieldName + ":");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        Component inputComponent;
        if (fieldType.equals("combo")) {
            JComboBox<String> comboBox = new JComboBox<>(options);
            comboBox.setFont(new Font("Arial", Font.PLAIN, 12));
            comboBox.setPreferredSize(new Dimension(300, 30));
            inputComponent = comboBox;
        } else {
            JTextField textField = new JTextField();
            textField.setFont(new Font("Arial", Font.PLAIN, 12));
            textField.setPreferredSize(new Dimension(300, 30));
            inputComponent = textField;
        }
        
        contentPanel.add(inputComponent);
        contentPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 150, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setPreferredSize(new Dimension(80, 35));
        saveButton.addActionListener(e -> {
            String newValue;
            if (fieldType.equals("combo")) {
                newValue = ((JComboBox<String>) inputComponent).getSelectedItem() != null ? 
                          ((JComboBox<String>) inputComponent).getSelectedItem().toString() : "None";
            } else {
                newValue = ((JTextField) inputComponent).getText().trim();
            }
            
            if (!newValue.isEmpty()) {
                updateFieldValue(component, newValue);
                JOptionPane.showMessageDialog(editDialog, 
                    fieldName + " updated successfully!", 
                    "Update Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                editDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(editDialog, 
                    "Please enter a valid value.", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.addActionListener(e -> editDialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.setVisible(true);
    }

    private void updateFieldValue(Component component, String newValue) {
        if (component instanceof JTextField) {
            ((JTextField) component).setText(newValue);
        } else if (component instanceof JComboBox) {
            ((JComboBox<String>) component).setSelectedItem(newValue);
        }
    }
// Generic Content

    private void showGenericContent(String menuItem) {
        JPanel genericPanel = new JPanel(new BorderLayout());
        genericPanel.setBorder(BorderFactory.createTitledBorder(menuItem));

        JTextArea contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        contentArea.setText("This is the " + menuItem + " section.\n\n" +
                "Content for this section is currently under development.\n" +
                "Please check back later for updates.");
        contentArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        genericPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(genericPanel);
    }

    /**
     * Retrieves student name using DataManager
     * @param studentID The student ID to look up
     * @return Formatted student name (FirstName LastName) or default if not found
     */
    private String getStudentNameFromDatabase(String studentID) {
        StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
        if (studentInfo != null) {
            return studentInfo.getFullName();
        }
        return "STUDENT NAME NOT FOUND";
    }

    /**
     * Creates the Statement of Accounts panel matching the HTML UI design exactly
     */
    private JPanel createStatementOfAccountsPanel(MySinglyLinkedList<String> subItems) {
        // Initialize account statement for the current student
        accountStatement = AccountStatementManager.getStatement(studentID);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create main content table layout (70% left, 30% right like HTML)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 240, 240));
        
        // Left panel - Statement content (70% width)
        JPanel leftPanel = createStatementContentPanel(subItems);
        leftPanel.setPreferredSize(new Dimension(700, 0));

        // Right panel - Online Payment Channels (30% width) 
        JPanel rightPanel = createOnlinePaymentChannelsPanel();
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        tablePanel.add(leftPanel, BorderLayout.CENTER);
        tablePanel.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * Creates the main statement content panel matching HTML structure
     */
    private JPanel createStatementContentPanel(MySinglyLinkedList<String> subItems) {
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setBackground(new Color(240, 240, 240));
        
        // Statement of Accounts Panel (first panel in HTML)
        JPanel statementPanel = createStatementOfAccountsMainPanel(subItems);
        
        // Fee Breakdown Panel (second panel in HTML)
        JPanel breakdownPanel = createFeeBreakdownMainPanel();
        breakdownPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        mainContentPanel.add(statementPanel);
        mainContentPanel.add(breakdownPanel);
        
        return mainContentPanel;
    }

    /**
     * Creates the Statement of Accounts main panel (matches first mws-panel in HTML)
     */
    private JPanel createStatementOfAccountsMainPanel(MySinglyLinkedList<String> subItems) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Header - Statement of Accounts (FIRST SEMESTER, 2025-2026)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel headerLabel = new JLabel("📊 Statement of Accounts (FIRST SEMESTER, 2025-2026)");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content panel - matches mws-panel-body in HTML
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Student Info section (matches mws-stat-container in HTML)
        JPanel studentStatPanel = createStudentStatContainer();
        contentPanel.add(studentStatPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Amount Due section - matches HTML structure exactly
        JPanel amountDueSection = createAmountDueSection();
        contentPanel.add(amountDueSection);
        contentPanel.add(Box.createVerticalStrut(20));

        // Remaining Balance section
        JPanel remainingBalanceSection = createRemainingBalanceSection();
        contentPanel.add(remainingBalanceSection);
        contentPanel.add(Box.createVerticalStrut(15));

        // Status message section
        JPanel statusSection = createStatusMessageSection();
        contentPanel.add(statusSection);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates student stat container (matches mws-stat-container in HTML)
     */
    private JPanel createStudentStatContainer() {
        JPanel statContainer = new JPanel(new BorderLayout());
        statContainer.setBackground(new Color(245, 245, 245));
        statContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        statContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Student icon
        JLabel studentIcon = new JLabel("👤");
        studentIcon.setFont(new Font("Arial", Font.PLAIN, 32));
        studentIcon.setPreferredSize(new Dimension(50, 50));
        studentIcon.setHorizontalAlignment(SwingConstants.CENTER);
        statContainer.add(studentIcon, BorderLayout.WEST);

        // Student info text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(245, 245, 245));
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));

        JLabel idProgramLabel = new JLabel(studentID + " | BSIT 2");
        idProgramLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idProgramLabel.setForeground(new Color(100, 100, 100));
        textPanel.add(idProgramLabel);

        JLabel nameLabel = new JLabel(getStudentFullName(studentID));
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(new Color(50, 50, 50));
        textPanel.add(nameLabel);

        statContainer.add(textPanel, BorderLayout.CENTER);
        return statContainer;
    }

    /**
     * Creates amount due section matching HTML structure
     */
    private JPanel createAmountDueSection() {
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.Y_AXIS));
        amountPanel.setBackground(Color.WHITE);
        amountPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Determine current exam period
        String currentPeriod = getCurrentExamPeriod();
        double amountDue = getCurrentExamPeriodDue(currentPeriod);

        // "Your amount due for PRELIM is:" text
        JLabel amountDueText = new JLabel("Your amount due for " + currentPeriod + " is:");
        amountDueText.setFont(new Font("Arial", Font.PLAIN, 30));
        amountDueText.setAlignmentX(Component.LEFT_ALIGNMENT);
        amountPanel.add(amountDueText);
        amountPanel.add(Box.createVerticalStrut(10));

        // Amount value - large red/green text
        JPanel amountValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        amountValuePanel.setBackground(Color.WHITE);
        amountValuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pesoSign = new JLabel("P ");
        pesoSign.setFont(new Font("Arial", Font.BOLD, 50));
        pesoSign.setForeground(Color.BLACK);

        JLabel amountValue = new JLabel(String.format("%.2f", amountDue));
        amountValue.setFont(new Font("Arial", Font.BOLD, 50));
        amountValue.setForeground(amountDue > 0 ? new Color(144, 24, 24) : new Color(0, 153, 0));
        amountDueValueLabel = amountValue; // Store reference for updates

        amountValuePanel.add(pesoSign);
        amountValuePanel.add(amountValue);
        amountPanel.add(amountValuePanel);

        return amountPanel;
    }

    /**
     * Creates remaining balance section
     */
    private JPanel createRemainingBalanceSection() {
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
        balancePanel.setBackground(Color.WHITE);
        balancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Get current date
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String dateStr = currentDate.format(dateFormatter);

        // "Your remaining balance as of..." text
        JLabel balanceText = new JLabel("Your remaining balance as of " + dateStr + " is:");
        balanceText.setFont(new Font("Arial", Font.PLAIN, 20));
        balanceText.setAlignmentX(Component.LEFT_ALIGNMENT);
        balancePanel.add(balanceText);
        balancePanel.add(Box.createVerticalStrut(10));

        // Balance value - large red/green text
        JPanel balanceValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        balanceValuePanel.setBackground(Color.WHITE);
        balanceValuePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pesoSign = new JLabel("P ");
        pesoSign.setFont(new Font("Arial", Font.BOLD, 50));
        pesoSign.setForeground(Color.BLACK);

        double balance = accountStatement.getBalance();
        JLabel balanceValue = new JLabel(String.format("%,.2f", balance));
        balanceValue.setFont(new Font("Arial", Font.BOLD, 50));
        balanceValue.setForeground(balance > 0 ? new Color(144, 24, 24) : new Color(0, 153, 0));

        balanceValuePanel.add(pesoSign);
        balanceValuePanel.add(balanceValue);
        balancePanel.add(balanceValuePanel);

        return balancePanel;
    }

    /**
     * Creates status message section
     */
    private JPanel createStatusMessageSection() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Status message based on current exam period
        String currentPeriod = getCurrentExamPeriod();
        boolean isPaid = isCurrentExamPeriodPaid(currentPeriod);
        
        JLabel statusLabel;
        if (isPaid) {
            statusLabel = new JLabel(currentPeriod + " STATUS: PAID. Permitted to take the exams.");
            statusLabel.setForeground(new Color(0, 128, 0));
        } else {
            statusLabel = new JLabel(currentPeriod + " STATUS: NOT PAID. Please pay before " + 
                currentPeriod.toLowerCase() + " exams. Ignore if you're SLU Dependent or Full TOF Scholar.");
            statusLabel.setForeground(new Color(200, 0, 0));
        }
        
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createVerticalStrut(5));

        // Verification note
        JLabel verificationNote = new JLabel("For verification on unposted payments after 'as of' date, please email sass@slu.edu.ph");
        verificationNote.setFont(new Font("Arial", Font.PLAIN, 11));
        verificationNote.setForeground(new Color(100, 100, 100));
        verificationNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusPanel.add(verificationNote);

        return statusPanel;
    }

    /**
     * Creates the Fee Breakdown panel (matches second mws-panel in HTML)
     */
    private JPanel createFeeBreakdownMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Get current date
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String dateStr = currentDate.format(dateFormatter);
        
        JLabel headerLabel = new JLabel("📋 Breakdown of fees as of " + dateStr);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table panel with no padding (matches HTML mws-panel-body style="padding: 0px;")
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        
        // Create table matching HTML structure exactly
        String[] columnNames = {"Date", "Description", "Amount"};
        
        // Build table data matching the HTML structure
        java.util.List<Object[]> tableData = new java.util.ArrayList<>();
        
        // Beginning balance row
        tableData.add(new Object[]{"", "BEGINNING BALANCE", "0.00"});
        
        // Payment history (negative amounts in parentheses)
        for (PaymentTransaction payment : accountStatement.getPaymentHistory()) {
            String amount = payment.getAmount().replace("P ", "").replace(",", "");
            try {
                double amountValue = Double.parseDouble(amount);
                String formattedAmount = String.format("(%,.2f)", amountValue);
                
                String dateStr2 = payment.getDate().contains(" ") ? payment.getDate().split(" ")[0] : payment.getDate();
                tableData.add(new Object[]{dateStr2, "PAYMENT RECEIVED (" + payment.getReference() + ")", formattedAmount});
            } catch (NumberFormatException e) {
                tableData.add(new Object[]{payment.getDate(), "PAYMENT RECEIVED (" + payment.getReference() + ")", payment.getAmount()});
            }
        }
        
        // Fee breakdown items
        for (FeeBreakdown fee : accountStatement.getFeeBreakdowns()) {
            if (fee.getAmount() > 0) { // Only show positive fees in breakdown
                String feeDate = fee.getDatePosted().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"));
                String feeAmount = String.format("%,.2f", fee.getAmount());
                tableData.add(new Object[]{feeDate, fee.getDescription(), feeAmount});
            }
        }
        
        // Convert to array
        Object[][] data = tableData.toArray(new Object[0][]);
        
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Set column widths to match HTML
        table.getColumnModel().getColumn(0).setPreferredWidth(70);  // Date column
        table.getColumnModel().getColumn(1).setPreferredWidth(400); // Description column  
        table.getColumnModel().getColumn(2).setPreferredWidth(70);  // Amount column
        
        // Custom renderer for formatting
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Right align amounts (column 2)
                if (column == 2) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                // Italic for dates in column 0 (except empty ones)
                if (column == 0 && value != null && !value.toString().isEmpty()) {
                    setFont(getFont().deriveFont(Font.ITALIC));
                }
                
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the Online Payment Channels panel (matches right side of HTML)
     */
    private JPanel createOnlinePaymentChannelsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 45));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel headerLabel = new JLabel("🛒 Online Payment Channels");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title text
        JLabel titleLabel = new JLabel("<html><center><b>Tuition fees can be paid via the available online payment channels.</b></center></html>");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(14, 40, 79));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Payment buttons
        createPaymentButtons(contentPanel);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates payment buttons for online payment channels
     */
    private void createPaymentButtons(JPanel contentPanel) {
        String[] paymentOptions = {
            "UnionBank UPay Online",
            "DragonPay Payment Gateway", 
            "BPI Online",
            "BDO Online",
            "BDO Bills Payment",
            "Bukas Tuition Installment Plans"
        };
        
        String[] paymentIcons = {"🏦", "🐲", "🏛️", "🏢", "💳", "📚"};
        Color[] buttonColors = {
            new Color(255, 140, 0),   // Orange for UPay
            new Color(220, 20, 60),   // Crimson for DragonPay
            new Color(0, 100, 200),   // Blue for BPI
            new Color(0, 80, 160),    // Dark Blue for BDO
            new Color(0, 120, 180),   // Medium Blue for BDO Bills
            new Color(46, 125, 50)    // Green for Bukas
        };

        for (int i = 0; i < paymentOptions.length; i++) {
            JButton paymentButton = new JButton(paymentIcons[i] + " " + paymentOptions[i]);
            paymentButton.setFont(new Font("Arial", Font.BOLD, 13));
            paymentButton.setForeground(Color.WHITE);
            paymentButton.setBackground(buttonColors[i]);
            paymentButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            paymentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            paymentButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            paymentButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            
            final String paymentMethod = paymentOptions[i];
            paymentButton.addActionListener(e -> handlePaymentButtonClick(paymentMethod));
            
            contentPanel.add(paymentButton);
            contentPanel.add(Box.createVerticalStrut(10));
        }
    }

    /**
     * Handles payment button clicks
     */
    private void handlePaymentButtonClick(String paymentMethod) {
        String currentPeriod = getCurrentExamPeriod();
        double amountDue = getCurrentExamPeriodDue(currentPeriod);
        
        String amountStr = JOptionPane.showInputDialog(this, 
            "Enter payment amount for " + paymentMethod + ":", 
            String.format("%.0f", amountDue));
            
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0) {
                    processPayment(amount, paymentMethod);
                } else {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Processes payment
     */
    private void processPayment(double amount, String paymentMethod) {
        String prefix = "PAY";
        if (paymentMethod.contains("DragonPay")) prefix = "DP";
        else if (paymentMethod.contains("UPay")) prefix = "UP";
        else if (paymentMethod.contains("BPI")) prefix = "BPI";
        else if (paymentMethod.contains("BDO")) prefix = "BDO";
        else if (paymentMethod.contains("Bukas")) prefix = "BKS";
        
        String reference = prefix + System.currentTimeMillis();
        
        AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
            studentID, amount, paymentMethod, reference);
        
        if (result.success) {
            JOptionPane.showMessageDialog(this, 
                result.message + "\nReference: " + reference, 
                "Payment Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            refreshStatementOfAccounts();
        } else {
            JOptionPane.showMessageDialog(this, 
                result.message, 
                "Payment Failed", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper methods for exam period logic
     */
    private String getCurrentExamPeriod() {
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        
        if (currentDate.isBefore(java.time.LocalDate.of(2025, 10, 15))) {
            return "PRELIM";
        } else if (currentDate.isBefore(java.time.LocalDate.of(2025, 11, 30))) {
            return "MIDTERM";
        } else {
            return "FINALS";
        }
    }
    
    private double getCurrentExamPeriodDue(String period) {
        switch (period) {
            case "PRELIM":
                return accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
            case "MIDTERM":
                return accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM);
            case "FINALS":
                return accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS);
            default:
                return accountStatement.getBalance();
        }
    }
    
    private boolean isCurrentExamPeriodPaid(String period) {
        switch (period) {
            case "PRELIM":
                return accountStatement.isPrelimPaid();
            case "MIDTERM":
                return accountStatement.isMidtermPaid();
            case "FINALS":
                return accountStatement.isFinalsPaid();
            default:
                return accountStatement.getBalance() <= 0;
        }
    }
}
