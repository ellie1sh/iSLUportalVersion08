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
     * Creates the Statement of Accounts panel matching the UI design
     */
    private JPanel createStatementOfAccountsPanel(MySinglyLinkedList<String> subItems) {
        // Initialize account statement for the current student
        accountStatement = AccountStatementManager.getStatement(studentID);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Left panel - Statement of Accounts
        JPanel leftPanel = createStatementLeftPanel(subItems);
        leftPanel.setPreferredSize(new Dimension(700, 0));

        // Right panel - Online Payment Channels
        JPanel rightPanel = createPaymentChannelsPanel(subItems);
        rightPanel.setPreferredSize(new Dimension(350, 0));

        mainPanel.add(leftPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        return mainPanel;
    }

    private JPanel createStatementLeftPanel(MySinglyLinkedList<String> subItems) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Get current date
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String dateStr = currentDate.format(dateFormatter);
        
        JLabel headerLabel = new JLabel(subItems.getFirst() + " - As of " + dateStr);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Profile icon on the right
        JLabel profileIcon = new JLabel("👤");
        profileIcon.setForeground(Color.WHITE);
        profileIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        profileIcon.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(profileIcon, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Student Info - Matching HTML design
        JPanel studentInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        studentInfoPanel.setBackground(new Color(245, 245, 245));
        studentInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        studentInfoPanel.setPreferredSize(new Dimension(0, 80));
        
        // User icon (female icon as in HTML)
        JLabel studentIcon = new JLabel("👩");
        studentIcon.setFont(new Font("Arial", Font.PLAIN, 24));
        studentInfoPanel.add(studentIcon);
        
        JPanel studentTextPanel = new JPanel();
        studentTextPanel.setLayout(new BoxLayout(studentTextPanel, BoxLayout.Y_AXIS));
        studentTextPanel.setBackground(new Color(245, 245, 245));
        
        // Student ID and Program (format: "2255146 | BSIT 2")
        String programInfo = getStudentProgram(studentID);
        JLabel studentIDLabel = new JLabel(studentID + " | " + programInfo);
        studentIDLabel.setFont(new Font("Arial", Font.BOLD, 14));
        studentTextPanel.add(studentIDLabel);
        
        // Student Name (format: "Sherlie O. Rivera")
        JLabel studentNameLabel = new JLabel(studentName);
        studentNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        studentTextPanel.add(studentNameLabel);
        
        studentInfoPanel.add(studentTextPanel);
        contentPanel.add(studentInfoPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Amount Due Section - Matching HTML design with large typography
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.Y_AXIS));
        amountPanel.setBackground(Color.WHITE);
        
        // PRELIM Amount Due (matching HTML: "Your amount due for PRELIM is:")
        JLabel prelimDueLabel = new JLabel("Your amount due for PRELIM is:");
        prelimDueLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        amountPanel.add(prelimDueLabel);
        
        // PRELIM Amount Value (large red typography)
        double prelimDue = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
        JLabel prelimAmountValue = new JLabel("P " + String.format("%,.2f", prelimDue));
        prelimAmountValue.setFont(new Font("Arial", Font.BOLD, 50));
        prelimAmountValue.setForeground(new Color(144, 24, 24)); // #901818 from HTML
        amountDueValueLabel = prelimAmountValue; // Store reference for updates
        amountPanel.add(prelimAmountValue);
        amountPanel.add(Box.createVerticalStrut(20));
        
        // Remaining Balance Section (matching HTML: "Your remaining balance as of [date] is:")
        // Reuse the existing dateStr from header
        
        JLabel remainingBalanceLabel = new JLabel("Your remaining balance as of " + dateStr + " is:");
        remainingBalanceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        amountPanel.add(remainingBalanceLabel);
        amountPanel.add(Box.createVerticalStrut(10));
        
        // Remaining Balance Value (large red typography)
        JLabel remainingBalanceValue = new JLabel("P " + String.format("%,.2f", accountStatement.getBalance()));
        remainingBalanceValue.setFont(new Font("Arial", Font.BOLD, 50));
        remainingBalanceValue.setForeground(new Color(144, 24, 24)); // #901818 from HTML
        amountPanel.add(remainingBalanceValue);
        
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // PRELIM STATUS Section (matching HTML design)
        String prelimStatusText;
        Color prelimStatusColor;
        
        if (accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM) <= 0) {
            prelimStatusText = "PRELIM STATUS: PAID. Permitted to take the exams.";
            prelimStatusColor = new Color(0, 150, 0); // Green
        } else {
            prelimStatusText = "PRELIM STATUS: NOT PAID. Please pay before prelim exams. Ignore if you're SLU Dependent or Full TOF Scholar.";
            prelimStatusColor = new Color(200, 0, 0); // Red
        }
        
        JLabel prelimStatusLabel = new JLabel(prelimStatusText);
        prelimStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        prelimStatusLabel.setForeground(prelimStatusColor);
        this.prelimStatusLabel = prelimStatusLabel; // Store reference for updates
        contentPanel.add(prelimStatusLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Overpayment Section
        JPanel overpaymentPanel = new JPanel();
        overpaymentPanel.setLayout(new BoxLayout(overpaymentPanel, BoxLayout.Y_AXIS));
        overpaymentPanel.setBackground(Color.WHITE);
        
        // Reuse the currentDate and dateFormatter from header, just create new formatted string
        String currentDateStr = currentDate.format(dateFormatter);
        
        if (accountStatement.getOverpayment() > 0) {
            JLabel overpaymentLabel = new JLabel("Overpayment/Credit Balance as of " + currentDateStr + ":");
            overpaymentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            overpaymentPanel.add(overpaymentLabel);
            
            JLabel overpaymentValue = new JLabel("P " + String.format("%,.2f", accountStatement.getOverpayment()));
            overpaymentValue.setFont(new Font("Arial", Font.BOLD, 20));
            overpaymentValue.setForeground(new Color(0, 100, 200));
            overpaymentValueLabel = overpaymentValue; // Store reference for updates
            overpaymentPanel.add(overpaymentValue);
        } else {
            JLabel paymentSummaryLabel = new JLabel("Payment Summary as of " + currentDateStr + ":");
            paymentSummaryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            overpaymentPanel.add(paymentSummaryLabel);
            
            JLabel totalAmountLabel = new JLabel("Total Amount: P " + String.format("%,.2f", accountStatement.getTotalAmount()));
            totalAmountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            overpaymentPanel.add(totalAmountLabel);
            
            JLabel amountPaidLabel = new JLabel("Amount Paid: P " + String.format("%,.2f", accountStatement.getAmountPaid()));
            amountPaidLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            overpaymentPanel.add(amountPaidLabel);
        }
        
        contentPanel.add(overpaymentPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Note: Exam status is now displayed in the PRELIM STATUS section above

        // Breakdown of Fees
        JPanel breakdownPanel = createBreakdownPanel();
        contentPanel.add(breakdownPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Online Payment Transactions
        JPanel transactionsPanel = createTransactionsPanel();
        contentPanel.add(transactionsPanel);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBreakdownPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        
        // Get current date
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String dateStr = currentDate.format(dateFormatter);
        
        JLabel headerLabel = new JLabel("Fee Breakdown as of " + dateStr);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Update payment statuses first
        accountStatement.updatePaymentStatuses();
        
        // Table matching HTML structure: Date, Description, Amount (3 columns)
        String[] columnNames = {"Date", "Description", "Amount"};
        
        // Get fee breakdowns and payment history from account statement
        List<FeeBreakdown> fees = accountStatement.getFeeBreakdowns();
        List<PaymentTransaction> payments = accountStatement.getPaymentHistory();
        
        // Calculate total rows needed: beginning balance + fees + payments + 1 for spacing
        int totalRows = 1 + fees.size() + payments.size() + 1;
        Object[][] data = new Object[totalRows][3];
        
        int row = 0;
        
        // Add beginning balance row (matching HTML)
        data[row] = new Object[]{"", "BEGINNING BALANCE", "0.00"};
        row++;
        
        // Add payment history first (as they appear chronologically)
        for (PaymentTransaction payment : payments) {
            // Format date as MM/dd/yyyy to match HTML
            String formattedDate = payment.getDate().split(" ")[0]; // Get date part only
            String description = "PAYMENT RECEIVED (" + payment.getReference() + ")";
            String amount = "(" + payment.getAmount().replace("P ", "").replace(",", "") + ")";
            data[row] = new Object[]{formattedDate, description, amount};
            row++;
        }
        
        // Add fee items (matching HTML structure)
        for (FeeBreakdown fee : fees) {
            String feeDate = "09/15/2025"; // Default fee date as in HTML
            String description = fee.getDescription();
            String amount = String.format("%.2f", fee.getAmount());
            data[row] = new Object[]{feeDate, description, amount};
            row++;
        }

        feeBreakdownTableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(feeBreakdownTableModel);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Custom renderer for formatting (matching HTML table styling)
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Bold for beginning balance row
                if (row == 0) {
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                
                // Right align amounts (column 2)
                if (column == 2) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                // Alternating row colors for better readability
                if (row % 2 == 0) {
                    setBackground(new Color(255, 255, 255)); // White
                } else {
                    setBackground(new Color(248, 248, 248)); // Light gray
                }
                
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add exam eligibility indicator panel
        JPanel eligibilityPanel = new JPanel();
        eligibilityPanel.setLayout(new BoxLayout(eligibilityPanel, BoxLayout.Y_AXIS));
        eligibilityPanel.setBackground(new Color(245, 245, 245));
        eligibilityPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel eligibilityTitle = new JLabel("Examination Eligibility Status:");
        eligibilityTitle.setFont(new Font("Arial", Font.BOLD, 13));
        eligibilityTitle.setForeground(new Color(10, 45, 90));
        eligibilityPanel.add(eligibilityTitle);
        eligibilityPanel.add(Box.createVerticalStrut(5));
        
        // Prelim eligibility
        JLabel prelimEligibility = new JLabel(accountStatement.getExamEligibilityMessage(AccountStatement.ExamPeriod.PRELIM));
        prelimEligibility.setFont(new Font("Arial", Font.PLAIN, 12));
        prelimEligibility.setForeground(accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM) <= 0 ? 
            new Color(0, 150, 0) : new Color(200, 0, 0));
        eligibilityPanel.add(prelimEligibility);
        
        // Midterm eligibility
        JLabel midtermEligibility = new JLabel(accountStatement.getExamEligibilityMessage(AccountStatement.ExamPeriod.MIDTERM));
        midtermEligibility.setFont(new Font("Arial", Font.PLAIN, 12));
        midtermEligibility.setForeground(accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM) <= 0 ? 
            new Color(0, 150, 0) : new Color(200, 0, 0));
        eligibilityPanel.add(midtermEligibility);
        
        // Finals eligibility
        JLabel finalsEligibility = new JLabel(accountStatement.getExamEligibilityMessage(AccountStatement.ExamPeriod.FINALS));
        finalsEligibility.setFont(new Font("Arial", Font.PLAIN, 12));
        finalsEligibility.setForeground(accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS) <= 0 ? 
            new Color(0, 150, 0) : new Color(200, 0, 0));
        eligibilityPanel.add(finalsEligibility);
        
        // Add note about processing payments
        boolean hasProcessingPayments = accountStatement.getPaymentHistory().stream()
            .anyMatch(p -> p.getStatus() != null && p.getStatus().isInProgress());
        
        if (hasProcessingPayments) {
            eligibilityPanel.add(Box.createVerticalStrut(5));
            JLabel processingNote = new JLabel("⚠ Payment is being processed. Status will update shortly.");
            processingNote.setFont(new Font("Arial", Font.ITALIC, 11));
            processingNote.setForeground(new Color(255, 140, 0));
            eligibilityPanel.add(processingNote);
        }
        
        panel.add(eligibilityPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        
        // Get current date
        java.time.LocalDate currentDate2 = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter2 = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String dateStr2 = currentDate2.format(dateFormatter2);
        
        JLabel headerLabel = new JLabel("Payment History as of " + dateStr2);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Update payment statuses
        accountStatement.updatePaymentStatuses();
        
        // Table with status
        String[] columnNames = {"Date", "Channel", "Reference", "Amount", "Status"};
        
        // Get payment history from account statement
        List<PaymentTransaction> payments = accountStatement.getPaymentHistory();
        Object[][] data = new Object[payments.size()][5];
        
        for (int i = 0; i < payments.size(); i++) {
            data[i] = payments.get(i).toTableRow();
        }

        paymentTableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(paymentTableModel);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Custom renderer for formatting
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Right align amounts
                if (column == 3) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the Transcript of Records panel matching the UI design
     */
    private JPanel createTranscriptOfRecordsPanel(MySinglyLinkedList<String> subItems) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Header - Transcript of Records
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel headerLabel = new JLabel(subItems.toString());
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Create transcript table with all semesters
        String[] columnNames = {"Course Number", "Descriptive Title", "Grade", "Units"};
        
        // Generate random grades (76-99)
        Object[][] transcriptData = generateTranscriptData();
        
        DefaultTableModel transcriptModel = new DefaultTableModel(transcriptData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        JTable transcriptTable = new JTable(transcriptModel);
        transcriptTable.setRowHeight(30);
        transcriptTable.getTableHeader().setReorderingAllowed(false);
        transcriptTable.setAutoCreateRowSorter(false);
        transcriptTable.setShowGrid(true);
        transcriptTable.setGridColor(new Color(200, 200, 200));
        transcriptTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Style the table
        transcriptTable.getTableHeader().setBackground(new Color(240, 240, 240));
        transcriptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set column alignments
        transcriptTable.getColumnModel().getColumn(0).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });
        
        transcriptTable.getColumnModel().getColumn(1).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });
        
        transcriptTable.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return this;
            }
        });
        
        transcriptTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.RIGHT);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(transcriptTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createPaymentChannelsPanel(MySinglyLinkedList<String> subItems) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel headerLabel = new JLabel(subItems.get(1));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Instruction text (matching HTML design)
        JLabel instructionLabel = new JLabel("Tuition fees can be paid via the available online payment channels.");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        instructionLabel.setForeground(new Color(14, 40, 79)); // #0e284f from HTML
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Horizontal line separator
        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(200, 200, 200));
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(20));

        // Payment channel buttons (matching HTML design - 6 buttons as shown in images)
        String[] channels = {
            "UB UnionBank UPay Online",
            "@dragonpay Payment Gateway", 
            "BPI BPI Online",
            "BDO BDO Online",
            "BDO Bills Payment",
            "Bukas Tuition Installment Plans"
        };

        // Colors matching the HTML design
        Color[] colors = {
            new Color(255, 140, 0), // Orange for UnionBank
            new Color(220, 20, 60), // Red for Dragonpay
            new Color(139, 69, 19), // Maroon for BPI
            new Color(0, 100, 200), // Blue for BDO
            new Color(0, 100, 200), // Blue for BDO Bills
            new Color(135, 206, 235) // Light Blue for Bukas
        };

        for (int i = 0; i < channels.length; i++) {
            JButton channelButton = new JButton(channels[i]);
            channelButton.setBackground(colors[i]);
            channelButton.setForeground(Color.WHITE);
            channelButton.setFont(new Font("Arial", Font.BOLD, 12));
            channelButton.setPreferredSize(new Dimension(280, 45));
            channelButton.setFocusPainted(false);
            channelButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            channelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Add action listener for payment processing
            final String channelName = channels[i];
            channelButton.addActionListener(e -> {
                if (channelName.contains("Bukas")) {
                    // Open Bukas website
                    try {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI("https://bukas.ph/s/slu"));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Please visit https://bukas.ph/s/slu for tuition installment plans.", 
                            "Bukas Tuition Installment", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    showPaymentDialog(channelName);
                }
            });
            
            contentPanel.add(channelButton);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    // Fields to track payment information
    private AccountStatement accountStatement; // Account statement for current student
    private JLabel overpaymentValueLabel; // Reference to update the display
    private JLabel amountDueValueLabel; // Reference to update the amount due display
    private JLabel prelimStatusLabel; // Reference to update the PRELIM STATUS display
    private JLabel midtermStatusLabel; // Reference to update the MIDTERM STATUS display
    private JLabel finalsStatusLabel; // Reference to update the FINALS STATUS display
    private DefaultTableModel paymentTableModel; // Reference to payment transactions table model
    private DefaultTableModel feeBreakdownTableModel; // Reference to fee breakdown table model
    
    /**
     * Gets the student program information based on student ID
     */
    private String getStudentProgram(String studentID) {
        // Default program for demonstration - in real implementation, this would come from database
        if (studentID.startsWith("225")) {
            return "BSIT 2";
        } else if (studentID.startsWith("224")) {
            return "BSIT 3";
        } else if (studentID.startsWith("223")) {
            return "BSIT 4";
        } else {
            return "BSIT 1";
        }
    }

    /**
     * Shows onsite payment dialog for cashier payment
     */
    private void showOnsitePaymentDialog(String channelName) {
        JDialog paymentDialog = new JDialog(this, "Onsite Payment - " + channelName, true);
        paymentDialog.setSize(450, 350);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel("Onsite Payment Information");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        
        paymentDialog.add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Information panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel infoTitle = new JLabel("ℹ Onsite Payment Instructions:");
        infoTitle.setFont(new Font("Arial", Font.BOLD, 13));
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(10));
        
        String[] instructions = {
            "1. Proceed to the Cashier's Office",
            "2. Present your Student ID",
            "3. Inform the cashier of your payment amount",
            "4. Receive your Official Receipt",
            "5. Payment will be posted within 1-2 hours"
        };
        
        for (String instruction : instructions) {
            JLabel instrLabel = new JLabel(instruction);
            instrLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(instrLabel);
            infoPanel.add(Box.createVerticalStrut(3));
        }
        
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Amount to Pay
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        amountPanel.setBackground(Color.WHITE);
        JLabel amountLabel = new JLabel("Amount to Pay:");
        amountLabel.setPreferredSize(new Dimension(150, 25));
        amountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        amountPanel.add(amountLabel);
        
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(150, 25));
        amountPanel.add(amountField);
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // OR Number (optional)
        JPanel orPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        orPanel.setBackground(Color.WHITE);
        JLabel orLabel = new JLabel("OR Number (if available):");
        orLabel.setPreferredSize(new Dimension(150, 25));
        orLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        orPanel.add(orLabel);
        
        JTextField orField = new JTextField();
        orField.setPreferredSize(new Dimension(150, 25));
        orPanel.add(orField);
        contentPanel.add(orPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Note
        JLabel noteLabel = new JLabel("<html><i>Note: This will record your intent to pay onsite.<br>" +
            "Actual payment must be made at the Cashier's Office.</i></html>");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        noteLabel.setForeground(new Color(100, 100, 100));
        contentPanel.add(noteLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton submitButton = new JButton("Record Payment");
        submitButton.setBackground(new Color(100, 100, 100));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.setPreferredSize(new Dimension(130, 35));
        submitButton.addActionListener(e -> {
            String amountStr = amountField.getText().trim();
            String orNumber = orField.getText().trim();
            
            if (amountStr.isEmpty()) {
                JOptionPane.showMessageDialog(paymentDialog, "Please enter the amount to pay", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                double amount = Double.parseDouble(amountStr.replaceAll(",", ""));
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(paymentDialog, "Amount must be greater than 0", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Process onsite payment
                String reference = orNumber.isEmpty() ? "ONSITE-" + System.currentTimeMillis() : orNumber;
                if (processOnsitePayment(amount, channelName, reference)) {
                    paymentDialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(paymentDialog, "Invalid amount format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.addActionListener(e -> paymentDialog.dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);

        paymentDialog.add(contentPanel, BorderLayout.CENTER);
        paymentDialog.setVisible(true);
    }
    
    /**
     * Process onsite payment
     */
    private boolean processOnsitePayment(double amount, String channelName, String reference) {
        // Process payment using AccountStatementManager
        AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
            studentID, amount, channelName, reference
        );
        
        if (result.success) {
            // Update the account statement reference
            accountStatement = AccountStatementManager.getStatement(studentID);
            
            // Add payment to table
            addPaymentToTable(result.transaction);
            
            // Show success message
            String message = "Onsite Payment Recorded Successfully!\n\n" +
                "Amount: P " + String.format("%,.2f", amount) + "\n" +
                "Reference: " + reference + "\n" +
                "Status: FOR POSTING - Cashier\n\n" +
                "Please proceed to the Cashier's Office to complete the payment.\n" +
                "Your payment will be posted within 1-2 hours after cashier verification.\n\n" +
                "Current Balance: P " + String.format("%,.2f", result.newBalance);
            
            JOptionPane.showMessageDialog(this, message, "Payment Recorded", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the display
            refreshStatementOfAccounts();
            
            return true;
        } else {
            JOptionPane.showMessageDialog(this, result.message, "Payment Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Shows payment dialog to collect card information and process payment
     */
    private void showPaymentDialog(String channelName) {
        JDialog paymentDialog = new JDialog(this, "Payment through " + channelName, true);
        paymentDialog.setSize(500, 350);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new BorderLayout());

        // Header (matching HTML design)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel("Payment through " + channelName);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        
        paymentDialog.add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // Amount to Pay Section (matching HTML design)
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.Y_AXIS));
        amountPanel.setBackground(Color.WHITE);
        
        JLabel amountLabel = new JLabel("AMOUNT TO PAY");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountPanel.add(amountLabel);
        amountPanel.add(Box.createVerticalStrut(10));
        
        // Amount input field (large, centered)
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(300, 50));
        amountField.setFont(new Font("Arial", Font.PLAIN, 30));
        amountField.setHorizontalAlignment(JTextField.CENTER);
        amountField.setText(String.valueOf((int)accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM)));
        amountPanel.add(amountField);
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Payment Method Selection (for Dragonpay)
        if (channelName.contains("Dragonpay")) {
            JPanel methodPanel = new JPanel();
            methodPanel.setLayout(new BoxLayout(methodPanel, BoxLayout.Y_AXIS));
            methodPanel.setBackground(Color.WHITE);
            
            JLabel methodLabel = new JLabel("Payment Method:");
            methodLabel.setFont(new Font("Arial", Font.BOLD, 12));
            methodPanel.add(methodLabel);
            methodPanel.add(Box.createVerticalStrut(5));
            
            String[] paymentMethods = {
                "--- SELECT A PAYMENT OPTION ---",
                "--- ATM Banks ---",
                "Banco de Oro ATM",
                "--- E-Wallets / MobilePayments ---",
                "GCash",
                "PayMaya",
                "Coins.ph Wallet / Bitcoin",
                "GrabPay",
                "--- Online Bank ---"
            };
            
            JComboBox<String> methodCombo = new JComboBox<>(paymentMethods);
            methodCombo.setPreferredSize(new Dimension(300, 30));
            methodPanel.add(methodCombo);
            
            // Service charge note
            JLabel noteLabel = new JLabel("<html><b>Note:</b> There will be a twenty five pesos (P 25.00) service charge for using dragon pay.<br>" +
                "An additional fee will be charged depending on the payment channel.</html>");
            noteLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            noteLabel.setForeground(Color.RED);
            methodPanel.add(Box.createVerticalStrut(10));
            methodPanel.add(noteLabel);
            
            contentPanel.add(methodPanel);
            contentPanel.add(Box.createVerticalStrut(20));
        }

        // Buttons panel (matching HTML design)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton submitButton = new JButton("Proceed");
        submitButton.setBackground(new Color(200, 200, 200));
        submitButton.setForeground(Color.BLACK);
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.setPreferredSize(new Dimension(120, 35));
        submitButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(paymentDialog, "Please enter a valid amount.", 
                        "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Process payment
                AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                    studentID, amount, channelName, "PAY" + System.currentTimeMillis());
                
                if (result.success) {
                    JOptionPane.showMessageDialog(paymentDialog, result.message, 
                        "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                    paymentDialog.dispose();
                    
                    // Refresh the statement display
                    accountStatement = AccountStatementManager.getStatement(studentID);
                    refreshStatementOfAccounts();
                } else {
                    JOptionPane.showMessageDialog(paymentDialog, result.message, 
                        "Payment Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(paymentDialog, "Please enter a valid amount.", 
                    "Invalid Amount", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(200, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.addActionListener(e -> paymentDialog.dispose());
        
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel);

        paymentDialog.add(contentPanel, BorderLayout.CENTER);
        paymentDialog.setVisible(true);
    }

    /**
     * Processes the payment and updates the balance
     */
    private boolean processPayment(String cardNumber, String cvv, String expDate, 
                                 String cardHolderName, String amountStr, String channelName) {
        // Validate inputs
        if (cardNumber.replaceAll("\\s", "").length() != 16) {
            JOptionPane.showMessageDialog(this, "Card number must be 16 digits", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (cvv.length() != 3) {
            JOptionPane.showMessageDialog(this, "CVV must be 3 digits", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!expDate.matches("\\d{2}/\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Expiration date must be in MM/YY format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Validate expiration date
        try {
            String[] expParts = expDate.split("/");
            int month = Integer.parseInt(expParts[0]);
            int year = Integer.parseInt(expParts[1]) + 2000;
            
            if (month < 1 || month > 12) {
                JOptionPane.showMessageDialog(this, "Invalid month in expiration date", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Check if card is expired
            java.time.YearMonth expiration = java.time.YearMonth.of(year, month);
            java.time.YearMonth current = java.time.YearMonth.now();
            if (expiration.isBefore(current)) {
                JOptionPane.showMessageDialog(this, "Card has expired", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid expiration date format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (cardHolderName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Card holder name is required", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountStr.replaceAll(",", ""));
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Generate reference number
        String reference = generatePaymentReference(channelName);
        
        // Process payment using AccountStatementManager
        AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
            studentID, amount, channelName, reference
        );
        
        if (result.success) {
            // Update the account statement reference
            accountStatement = AccountStatementManager.getStatement(studentID);
            
            // Add payment to table
            addPaymentToTable(result.transaction);
            
            // Show detailed success message
            StringBuilder message = new StringBuilder();
            message.append("Payment Processed Successfully!\n\n");
            message.append("Transaction Details:\n");
            message.append("==================\n");
            message.append("Channel: ").append(channelName).append("\n");
            message.append("Reference: ").append(reference).append("\n");
            message.append("Amount Paid: P ").append(String.format("%,.2f", amount)).append("\n\n");
            
            message.append("Account Status:\n");
            message.append("==================\n");
            message.append("Total Balance: P ").append(String.format("%,.2f", result.newBalance)).append("\n");
            
            if (result.newOverpayment > 0) {
                message.append("Credit Balance: P ").append(String.format("%,.2f", result.newOverpayment)).append("\n");
            }
            
            message.append("\nExam Payment Status:\n");
            message.append("==================\n");
            
            // Show payment processing status
            if (result.transaction.getStatus().isInProgress()) {
                message.append("Payment Status: ").append(result.transaction.getStatus().getDisplayName()).append("\n");
                if (result.transaction.getPaymentType().equals("ONSITE")) {
                    message.append("Note: Onsite payment requires cashier posting\n");
                } else {
                    message.append("Note: Online payment is being processed\n");
                }
                message.append("\n");
            }
            
            message.append("Prelim: ").append(accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM) <= 0 ? "✓ ELIGIBLE" : "✗ NOT ELIGIBLE").append("\n");
            message.append("Midterm: ").append(accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM) <= 0 ? "✓ ELIGIBLE" : "✗ NOT ELIGIBLE").append("\n");
            message.append("Finals: ").append(accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS) <= 0 ? "✓ ELIGIBLE" : "✗ NOT ELIGIBLE").append("\n");
            
            JOptionPane.showMessageDialog(this, message.toString(), "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the display
            refreshStatementOfAccounts();
            
            return true;
        } else {
            JOptionPane.showMessageDialog(this, result.message, "Payment Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Generates a unique payment reference number
     */
    private String generatePaymentReference(String channelName) {
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new java.util.Date());
        String channelCode = channelName.substring(0, Math.min(3, channelName.length())).toUpperCase();
        int random = (int)(Math.random() * 10000);
        return String.format("%s-%s-%04d", date, channelCode, random);
    }

    /**
     * Loads payment transactions using DataManager
     */
    private Object[][] loadPaymentTransactions() {
        java.util.List<PaymentTransaction> transactions = DataManager.loadPaymentTransactions(studentID);
        
        if (transactions.isEmpty()) {
            return new Object[0][4];
        }
        
        // Convert to 2D array
        Object[][] result = new Object[transactions.size()][4];
        for (int i = 0; i < transactions.size(); i++) {
            result[i] = transactions.get(i).toTableRow();
        }
        
        return result;
    }

    /**
     * Logs payment transaction using DataManager
     */
    private void logPaymentTransaction(String channelName, double amount) {
        DataManager.logPaymentTransaction(channelName, amount, studentID);
    }

    /**
     * Adds a new payment transaction to the table
     */
    private void addPaymentToTable(PaymentTransaction transaction) {
        if (paymentTableModel != null) {
            // Add new row to table
            paymentTableModel.addRow(transaction.toTableRow());
        }
    }

    /**
     * Refreshes the Statement of Accounts display with updated balance
     */
    private void refreshStatementOfAccounts() {
        // Reload account statement
        accountStatement = AccountStatementManager.getStatement(studentID);
        
        // Update the balance label
        if (amountDueValueLabel != null) {
            amountDueValueLabel.setText("P " + String.format("%,.2f", accountStatement.getBalance()));
            amountDueValueLabel.setForeground(accountStatement.getBalance() > 0 ? 
                new Color(200, 0, 0) : new Color(0, 150, 0));
        }
        
        // Update the overpayment label if it exists
        if (overpaymentValueLabel != null && accountStatement.getOverpayment() > 0) {
            overpaymentValueLabel.setText("P " + String.format("%,.2f", accountStatement.getOverpayment()));
        }
        
        // Update exam status labels
        accountStatement.updatePaymentStatuses();
        
        if (prelimStatusLabel != null) {
            String prelimStatusText = accountStatement.getExamEligibilityMessage(AccountStatement.ExamPeriod.PRELIM);
            Color prelimStatusColor = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM) <= 0 ? 
                new Color(0, 150, 0) : new Color(200, 0, 0);
            
            // Check for processing payments
            boolean hasProcessing = accountStatement.getPaymentHistory().stream()
                .anyMatch(p -> p.getStatus() != null && p.getStatus().isInProgress());
            if (hasProcessing) {
                prelimStatusColor = new Color(255, 140, 0); // Orange for processing
            }
            
            prelimStatusLabel.setText(prelimStatusText);
            prelimStatusLabel.setForeground(prelimStatusColor);
        }
        
        if (midtermStatusLabel != null) {
            String midtermStatusText = accountStatement.getExamEligibilityMessage(AccountStatement.ExamPeriod.MIDTERM);
            Color midtermStatusColor = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM) <= 0 ? 
                new Color(0, 150, 0) : new Color(200, 0, 0);
            
            // Check for processing payments
            boolean hasProcessing = accountStatement.getPaymentHistory().stream()
                .anyMatch(p -> p.getStatus() != null && p.getStatus().isInProgress());
            if (hasProcessing) {
                midtermStatusColor = new Color(255, 140, 0); // Orange for processing
            }
            
            midtermStatusLabel.setText(midtermStatusText);
            midtermStatusLabel.setForeground(midtermStatusColor);
        }
        
        if (finalsStatusLabel != null) {
            String finalsStatusText = accountStatement.getExamEligibilityMessage(AccountStatement.ExamPeriod.FINALS);
            Color finalsStatusColor = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS) <= 0 ? 
                new Color(0, 150, 0) : new Color(200, 0, 0);
            
            // Check for processing payments
            boolean hasProcessing = accountStatement.getPaymentHistory().stream()
                .anyMatch(p -> p.getStatus() != null && p.getStatus().isInProgress());
            if (hasProcessing) {
                finalsStatusColor = new Color(255, 140, 0); // Orange for processing
            }
            
            finalsStatusLabel.setText(finalsStatusText);
            finalsStatusLabel.setForeground(finalsStatusColor);
        }
        
        // Update fee breakdown table if needed
        if (feeBreakdownTableModel != null) {
            updateFeeBreakdownTable();
        }
        
        // Update payment history table
        if (paymentTableModel != null) {
            updatePaymentHistoryTable();
        }
    }
    
    /**
     * Updates the fee breakdown table with current data
     */
    private void updateFeeBreakdownTable() {
        // Clear existing rows
        while (feeBreakdownTableModel.getRowCount() > 0) {
            feeBreakdownTableModel.removeRow(0);
        }
        
        // Add header row
        feeBreakdownTableModel.addRow(new Object[]{"", "ASSESSMENT DETAILS", ""});
        
        // Add fee items
        for (FeeBreakdown fee : accountStatement.getFeeBreakdowns()) {
            feeBreakdownTableModel.addRow(fee.toTableRow());
        }
        
        // Add total row
        feeBreakdownTableModel.addRow(new Object[]{
            "", "TOTAL ASSESSMENT", 
            String.format("P %,.2f", accountStatement.getTotalAmount())
        });
        
        // Add balance row
        feeBreakdownTableModel.addRow(new Object[]{
            "", "CURRENT BALANCE", 
            String.format("P %,.2f", accountStatement.getBalance())
        });
    }
    
    /**
     * Updates the payment history table with current data
     */
    private void updatePaymentHistoryTable() {
        // Clear existing rows
        while (paymentTableModel.getRowCount() > 0) {
            paymentTableModel.removeRow(0);
        }
        
        // Add payment history
        for (PaymentTransaction payment : accountStatement.getPaymentHistory()) {
            paymentTableModel.addRow(payment.toTableRow());
        }
    }

    // Custom Document classes for input formatting
    private static class CardNumberDocument extends javax.swing.text.PlainDocument {
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            
            String currentText = getText(0, getLength());
            String newText = currentText.substring(0, offs) + str + currentText.substring(offs);
            newText = newText.replaceAll("\\D", ""); // Remove non-digits
            
            if (newText.length() <= 16) {
                // Format with spaces every 4 digits
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < newText.length(); i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(newText.charAt(i));
                }
                super.remove(0, getLength());
                super.insertString(0, formatted.toString(), a);
            }
        }
    }

    private static class CVVDocument extends javax.swing.text.PlainDocument {
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            
            String currentText = getText(0, getLength());
            String newText = currentText.substring(0, offs) + str + currentText.substring(offs);
            newText = newText.replaceAll("\\D", ""); // Remove non-digits
            
            if (newText.length() <= 3) {
                super.remove(0, getLength());
                super.insertString(0, newText, a);
            }
        }
    }

    private static class ExpirationDateDocument extends javax.swing.text.PlainDocument {
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
            if (str == null) return;
            
            String currentText = getText(0, getLength());
            String newText = currentText.substring(0, offs) + str + currentText.substring(offs);
            newText = newText.replaceAll("\\D", ""); // Remove non-digits
            
            if (newText.length() <= 4) {
                // Format as MM/YY
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < newText.length(); i++) {
                    if (i == 2) {
                        formatted.append("/");
                    }
                    formatted.append(newText.charAt(i));
                }
                super.remove(0, getLength());
                super.insertString(0, formatted.toString(), a);
            }
        }
    }

    /**
     * Creates the About iSLU panel with downloadables and overview sections
     */
    private JPanel createAboutISLUPanel(MySinglyLinkedList<String> subItems) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create scroll pane for the content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Add Downloadables section
        JPanel downloadablesPanel = createDownloadablesSection(subItems);
        contentPanel.add(downloadablesPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add About iSLU section
        JPanel aboutPanel = createAboutISLUSection(subItems);
        contentPanel.add(aboutPanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    /**
     * Creates the downloadables section with all categories
     */
    private JPanel createDownloadablesSection(MySinglyLinkedList<String> subItems) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel headerIcon = new JLabel("📁");
        headerIcon.setForeground(Color.WHITE);
        headerIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        headerPanel.add(headerIcon);
        
        JLabel headerLabel = new JLabel("Downloadables");
        headerLabel.setForeground(new Color(255, 204, 102));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content with downloadable items
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Add all downloadable categories
        addDownloadableCategory(contentPanel, "General", createGeneralDownloadables());
        addDownloadableCategory(contentPanel, "Center for Counseling and Wellness", createCounselingDownloadables());
        addDownloadableCategory(contentPanel, "Other Student Services", createOtherServicesDownloadables());
        addDownloadableCategory(contentPanel, "SAMCIS", createSAMCISDownloadables());
        addDownloadableCategory(contentPanel, "SAS", createSASDownloadables());
        addDownloadableCategory(contentPanel, "SEA", createSEADownloadables());
        addDownloadableCategory(contentPanel, "SONAHBS", createSONAHBSDownloadables());
        addDownloadableCategory(contentPanel, "SOL", createSOLDownloadables());
        addDownloadableCategory(contentPanel, "SOM", createSOMDownloadables());
        addDownloadableCategory(contentPanel, "STELA", createSTELADownloadables());
        addDownloadableCategory(contentPanel, "Student Services Orientation", createStudentServicesDownloadables());

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the About iSLU section
     */
    private JPanel createAboutISLUSection(MySinglyLinkedList<String> subItems) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel headerIcon = new JLabel("ℹ️");
        headerIcon.setForeground(Color.WHITE);
        headerIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        headerPanel.add(headerIcon);
        
        JLabel headerLabel = new JLabel("About iSLU");
        headerLabel.setForeground(new Color(255, 204, 102));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // OVERVIEW section
        JLabel overviewTitle = new JLabel("OVERVIEW");
        overviewTitle.setFont(new Font("Arial", Font.BOLD, 18));
        overviewTitle.setForeground(Color.BLACK);
        overviewTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(overviewTitle);
        contentPanel.add(Box.createVerticalStrut(15));

        // ABOUT ISLU PORTAL section
        JLabel aboutPortalTitle = new JLabel("ABOUT ISLU PORTAL");
        aboutPortalTitle.setFont(new Font("Arial", Font.BOLD, 14));
        aboutPortalTitle.setForeground(Color.BLACK);
        aboutPortalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(aboutPortalTitle);
        contentPanel.add(Box.createVerticalStrut(8));

        JTextArea aboutPortalText = new JTextArea();
        aboutPortalText.setText("iSLU Portal is for Saint Louis University students and parents, that serves as a personal assistant in carrying out university-related tasks.\n\n" +
                               "Students can view their personal details, schedules, current grades, accountabilities, curriculum checklist and more. Parents can also view these modules of their " +
                               "children as long they register.");
        aboutPortalText.setFont(new Font("Arial", Font.PLAIN, 12));
        aboutPortalText.setLineWrap(true);
        aboutPortalText.setWrapStyleWord(true);
        aboutPortalText.setEditable(false);
        aboutPortalText.setBackground(Color.WHITE);
        aboutPortalText.setBorder(null);
        aboutPortalText.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(aboutPortalText);
        contentPanel.add(Box.createVerticalStrut(20));

        // FEATURES AVAILABLE section
        JLabel featuresTitle = new JLabel("FEATURES AVAILABLE");
        featuresTitle.setFont(new Font("Arial", Font.BOLD, 14));
        featuresTitle.setForeground(Color.BLACK);
        featuresTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(featuresTitle);
        contentPanel.add(Box.createVerticalStrut(8));

        String[] features = {
            "Personal Details: where a student can view his/her personal information.",
            "Class Schedule: where a student can view his/her enrolled subjects of the current term.",
            "Class Attendance: displays the absences of the student of the current term.",
            "Current Grades: shows the Prelim, Mid-term and Tentative final grades of a student.",
            "Transcript of Records: shows the final grades in the previous term of a student.",
            "Statements of Accounts: shows if a student still has account balance from the accounting office.",
            "Curriculum Checklist: shows the student's checklist of a curriculum chosen.",
            "School Calendar: where a student can view the school events and no classes within an academic year."
        };

        for (String feature : features) {
            JTextArea featureText = new JTextArea(feature);
            featureText.setFont(new Font("Arial", Font.PLAIN, 12));
            featureText.setLineWrap(true);
            featureText.setWrapStyleWord(true);
            featureText.setEditable(false);
            featureText.setBackground(Color.WHITE);
            featureText.setBorder(null);
            featureText.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(featureText);
            contentPanel.add(Box.createVerticalStrut(5));
        }
        contentPanel.add(Box.createVerticalStrut(15));

        // HOW TO REGISTER section
        JLabel registerTitle = new JLabel("HOW TO REGISTER? Or FORGOT USER ID Number and PASSWORD!");
        registerTitle.setFont(new Font("Arial", Font.BOLD, 14));
        registerTitle.setForeground(Color.BLACK);
        registerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(registerTitle);
        contentPanel.add(Box.createVerticalStrut(8));

        JTextArea studentsText = new JTextArea();
        studentsText.setText("STUDENTS: proceed to IT CENTER SOFTWARE DEVELOPMENT (MIS), 2nd floor, Burgos Administrative Center, Saint Louis University.");
        studentsText.setFont(new Font("Arial", Font.PLAIN, 12));
        studentsText.setLineWrap(true);
        studentsText.setWrapStyleWord(true);
        studentsText.setEditable(false);
        studentsText.setBackground(Color.WHITE);
        studentsText.setBorder(null);
        studentsText.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(studentsText);
        contentPanel.add(Box.createVerticalStrut(8));

        JTextArea parentsText = new JTextArea();
        parentsText.setText("PARENTS/GUARDIANS: register at Registrar's office, Diego Silang building, Saint Louis University.");
        parentsText.setFont(new Font("Arial", Font.PLAIN, 12));
        parentsText.setLineWrap(true);
        parentsText.setWrapStyleWord(true);
        parentsText.setEditable(false);
        parentsText.setBackground(Color.WHITE);
        parentsText.setBorder(null);
        parentsText.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(parentsText);
        contentPanel.add(Box.createVerticalStrut(20));

        // Copyright footer
        JLabel copyrightLabel = new JLabel("Copyright © 2025 Saint Louis University Inc. All rights reserved.");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        copyrightLabel.setForeground(Color.GRAY);
        copyrightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(copyrightLabel);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Helper method to add downloadable categories
     */
    private void addDownloadableCategory(JPanel parent, String categoryName, String[] items) {
        // Category header
        JLabel categoryLabel = new JLabel("• " + categoryName);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 13));
        categoryLabel.setForeground(Color.BLACK);
        categoryLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        categoryLabel.setHorizontalAlignment(SwingConstants.LEFT);
        categoryLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, categoryLabel.getPreferredSize().height));
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(categoryLabel);

        // Category items
        for (String item : items) {
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
            itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Parse item data (format: "Display Name|PDF Filename")
            String[] itemData = item.split("\\|");
            String displayName = itemData[0];
            String pdfFileName = itemData.length > 1 ? itemData[1] : "";

            JLabel itemLabel = new JLabel("○ " + displayName + " ");
            itemLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            itemLabel.setForeground(Color.BLACK);
            itemPanel.add(itemLabel);

            // Always show download link in red initially
            JLabel downloadLink = new JLabel("[download]");
            downloadLink.setFont(new Font("Arial", Font.PLAIN, 12));
            downloadLink.setForeground(Color.RED);
            downloadLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            final String finalPdfFileName = pdfFileName;
            final String finalDisplayName = displayName;
            downloadLink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Check if PDF exists
                    if (finalPdfFileName == null || finalPdfFileName.isEmpty()) {
                        // No PDF file specified - show 404 page
                        show404Page(finalDisplayName);
                        return;
                    }
                    
                    File pdfFile = new File(finalPdfFileName);
                    if (!pdfFile.exists()) {
                        // PDF file doesn't exist - show 404 page
                        show404Page(finalDisplayName);
                        return;
                    }
                    
                    // PDF exists - download it
                    downloadPDF(pdfFile, finalDisplayName);
                }
            });
            itemPanel.add(downloadLink);

            parent.add(itemPanel);
        }
        parent.add(Box.createVerticalStrut(10));
    }

    /**
     * Shows 404 error page when PDF is not found
     */
    private void show404Page(String displayName) {
        // Create custom 404 dialog
        JDialog dialog404 = new JDialog(this, "404 - Not Found", true);
        dialog404.setSize(600, 400);
        dialog404.setLocationRelativeTo(this);
        dialog404.setResizable(false);
        
        // Main panel with light gray background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Large 404 text
        JLabel label404 = new JLabel("404");
        label404.setFont(new Font("Arial", Font.BOLD, 72));
        label404.setForeground(new Color(52, 73, 94));
        label404.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(label404);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Error message
        JLabel messageLabel = new JLabel("<html><center>The page you are looking for was moved, removed, renamed,<br>or might never existed.</center></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.DARK_GRAY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // File info
        JLabel fileLabel = new JLabel("<html><center>Requested file: <b>" + displayName + "</b></center></html>");
        fileLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        fileLabel.setForeground(Color.GRAY);
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(fileLabel);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Click here to go back home link
        JLabel backLink = new JLabel("<html><center><u>click here to go back home</u></center></html>");
        backLink.setFont(new Font("Arial", Font.PLAIN, 12));
        backLink.setForeground(Color.BLUE);
        backLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        backLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dialog404.dispose();
            }
        });
        mainPanel.add(backLink);
        
        dialog404.add(mainPanel);
        dialog404.setVisible(true);
    }
    
    /**
     * Downloads PDF file to user's Downloads folder
     */
    private void downloadPDF(File sourceFile, String displayName) {
        try {
            // Get user's Downloads folder
            String userHome = System.getProperty("user.home");
            File downloadsFolder = new File(userHome, "Downloads");
            
            // Create Downloads folder if it doesn't exist
            if (!downloadsFolder.exists()) {
                downloadsFolder.mkdirs();
            }
            
            // Create destination file
            File destFile = new File(downloadsFolder, sourceFile.getName());
            
            // Copy file to Downloads folder
            java.nio.file.Files.copy(
                sourceFile.toPath(),
                destFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            // Show success message
            JOptionPane.showMessageDialog(null, 
                "Successfully downloaded: " + displayName + "\nSaved to: " + destFile.getAbsolutePath(),
                "Download Complete", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // Try to open the Downloads folder (optional)
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(downloadsFolder);
            }
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, 
                "Error downloading file: " + ex.getMessage(),
                "Download Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper methods to create downloadable items for each category
    private String[] createGeneralDownloadables() {
        return new String[]{
            "Student Handbook|Student Handbook.pdf",
            "Safety Orientation Manual|Safety Orientation Manual.pdf",
            "Ordinance 021-2018 - Harassment|",  // No PDF file
            "GGuide for Education|GSuite for Education.pdf",
            "The Mission and Identity Cluster|The Mission and Identity Cluster.pdf",
            "Official List of Student Organizations|Official List of Student Organizations.pdf",
            "UNIVERSITY LIBRARIES|UNIVERSITY LIBRARIES.pdf",
            "OSA Student Services Orientation|OSA Student Services Orientation.pdf",
            "KASAMA SSC Orientation|KASAMA SSC Orientation.pdf",
            "Feeling at Home in SLU|",  // No PDF file
            "Health and Safety Protocols re COVID 19 Prevention and Control|Health and Safety Protocols re COVID 19 Prevention and Control.pdf",
            "Policies and Guidelines on Student Behavior during Online Correspondence Based Learning|Policies and Guidelines on Student Behavior during Online Correspondence Based Learning.pdf",
            "SLU Policy on COVID-19 Prevention and Control Measures in the Workplace|SLU Policy on COVID-19 Prevention and Control Measures in the Workplace.pdf",
            "SLU Privacy Policy|SLU Privacy Policy.pdf",
            "SLU Quality Policy|SLU Quality Policy.pdf",
            "SLU UNIVERSITY PRAYER|SLU UNIVERSITY PRAYER.pdf",
            "Special Guidelines for Recognition or Renewal of Recognition 2021 to 2022|Special Guidelines for Recognition or Renewal of Recognition 2021 to 2022.pdf",
            "Baguio City Anti Discrimination Ordinance|Baguio City Anti Discrimination Ordinance.pdf",
            "IRR of RA 11313 Safe Spaces Act|IRR of RA 11313 Safe Spaces Act.pdf",
            "Safe Spaces Act 20190417 RA 11313 R&D|Safe Spaces Act 20190417 RA 11313 RRD.pdf",
            "Emails of SLU Student Groups|Emails of SLU Student Groups.pdf"
        };
    }

    private String[] createCounselingDownloadables() {
        return new String[]{
            "Handouts for Parents on their Children's Independent Living in College|",  // No PDF file
            "Freshie Career Booklet|",  // No PDF file
            "Referral Guide Poster|",  // No PDF file
            "SULN Brochure|",  // No PDF file
            "SULN Form|",  // No PDF file
            "The Louisian Journal 2023|",  // No PDF file
            "CCV Referral Form|"  // No PDF file
        };
    }

    private String[] createOtherServicesDownloadables() {
        return new String[]{
            "MEDICAL CLINIC CONTINUITY PLAN|MEDICAL CLINIC CONTINUITY PLAN.pdf",
            "Online Schedule of Medical Clinic|Online Schedule of Medical Clinic.pdf",
            "SLU Residence Halls|SLU Residence Halls.pdf"
        };
    }

    private String[] createSAMCISDownloadables() {
        return new String[]{
            "SAMCIS Online Helpdesk|SAMCIS Online Helpdesk.pdf",
            "SAMCIS Program Offerings Overview|SAMCIS Program Offerings Overview.pdf"
        };
    }

    private String[] createSASDownloadables() {
        return new String[]{
            "SAS HELPDESK|SAS HELPDESK.pdf"
        };
    }

    private String[] createSEADownloadables() {
        return new String[]{
            "SEA Online Helpdesk|SEA Online Helpdesk.pdf",
            "SEA Program Offerings Overview|SEA Program Offerings Overview.pdf"
        };
    }

    private String[] createSONAHBSDownloadables() {
        return new String[]{
            "SONAHBS Online Helpdesk|",  // No PDF file
            "SNS Program Offerings Overview|SNS Program Offerings Overview.pdf",
            "SON Program Offering Overview|SON Program Offering Overview.pdf"
        };
    }

    private String[] createSOLDownloadables() {
        return new String[]{
            "SOL Online Helpdesk|SOL Online Helpdesk.pdf"
        };
    }

    private String[] createSOMDownloadables() {
        return new String[]{
            "SOM Online Helpdesk|SOM Online Helpdesk.pdf"
        };
    }

    private String[] createSTELADownloadables() {
        return new String[]{
            "STELA Online Helpdesk|STELA Online Helpdesk.pdf",
            "STELA Program Offerings Overview|STELA Program Offerings Overview.pdf"
        };
    }

    private String[] createStudentServicesDownloadables() {
        return new String[]{
            "Guidance Center|",  // No PDF file
            "Office of Student Affairs|",  // No PDF file
            "University Registrar's Office|",  // No PDF file
            "University Libraries|",  // No PDF file
            "Office of External Relations, Media & Communications and Alumni Affairs|",  // No PDF file
            "Office of the Vice President for Mission and Identity|",  // No PDF file
            "Campus Planning, Maintenance, and Security Department|",  // No PDF file
            "Dental Clinic|",  // No PDF file
            "Medical Clinic|",  // No PDF file
            "Technology Management and Development Department|"  // No PDF file
        };
    }

    /**
     * Creates the Curriculum Checklist panel matching the UI design from the image
     */
    private JPanel createCurriculumChecklistPanel(MySinglyLinkedList<String> subItems) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Header - Curriculum Checklist
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header content
        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setBackground(new Color(10, 45, 90));
        
        JLabel headerLabel = new JLabel(subItems.toString());
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerContent.add(headerLabel);
        
        // Note text
        JLabel noteLabel = new JLabel("NOTE: For inquiries regarding your checklist please proceed to your respective Dean's offices.");
        noteLabel.setForeground(new Color(255, 204, 102));
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        headerContent.add(Box.createVerticalStrut(5));
        headerContent.add(noteLabel);
        
        headerPanel.add(headerContent, BorderLayout.WEST);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Create curriculum table
        String[] columnNames = {"", "Course Number", "Course Description", "Units"};
        
        // Generate curriculum data matching the image
        Object[][] curriculumData = generateCurriculumData();
        
        DefaultTableModel curriculumModel = new DefaultTableModel(curriculumData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class; // For checkboxes
                }
                return String.class;
            }
        };

        JTable curriculumTable = new JTable(curriculumModel);
        curriculumTable.setRowHeight(25);
        curriculumTable.getTableHeader().setReorderingAllowed(false);
        curriculumTable.setAutoCreateRowSorter(false);
        curriculumTable.setShowGrid(true);
        curriculumTable.setGridColor(new Color(200, 200, 200));
        curriculumTable.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Set column widths
        curriculumTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // Checkbox
        curriculumTable.getColumnModel().getColumn(0).setMaxWidth(30);
        curriculumTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Course Number
        curriculumTable.getColumnModel().getColumn(2).setPreferredWidth(500); // Description
        curriculumTable.getColumnModel().getColumn(3).setPreferredWidth(50);  // Units
        
        // Style the table header
        curriculumTable.getTableHeader().setBackground(new Color(220, 220, 220));
        curriculumTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Custom renderer for semester headers (bold rows with no checkbox)
        curriculumTable.getColumnModel().getColumn(0).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            private final JCheckBox defaultRenderer = new JCheckBox();
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Object courseNum = table.getValueAt(row, 1);

                if (courseNum == null || courseNum.toString().trim().isEmpty()) {
                    return null;
                } else {
                    defaultRenderer.setSelected(value != null && (Boolean) value);
                    defaultRenderer.setHorizontalAlignment(SwingConstants.CENTER);
                    if (row % 2 != 0) {
                        defaultRenderer.setBackground(Color.WHITE);
                    } else {
                        defaultRenderer.setBackground(Color.WHITE);
                    }
                    return defaultRenderer;
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(curriculumTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    /**
     * Generates curriculum data matching the BSIT curriculum from the image
     */
    private Object[][] generateCurriculumData() {
        java.util.List<Object[]> data = new java.util.ArrayList<>();
        
        // First Year, First Semester
        data.add(new Object[]{null, "", "First Year, First Semester", ""});
        data.add(new Object[]{true, "CFE 101", "GOD'S JOURNEY WITH HIS PEOPLE", "3"});
        data.add(new Object[]{true, "FIT HW", "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (HEALTH AND WELLNESS)", "2"});
        data.add(new Object[]{true, "GART", "ART APPRECIATION", "3"});
        data.add(new Object[]{true, "GHIST", "READINGS IN PHILIPPINE HISTORY", "3"});
        data.add(new Object[]{true, "GSELF", "UNDERSTANDING THE SELF", "3"});
        data.add(new Object[]{true, "IT 111", "INTRODUCTION TO COMPUTING (LEC)", "2"});
        data.add(new Object[]{true, "IT 111L", "INTRODUCTION TO COMPUTING (LAB)", "1"});
        data.add(new Object[]{true, "IT 112", "COMPUTER PROGRAMMING 1 (LEC)", "2"});
        data.add(new Object[]{true, "IT 112L", "COMPUTER PROGRAMMING 1 (LAB)", "1"});
        data.add(new Object[]{true, "IT 113", "DISCRETE MATHEMATICS", "3"});
        
        // First Year, Second Semester
        data.add(new Object[]{null, "", "First Year, Second Semester", ""});
        data.add(new Object[]{true, "CFE 102", "CHRISTIAN MORALITY IN OUR TIMES", "3"});
        data.add(new Object[]{true, "FIT CS", "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (COMBATIVE SPORTS)", "2"});
        data.add(new Object[]{true, "GCWORLD", "THE CONTEMPORARY WORLD", "3"});
        data.add(new Object[]{true, "GMATH", "MATHEMATICS IN THE MODERN WORLD", "3"});
        data.add(new Object[]{true, "GPCOM", "PURPOSIVE COMMUNICATION", "3"});
        data.add(new Object[]{true, "IT 121", "INFORMATION SYSTEM FUNDAMENTALS", "3"});
        data.add(new Object[]{true, "IT 122", "COMPUTER PROGRAMMING 2", "2"});
        data.add(new Object[]{true, "IT 122L", "COMPUTER PROGRAMMING 2 (LAB)", "1"});
        data.add(new Object[]{true, "IT 123", "PLATFORM TECHNOLOGIES", "2"});
        data.add(new Object[]{true, "IT 123L", "PLATFORM TECHNOLOGIES (LAB)", "1"});
        
        // First Year, Short Term
        data.add(new Object[]{null, "", "First Year, Short Term", ""});
        data.add(new Object[]{true, "GRIZAL", "THE LIFE AND WORKS OF RIZAL", "3"});
        data.add(new Object[]{true, "IT 131", "COMPUTER ARCHITECTURE", "2"});
        data.add(new Object[]{true, "IT 131L", "COMPUTER ARCHITECTURE (LAB)", "1"});
        
        // Second Year, First Semester
        data.add(new Object[]{null, "", "Second Year, First Semester", ""});
        data.add(new Object[]{false, "CFE 103", "CATHOLIC FOUNDATION OF MISSION", "3"});
        data.add(new Object[]{false, "FIT OA", "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (OUTDOOR AND ADVENTURE ACTIVITIES)", "2"});
        data.add(new Object[]{false, "GENVI", "ENVIRONMENTAL SCIENCE", "3"});
        data.add(new Object[]{false, "GSTS", "SCIENCE, TECHNOLOGY, AND SOCIETY", "3"});
        data.add(new Object[]{false, "IT 211", "REQUIREMENTS ANALYSIS AND MODELING", "3"});
        data.add(new Object[]{false, "IT 212", "DATA STRUCTURES (LEC)", "2"});
        data.add(new Object[]{false, "IT 212L", "DATA STRUCTURES (LAB)", "1"});
        data.add(new Object[]{false, "IT 213", "NETWORK FUNDAMENTALS (LEC)", "2"});
        data.add(new Object[]{false, "IT 213L", "NETWORK FUNDAMENTALS (LAB)", "1"});
        data.add(new Object[]{false, "NSTP-CWTS 1", "FOUNDATIONS OF SERVICE", "3"});
        
        // Second Year, Second Semester
        data.add(new Object[]{null, "", "Second Year, Second Semester", ""});
        data.add(new Object[]{false, "CFE 104", "CICM MISSIONARY IDENTITY", "3"});
        data.add(new Object[]{false, "FIT AQ", "PHYSICAL ACTIVITY TOWARDS HEALTH AND FITNESS (AQUATICS)", "2"});
        data.add(new Object[]{false, "GENTREP", "THE ENTREPRENEURIAL MIND", "3"});
        data.add(new Object[]{false, "GRVA", "READING VISUAL ART", "3"});
        data.add(new Object[]{false, "IT 221", "INFORMATION MANAGEMENT (LEC)", "2"});
        data.add(new Object[]{false, "IT 221L", "INFORMATION MANAGEMENT (LAB)", "1"});
        data.add(new Object[]{false, "IT 222", "INTEGRATIVE TECHNOLOGIES (LEC)", "2"});
        data.add(new Object[]{false, "IT 222L", "INTEGRATIVE TECHNOLOGIES (LAB)", "1"});
        data.add(new Object[]{false, "IT 223", "HUMAN COMPUTER INTERACTION", "3"});
        data.add(new Object[]{false, "NSTP-CWTS 2", "SOCIAL AWARENESS AND EMPOWERMENT FOR SERVICE", "3"});
        
        // Second Year, Short Term
        data.add(new Object[]{null, "", "Second Year, Short Term", ""});
        data.add(new Object[]{false, "CS 314", "SOCIAL AND PERSONAL DEVELOPMENT IN THE ICT WORKPLACE", "3"});
        data.add(new Object[]{false, "CS 315", "TECHNOLOGY-ASSISTED PRESENTATION AND COMMUNICATION", "3"});
        data.add(new Object[]{false, "GETHICS", "ETHICS", "3"});
        
        // Third Year, First Semester
        data.add(new Object[]{null, "", "Third Year, First Semester", ""});
        data.add(new Object[]{false, "CFE 105A", "CICM IN ACTION: JUSTICE, PEACE, INTEGRITY OF CREATION, INDIGENOUS PEOPLES & INTERRELIGIOUS DIALOGUE", "1.5"});
        data.add(new Object[]{false, "IT 311", "APPLICATIONS DEVELOPMENT (LEC)", "2"});
        data.add(new Object[]{false, "IT 311L", "APPLICATIONS DEVELOPMENT (LAB)", "1"});
        data.add(new Object[]{false, "IT 312", "WEB TECHNOLOGIES (LEC)", "2"});
        data.add(new Object[]{false, "IT 312L", "WEB TECHNOLOGIES (LAB)", "1"});
        data.add(new Object[]{false, "IT 313", "SOFTWARE ENGINEERING", "3"});
        data.add(new Object[]{false, "IT 314", "SOCIAL AND PROFESSIONAL ISSUES IN INFORMATION TECHNOLOGY", "3"});
        data.add(new Object[]{false, "IT 315", "TECHNOPRENEURSHIP", "3"});
        data.add(new Object[]{false, "ITE 15", "IT SECURITY MANAGEMENT (Elective)", "3"});
        data.add(new Object[]{false, "ITE 23", "ELECTRONIC COMMERCE (Elective)", "3"});
        data.add(new Object[]{false, "ITE 30", "INFORMATION TECHNOLOGY CERTIFICATION REVIEW (Elective)", "3"});
        
        // Third Year, Second Semester
        data.add(new Object[]{null, "", "Third Year, Second Semester", ""});
        data.add(new Object[]{false, "CFE 105B", "CICM IN ACTION: ENVIRONMENTAL PLANNING & MANAGEMENT, AND DISASTER RISK REDUCTION MANAGEMENT", "1.5"});
        data.add(new Object[]{false, "IT 321", "IT PROJECT 1", "3"});
        data.add(new Object[]{false, "IT 322", "DATA ANALYTICS (LEC)", "2"});
        data.add(new Object[]{false, "IT 322L", "DATA ANALYTICS (LAB)", "1"});
        data.add(new Object[]{false, "IT 323", "SYSTEM ADMINISTRATION AND MAINTENANCE (LEC)", "2"});
        data.add(new Object[]{false, "IT 323L", "SYSTEM ADMINISTRATION AND MAINTENANCE (LAB)", "1"});
        data.add(new Object[]{false, "IT 324", "SYSTEM INTEGRATION AND ARCHITECTURE", "3"});
        data.add(new Object[]{false, "IT 325", "FIELD TRIPS AND SEMINARS", "3"});
        data.add(new Object[]{false, "ITE 16", "CURRENT TRENDS 1 (Elective)", "3"});
        data.add(new Object[]{false, "ITE 27", "CURRENT TRENDS 2 (Elective)", "3"});
        data.add(new Object[]{false, "ITE 29", "SPECIAL TOPICS 2 (Elective)", "3"});
        
        // Third Year, Short Term
        data.add(new Object[]{null, "", "Third Year, Short Term", ""});
        data.add(new Object[]{false, "IT 331", "INFORMATION ASSURANCE AND SECURITY", "3"});
        data.add(new Object[]{false, "ITE 17", "DATA MINING (LEC)", "2"});
        data.add(new Object[]{false, "ITE 17L", "DATA MINING (LAB)", "1"});
        
        // Fourth Year, First Semester
        data.add(new Object[]{null, "", "Fourth Year, First Semester", ""});
        data.add(new Object[]{false, "CFE 106A", "EMBRACING THE CICM MISSION", "1.5"});
        data.add(new Object[]{false, "FOR LANG 1", "FOREIGN LANGUAGE 1", "3"});
        data.add(new Object[]{false, "IT 411", "IT PROJECT 2", "3"});
        data.add(new Object[]{false, "IT 412", "IT RESOURCE MANAGEMENT", "3"});
        data.add(new Object[]{false, "ITE 14", "UX CONCEPTS AND DESIGN (Elective)", "3"});
        data.add(new Object[]{false, "ITE 28", "SPECIAL TOPICS 1 (Elective)", "3"});
        
        // Fourth Year, Second Semester
        data.add(new Object[]{null, "", "Fourth Year, Second Semester", ""});
        data.add(new Object[]{false, "CFE 106B", "EMBRACING THE CICM MISSION", "1.5"});
        data.add(new Object[]{false, "IT 421", "PRACTICUM", "9"});
        
        return data.toArray(new Object[data.size()][4]);
    }
    
    /**
     * Validates password requirements
     * @param password The password to validate
     * @return null if valid, error message if invalid
     */
    private String validatePasswordRequirements(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;
        
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowercase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if (specialChars.contains(String.valueOf(c))) {
                hasSpecialChar = true;
            }
        }
        
        if (!hasUppercase) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!hasLowercase) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!hasDigit) {
            return "Password must contain at least one number.";
        }
        if (!hasSpecialChar) {
            return "Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?).";
        }
        
        return null; // Password is valid
    }
    
    /**
     * Refreshes the UI to reflect current database information
     */
    private void refreshUIFromDatabase() {
        // Refresh personal details if currently shown
        Component rightComponent = null;
        JPanel mainContentPanel = (JPanel) contentPanel.getComponent(0);
        if (mainContentPanel.getComponentCount() > 1) {
            rightComponent = mainContentPanel.getComponent(1);
        }
        
        // Check which panel is currently active and refresh it
        if (rightComponent != null) {
            // Find which button is currently selected by checking their borders
            Container leftPanel = (Container) mainContentPanel.getComponent(0);
            for (Component comp : leftPanel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton button = (JButton) comp;
                    if (button.getBorder() instanceof CompoundBorder) {
                        CompoundBorder border = (CompoundBorder) button.getBorder();
                        if (border.getOutsideBorder() instanceof LineBorder) {
                            LineBorder lineBorder = (LineBorder) border.getOutsideBorder();
                            if (lineBorder.getThickness() == 2) { // Selected button has thick border
                                String buttonText = button.getText();
                                switch (buttonText) {
                                    case "📋 Personal Details":
                                        // Remove existing right panel and add updated one
                                        Component centerComponentPD = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                                        if (centerComponentPD != null) {
                                            mainContentPanel.remove(centerComponentPD);
                                        }
                                        Component personalDetailsPanel = showPersonalDetailsInRightPanel();
                                        mainContentPanel.add(personalDetailsPanel, BorderLayout.CENTER);
                                        
                                        // Ensure left panel remains visible
                                        Component westComponent = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
                                        if (westComponent != null) {
                                            westComponent.setVisible(true);
                                        }
                                        
                                        mainContentPanel.revalidate();
                                        mainContentPanel.repaint();
                                        break;
                                    case "🔐 Account Info":
                                        // Remove existing right panel and add updated one
                                        Component centerComponentAI = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                                        if (centerComponentAI != null) {
                                            mainContentPanel.remove(centerComponentAI);
                                        }
                                        Component accountInfoPanel = showAccountInfo();
                                        mainContentPanel.add(accountInfoPanel, BorderLayout.CENTER);
                                        
                                        // Ensure left panel remains visible
                                        Component westComponentAI = ((BorderLayout)mainContentPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
                                        if (westComponentAI != null) {
                                            westComponentAI.setVisible(true);
                                        }
                                        
                                        mainContentPanel.revalidate();
                                        mainContentPanel.repaint();
                                        break;
                                    // Change Password panel doesn't need refresh as it's always empty
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Starts monitoring the database file for changes
     */
    private void startDatabaseMonitoring() {
        // Get initial database modification time
        updateDatabaseModificationTime();
        
        // Create timer to check for database changes every 5 seconds
        databaseCheckTimer = new Timer(5000, e -> checkDatabaseChanges());
        databaseCheckTimer.start();
    }
    
    /**
     * Stops database monitoring
     */
    private void stopDatabaseMonitoring() {
        if (databaseCheckTimer != null) {
            databaseCheckTimer.stop();
            databaseCheckTimer = null;
        }
    }
    
    /**
     * Updates the stored database modification time
     */
    private void updateDatabaseModificationTime() {
        try {
            File dbFile = new File("Database.txt");
            if (dbFile.exists()) {
                lastDatabaseModified = dbFile.lastModified();
            }
        } catch (Exception e) {
            System.err.println("Error checking database modification time: " + e.getMessage());
        }
    }
    
    /**
     * Checks if the database has been modified and refreshes UI if needed
     */
    private void checkDatabaseChanges() {
        try {
            File dbFile = new File("Database.txt");
            if (dbFile.exists()) {
                long currentModified = dbFile.lastModified();
                if (currentModified > lastDatabaseModified) {
                    lastDatabaseModified = currentModified;
                    // Database has been modified, refresh the UI
                    SwingUtilities.invokeLater(() -> refreshUIFromDatabase());
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking database changes: " + e.getMessage());
        }
    }

    /**
     * Parses profile data string from Database.txt into ProfileData object
     */
    private ProfileData parseProfileData(String profileData) {
        ProfileData profile = new ProfileData();
        
        if (profileData == null || profileData.trim().isEmpty()) {
            System.out.println("DEBUG: No profile data found, using defaults");
            return profile; // Return default values
        }
        
        System.out.println("DEBUG: Parsing profile data: " + profileData);
        
        try {
            String[] pairs = profileData.split(";");
            System.out.println("DEBUG: Found " + pairs.length + " data pairs");
            for (String pair : pairs) {
                if (pair.trim().isEmpty()) continue;
                
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    System.out.println("DEBUG: Parsing " + key + " = " + value);
                    
                    switch (key) {
                        case "Gender": profile.gender = value; break;
                        case "Citizenship": profile.citizenship = value; break;
                        case "Religion": profile.religion = value; break;
                        case "CivilStatus": profile.civilStatus = value; break;
                        case "Birthplace": profile.birthplace = value; break;
                        case "Nationality": profile.nationality = value; break;
                        case "HomeAddress": profile.homeAddress = value; break;
                        case "HomeTel": profile.homeTel = value; break;
                        case "BaguioAddress": profile.baguioAddress = value; break;
                        case "BaguioTel": profile.baguioTel = value; break;
                        case "Cellphone": profile.cellphone = value; break;
                        case "FatherName": profile.fatherName = value; break;
                        case "FatherOcc": profile.fatherOcc = value; break;
                        case "MotherName": profile.motherName = value; break;
                        case "MotherOcc": profile.motherOcc = value; break;
                        case "GuardianName": profile.guardianName = value; break;
                        case "GuardianAddress": profile.guardianAddress = value; break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing profile data: " + e.getMessage());
        }
        
        return profile;
    }

    /**
     * Creates the Medical Record panel with sidebar and content areas
     */
    // Store current medical record and content panel reference
    private MedicalRecord currentMedicalRecord;
    private JPanel medicalContentPanel;
    private JPanel currentMedicalContentView; // Store current view to preserve state
    
    private JPanel createMedicalRecordPanel(MySinglyLinkedList<String> subItems) {
        // Always reload medical record from database when creating panel
        currentMedicalRecord = DataManager.getMedicalRecord(studentID);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main content wrapper
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        contentWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(13, 37, 73));
        titlePanel.setPreferredSize(new Dimension(0, 40));
        
        JLabel titleLabel = new JLabel("🏥 Medical Record");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        contentWrapper.add(titlePanel, BorderLayout.NORTH);

        // Create split pane for sidebar and content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);

        // Left sidebar with medical categories
        JPanel sidebarPanel = createMedicalSidebar();
        splitPane.setLeftComponent(sidebarPanel);

        // Right content area
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(Color.WHITE);
        
        // Store reference to content panel for updates
        medicalContentPanel = new JPanel(new BorderLayout());
        medicalContentPanel.setBackground(Color.WHITE);
        medicalContentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Reset current view
        currentMedicalContentView = null;
        
        // Show default content (Personal Information)
        showMedicalPersonalInfo();
        
        contentArea.add(medicalContentPanel, BorderLayout.CENTER);
        
        // Update button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton updateButton = new JButton("Update Medical Records");
        updateButton.setBackground(new Color(13, 37, 73));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Arial", Font.BOLD, 13));
        updateButton.setPreferredSize(new Dimension(180, 35)); // Larger button
        updateButton.setFocusPainted(false);
        updateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        updateButton.addActionListener(e -> {
            // Force save medical record to database
            if (DataManager.saveMedicalRecord(currentMedicalRecord)) {
                // Reload the data to ensure consistency
                currentMedicalRecord = DataManager.getMedicalRecord(studentID);
                JOptionPane.showMessageDialog(this, 
                    "Medical records updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update medical records!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(updateButton);
        contentArea.add(buttonPanel, BorderLayout.SOUTH);
        
        splitPane.setRightComponent(contentArea);
        contentWrapper.add(splitPane, BorderLayout.CENTER);
        
        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        return mainPanel;
    }
    
    // Store reference to currently selected button
    private JButton currentSelectedMedicalButton = null;
    
    /**
     * Creates the medical sidebar with category buttons
     */
    private JPanel createMedicalSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        sidebar.setPreferredSize(new Dimension(250, 0)); // Fixed width for consistency
        
        // Medical categories
        String[] categories = {
            "👤 Personal Info",
            "📋 Allergies",
            "🏥 Past Medical History", 
            "👶 Childhood Diseases",
            "👩 Gynecologic History",
            "🧑 Personal/Social History",
            "💉 Immunization",
            "📝 Others",
            "🔍 Consultation Record"
        };
        
        JButton firstButton = null;
        for (String category : categories) {
            JButton categoryBtn = createMedicalCategoryButton(category);
            if (firstButton == null) {
                firstButton = categoryBtn;
            }
            sidebar.add(categoryBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 8))); // Increased spacing for better visual separation
        }
        
        // Select the first button by default
        if (firstButton != null) {
            firstButton.setBackground(new Color(220, 240, 255));
            firstButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
            currentSelectedMedicalButton = firstButton;
        }
        
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }
    
    /**
     * Creates a styled medical category button
     */
    private JButton createMedicalCategoryButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Increased height for better touch target
        button.setPreferredSize(new Dimension(220, 40)); // Fixed size for consistency
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(13, 37, 73));
        button.setFont(new Font("Arial", Font.PLAIN, 13)); // Slightly larger font
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 15, 8, 15) // More padding
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Add hand cursor for better UX
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != currentSelectedMedicalButton) {
                    button.setBackground(new Color(245, 245, 245));
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180)), // Blue border on hover
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != currentSelectedMedicalButton) {
                    button.setBackground(Color.WHITE);
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                // Update button selection
                if (currentSelectedMedicalButton != null) {
                    currentSelectedMedicalButton.setBackground(Color.WHITE);
                    currentSelectedMedicalButton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                    ));
                }
                button.setBackground(new Color(220, 240, 255)); // Light blue for selected
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180), 2), // Thicker blue border for selected
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)
                ));
                currentSelectedMedicalButton = button;
                
                // Reload medical record before switching panels to ensure data is current
                currentMedicalRecord = DataManager.getMedicalRecord(studentID);
                
                // Update content panel based on selected category
                if (text.contains("Personal Info")) {
                    showMedicalPersonalInfo();
                } else if (text.contains("Allergies")) {
                    showMedicalCategoryPanel("Allergies", currentMedicalRecord.getAllergies(), 
                        newContent -> currentMedicalRecord.setAllergies(newContent));
                } else if (text.contains("Past Medical History")) {
                    showMedicalCategoryPanel("Past Medical History", currentMedicalRecord.getPastMedicalHistory(),
                        newContent -> currentMedicalRecord.setPastMedicalHistory(newContent));
                } else if (text.contains("Childhood Diseases")) {
                    showMedicalCategoryPanel("Childhood Diseases", currentMedicalRecord.getChildhoodDiseases(),
                        newContent -> currentMedicalRecord.setChildhoodDiseases(newContent));
                } else if (text.contains("Gynecologic History")) {
                    showMedicalCategoryPanel("Gynecologic History", currentMedicalRecord.getGynecologicHistory(),
                        newContent -> currentMedicalRecord.setGynecologicHistory(newContent));
                } else if (text.contains("Personal/Social History")) {
                    showMedicalCategoryPanel("Personal/Social History", currentMedicalRecord.getPersonalSocialHistory(),
                        newContent -> currentMedicalRecord.setPersonalSocialHistory(newContent));
                } else if (text.contains("Immunization")) {
                    showMedicalCategoryPanel("Immunization", currentMedicalRecord.getImmunization(),
                        newContent -> currentMedicalRecord.setImmunization(newContent));
                } else if (text.contains("Others")) {
                    showMedicalCategoryPanel("Others", currentMedicalRecord.getOthers(),
                        newContent -> currentMedicalRecord.setOthers(newContent));
                } else if (text.contains("Consultation Record")) {
                    showMedicalCategoryPanel("Consultation Record", currentMedicalRecord.getConsultationRecord(),
                        newContent -> currentMedicalRecord.setConsultationRecord(newContent));
                } else {
                    // Default: show personal info
                    showMedicalPersonalInfo();
                }
            }
        });
        
        return button;
    }
    
    /**
     * Shows medical category panel in the main content area
     */
    private void showMedicalCategoryPanel(String category, String currentContent, java.util.function.Consumer<String> updateFunction) {
        // Save any pending changes before switching
        if (currentMedicalContentView != null) {
            DataManager.saveMedicalRecord(currentMedicalRecord);
        }
        
        medicalContentPanel.removeAll();
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(245, 245, 245));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel titleLabel = new JLabel(category.toUpperCase());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(13, 37, 73));
        titlePanel.add(titleLabel);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Create specific panel based on category
        JPanel contentPanel = null;
        
        if (category.contains("Past Medical History")) {
            contentPanel = createPastMedicalHistoryPanel();
        } else if (category.contains("Childhood Diseases")) {
            contentPanel = createChildhoodDiseasesPanel();
        } else if (category.contains("Gynecologic History")) {
            contentPanel = createGynecologicHistoryPanel();
        } else if (category.contains("Personal/Social History")) {
            contentPanel = createPersonalSocialHistoryPanel();
        } else if (category.contains("Immunization")) {
            contentPanel = createImmunizationPanel();
        } else if (category.contains("Others")) {
            contentPanel = createOthersPanel();
        } else if (category.contains("Consultation Record")) {
            contentPanel = createConsultationRecordPanel();
        } else {
            // Default text area for other categories
            contentPanel = createDefaultTextPanel(category, currentContent, updateFunction);
        }
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        currentMedicalContentView = panel; // Store current view
        medicalContentPanel.add(panel, BorderLayout.CENTER);
        medicalContentPanel.revalidate();
        medicalContentPanel.repaint();
    }
    
    /**
     * Creates default text panel for categories
     */
    private JPanel createDefaultTextPanel(String category, String currentContent, java.util.function.Consumer<String> updateFunction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Content area
        JTextArea contentArea = new JTextArea();
        contentArea.setFont(new Font("Arial", Font.PLAIN, 12));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Load existing content or show placeholder
        if (currentContent != null && !currentContent.isEmpty()) {
            contentArea.setText(currentContent);
        } else {
            // Set helpful placeholder text based on category
            String placeholder = getPlaceholderText(category);
            contentArea.setText(placeholder);
            contentArea.setForeground(Color.GRAY);
        }
        
        // Clear placeholder on focus
        contentArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (contentArea.getForeground() == Color.GRAY) {
                    contentArea.setText("");
                    contentArea.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (contentArea.getText().isEmpty()) {
                    contentArea.setText(getPlaceholderText(category));
                    contentArea.setForeground(Color.GRAY);
                }
            }
        });
        
        // Update the medical record when text changes
        contentArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateContent(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateContent(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateContent(); }
            
            private void updateContent() {
                if (contentArea.getForeground() != Color.GRAY) {
                    updateFunction.accept(contentArea.getText());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Get placeholder text for medical categories
     */
    private String getPlaceholderText(String category) {
        if (category.contains("Allergies")) {
            return "Enter any known allergies to medications, foods, or environmental factors...\n\nExample:\n- Penicillin\n- Peanuts\n- Dust mites";
        } else if (category.contains("Past Medical History")) {
            return "Enter previous hospitalizations, surgeries, or chronic conditions...\n\nExample:\n- Appendectomy (2020)\n- Asthma since childhood\n- Hospitalized for dengue (2019)";
        } else if (category.contains("Childhood Diseases")) {
            return "Enter childhood diseases...\n\nExample:\n- Chickenpox (age 5)\n- Measles (age 7)\n- Mumps (age 8)";
        } else if (category.contains("Gynecologic History")) {
            return "For female students: Enter relevant gynecologic history...\n\n- LMP (Last Menstrual Period):\n- Cycle regularity:\n- Any concerns:";
        } else if (category.contains("Personal/Social History")) {
            return "Enter lifestyle and social history...\n\nExample:\n- Smoking: None\n- Alcohol: Occasional\n- Exercise: 3x per week\n- Sleep: 6-8 hours daily";
        } else if (category.contains("Immunization")) {
            return "Enter immunization history...\n\nExample:\n- COVID-19 Vaccine: Completed (2021)\n- Hepatitis B: Completed\n- Flu Vaccine: Annual\n- Tetanus: Last booster (2020)";
        } else if (category.contains("Others")) {
            return "Enter any other relevant medical information...\n\n- Family medical history\n- Current medications\n- Vitamins/supplements\n- Other concerns";
        } else if (category.contains("Consultation Record")) {
            return "Enter consultation details...\n\nDate: \nTime: \nChief Complaint: \nVital Signs: \nDiagnosis: \nTreatment/Medications: \nFollow-up: ";
        }
        return "Enter information...";
    }
    
    /**
     * Creates Past Medical History panel with table and details
     */
    private JPanel createPastMedicalHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Parse existing data
        String existingData = currentMedicalRecord.getPastMedicalHistory();
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Table headers
        String[] columnNames = {"Sickness", "Details"};
        
        // Create table model
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        // Parse existing data and add to table
        if (existingData != null && !existingData.isEmpty()) {
            String[] lines = existingData.split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    model.addRow(new Object[]{parts[0].trim(), parts[1].trim()});
                }
            }
        }
        
        // Add some empty rows if table is empty
        if (model.getRowCount() == 0) {
            for (int i = 0; i < 5; i++) {
                model.addRow(new Object[]{"", ""});
            }
        }
        
        // Create table
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        
        // Add table change listener to update medical record
        model.addTableModelListener(e -> {
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < model.getRowCount(); i++) {
                String sickness = (String) model.getValueAt(i, 0);
                String details = (String) model.getValueAt(i, 1);
                if (sickness != null && !sickness.trim().isEmpty()) {
                    data.append(sickness).append(": ").append(details).append("\n");
                }
            }
            currentMedicalRecord.setPastMedicalHistory(data.toString().trim());
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setPreferredSize(new Dimension(600, 200));
        formPanel.add(scrollPane);
        
        // Add row button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addRowBtn = new JButton("+ Add Row");
        addRowBtn.addActionListener(e -> {
            model.addRow(new Object[]{"", ""});
        });
        buttonPanel.add(addRowBtn);
        formPanel.add(buttonPanel);
        
        // Select link
        JLabel selectLabel = new JLabel("<html><u>SELECT</u></html>");
        selectLabel.setForeground(new Color(0, 102, 204));
        selectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show common conditions dialog
                String[] commonConditions = {
                    "Hypertension", "Diabetes", "Asthma", "Allergies", 
                    "Heart Disease", "Kidney Disease", "Liver Disease",
                    "Tuberculosis", "Pneumonia", "COVID-19"
                };
                String selected = (String) JOptionPane.showInputDialog(
                    panel, "Select a condition:", "Common Conditions",
                    JOptionPane.PLAIN_MESSAGE, null, commonConditions, commonConditions[0]
                );
                if (selected != null) {
                    // Find first empty row or add new one
                    boolean added = false;
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (model.getValueAt(i, 0).toString().isEmpty()) {
                            model.setValueAt(selected, i, 0);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        model.addRow(new Object[]{selected, ""});
                    }
                }
            }
        });
        buttonPanel.add(selectLabel);
        
        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates Childhood Diseases panel with checkboxes
     */
    private JPanel createChildhoodDiseasesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Parse existing data
        String existingData = currentMedicalRecord.getChildhoodDiseases();
        java.util.Set<String> selectedDiseases = new java.util.HashSet<>();
        if (existingData != null && !existingData.isEmpty()) {
            selectedDiseases.addAll(java.util.Arrays.asList(existingData.split(",")));
        }
        
        // Common childhood diseases
        String[] diseases = {
            "Chickenpox", "Measles", "Mumps", "Rubella",
            "Whooping Cough", "Polio", "Diphtheria", "Tetanus",
            "Hepatitis A", "Hepatitis B", "Meningitis", "Pneumonia"
        };
        
        // Create checkbox panel
        JPanel checkboxPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        checkboxPanel.setBackground(Color.WHITE);
        
        java.util.List<JCheckBox> checkboxes = new java.util.ArrayList<>();
        for (String disease : diseases) {
            JCheckBox checkbox = new JCheckBox(disease);
            checkbox.setBackground(Color.WHITE);
            checkbox.setSelected(selectedDiseases.contains(disease.trim()));
            checkbox.addActionListener(e -> {
                // Update medical record
                StringBuilder selected = new StringBuilder();
                for (JCheckBox cb : checkboxes) {
                    if (cb.isSelected()) {
                        if (selected.length() > 0) selected.append(", ");
                        selected.append(cb.getText());
                    }
                }
                currentMedicalRecord.setChildhoodDiseases(selected.toString());
            });
            checkboxes.add(checkbox);
            checkboxPanel.add(checkbox);
        }
        
        contentPanel.add(checkboxPanel);
        
        // Add "Others" text field
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel othersPanel = new JPanel(new BorderLayout());
        othersPanel.setBackground(Color.WHITE);
        othersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel othersLabel = new JLabel("Others: ");
        othersLabel.setPreferredSize(new Dimension(60, 25));
        othersPanel.add(othersLabel, BorderLayout.WEST);
        
        JTextField othersField = new JTextField();
        othersField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateOthers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateOthers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateOthers(); }
            
            private void updateOthers() {
                String current = currentMedicalRecord.getChildhoodDiseases();
                String others = othersField.getText();
                if (!others.isEmpty()) {
                    if (current != null && !current.isEmpty()) {
                        currentMedicalRecord.setChildhoodDiseases(current + ", " + others);
                    } else {
                        currentMedicalRecord.setChildhoodDiseases(others);
                    }
                }
            }
        });
        othersPanel.add(othersField, BorderLayout.CENTER);
        contentPanel.add(othersPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates Gynecologic History panel
     */
    private JPanel createGynecologicHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Parse existing data
        String existingData = currentMedicalRecord.getGynecologicHistory();
        java.util.Map<String, String> dataMap = new java.util.HashMap<>();
        if (existingData != null && !existingData.isEmpty()) {
            String[] lines = existingData.split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    dataMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        
        // Menarche
        JPanel menarchePanel = createLabeledFieldPanel("Menarche (Age of 1st Menstruation):", 
            dataMap.getOrDefault("Menarche", ""));
        formPanel.add(menarchePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Interval
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        intervalPanel.setBackground(Color.WHITE);
        intervalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        intervalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel intervalLabel = new JLabel("Interval:");
        intervalLabel.setPreferredSize(new Dimension(200, 25));
        intervalPanel.add(intervalLabel);
        
        JComboBox<String> intervalCombo = new JComboBox<>(new String[]{
            "", "Regular", "Irregular", "21-35 days", "Less than 21 days", "More than 35 days"
        });
        intervalCombo.setSelectedItem(dataMap.getOrDefault("Interval", ""));
        intervalCombo.setPreferredSize(new Dimension(200, 25));
        intervalPanel.add(intervalCombo);
        formPanel.add(intervalPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Duration of menstruation
        JPanel durationPanel = createLabeledFieldPanel("Duration of menstruation:", 
            dataMap.getOrDefault("Duration", ""));
        formPanel.add(durationPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Dysmenorrhea
        JPanel dysmenorrheaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dysmenorrheaPanel.setBackground(Color.WHITE);
        dysmenorrheaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        dysmenorrheaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel dysmenorrheaLabel = new JLabel("Dysmenorrhea:");
        dysmenorrheaLabel.setPreferredSize(new Dimension(200, 25));
        dysmenorrheaPanel.add(dysmenorrheaLabel);
        
        JComboBox<String> dysmenorrheaCombo = new JComboBox<>(new String[]{"", "None", "Mild", "Moderate", "Severe"});
        dysmenorrheaCombo.setSelectedItem(dataMap.getOrDefault("Dysmenorrhea", ""));
        dysmenorrheaCombo.setPreferredSize(new Dimension(200, 25));
        dysmenorrheaPanel.add(dysmenorrheaCombo);
        formPanel.add(dysmenorrheaPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // No. of Pregnancy
        JPanel pregnancyPanel = createLabeledFieldPanel("No. of Pregnancy:", 
            dataMap.getOrDefault("Pregnancy", ""));
        formPanel.add(pregnancyPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // No. of Living Children
        JPanel childrenPanel = createLabeledFieldPanel("No. of Living Children:", 
            dataMap.getOrDefault("Children", ""));
        formPanel.add(childrenPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Update button
        JButton updateBtn = new JButton("Update");
        updateBtn.setBackground(new Color(13, 37, 73));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.addActionListener(e -> {
            // Gather all data and save
            StringBuilder data = new StringBuilder();
            
            JTextField menarcheField = (JTextField) menarchePanel.getComponent(1);
            if (!menarcheField.getText().isEmpty()) {
                data.append("Menarche: ").append(menarcheField.getText()).append("\n");
            }
            
            if (!intervalCombo.getSelectedItem().toString().isEmpty()) {
                data.append("Interval: ").append(intervalCombo.getSelectedItem()).append("\n");
            }
            
            JTextField durationField = (JTextField) durationPanel.getComponent(1);
            if (!durationField.getText().isEmpty()) {
                data.append("Duration: ").append(durationField.getText()).append("\n");
            }
            
            if (!dysmenorrheaCombo.getSelectedItem().toString().isEmpty()) {
                data.append("Dysmenorrhea: ").append(dysmenorrheaCombo.getSelectedItem()).append("\n");
            }
            
            JTextField pregnancyField = (JTextField) pregnancyPanel.getComponent(1);
            if (!pregnancyField.getText().isEmpty()) {
                data.append("Pregnancy: ").append(pregnancyField.getText()).append("\n");
            }
            
            JTextField childrenField = (JTextField) childrenPanel.getComponent(1);
            if (!childrenField.getText().isEmpty()) {
                data.append("Children: ").append(childrenField.getText()).append("\n");
            }
            
            currentMedicalRecord.setGynecologicHistory(data.toString().trim());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(updateBtn);
        formPanel.add(buttonPanel);
        
        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }
    
    /**
     * Creates Personal/Social History panel
     */
    private JPanel createPersonalSocialHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Parse existing data
        String existingData = currentMedicalRecord.getPersonalSocialHistory();
        java.util.Map<String, String> dataMap = new java.util.HashMap<>();
        if (existingData != null && !existingData.isEmpty()) {
            String[] lines = existingData.split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    dataMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        
        // Cigarette Smoker
        JPanel smokerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        smokerPanel.setBackground(Color.WHITE);
        smokerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        smokerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JCheckBox smokerCheckbox = new JCheckBox("Cigarette Smoker:");
        smokerCheckbox.setBackground(Color.WHITE);
        smokerCheckbox.setSelected(dataMap.containsKey("Cigarette Smoker"));
        smokerPanel.add(smokerCheckbox);
        
        JTextField sticksField = new JTextField(10);
        sticksField.setText(dataMap.getOrDefault("Cigarette Smoker", ""));
        JLabel sticksLabel = new JLabel("sticks/day");
        smokerPanel.add(sticksField);
        smokerPanel.add(sticksLabel);
        
        formPanel.add(smokerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Alcoholic Beverage Drinker
        JPanel drinkerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        drinkerPanel.setBackground(Color.WHITE);
        drinkerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        drinkerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel drinkerLabel = new JLabel("Alcoholic Beverage Drinker:");
        drinkerLabel.setPreferredSize(new Dimension(200, 25));
        drinkerPanel.add(drinkerLabel);
        
        JComboBox<String> drinkerCombo = new JComboBox<>(new String[]{
            "", "None", "Occasional", "Regular", "Heavy"
        });
        String drinkerValue = dataMap.getOrDefault("Alcoholic Beverage Drinker", "");
        drinkerCombo.setSelectedItem(drinkerValue);
        drinkerCombo.setPreferredSize(new Dimension(200, 25));
        drinkerPanel.add(drinkerCombo);
        
        formPanel.add(drinkerPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Update button
        JButton updateBtn = new JButton("Update");
        updateBtn.setBackground(new Color(13, 37, 73));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.addActionListener(e -> {
            StringBuilder data = new StringBuilder();
            
            if (smokerCheckbox.isSelected() && !sticksField.getText().isEmpty()) {
                data.append("Cigarette Smoker: ").append(sticksField.getText()).append(" sticks/day\n");
            }
            
            if (!drinkerCombo.getSelectedItem().toString().isEmpty()) {
                data.append("Alcoholic Beverage Drinker: ").append(drinkerCombo.getSelectedItem()).append("\n");
            }
            
            currentMedicalRecord.setPersonalSocialHistory(data.toString().trim());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(updateBtn);
        formPanel.add(buttonPanel);
        
        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }
    
    /**
     * Creates Immunization panel with table
     */
    private JPanel createImmunizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Parse existing data
        String existingData = currentMedicalRecord.getImmunization();
        
        // Create table
        String[] columnNames = {"Immunizations", "Date"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        // Parse existing data and add to table
        if (existingData != null && !existingData.isEmpty()) {
            String[] lines = existingData.split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    model.addRow(new Object[]{parts[0].trim(), parts[1].trim()});
                }
            }
        }
        
        // Add some default immunizations if empty
        if (model.getRowCount() == 0) {
            String[] defaultImmunizations = {
                "BCG", "Hepatitis B", "DPT", "OPV/IPV", "Measles", 
                "MMR", "COVID-19", "Influenza", "Pneumococcal", "HPV"
            };
            for (String immunization : defaultImmunizations) {
                model.addRow(new Object[]{immunization, ""});
            }
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(300);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        
        // Add table change listener
        model.addTableModelListener(e -> {
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < model.getRowCount(); i++) {
                String immunization = (String) model.getValueAt(i, 0);
                String date = (String) model.getValueAt(i, 1);
                if (immunization != null && !immunization.trim().isEmpty() && 
                    date != null && !date.trim().isEmpty()) {
                    data.append(immunization).append(": ").append(date).append("\n");
                }
            }
            currentMedicalRecord.setImmunization(data.toString().trim());
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setPreferredSize(new Dimension(500, 300));
        contentPanel.add(scrollPane);
        
        // Add row button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addRowBtn = new JButton("+ Add Immunization");
        addRowBtn.addActionListener(e -> {
            model.addRow(new Object[]{"", ""});
        });
        buttonPanel.add(addRowBtn);
        
        // Select link
        JLabel selectLabel = new JLabel("<html><u>SELECT</u></html>");
        selectLabel.setForeground(new Color(0, 102, 204));
        selectLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        selectLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show additional immunizations dialog
                String[] additionalImmunizations = {
                    "Varicella", "Hepatitis A", "Meningococcal", "Rotavirus",
                    "Japanese Encephalitis", "Typhoid", "Yellow Fever", "Rabies"
                };
                String selected = (String) JOptionPane.showInputDialog(
                    panel, "Select an immunization:", "Additional Immunizations",
                    JOptionPane.PLAIN_MESSAGE, null, additionalImmunizations, additionalImmunizations[0]
                );
                if (selected != null) {
                    model.addRow(new Object[]{selected, ""});
                }
            }
        });
        buttonPanel.add(selectLabel);
        contentPanel.add(buttonPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates Others panel with medication fields
     */
    private JPanel createOthersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Parse existing data
        String existingData = currentMedicalRecord.getOthers();
        java.util.Map<String, String> dataMap = new java.util.HashMap<>();
        if (existingData != null && !existingData.isEmpty()) {
            String[] lines = existingData.split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    dataMap.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        
        // Any special medication?
        JPanel medicationPanel = new JPanel(new BorderLayout());
        medicationPanel.setBackground(Color.WHITE);
        medicationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        medicationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel medicationLabel = new JLabel("Any special medication?");
        medicationLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        medicationPanel.add(medicationLabel, BorderLayout.NORTH);
        
        JTextArea medicationArea = new JTextArea(3, 40);
        medicationArea.setText(dataMap.getOrDefault("Special Medication", ""));
        medicationArea.setLineWrap(true);
        medicationArea.setWrapStyleWord(true);
        JScrollPane medicationScroll = new JScrollPane(medicationArea);
        medicationScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        medicationPanel.add(medicationScroll, BorderLayout.CENTER);
        
        formPanel.add(medicationPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Requires Special Care?
        JPanel specialCarePanel = new JPanel(new BorderLayout());
        specialCarePanel.setBackground(Color.WHITE);
        specialCarePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        specialCarePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel specialCareLabel = new JLabel("Requires Special Care?");
        specialCareLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        specialCarePanel.add(specialCareLabel, BorderLayout.NORTH);
        
        JTextArea specialCareArea = new JTextArea(3, 40);
        specialCareArea.setText(dataMap.getOrDefault("Special Care", ""));
        specialCareArea.setLineWrap(true);
        specialCareArea.setWrapStyleWord(true);
        JScrollPane specialCareScroll = new JScrollPane(specialCareArea);
        specialCareScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        specialCarePanel.add(specialCareScroll, BorderLayout.CENTER);
        
        formPanel.add(specialCarePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Others field
        JPanel othersFieldPanel = new JPanel(new BorderLayout());
        othersFieldPanel.setBackground(Color.WHITE);
        othersFieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        othersFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel othersLabel = new JLabel("Others:");
        othersLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        othersFieldPanel.add(othersLabel, BorderLayout.NORTH);
        
        JTextArea othersArea = new JTextArea(3, 40);
        othersArea.setText(dataMap.getOrDefault("Others", ""));
        othersArea.setLineWrap(true);
        othersArea.setWrapStyleWord(true);
        JScrollPane othersScroll = new JScrollPane(othersArea);
        othersScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        othersFieldPanel.add(othersScroll, BorderLayout.CENTER);
        
        formPanel.add(othersFieldPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Update button
        JButton updateBtn = new JButton("Update");
        updateBtn.setBackground(new Color(13, 37, 73));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.addActionListener(e -> {
            StringBuilder data = new StringBuilder();
            
            if (!medicationArea.getText().trim().isEmpty()) {
                data.append("Special Medication: ").append(medicationArea.getText().trim()).append("\n");
            }
            
            if (!specialCareArea.getText().trim().isEmpty()) {
                data.append("Special Care: ").append(specialCareArea.getText().trim()).append("\n");
            }
            
            if (!othersArea.getText().trim().isEmpty()) {
                data.append("Others: ").append(othersArea.getText().trim()).append("\n");
            }
            
            currentMedicalRecord.setOthers(data.toString().trim());
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(updateBtn);
        formPanel.add(buttonPanel);
        
        panel.add(formPanel, BorderLayout.NORTH);
        return panel;
    }
    
    /**
     * Creates Consultation Record panel with table
     */
    private JPanel createConsultationRecordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Parse existing data
        String existingData = currentMedicalRecord.getConsultationRecord();
        
        // Create table with all columns from the image
        String[] columnNames = {"Date/Time", "Age", "BMI", "BP", "TEMP", "RR", "PR", "SPO2", "Remarks"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        
        // Parse existing data and add to table
        if (existingData != null && !existingData.isEmpty()) {
            String[] lines = existingData.split("\n");
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 9) {
                    model.addRow(parts);
                }
            }
        }
        
        // Add empty rows if table is empty
        if (model.getRowCount() == 0) {
            for (int i = 0; i < 3; i++) {
                model.addRow(new Object[]{"", "", "", "", "", "", "", "", ""});
            }
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Date/Time
        table.getColumnModel().getColumn(1).setPreferredWidth(50);  // Age
        table.getColumnModel().getColumn(2).setPreferredWidth(50);  // BMI
        table.getColumnModel().getColumn(3).setPreferredWidth(60);  // BP
        table.getColumnModel().getColumn(4).setPreferredWidth(50);  // TEMP
        table.getColumnModel().getColumn(5).setPreferredWidth(50);  // RR
        table.getColumnModel().getColumn(6).setPreferredWidth(50);  // PR
        table.getColumnModel().getColumn(7).setPreferredWidth(50);  // SPO2
        table.getColumnModel().getColumn(8).setPreferredWidth(150); // Remarks
        
        // Add table change listener
        model.addTableModelListener(e -> {
            StringBuilder data = new StringBuilder();
            for (int i = 0; i < model.getRowCount(); i++) {
                boolean hasData = false;
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    if (value != null && !value.toString().trim().isEmpty()) {
                        hasData = true;
                    }
                    row.append(value != null ? value.toString() : "");
                    if (j < model.getColumnCount() - 1) {
                        row.append("|");
                    }
                }
                if (hasData) {
                    data.append(row).append("\n");
                }
            }
            currentMedicalRecord.setConsultationRecord(data.toString().trim());
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setPreferredSize(new Dimension(700, 300));
        contentPanel.add(scrollPane);
        
        // Add row button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addRowBtn = new JButton("+ Add Record");
        addRowBtn.addActionListener(e -> {
            // Add new row with current date/time
            String currentDateTime = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date());
            model.addRow(new Object[]{currentDateTime, "", "", "", "", "", "", "", ""});
        });
        buttonPanel.add(addRowBtn);
        contentPanel.add(buttonPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Helper method to create labeled field panel
     */
    private JPanel createLabeledFieldPanel(String labelText, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(200, 25));
        panel.add(label);
        
        JTextField field = new JTextField(20);
        field.setText(value);
        panel.add(field);
        
        return panel;
    }
    
    /**
     * Shows personal information and emergency contact in the main content area
     */
    private void showMedicalPersonalInfo() {
        // Save any pending changes before switching
        if (currentMedicalContentView != null) {
            DataManager.saveMedicalRecord(currentMedicalRecord);
        }
        
        medicalContentPanel.removeAll();
        
        // Create scrollable content panel
        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));
        scrollContent.setBackground(Color.WHITE);
        scrollContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Personal Information Section
        JPanel personalInfoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        personalInfoTitlePanel.setBackground(new Color(245, 245, 245));
        personalInfoTitlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        personalInfoTitlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel personalInfoTitle = new JLabel("PERSONAL INFORMATION");
        personalInfoTitle.setFont(new Font("Arial", Font.BOLD, 15));
        personalInfoTitle.setForeground(new Color(13, 37, 73));
        personalInfoTitlePanel.add(personalInfoTitle);
        scrollContent.add(personalInfoTitlePanel);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel personalInfoPanel = createPersonalInfoPanel();
        scrollContent.add(personalInfoPanel);
        
        // Add separator
        scrollContent.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        scrollContent.add(separator);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Emergency Contact Section
        JPanel emergencyTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        emergencyTitlePanel.setBackground(new Color(245, 245, 245));
        emergencyTitlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        emergencyTitlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel emergencyTitle = new JLabel("PERSON TO CONTACT IN CASE OF EMERGENCY");
        emergencyTitle.setFont(new Font("Arial", Font.BOLD, 15));
        emergencyTitle.setForeground(new Color(13, 37, 73));
        emergencyTitlePanel.add(emergencyTitle);
        scrollContent.add(emergencyTitlePanel);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel emergencyContactPanel = createEmergencyContactPanel();
        scrollContent.add(emergencyContactPanel);
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        currentMedicalContentView = scrollContent; // Store current view
        medicalContentPanel.add(scrollPane, BorderLayout.CENTER);
        medicalContentPanel.revalidate();
        medicalContentPanel.repaint();
    }
    
    /**
     * Creates the Personal Information panel
     */
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
        
        // Get student data from database
        StudentInfo studentInfo = DataManager.getStudentInfo(studentID);
        String profileString = DataManager.getStudentProfile(studentID);
        ProfileData profileData = parseProfileData(profileString);
        
        // Name field
        String fullName = studentInfo != null ? 
            studentInfo.getFirstName() + " " + studentInfo.getLastName() : studentName;
        panel.add(createMedicalFieldPanel("Name:", fullName.toUpperCase(), false));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Date of Birth field
        String dob = studentInfo != null ? studentInfo.getDateOfBirth() : "APR 01, 2006";
        panel.add(createMedicalFieldPanel("Date of Birth:", dob, false));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Gender field
        String gender = profileData != null ? profileData.getGender() : "Female";
        panel.add(createMedicalFieldPanel("Gender:", gender, false));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Address field
        String address = profileData != null ? profileData.getBaguioAddress() : 
            "Camp 6, Camp 4, Tuba Benguet";
        panel.add(createMedicalFieldPanel("Address:", address, false));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Contact Number field
        String contact = profileData != null ? profileData.getCellphone() : "09665295444";
        panel.add(createMedicalFieldPanel("Contact Number:", contact, false));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));
        
        // Course/Year field
        panel.add(createMedicalFieldPanel("Course/Year:", "BSIT 2", false));
        
        return panel;
    }
    
    /**
     * Creates a field panel with label and text field
     */
    private JPanel createMedicalFieldPanel(String labelText, String value, boolean editable) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Increased height
        fieldPanel.setPreferredSize(new Dimension(600, 40)); // Fixed preferred size
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = createMedicalLabel(labelText);
        label.setPreferredSize(new Dimension(180, 30)); // Wider label
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldPanel.add(label, BorderLayout.WEST);
        
        JTextField field = createMedicalTextField(value, editable);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldPanel.add(field, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    /**
     * Creates the Emergency Contact panel
     */
    private JPanel createEmergencyContactPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
        
        // Get data from medical record or profile data as fallback
        String profileString = DataManager.getStudentProfile(studentID);
        ProfileData profileData = parseProfileData(profileString);
        
        // Name field - use from medical record first, then fallback to profile data
        String emergencyName = currentMedicalRecord.getEmergencyContactName();
        if (emergencyName == null || emergencyName.isEmpty()) {
            emergencyName = profileData != null && profileData.getGuardianName() != null ? 
                profileData.getGuardianName() : "";
            // Update medical record with profile data
            currentMedicalRecord.setEmergencyContactName(emergencyName);
        }
        JTextField nameField = createEditableMedicalField("Name:", emergencyName);
        nameField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateName(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateName(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateName(); }
            private void updateName() {
                currentMedicalRecord.setEmergencyContactName(nameField.getText());
            }
        });
        panel.add((JPanel)nameField.getClientProperty("parentPanel"));
        panel.add(Box.createRigidArea(new Dimension(0, 12))); // Consistent spacing
        
        // Address field - use from medical record first, then fallback to profile data
        String emergencyAddress = currentMedicalRecord.getEmergencyContactAddress();
        if (emergencyAddress == null || emergencyAddress.isEmpty()) {
            emergencyAddress = profileData != null && profileData.getGuardianAddress() != null ? 
                profileData.getGuardianAddress() : "";
            currentMedicalRecord.setEmergencyContactAddress(emergencyAddress);
        }
        JTextField addressField = createEditableMedicalField("Address:", emergencyAddress);
        addressField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateAddress(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateAddress(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateAddress(); }
            private void updateAddress() {
                currentMedicalRecord.setEmergencyContactAddress(addressField.getText());
            }
        });
        panel.add((JPanel)addressField.getClientProperty("parentPanel"));
        panel.add(Box.createRigidArea(new Dimension(0, 12))); // Consistent spacing
        
        // Contact Number field
        String emergencyNumber = currentMedicalRecord.getEmergencyContactNumber();
        if (emergencyNumber == null || emergencyNumber.isEmpty()) {
            emergencyNumber = "";
            currentMedicalRecord.setEmergencyContactNumber(emergencyNumber);
        }
        JTextField numberField = createEditableMedicalField("Contact Number:", emergencyNumber);
        numberField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateNumber(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateNumber(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateNumber(); }
            private void updateNumber() {
                currentMedicalRecord.setEmergencyContactNumber(numberField.getText());
            }
        });
        panel.add((JPanel)numberField.getClientProperty("parentPanel"));
        
        return panel;
    }
    
    /**
     * Creates an editable medical field that returns the text field for further processing
     */
    private JTextField createEditableMedicalField(String labelText, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Consistent height
        fieldPanel.setPreferredSize(new Dimension(600, 40));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = createMedicalLabel(labelText);
        label.setPreferredSize(new Dimension(180, 30)); // Consistent width
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldPanel.add(label, BorderLayout.WEST);
        
        JTextField field = createMedicalTextField(value, true);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        fieldPanel.add(field, BorderLayout.CENTER);
        
        // Store the parent panel in the text field's client property for retrieval
        field.putClientProperty("parentPanel", fieldPanel);
        
        return field;
    }
    
    /**
     * Creates a label for medical record fields
     */
    private JLabel createMedicalLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(new Color(50, 50, 50));
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }
    
    /**
     * Creates a text field for medical record data
     */
    private JTextField createMedicalTextField(String text, boolean editable) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setEditable(editable);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10) // More padding
        ));
        field.setPreferredSize(new Dimension(400, 35)); // Consistent size
        if (!editable) {
            field.setBackground(new Color(248, 248, 248));
            field.setForeground(new Color(80, 80, 80));
        }
        return field;
    }
}

/**
 * Data class to hold parsed profile information
 */
class ProfileData {
    String gender = "";
    String citizenship = "";
    String religion = "";
    String civilStatus = "";
    String birthplace = "";
    String nationality = "";
    String homeAddress = "";
    String homeTel = "";
    String baguioAddress = "";
    String baguioTel = "";
    String cellphone = "";
    String fatherName = "";
    String fatherOcc = "";
    String motherName = "";
    String motherOcc = "";
    String guardianName = "";
    String guardianAddress = "";
    
    // Getter methods with proper null/empty handling
    public String getGender() { return (gender != null && !gender.trim().isEmpty()) ? gender : "N/A"; }
    public String getCitizenship() { return (citizenship != null && !citizenship.trim().isEmpty()) ? citizenship : "N/A"; }
    public String getReligion() { return (religion != null && !religion.trim().isEmpty()) ? religion : "N/A"; }
    public String getCivilStatus() { return (civilStatus != null && !civilStatus.trim().isEmpty()) ? civilStatus : "N/A"; }
    public String getBirthplace() { return (birthplace != null && !birthplace.trim().isEmpty()) ? birthplace : "N/A"; }
    public String getNationality() { return (nationality != null && !nationality.trim().isEmpty()) ? nationality : "N/A"; }
    public String getHomeAddress() { return (homeAddress != null && !homeAddress.trim().isEmpty()) ? homeAddress : "N/A"; }
    public String getHomeTel() { return (homeTel != null && !homeTel.trim().isEmpty()) ? homeTel : "N/A"; }
    public String getBaguioAddress() { return (baguioAddress != null && !baguioAddress.trim().isEmpty()) ? baguioAddress : "N/A"; }
    public String getBaguioTel() { return (baguioTel != null && !baguioTel.trim().isEmpty()) ? baguioTel : "N/A"; }
    public String getCellphone() { return (cellphone != null && !cellphone.trim().isEmpty()) ? cellphone : "N/A"; }
    public String getFatherName() { return (fatherName != null && !fatherName.trim().isEmpty()) ? fatherName : "N/A"; }
    public String getFatherOcc() { return (fatherOcc != null && !fatherOcc.trim().isEmpty()) ? fatherOcc : "N/A"; }
    public String getMotherName() { return (motherName != null && !motherName.trim().isEmpty()) ? motherName : "N/A"; }
    public String getMotherOcc() { return (motherOcc != null && !motherOcc.trim().isEmpty()) ? motherOcc : "N/A"; }
    public String getGuardianName() { return (guardianName != null && !guardianName.trim().isEmpty()) ? guardianName : "N/A"; }
    public String getGuardianAddress() { return (guardianAddress != null && !guardianAddress.trim().isEmpty()) ? guardianAddress : "N/A"; }
}