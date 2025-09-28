import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Test class to demonstrate the Statement of Accounts functionality
 * This creates a simplified version to show the HTML-style layout
 */
public class TestStatementOfAccounts extends JFrame {
    private String studentID = "2255146";
    private AccountStatement accountStatement;
    
    public TestStatementOfAccounts() {
        setTitle("iSLU Student Portal - Statement of Accounts Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize account statement
        accountStatement = AccountStatementManager.getStatement(studentID);
        
        // Create the main panel
        JPanel mainPanel = createStatementOfAccountsPanel();
        add(mainPanel);
        
        setVisible(true);
    }
    
    private JPanel createStatementOfAccountsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create table layout like HTML
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 240, 240));
        
        // Left panel - Statement of Accounts (70% width)
        JPanel leftPanel = createStatementLeftPanel();
        leftPanel.setPreferredSize(new Dimension(700, 0));

        // Right panel - Online Payment Channels (30% width)
        JPanel rightPanel = createPaymentChannelsPanel();
        rightPanel.setPreferredSize(new Dimension(300, 0));

        tablePanel.add(leftPanel, BorderLayout.CENTER);
        tablePanel.add(rightPanel, BorderLayout.EAST);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        return mainPanel;
    }
    
    private JPanel createStatementLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Header with pie chart icon - matching HTML structure
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel headerLabel = new JLabel("📊 Statement of Accounts (FIRST SEMESTER, 2025-2026)");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content panel with white background
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Student Information Section - matching HTML mws-stat-container
        JPanel studentInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        studentInfoPanel.setBackground(Color.WHITE);
        studentInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        studentInfoPanel.setPreferredSize(new Dimension(0, 80));
        
        // Female user icon
        JLabel studentIcon = new JLabel("👩");
        studentIcon.setFont(new Font("Arial", Font.PLAIN, 32));
        studentInfoPanel.add(studentIcon);
        
        JPanel studentTextPanel = new JPanel();
        studentTextPanel.setLayout(new BoxLayout(studentTextPanel, BoxLayout.Y_AXIS));
        studentTextPanel.setBackground(Color.WHITE);
        
        // Student ID and Program
        JLabel studentIDLabel = new JLabel("2255146 | BSIT 2");
        studentIDLabel.setFont(new Font("Arial", Font.BOLD, 14));
        studentIDLabel.setForeground(new Color(10, 45, 90));
        studentTextPanel.add(studentIDLabel);
        
        // Student Name
        JLabel studentNameLabel = new JLabel("Sherlie O. Rivera");
        studentNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        studentNameLabel.setForeground(new Color(10, 45, 90));
        studentTextPanel.add(studentNameLabel);
        
        studentInfoPanel.add(studentTextPanel);
        contentPanel.add(studentInfoPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Amount Due for PRELIM Section - matching HTML structure
        JPanel prelimAmountPanel = new JPanel();
        prelimAmountPanel.setLayout(new BoxLayout(prelimAmountPanel, BoxLayout.Y_AXIS));
        prelimAmountPanel.setBackground(Color.WHITE);
        
        JLabel prelimLabel = new JLabel("Your amount due for PRELIM is:");
        prelimLabel.setFont(new Font("Arial", Font.PLAIN, 30));
        prelimLabel.setForeground(Color.BLACK);
        prelimAmountPanel.add(prelimLabel);
        prelimAmountPanel.add(Box.createVerticalStrut(10));
        
        // Large amount display - using dynamic data
        double prelimDue = accountStatement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
        JLabel prelimAmountValue = new JLabel("P " + String.format("%,.2f", prelimDue));
        prelimAmountValue.setFont(new Font("Arial", Font.BOLD, 50));
        prelimAmountValue.setForeground(new Color(144, 24, 24)); // #901818 from HTML
        prelimAmountPanel.add(prelimAmountValue);
        prelimAmountPanel.add(Box.createVerticalStrut(20));
        
        // Remaining Balance Section - using dynamic data
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String currentDateStr = currentDate.format(dateFormatter);
        
        JLabel balanceLabel = new JLabel("Your remaining balance as of " + currentDateStr + " is:");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        balanceLabel.setForeground(Color.BLACK);
        prelimAmountPanel.add(balanceLabel);
        prelimAmountPanel.add(Box.createVerticalStrut(10));
        
        JLabel balanceValue = new JLabel("P " + String.format("%,.2f", accountStatement.getBalance()));
        balanceValue.setFont(new Font("Arial", Font.BOLD, 50));
        balanceValue.setForeground(new Color(144, 24, 24)); // #901818 from HTML
        prelimAmountPanel.add(balanceValue);
        
        contentPanel.add(prelimAmountPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // PRELIM STATUS Section - using dynamic data
        boolean isPrelimPaid = accountStatement.isPrelimPaid();
        String prelimStatusText = isPrelimPaid ? 
            "PRELIM STATUS: PAID. Permitted to take the exams." : 
            "PRELIM STATUS: NOT PAID. Please pay before prelim exams.";
        Color prelimStatusColor = isPrelimPaid ? 
            new Color(0, 150, 0) : new Color(200, 0, 0);
        
        JLabel prelimStatusLabel = new JLabel(prelimStatusText);
        prelimStatusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        prelimStatusLabel.setForeground(prelimStatusColor);
        contentPanel.add(prelimStatusLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Breakdown of Fees Panel - matching HTML structure
        JPanel breakdownPanel = createBreakdownPanel();
        contentPanel.add(breakdownPanel);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createBreakdownPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Header with list icon - matching HTML structure
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 40));
        
        // Get current date for header
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        String currentDateStr = currentDate.format(dateFormatter);
        
        JLabel headerLabel = new JLabel("📋 Breakdown of fees as of " + currentDateStr);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table matching HTML structure - Date, Description, Amount
        String[] columnNames = {"Date", "Description", "Amount"};
        
        // Get dynamic data from account statement
        java.util.List<FeeBreakdown> fees = accountStatement.getFeeBreakdowns();
        java.util.List<PaymentTransaction> payments = accountStatement.getPaymentHistory();
        
        // Create data array with dynamic content
        Object[][] data = new Object[fees.size() + payments.size() + 1][3];
        int row = 0;
        
        // Add beginning balance
        data[row++] = new Object[]{"", "BEGINNING BALANCE", "0.00"};
        
        // Add payment transactions (negative amounts in parentheses)
        for (PaymentTransaction payment : payments) {
            String dateStr = payment.getDate().split(" ")[0]; // Get date part only
            String amountStr = payment.getAmount().replace("P ", "").replace(",", "");
            data[row++] = new Object[]{dateStr, "PAYMENT RECEIVED (" + payment.getReference() + ")", "(" + amountStr + ")"};
        }
        
        // Add fee breakdowns
        for (FeeBreakdown fee : fees) {
            String dateStr = fee.getDatePosted() != null ? 
                fee.getDatePosted().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy")) : "";
            String amountStr = String.format("%,.2f", fee.getAmount());
            data[row++] = new Object[]{dateStr, fee.getDescription(), amountStr};
        }

        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));
        
        // Custom renderer for formatting - matching HTML table style
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Set background colors for alternating rows
                if (row % 2 == 0) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(new Color(248, 248, 248));
                }
                
                // Right align amounts
                if (column == 2) {
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                
                // Italicize dates
                if (column == 0 && row > 0) {
                    setFont(getFont().deriveFont(Font.ITALIC));
                }
                
                return this;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createPaymentChannelsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Header with shopping cart icon - matching HTML structure
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel headerLabel = new JLabel("🛒 Online Payment Channels");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        
        panel.add(headerPanel, BorderLayout.NORTH);

        // Content panel with white background
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Instructional text - matching HTML
        JLabel instructionLabel = new JLabel("Tuition fees can be paid via the available online payment channels.");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        instructionLabel.setForeground(new Color(14, 40, 79)); // #0e284f from HTML
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(instructionLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Horizontal rule
        JSeparator separator = new JSeparator();
        separator.setBackground(new Color(200, 200, 200));
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(20));

        // Payment channel buttons - matching HTML structure
        String[] channels = {
            "UB UnionBank UPay Online",
            "@dragonpay Payment Gateway", 
            "BPI BPI Online",
            "BDO BDO Online",
            "BDO Bills Payment",
            "Bukas Tuition Installment Plans"
        };

        Color[] colors = {
            new Color(255, 140, 0), // Orange for UnionBank
            new Color(255, 69, 0),  // Red for Dragonpay
            new Color(220, 20, 60), // Crimson for BPI
            new Color(0, 100, 200), // Blue for BDO
            new Color(0, 100, 200), // Blue for BDO Bills
            new Color(135, 206, 235) // Light Blue for Bukas
        };

        for (int i = 0; i < channels.length; i++) {
            JButton channelButton = new JButton(channels[i]);
            channelButton.setBackground(colors[i]);
            channelButton.setForeground(Color.WHITE);
            channelButton.setFont(new Font("Arial", Font.BOLD, 12));
            channelButton.setPreferredSize(new Dimension(280, 50));
            channelButton.setFocusPainted(false);
            channelButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            channelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Add action listener for payment processing
            final String channelName = channels[i];
            channelButton.addActionListener(e -> {
                showPaymentDialog(channelName);
            });
            
            contentPanel.add(channelButton);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private void showPaymentDialog(String channelName) {
        // Create dialog title based on channel - matching HTML modal titles
        String dialogTitle;
        if (channelName.contains("UnionBank") || channelName.contains("UPay")) {
            dialogTitle = "Payment through UPay by UnionBank";
        } else if (channelName.contains("Dragonpay")) {
            dialogTitle = "Payment through Dragon Pay";
        } else if (channelName.contains("BPI")) {
            dialogTitle = "Payment through BPI";
        } else if (channelName.contains("BDO")) {
            dialogTitle = "Payment through BDO Online";
        } else {
            dialogTitle = "Payment through " + channelName;
        }
        
        JDialog paymentDialog = new JDialog(this, dialogTitle, true);
        paymentDialog.setSize(500, 400);
        paymentDialog.setLocationRelativeTo(this);
        paymentDialog.setLayout(new BorderLayout());

        // Header with close button - matching HTML modal structure
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(10, 45, 90));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel headerLabel = new JLabel(dialogTitle);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        // Close button (X)
        JButton closeButton = new JButton("✕");
        closeButton.setBackground(new Color(10, 45, 90));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.addActionListener(e -> paymentDialog.dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);
        
        paymentDialog.add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);

        // AMOUNT TO PAY section - matching HTML structure
        JPanel amountSection = new JPanel(new BorderLayout());
        amountSection.setBackground(Color.WHITE);
        amountSection.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "AMOUNT TO PAY",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            new Color(10, 45, 90)
        ));
        
        // Large amount input field - matching HTML style
        JPanel amountInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        amountInputPanel.setBackground(Color.WHITE);
        
        JTextField amountField = new JTextField("0");
        amountField.setFont(new Font("Arial", Font.PLAIN, 30));
        amountField.setHorizontalAlignment(SwingConstants.CENTER);
        amountField.setPreferredSize(new Dimension(300, 50));
        amountField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        amountInputPanel.add(amountField);
        
        amountSection.add(amountInputPanel, BorderLayout.CENTER);
        contentPanel.add(amountSection);
        contentPanel.add(Box.createVerticalStrut(20));

        // Payment method selection for Dragonpay
        if (channelName.contains("Dragonpay")) {
            JPanel paymentMethodPanel = new JPanel(new BorderLayout());
            paymentMethodPanel.setBackground(Color.WHITE);
            paymentMethodPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "SELECT A PAYMENT OPTION",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(10, 45, 90)
            ));
            
            JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{
                "GCash", "PayMaya", "Coins.ph Wallet", "BPI Online", "BDO Online", 
                "Metrobank Online", "RCBC Online", "Security Bank Online"
            });
            paymentMethodCombo.setFont(new Font("Arial", Font.PLAIN, 14));
            paymentMethodCombo.setPreferredSize(new Dimension(250, 30));
            paymentMethodPanel.add(paymentMethodCombo, BorderLayout.CENTER);
            
            contentPanel.add(paymentMethodPanel);
            contentPanel.add(Box.createVerticalStrut(20));
            
            // Service charge note for Dragonpay
            JLabel serviceChargeNote = new JLabel("<html>There will be a <b style='color: red;'>twenty five pesos (P 25.00)</b> service charge for using dragon pay.<br>An additional fee will be charged depending on the payment channel.</html>");
            serviceChargeNote.setFont(new Font("Arial", Font.PLAIN, 12));
            serviceChargeNote.setForeground(Color.BLACK);
            contentPanel.add(serviceChargeNote);
            contentPanel.add(Box.createVerticalStrut(20));
            
            // AMOUNT TO PAY + CHARGES
            JPanel totalAmountPanel = new JPanel(new BorderLayout());
            totalAmountPanel.setBackground(Color.WHITE);
            totalAmountPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "AMOUNT TO PAY + CHARGES",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14),
                new Color(10, 45, 90)
            ));
            
            JLabel totalAmountLabel = new JLabel("6658.16");
            totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 18));
            totalAmountLabel.setHorizontalAlignment(SwingConstants.CENTER);
            totalAmountPanel.add(totalAmountLabel, BorderLayout.CENTER);
            
            contentPanel.add(totalAmountPanel);
            contentPanel.add(Box.createVerticalStrut(20));
        }

        // Buttons panel - matching HTML structure
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton proceedButton = new JButton("Proceed");
        proceedButton.setBackground(new Color(0, 150, 0));
        proceedButton.setForeground(Color.WHITE);
        proceedButton.setFont(new Font("Arial", Font.BOLD, 12));
        proceedButton.setPreferredSize(new Dimension(120, 35));
        proceedButton.addActionListener(e -> {
            String amountStr = amountField.getText().trim();
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
                
                // Process payment using AccountStatementManager
                AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                    studentID, amount, channelName, "PAY-" + System.currentTimeMillis());
                
                if (result.success) {
                    JOptionPane.showMessageDialog(paymentDialog, result.message, "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                    paymentDialog.dispose();
                    // Refresh the display
                    dispose();
                    new TestStatementOfAccounts();
                } else {
                    JOptionPane.showMessageDialog(paymentDialog, result.message, "Payment Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(paymentDialog, "Invalid amount format", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(proceedButton);
        contentPanel.add(buttonPanel);

        paymentDialog.add(contentPanel, BorderLayout.CENTER);
        paymentDialog.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TestStatementOfAccounts();
        });
    }
}