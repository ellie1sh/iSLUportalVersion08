import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Statement of Accounts Panel - Java implementation of the HTML/CSS structure
 * Displays student account information, fees breakdown, and payment options
 */
public class StatementOfAccountsPanel extends JPanel {
    private AccountStatement accountStatement;
    private String studentID;
    private String studentName;
    private String studentProgram;
    
    // Main components
    private JPanel mainContentPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JLabel studentInfoLabel;
    private JLabel amountDueLabel;
    private JLabel balanceLabel;
    private JLabel statusLabel;
    private JTable feeBreakdownTable;
    private DefaultTableModel feeTableModel;
    
    // Payment dialog components
    private JDialog paymentDialog;
    private JTextField amountField;
    private JComboBox<String> paymentMethodCombo;
    private JLabel totalAmountLabel;
    private JLabel serviceChargeLabel;
    
    // Colors matching the HTML design
    private static final Color HEADER_BLUE = new Color(14, 40, 79); // #0e284f
    private static final Color DARK_RED = new Color(144, 24, 24); // #901818
    private static final Color LIGHT_GRAY = new Color(248, 248, 248);
    private static final Color WHITE = Color.WHITE;
    private static final Color GREEN = new Color(0, 128, 0);
    
    public StatementOfAccountsPanel(String studentID) {
        this.studentID = studentID;
        this.accountStatement = AccountStatementManager.getStatement(studentID);
        this.studentName = getStudentNameFromDatabase(studentID);
        this.studentProgram = getStudentProgramFromDatabase(studentID);
        
        initializeComponents();
        setupLayout();
        loadAccountData();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(LIGHT_GRAY);
        
        // Main content panel with two columns
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(LIGHT_GRAY);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Left panel (70% width) - Account details
        leftPanel = createLeftPanel();
        
        // Right panel (30% width) - Payment channels
        rightPanel = createRightPanel();
        
        // Add panels to main content
        mainContentPanel.add(leftPanel, BorderLayout.CENTER);
        mainContentPanel.add(rightPanel, BorderLayout.EAST);
        
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JPanel headerPanel = createHeaderPanel("Statement of Accounts (FIRST SEMESTER, 2025-2026)", "📊");
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Student Information
        JPanel studentInfoPanel = createStudentInfoPanel();
        panel.add(studentInfoPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Amount Due Section
        JPanel amountDuePanel = createAmountDuePanel();
        panel.add(amountDuePanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Balance Section
        JPanel balancePanel = createBalancePanel();
        panel.add(balancePanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Status Section
        JPanel statusPanel = createStatusPanel();
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Fee Breakdown Table
        JPanel feeBreakdownPanel = createFeeBreakdownPanel();
        panel.add(feeBreakdownPanel);
        
        return panel;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(300, 0));
        
        // Header
        JPanel headerPanel = createHeaderPanel("Online Payment Channels", "🛒");
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Instruction text
        JLabel instructionLabel = new JLabel("<html><div style='text-align: center; font-size: 16px; color: #0e284f; font-weight: bold;'>" +
            "Tuition fees can be paid via the available online payment channels.</div></html>");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(instructionLabel);
        panel.add(Box.createVerticalStrut(20));
        
        // Separator line
        JSeparator separator = new JSeparator();
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(separator);
        panel.add(Box.createVerticalStrut(20));
        
        // Payment channel buttons
        panel.add(createPaymentChannelButton("UnionBank UPay Online", "upay.png", "UPay"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createPaymentChannelButton("Dragonpay Payment Gateway", "dragonpay.png", "Dragonpay"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createPaymentChannelButton("BPI Online", "bpi.png", "BPI"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createPaymentChannelButton("BDO Online", "bdo.png", "BDO"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createPaymentChannelButton("BDO Bills Payment", "bdobills.png", "BDOBills"));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createPaymentChannelButton("Bukas Tuition Installment Plans", "bukas.png", "Bukas"));
        
        return panel;
    }
    
    private JPanel createHeaderPanel(String title, String icon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createStudentInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LIGHT_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Student icon (using emoji as placeholder)
        JLabel iconLabel = new JLabel("👤");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(50, 50));
        
        // Student info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(LIGHT_GRAY);
        
        studentInfoLabel = new JLabel(studentID + " | " + studentProgram);
        studentInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        studentInfoLabel.setForeground(HEADER_BLUE);
        
        JLabel nameLabel = new JLabel(studentName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(HEADER_BLUE);
        
        infoPanel.add(studentInfoLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(nameLabel);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAmountDuePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        
        JLabel titleLabel = new JLabel("Your amount due for PRELIM is:");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        amountDueLabel = new JLabel();
        amountDueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        amountDueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(amountDueLabel);
        
        return panel;
    }
    
    private JPanel createBalancePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        
        JLabel titleLabel = new JLabel("Your remaining balance as of " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + " is:");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 36));
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(balanceLabel);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        
        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(statusLabel);
        
        return panel;
    }
    
    private JPanel createFeeBreakdownPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        
        // Header
        JPanel headerPanel = createHeaderPanel("Breakdown of fees as of " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), "📋");
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columnNames = {"Date", "Description", "Amount"};
        feeTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        feeBreakdownTable = new JTable(feeTableModel);
        feeBreakdownTable.setRowHeight(25);
        feeBreakdownTable.setFont(new Font("Arial", Font.PLAIN, 12));
        feeBreakdownTable.setGridColor(Color.LIGHT_GRAY);
        feeBreakdownTable.setShowGrid(true);
        
        // Custom renderer for amount column
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2) { // Amount column
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    if (value != null && value.toString().contains("(")) {
                        setForeground(DARK_RED);
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };
        
        feeBreakdownTable.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);
        
        JScrollPane scrollPane = new JScrollPane(feeBreakdownTable);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createPaymentChannelButton(String text, String iconFile, String channel) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(250, 50));
        button.setMaximumSize(new Dimension(250, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Create button content with icon and text
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Icon (using emoji as placeholder for actual images)
        String icon = getIconForChannel(channel);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.BOLD, 10));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        contentPanel.add(iconLabel, BorderLayout.CENTER);
        contentPanel.add(textLabel, BorderLayout.SOUTH);
        
