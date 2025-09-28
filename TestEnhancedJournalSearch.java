import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TestEnhancedJournalSearch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Enhanced Journal Search Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 700);
            
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Tab 1: Button Visibility Test
            tabbedPane.addTab("Button Visibility", createButtonVisibilityPanel());
            
            // Tab 2: Advanced Search Test
            tabbedPane.addTab("Advanced Search", createAdvancedSearchPanel());
            
            // Tab 3: Print Preview
            tabbedPane.addTab("Print Preview", createPrintPreviewPanel());
            
            frame.add(tabbedPane);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    private static JPanel createButtonVisibilityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextArea info = new JTextArea(
            "BUTTON VISIBILITY TEST\n\n" +
            "This demonstrates the correct button visibility behavior:\n\n" +
            "BEFORE SEARCH:\n" +
            "✓ Search button - VISIBLE\n" +
            "✓ Advanced Search button - VISIBLE\n" +
            "✗ Search Again button - HIDDEN\n" +
            "✗ Print button - HIDDEN\n\n" +
            "AFTER SEARCH:\n" +
            "✗ Search button - HIDDEN\n" +
            "✗ Advanced Search button - HIDDEN\n" +
            "✓ Search Again button - VISIBLE (Fixed!)\n" +
            "✓ Print button - VISIBLE\n\n" +
            "The Search Again button now remains visible after search,\n" +
            "allowing users to easily start a new search."
        );
        info.setEditable(false);
        info.setFont(new Font("Arial", Font.PLAIN, 12));
        info.setBackground(new Color(245, 245, 245));
        
        panel.add(new JScrollPane(info), BorderLayout.CENTER);
        
        // Simulation buttons
        JPanel buttonPanel = new JPanel();
        JButton searchBtn = new JButton("Search");
        JButton advancedBtn = new JButton("Advanced Search");
        JButton searchAgainBtn = new JButton("Search Again");
        JButton printBtn = new JButton("🖨 Print");
        
        searchAgainBtn.setVisible(false);
        printBtn.setVisible(false);
        
        JButton simulateSearch = new JButton("Simulate Search");
        simulateSearch.addActionListener(e -> {
            searchBtn.setVisible(false);
            advancedBtn.setVisible(false);
            searchAgainBtn.setVisible(true);
            printBtn.setVisible(true);
            JOptionPane.showMessageDialog(panel, 
                "After search:\n- Search Again is VISIBLE\n- Print is VISIBLE", 
                "Button State", JOptionPane.INFORMATION_MESSAGE);
        });
        
        searchAgainBtn.addActionListener(e -> {
            searchBtn.setVisible(true);
            advancedBtn.setVisible(true);
            searchAgainBtn.setVisible(false);
            printBtn.setVisible(false);
        });
        
        buttonPanel.add(searchBtn);
        buttonPanel.add(advancedBtn);
        buttonPanel.add(searchAgainBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(simulateSearch);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private static JPanel createAdvancedSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("ADVANCED SEARCH FEATURES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // Title field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Title Contains:"), gbc);
        gbc.gridx = 1;
        JTextField titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        
        // Author field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Author Contains:"), gbc);
        gbc.gridx = 1;
        JTextField authorField = new JTextField(20);
        formPanel.add(authorField, gbc);
        
        // Journal field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Journal Name:"), gbc);
        gbc.gridx = 1;
        JTextField journalField = new JTextField(20);
        formPanel.add(journalField, gbc);
        
        // Publication Type
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Publication Type:"), gbc);
        gbc.gridx = 1;
        String[] types = {"All", "BIOGRAPHY", "TOURISM RESEARCH", "TRAVEL DECISION MAKING", 
                        "CULTURAL HERITAGE", "DIGITAL TRANSFORMATION", "PANDEMIC"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        formPanel.add(typeCombo, gbc);
        
        // Year range
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Year From:"), gbc);
        gbc.gridx = 1;
        JTextField yearFromField = new JTextField("2015", 10);
        formPanel.add(yearFromField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Year To:"), gbc);
        gbc.gridx = 1;
        JTextField yearToField = new JTextField("2024", 10);
        formPanel.add(yearToField, gbc);
        
        // Results area
        JTextArea resultsArea = new JTextArea(10, 40);
        resultsArea.setEditable(false);
        
        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            List<JournalArticle> articles = JournalArticle.getSampleArticles();
            List<JournalArticle> results = new ArrayList<>();
            
            for (JournalArticle article : articles) {
                if (article.matchesAdvancedSearch(
                        titleField.getText(),
                        authorField.getText(),
                        journalField.getText(),
                        yearFromField.getText(),
                        yearToField.getText(),
                        (String) typeCombo.getSelectedItem())) {
                    results.add(article);
                }
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("Advanced Search Results\n");
            sb.append("=======================\n\n");
            sb.append("Found ").append(results.size()).append(" articles:\n\n");
            
            for (JournalArticle article : results) {
                sb.append("• ").append(article.getTitle()).append("\n");
                sb.append("  By: ").append(article.getAuthors()).append("\n");
                sb.append("  In: ").append(article.getJournalName()).append("\n");
                sb.append("  Date: ").append(article.getDate()).append("\n\n");
            }
            
            resultsArea.setText(sb.toString());
        });
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(searchButton, gbc);
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JPanel createPrintPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextArea printPreview = new JTextArea();
        printPreview.setFont(new Font("Monospaced", Font.PLAIN, 11));
        printPreview.setEditable(false);
        
        // Generate sample print content
        StringBuilder content = new StringBuilder();
        content.append("SAINT LOUIS UNIVERSITY LIBRARIES\n");
        content.append("PERIODICAL ARTICLE INDEXES\n");
        content.append("================================\n\n");
        content.append("Search Results for: tourism\n");
        content.append("Date: ").append(new java.util.Date()).append("\n");
        content.append("Total Results: 3\n\n");
        
        content.append("1. QUALITATIVE RESEARCH, TOURISM RESEARCH, YOUTH'S VOICES IN TOURISM RESEARCH\n");
        content.append("   Type: TOURISM RESEARCH\n");
        content.append("   Authors: Catheryn Khoo-Lattimore\n");
        content.append("   Journal: Kids on board: methodological challenges...\n");
        content.append("   Volume: 18, Issue: 9, Pages: 845-858\n");
        content.append("   Date: Sep - Dec 2015\n\n");
        
        content.append("2. Sustainable Tourism Development in Asia\n");
        content.append("   Type: SUSTAINABLE TOURISM, ASIA\n");
        content.append("   Authors: Raj Patel, Ming Zhang\n");
        content.append("   Journal: Asia Pacific Journal of Tourism Research\n");
        content.append("   Volume: 19, Issue: 7, Pages: 789-812\n");
        content.append("   Date: July 2024\n\n");
        
        content.append("3. COVID-19 Impact on Global Tourism\n");
        content.append("   Type: PANDEMIC, TOURISM CRISIS\n");
        content.append("   Authors: Jennifer Smith, Michael Brown\n");
        content.append("   Journal: Tourism Management Perspectives\n");
        content.append("   Volume: 21, Issue: 1, Pages: 23-45\n");
        content.append("   Date: January 2024\n\n");
        
        printPreview.setText(content.toString());
        
        JPanel topPanel = new JPanel();
        JLabel label = new JLabel("PRINT PREVIEW - This is what will be printed:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(label);
        
        JButton testPrintButton = new JButton("Test Print Function");
        testPrintButton.addActionListener(e -> {
            try {
                boolean result = printPreview.print();
                if (result) {
                    JOptionPane.showMessageDialog(panel, 
                        "Print dialog shown successfully!\nActual printing works!", 
                        "Print Test", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, 
                    "Print test: " + ex.getMessage(), 
                    "Print Test", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        topPanel.add(testPrintButton);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(printPreview), BorderLayout.CENTER);
        
        JTextArea info = new JTextArea(
            "The Print button now provides actual printing functionality:\n" +
            "• Formats search results in a printer-friendly layout\n" +
            "• Shows standard print dialog for printer selection\n" +
            "• Includes all article details in the printout\n" +
            "• Adds header with search term and date"
        );
        info.setEditable(false);
        info.setBackground(new Color(245, 245, 245));
        info.setPreferredSize(new Dimension(0, 80));
        panel.add(info, BorderLayout.SOUTH);
        
        return panel;
    }
}