import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Statement of Accounts Panel for iSLU Student Portal
 * Displays student financial information, payment status, and online payment options
 * Based on the HTML structure and visual design from the provided images
 */
public class StatementOfAccountsPanel extends JPanel {
    private AccountStatement accountStatement;
    private String studentID;
    private String studentName;
    private String program;
    
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
    private JPanel paymentChannelsPanel;
    
    // Payment modal components
    private JDialog paymentDialog;
    private JTextField amountField;
    private JComboBox<String> paymentMethodCombo;
    private JLabel totalAmountLabel;
    private JLabel serviceChargeLabel;
    
    // Colors matching the HTML design
    private static final Color HEADER_BLUE = new Color(14, 40, 79); // #0e284f
    private static final Color DARK_BLUE = new Color(0, 51, 102);
    private static final Color LIGHT_GRAY = new Color(248, 248, 248);
    private static final Color WHITE = Color.WHITE;
    private static final Color RED_AMOUNT = new Color(144, 24, 24); // #901818
    private static final Color GREEN_STATUS = new Color(0, 128, 0);
    private static final Color RED_STATUS = new Color(220, 20, 60);
    
    public StatementOfAccountsPanel(String studentID) {
        this.studentID = studentID;
        this.accountStatement = AccountStatementManager.getStatement(studentID);
        this.studentName = getStudentNameFromDatabase(studentID);
        this.program = getStudentProgramFromDatabase(studentID);
        
        initializeComponents();
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
        
        // Left panel (70% width) - Statement details
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
        
        // Student information panel
        JPanel studentInfoPanel = createStudentInfoPanel();
        panel.add(studentInfoPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Amount due section
        JPanel amountDuePanel = createAmountDuePanel();
        panel.add(amountDuePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Balance section
        JPanel balancePanel = createBalancePanel();
        panel.add(balancePanel);
        panel.add(Box.createVerticalStrut(15));
        
        // Status section
        JPanel statusPanel = createStatusPanel();
        panel.add(statusPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Fee breakdown table
        JPanel tablePanel = createFeeBreakdownPanel();
        panel.add(tablePanel);
        
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
        
        // Instructional text
        JLabel instructionLabel = new JLabel("<html><div style='text-align: center; font-size: 16px; color: #0e284f; font-weight: bold;'>" +
            "Tuition fees can be paid via the available online payment channels.</div></html>");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(instructionLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // Separator line
        JSeparator separator = new JSeparator();
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(separator);
        panel.add(Box.createVerticalStrut(20));
        
        // Payment channels
        paymentChannelsPanel = createPaymentChannelsPanel();
        panel.add(paymentChannelsPanel);
        
        return panel;
    }
    
    private JPanel createHeaderPanel(String title, String icon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HEADER_BLUE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel(icon + " " + title);
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
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
        
        // Student details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(LIGHT_GRAY);
        
        studentInfoLabel = new JLabel(studentID + " | " + program);
        studentInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        studentInfoLabel.setForeground(HEADER_BLUE);
        
        JLabel nameLabel = new JLabel(studentName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(HEADER_BLUE);
        
        detailsPanel.add(studentInfoLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(nameLabel);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAmountDuePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        
        // Amount due label
        JLabel dueLabel = new JLabel("Your amount due for <strong>PRELIM</strong> is:");
        dueLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        dueLabel.setText("<html>" + dueLabel.getText().replace("<strong>", "<b>").replace("</strong>", "</b>") + "</html>");
        
        // Amount display
        amountDueLabel = new JLabel("P 0.00");
        amountDueLabel.setFont(new Font("Arial", Font.BOLD, 50));
        amountDueLabel.setForeground(RED_AMOUNT);
        amountDueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(dueLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(amountDueLabel);
        
        return panel;
    }
    
    private JPanel createBalancePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        
        // Balance label
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        JLabel balanceTextLabel = new JLabel("Your remaining balance as of <strong>" + currentDate + "</strong> is:");
        balanceTextLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        balanceTextLabel.setText("<html>" + balanceTextLabel.getText().replace("<strong>", "<b>").replace("</strong>", "</b>") + "</html>");
        
        // Balance display
        balanceLabel = new JLabel("P 0.00");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 50));
        balanceLabel.setForeground(RED_AMOUNT);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(balanceTextLabel);
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
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                    if (value != null && value.toString().contains("(")) {
                        c.setForeground(GREEN_STATUS); // Payment received
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };
        
        feeBreakdownTable.getColumnModel().getColumn(0).setCellRenderer(amountRenderer);
        feeBreakdownTable.getColumnModel().getColumn(1).setCellRenderer(amountRenderer);
        feeBreakdownTable.getColumnModel().getColumn(2).setCellRenderer(amountRenderer);
        
        JScrollPane scrollPane = new JScrollPane(feeBreakdownTable);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPaymentChannelsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(WHITE);
        
        // Payment channel buttons
        String[] paymentChannels = {
            "UB UnionBank UPay Online",
            "@dragonpay Payment Gateway", 
            "BPI BPI Online",
            "BDO BDO Online",
            "BDO Bills Payment",
            "Bukas Tuition Installment Plans"
        };
        
        String[] channelColors = {
            "#FF6B35", // Orange for UnionBank
            "#DC143C", // Red for Dragonpay
            "#8B0000", // Dark red for BPI
            "#0066CC", // Blue for BDO
            "#0066CC", // Blue for BDO Bills
            "#4A90E2"  // Light blue for Bukas
        };
        
        for (int i = 0; i < paymentChannels.length; i++) {
            JButton channelButton = createPaymentChannelButton(paymentChannels[i], channelColors[i]);
            channelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(channelButton);
            panel.add(Box.createVerticalStrut(10));
        }
        
        return panel;
    }
    
    private JButton createPaymentChannelButton(String text, String color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 40));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(Color.decode(color));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(button.getBackground().darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.decode(color));
            }
        });
        