        button.add(contentPanel);
        
        // Set background color based on channel
        button.setBackground(getColorForChannel(channel));
        button.setForeground(WHITE);
        
        // Add click handler
        button.addActionListener(e -> handlePaymentChannelClick(channel));
        
        return button;
    }
    
    private String getIconForChannel(String channel) {
        switch (channel) {
            case "UPay": return "🏦";
            case "Dragonpay": return "🐉";
            case "BPI": return "🏛️";
            case "BDO": return "🏢";
            case "BDOBills": return "💳";
            case "Bukas": return "🏠";
            default: return "💳";
        }
    }
    
    private Color getColorForChannel(String channel) {
        switch (channel) {
            case "UPay": return new Color(255, 140, 0); // Orange
            case "Dragonpay": return new Color(220, 20, 60); // Red
            case "BPI": return new Color(139, 0, 0); // Dark red
            case "BDO": return new Color(0, 100, 200); // Blue
            case "BDOBills": return new Color(0, 100, 200); // Blue
            case "Bukas": return new Color(135, 206, 235); // Light blue
            default: return Color.GRAY;
        }
    }
    
    private void setupLayout() {
        // Layout is already set up in initializeComponents
    }
    
    private void loadAccountData() {
        // Load student information
        studentInfoLabel.setText(studentID + " | " + studentProgram);
        
        // Calculate and display amount due for PRELIM
        double prelimDue = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
        amountDueLabel.setText("P " + formatAmount(prelimDue));
        amountDueLabel.setForeground(prelimDue > 0 ? DARK_RED : GREEN);
        
        // Display remaining balance
        double balance = accountStatement.getBalance();
        balanceLabel.setText("P " + formatAmount(balance));
        balanceLabel.setForeground(balance > 0 ? DARK_RED : GREEN);
        
        // Display status
        boolean isPrelimPaid = accountStatement.isPrelimPaid();
        if (isPrelimPaid) {
            statusLabel.setText("PRELIM STATUS: PAID. Permitted to take the exams.");
            statusLabel.setForeground(GREEN);
        } else {
            statusLabel.setText("PRELIM STATUS: NOT PAID. Please pay before prelim exams. Ignore if you're SLU Dependent or Full TOF Scholar.");
            statusLabel.setForeground(DARK_RED);
        }
        
        // Load fee breakdown
        loadFeeBreakdown();
    }
    
    private void loadFeeBreakdown() {
        feeTableModel.setRowCount(0);
        
        // Add beginning balance
        feeTableModel.addRow(new Object[]{"", "BEGINNING BALANCE", "0.00"});
        
        // Add payment history
        List<PaymentTransaction> payments = accountStatement.getPaymentHistory();
        for (PaymentTransaction payment : payments) {
            String date = payment.getDate().split(" ")[0]; // Get date part only
            String description = "PAYMENT RECEIVED (" + payment.getReference() + ")";
            String amount = "(" + formatAmount(parseAmount(payment.getAmount())) + ")";
            feeTableModel.addRow(new Object[]{date, description, amount});
        }
        
        // Add fee breakdowns
        List<FeeBreakdown> fees = accountStatement.getFeeBreakdowns();
        for (FeeBreakdown fee : fees) {
            String date = fee.getDatePosted().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String description = fee.getDescription();
            String amount = formatAmount(fee.getAmount());
            feeTableModel.addRow(new Object[]{date, description, amount});
        }
    }
    
    private void setupEventHandlers() {
        // Event handlers are set up in createPaymentChannelButton
    }
    
    private void handlePaymentChannelClick(String channel) {
        if ("Bukas".equals(channel)) {
            // Open external link for Bukas
            openBukasWebsite();
        } else {
            // Open payment dialog for other channels
            openPaymentDialog(channel);
        }
    }
    
    private void openPaymentDialog(String channel) {
        paymentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Payment through " + getChannelDisplayName(channel), true);
        paymentDialog.setSize(500, 400);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Amount input section
        JPanel amountPanel = new JPanel();
        amountPanel.setLayout(new BoxLayout(amountPanel, BoxLayout.Y_AXIS));
        
        JLabel amountLabel = new JLabel("AMOUNT TO PAY");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        amountField = new JTextField("0");
        amountField.setFont(new Font("Arial", Font.PLAIN, 24));
        amountField.setHorizontalAlignment(SwingConstants.CENTER);
        amountField.setPreferredSize(new Dimension(300, 50));
        amountField.setMaximumSize(new Dimension(300, 50));
        
        // Payment method selection (for Dragonpay)
        if ("Dragonpay".equals(channel)) {
            JLabel methodLabel = new JLabel("Payment Method:");
            methodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            String[] methods = {"GCash", "PayMaya", "BPI Online", "BDO Online", "UnionBank Online"};
            paymentMethodCombo = new JComboBox<>(methods);
            paymentMethodCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
            paymentMethodCombo.setPreferredSize(new Dimension(200, 30));
            
            amountPanel.add(methodLabel);
            amountPanel.add(Box.createVerticalStrut(10));
            amountPanel.add(paymentMethodCombo);
            amountPanel.add(Box.createVerticalStrut(10));
        }
        
        amountPanel.add(amountLabel);
        amountPanel.add(Box.createVerticalStrut(20));
        amountPanel.add(amountField);
        
        // Service charge info
        JPanel chargePanel = new JPanel();
        chargePanel.setLayout(new BoxLayout(chargePanel, BoxLayout.Y_AXIS));
        
        serviceChargeLabel = new JLabel();
        serviceChargeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        serviceChargeLabel.setForeground(DARK_RED);
        
        totalAmountLabel = new JLabel();
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalAmountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        chargePanel.add(serviceChargeLabel);
        chargePanel.add(Box.createVerticalStrut(10));
        chargePanel.add(totalAmountLabel);
        
        // Proceed button
        JButton proceedButton = new JButton("Proceed");
        proceedButton.setPreferredSize(new Dimension(100, 35));
        proceedButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        proceedButton.addActionListener(e -> processPayment(channel));
        
        // Layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(amountPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(chargePanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(proceedButton);
        
        dialogPanel.add(centerPanel, BorderLayout.CENTER);
        paymentDialog.add(dialogPanel);
        
        // Add amount change listener
        amountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotalAmount(channel); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotalAmount(channel); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotalAmount(channel); }
        });
        
        if (paymentMethodCombo != null) {
            paymentMethodCombo.addActionListener(e -> updateTotalAmount(channel));
        }
        
        // Initial calculation
        updateTotalAmount(channel);
        
        paymentDialog.setVisible(true);
    }
    
    private void updateTotalAmount(String channel) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            double serviceCharge = 0;
            double additionalFee = 0;
            
            if ("Dragonpay".equals(channel)) {
                serviceCharge = 25.00; // Dragonpay service charge
                // Additional fee based on payment method
                if (paymentMethodCombo != null) {
                    String method = (String) paymentMethodCombo.getSelectedItem();
                    switch (method) {
                        case "GCash":
                        case "PayMaya":
                            additionalFee = amount * 0.02; // 2% fee
                            break;
                        case "BPI Online":
                        case "BDO Online":
                        case "UnionBank Online":
                            additionalFee = amount * 0.015; // 1.5% fee
                            break;
                    }
                }
            }
            
            double total = amount + serviceCharge + additionalFee;
            
            serviceChargeLabel.setText("<html>There will be a <span style='color: red;'>twenty five pesos (P 25.00)</span> " +
                "service charge for using dragon pay. An additional fee will be charged depending on the payment channel.</html>");
            
            totalAmountLabel.setText("AMOUNT TO PAY + CHARGES: " + formatAmount(total));
            
        } catch (NumberFormatException e) {
            serviceChargeLabel.setText("");
            totalAmountLabel.setText("AMOUNT TO PAY + CHARGES: P 0.00");
        }
    }
    
    private void processPayment(String channel) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String reference = generatePaymentReference();
            String channelName = getChannelDisplayName(channel);
            
            // Process payment through AccountStatementManager
            AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                studentID, amount, channelName, reference);
            
            if (result.success) {
                JOptionPane.showMessageDialog(this, result.message, "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                paymentDialog.dispose();
                
                // Refresh the display
                loadAccountData();
            } else {
                JOptionPane.showMessageDialog(this, result.message, "Payment Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openBukasWebsite() {
        try {
            Desktop.getDesktop().browse(new java.net.URI("https://bukas.ph/s/slu"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to open Bukas website. Please visit: https://bukas.ph/s/slu", 
                "External Link", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private String getChannelDisplayName(String channel) {
        switch (channel) {
            case "UPay": return "UPay by UnionBank";
            case "Dragonpay": return "Dragon Pay";
            case "BPI": return "BPI";
            case "BDO": return "BDO Online";
            case "BDOBills": return "BDO Bills Payment";
            case "Bukas": return "Bukas";
            default: return channel;
        }
    }
    
    private String generatePaymentReference() {
        return String.format("%08d", (int)(Math.random() * 100000000));
    }
    
    private String formatAmount(double amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }
    
    private double parseAmount(String amountStr) {
        return Double.parseDouble(amountStr.replace("P ", "").replace(",", ""));
    }
    
    private String getStudentNameFromDatabase(String studentID) {
        // This would typically query the database
        // For now, return a placeholder
        return "Sherlie O. Rivera";
    }
    
    private String getStudentProgramFromDatabase(String studentID) {
        // This would typically query the database
        // For now, return a placeholder
        return "BSIT 2";
    }
    
    /**
     * Refreshes the account data display
     */
    public void refreshData() {
        loadAccountData();
    }
}