import javax.swing.*;
import java.awt.*;
import javax.swing.UIManager;

/**
 * Test class for StatementOfAccountsPanel
 * This class demonstrates the Statement of Accounts functionality
 */
public class StatementOfAccountsTest extends JFrame {
    
    public StatementOfAccountsTest() {
        setTitle("Statement of Accounts Test - iSLU Student Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Create the Statement of Accounts panel
        StatementOfAccountsPanel soaPanel = new StatementOfAccountsPanel("2255146");
        
        // Add to frame
        add(soaPanel, BorderLayout.CENTER);
        
        // Add a refresh button for testing
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> soaPanel.refreshData());
        
        JButton addPaymentButton = new JButton("Add Sample Payment");
        addPaymentButton.addActionListener(e -> {
            // Add a sample payment for testing
            AccountStatementManager.processPayment("2255146", 5000.0, "GCash", "TEST" + System.currentTimeMillis());
            soaPanel.refreshData();
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(addPaymentButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new StatementOfAccountsTest().setVisible(true);
        });
    }
}