        // Add click handler
        button.addActionListener(e -> openPaymentDialog(text));
        
        return button;
    }
    
    private void openPaymentDialog(String paymentChannel) {
        if (paymentDialog == null) {
            createPaymentDialog();
        }
        
        // Update dialog title
        paymentDialog.setTitle("Payment through " + paymentChannel);
        
        // Set default amount to prelim due
        double prelimDue = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
        amountField.setText(String.format("%.2f", prelimDue));
        
        // Update total amount
        updateTotalAmount();
        
        // Center dialog
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setVisible(true);
    }
    
    private void createPaymentDialog() {
        paymentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Payment", true);
        paymentDialog.setSize(500, 400);
        paymentDialog.setLayout(new BorderLayout());
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Payment Details");
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(titleLabel);
        
        paymentDialog.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Amount to pay section
        JPanel amountPanel = new JPanel(new BorderLayout());
        JLabel amountLabel = new JLabel("AMOUNT TO PAY");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountPanel.add(amountLabel, BorderLayout.NORTH);
        
        amountField = new JTextField();
        amountField.setFont(new Font("Arial", Font.BOLD, 24));
        amountField.setHorizontalAlignment(SwingConstants.CENTER);
        amountField.setPreferredSize(new Dimension(300, 50));
        amountField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        JPanel amountFieldPanel = new JPanel(new FlowLayout());
        amountFieldPanel.add(amountField);
        amountPanel.add(amountFieldPanel, BorderLayout.CENTER);
        
        contentPanel.add(amountPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Payment method selection
        JPanel methodPanel = new JPanel(new BorderLayout());
        JLabel methodLabel = new JLabel("SELECT A PAYMENT OPTION");
        methodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        methodPanel.add(methodLabel, BorderLayout.NORTH);
        
        String[] paymentMethods = {
            "GCash", "PayMaya", "BDO Online", "BPI Online", 
            "UnionBank Online", "Dragonpay Prepaid Credits"
        };
        paymentMethodCombo = new JComboBox<>(paymentMethods);
        paymentMethodCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        paymentMethodCombo.setPreferredSize(new Dimension(300, 30));
        methodPanel.add(paymentMethodCombo, BorderLayout.CENTER);
        
        contentPanel.add(methodPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Service charge info
        serviceChargeLabel = new JLabel("Note: There will be a twenty five pesos (P 25.00) service charge for using dragon pay.");
        serviceChargeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        serviceChargeLabel.setForeground(RED_STATUS);
        contentPanel.add(serviceChargeLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        
        // Total amount
        JPanel totalPanel = new JPanel(new BorderLayout());
        JLabel totalLabel = new JLabel("Amount to Pay + Charges:");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel, BorderLayout.WEST);
        
        totalAmountLabel = new JLabel("P 0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalAmountLabel.setForeground(HEADER_BLUE);
        totalPanel.add(totalAmountLabel, BorderLayout.EAST);
        
        contentPanel.add(totalPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> paymentDialog.setVisible(false));
        
        JButton proceedButton = new JButton("Proceed");
        proceedButton.setBackground(HEADER_BLUE);
        proceedButton.setForeground(WHITE);
        proceedButton.setFont(new Font("Arial", Font.BOLD, 12));
        proceedButton.addActionListener(e -> processPayment());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(proceedButton);
        
        contentPanel.add(buttonPanel);
        
        paymentDialog.add(contentPanel, BorderLayout.CENTER);
        
        // Add listeners for real-time updates
        amountField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotalAmount(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateTotalAmount(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateTotalAmount(); }
        });
        
        paymentMethodCombo.addActionListener(e -> updateTotalAmount());
    }
    
    private void updateTotalAmount() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            double serviceCharge = 25.00; // Dragonpay service charge
            double channelFee = 0.00;
            
            // Add channel-specific fees
            String selectedMethod = (String) paymentMethodCombo.getSelectedItem();
            if (selectedMethod != null) {
                switch (selectedMethod) {
                    case "GCash":
                        channelFee = amount * 0.02; // 2% fee
                        break;
                    case "PayMaya":
                        channelFee = amount * 0.015; // 1.5% fee
                        break;
                    default:
                        channelFee = amount * 0.01; // 1% fee
                        break;
                }
            }
            
            double total = amount + serviceCharge + channelFee;
            totalAmountLabel.setText(String.format("P %,.2f", total));
            
        } catch (NumberFormatException e) {
            totalAmountLabel.setText("P 0.00");
        }
    }
    
    private void processPayment() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String channel = (String) paymentMethodCombo.getSelectedItem();
            String reference = "PAY" + System.currentTimeMillis();
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Process payment through AccountStatementManager
            AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                studentID, amount, channel, reference);
            
            if (result.success) {
                JOptionPane.showMessageDialog(this, result.message, "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                paymentDialog.setVisible(false);
                loadAccountData(); // Refresh the display
            } else {
                JOptionPane.showMessageDialog(this, result.message, "Payment Failed", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadAccountData() {
        // Update student info
        studentInfoLabel.setText(studentID + " | " + program);
        
        // Update amount due for PRELIM
        double prelimDue = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
        amountDueLabel.setText(String.format("P %,.2f", prelimDue));
        
        // Update balance
        balanceLabel.setText(String.format("P %,.2f", accountStatement.getBalance()));
        
        // Update status
        if (accountStatement.isPrelimPaid()) {
            statusLabel.setText("PRELIM STATUS: PAID. Permitted to take the exams.");
            statusLabel.setForeground(GREEN_STATUS);
        } else {
            statusLabel.setText("PRELIM STATUS: NOT PAID. Please pay before prelim exams.");
            statusLabel.setForeground(RED_STATUS);
        }
        
        // Update fee breakdown table
        updateFeeBreakdownTable();
    }
    
    private void updateFeeBreakdownTable() {
        feeTableModel.setRowCount(0);
        
        // Add beginning balance
        feeTableModel.addRow(new Object[]{"", "BEGINNING BALANCE", "0.00"});
        
        // Add payment history
        List<PaymentTransaction> payments = accountStatement.getPaymentHistory();
        for (PaymentTransaction payment : payments) {
            String date = payment.getDate().split(" ")[0]; // Get date part only
            String description = "PAYMENT RECEIVED (" + payment.getReference() + ")";
            String amount = "(" + payment.getAmount().replace("P ", "").replace(",", "") + ")";
            feeTableModel.addRow(new Object[]{date, description, amount});
        }
        
        // Add fee breakdowns
        List<FeeBreakdown> fees = accountStatement.getFeeBreakdowns();
        for (FeeBreakdown fee : fees) {
            String date = fee.getDatePosted().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            String description = fee.getDescription();
            String amount = String.format("%,.2f", fee.getAmount());
            feeTableModel.addRow(new Object[]{date, description, amount});
        }
    }
    
    private void setupEventHandlers() {
        // Add any additional event handlers here
    }
    
    private String getStudentNameFromDatabase(String studentID) {
        // This would typically query the database
        // For now, return a sample name
        return "Sherlie O. Rivera";
    }
    
    private String getStudentProgramFromDatabase(String studentID) {
        // This would typically query the database
        // For now, return a sample program
        return "BSIT 2";
    }
    
    /**
     * Refreshes the panel data from the database
     */
    public void refreshData() {
        accountStatement = AccountStatementManager.getStatement(studentID);
        loadAccountData();
    }
    
    /**
     * Gets the current account statement
     */
    public AccountStatement getAccountStatement() {
        return accountStatement;
    }
}