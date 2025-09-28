import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TestJournalSearch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create a simple test frame to demonstrate the search functionality
            JFrame frame = new JFrame("Journal/Periodical Search Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            
            // Create a panel to hold search demo
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Title
            JLabel titleLabel = new JLabel("Journal Article Search Demo", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            
            // Test panel
            JPanel testPanel = new JPanel();
            testPanel.setLayout(new BoxLayout(testPanel, BoxLayout.Y_AXIS));
            testPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            // Info text
            JTextArea infoText = new JTextArea(
                "This demonstrates the case-insensitive search functionality:\n\n" +
                "Sample search terms to try:\n" +
                "• 'tourism' - finds articles about tourism\n" +
                "• 'biography' - finds biographical articles\n" +
                "• 'COVID' - finds pandemic-related articles\n" +
                "• 'digital' - finds technology-related articles\n" +
                "• 'airbnb' - finds specific topic articles\n" +
                "• 'women' - finds gender-related articles\n\n" +
                "The search is case-insensitive and searches through:\n" +
                "- Article titles\n" +
                "- Author names\n" +
                "- Journal names\n" +
                "- Keywords\n" +
                "- Publication types"
            );
            infoText.setEditable(false);
            infoText.setBackground(new Color(245, 245, 245));
            infoText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            testPanel.add(infoText);
            
            // Search test section
            JPanel searchTestPanel = new JPanel(new FlowLayout());
            JTextField searchField = new JTextField(20);
            JButton testButton = new JButton("Test Search");
            JTextArea resultArea = new JTextArea(10, 60);
            resultArea.setEditable(false);
            resultArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            testButton.addActionListener(e -> {
                String searchTerm = searchField.getText();
                List<JournalArticle> articles = JournalArticle.getSampleArticles();
                StringBuilder results = new StringBuilder();
                results.append("Search results for: \"").append(searchTerm).append("\"\n");
                results.append("=====================================\n\n");
                
                int count = 0;
                for (JournalArticle article : articles) {
                    if (article.matchesSearch(searchTerm)) {
                        count++;
                        results.append(count).append(". ").append(article.getTitle()).append("\n");
                        results.append("   Authors: ").append(article.getAuthors()).append("\n");
                        results.append("   Journal: ").append(article.getJournalName()).append("\n");
                        results.append("   Date: ").append(article.getDate()).append("\n\n");
                    }
                }
                
                if (count == 0) {
                    results.append("No results found.");
                } else {
                    results.append("\nTotal results: ").append(count);
                }
                
                resultArea.setText(results.toString());
            });
            
            searchTestPanel.add(new JLabel("Search Term:"));
            searchTestPanel.add(searchField);
            searchTestPanel.add(testButton);
            
            testPanel.add(Box.createVerticalStrut(20));
            testPanel.add(searchTestPanel);
            testPanel.add(Box.createVerticalStrut(10));
            testPanel.add(new JScrollPane(resultArea));
            
            mainPanel.add(testPanel, BorderLayout.CENTER);
            
            // Button to launch full portal
            JButton launchPortalButton = new JButton("Launch Full Student Portal");
            launchPortalButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, 
                    "To see the full Journal/Periodical search in action:\n\n" +
                    "1. Run the Login class\n" +
                    "2. Login with any student ID (e.g., 2023001)\n" +
                    "3. Navigate to '📚 Journal/Periodical' in the sidebar\n" +
                    "4. Use the search box to search for articles",
                    "How to Access Full Feature", 
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(launchPortalButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            frame.add(mainPanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}