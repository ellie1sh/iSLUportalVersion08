import java.util.ArrayList;
import java.util.List;

/**
 * Represents a journal article with all bibliographic details
 */
public class JournalArticle {
    private String title;
    private String authors;
    private String journalName;
    private String publicationType; // e.g., "TRAVEL DECISION MAKING", "TOURISM RESEARCH", etc.
    private String volume;
    private String issue;
    private String pages;
    private String date;
    private String keywords;
    private boolean currentIssue;
    
    public JournalArticle(String title, String authors, String journalName, 
                         String publicationType, String volume, String issue, 
                         String pages, String date, String keywords, boolean currentIssue) {
        this.title = title;
        this.authors = authors;
        this.journalName = journalName;
        this.publicationType = publicationType;
        this.volume = volume;
        this.issue = issue;
        this.pages = pages;
        this.date = date;
        this.keywords = keywords;
        this.currentIssue = currentIssue;
    }
    
    // Search method - case insensitive
    public boolean matchesSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return true;
        }
        
        String lowerSearch = searchTerm.toLowerCase();
        
        return (title != null && title.toLowerCase().contains(lowerSearch)) ||
               (authors != null && authors.toLowerCase().contains(lowerSearch)) ||
               (journalName != null && journalName.toLowerCase().contains(lowerSearch)) ||
               (publicationType != null && publicationType.toLowerCase().contains(lowerSearch)) ||
               (keywords != null && keywords.toLowerCase().contains(lowerSearch)) ||
               (date != null && date.toLowerCase().contains(lowerSearch));
    }
    
    // Advanced search with multiple criteria
    public boolean matchesAdvancedSearch(String titleSearch, String authorSearch, 
                                        String journalSearch, String yearFrom, String yearTo,
                                        String publicationType) {
        boolean matches = true;
        
        // Title search
        if (titleSearch != null && !titleSearch.trim().isEmpty()) {
            matches = matches && (title != null && title.toLowerCase().contains(titleSearch.toLowerCase()));
        }
        
        // Author search
        if (authorSearch != null && !authorSearch.trim().isEmpty()) {
            matches = matches && (authors != null && authors.toLowerCase().contains(authorSearch.toLowerCase()));
        }
        
        // Journal search
        if (journalSearch != null && !journalSearch.trim().isEmpty()) {
            matches = matches && (journalName != null && journalName.toLowerCase().contains(journalSearch.toLowerCase()));
        }
        
        // Publication type filter
        if (publicationType != null && !publicationType.trim().isEmpty() && !publicationType.equals("All")) {
            matches = matches && (this.publicationType != null && 
                                 this.publicationType.toLowerCase().contains(publicationType.toLowerCase()));
        }
        
        // Year range filter
        if (yearFrom != null && !yearFrom.trim().isEmpty()) {
            try {
                int fromYear = Integer.parseInt(yearFrom);
                String yearStr = extractYear(date);
                if (yearStr != null) {
                    int articleYear = Integer.parseInt(yearStr);
                    matches = matches && (articleYear >= fromYear);
                }
            } catch (NumberFormatException e) {
                // Invalid year format, ignore this filter
            }
        }
        
        if (yearTo != null && !yearTo.trim().isEmpty()) {
            try {
                int toYear = Integer.parseInt(yearTo);
                String yearStr = extractYear(date);
                if (yearStr != null) {
                    int articleYear = Integer.parseInt(yearStr);
                    matches = matches && (articleYear <= toYear);
                }
            } catch (NumberFormatException e) {
                // Invalid year format, ignore this filter
            }
        }
        
        return matches;
    }
    
    // Helper method to extract year from date string
    private String extractYear(String dateStr) {
        if (dateStr == null) return null;
        // Extract 4-digit year from date string
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b(19|20)\\d{2}\\b");
        java.util.regex.Matcher matcher = pattern.matcher(dateStr);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
    
    // Getters
    public String getTitle() { return title; }
    public String getAuthors() { return authors; }
    public String getJournalName() { return journalName; }
    public String getPublicationType() { return publicationType; }
    public String getVolume() { return volume; }
    public String getIssue() { return issue; }
    public String getPages() { return pages; }
    public String getDate() { return date; }
    public String getKeywords() { return keywords; }
    public boolean isCurrentIssue() { return currentIssue; }
    
    // Create sample data
    public static List<JournalArticle> getSampleArticles() {
        List<JournalArticle> articles = new ArrayList<>();
        
        // E-WORD-OF-MOUTH ON TRAVELS
        articles.add(new JournalArticle(
            "E-WORD-OF-MOUTH ON TRAVELS",
            "Janet Hernandez-Mendez, Juan Sanchez-Fernandez",
            "The influence of e-word-of-mouth on travel decision-making: consumer profiles",
            "TRAVEL 2.0 APPLICATIONS, TRAVEL DECISION MAKING, eWOM (E-WORD-OF-MOUTH)",
            "18", "9",
            "1001-1021",
            "Sep - Dec 2015",
            "e-word-of-mouth, travel, consumer profiles, tourism",
            true
        ));
        
        // CULTURAL CREATIVE INDUSTRIES (CCI)
        articles.add(new JournalArticle(
            "CULTURAL CREATIVE INDUSTRIES (CCI), MARKOV-SWITCHING MODEL (MSM)",
            "William S. Chang",
            "Policy momentum for the development of Taiwan's cultural creative industries",
            "CULTURAL CREATIVE INDUSTRIES",
            "18", "9",
            "1088-1098",
            "Sep - Dec 2015",
            "cultural industries, Taiwan, policy, Markov model",
            true
        ));
        
        // EMOTION MEASUREMENT
        articles.add(new JournalArticle(
            "EMOTION, EMOTION MEASUREMENT, PSYCHOPHYSIOLOGICAL MEASUREMENT OF EMOTION",
            "Shanshi Li, Gabby Walters",
            "Current and potential methods for measuring emotion in tourism experiences: a review",
            "PSYCHOPHYSIOLOGICAL MEASUREMENT",
            "18", "9",
            "805-807",
            "Sep - Dec 2015",
            "emotion, measurement, tourism experiences, psychology",
            true
        ));
        
        // QUALITATIVE RESEARCH
        articles.add(new JournalArticle(
            "QUALITATIVE RESEARCH, TOURISM RESEARCH, YOUTH'S VOICES IN TOURISM RESEARCH",
            "Catheryn Khoo-Lattimore",
            "Kids on board: methodological challenges, concerns and clarifications when including young children's voices in tourism research",
            "TOURISM RESEARCH",
            "18", "9",
            "845-858",
            "Sep - Dec 2015",
            "qualitative research, youth, tourism, methodology",
            true
        ));
        
        // INFORMATION SEARCH BEHAVIOURS
        articles.add(new JournalArticle(
            "INFORMATION SEARCH BEHAVIOURS, MOBILE INTERNET DEVICES, TOURIST INFORMATION CENTERS, WILLINGNESS-TO-PAY",
            "Seong Ok Lyu",
            "Preferences for tourist information centres in the ubiquitous information environment",
            "TOURIST INFORMATION CENTERS",
            "18", "9",
            "1032-1047",
            "Sep - Dec 2015",
            "information search, mobile devices, tourist centers, technology",
            true
        ));
        
        // MOBILE APPLICATIONS IN TRAVELS
        articles.add(new JournalArticle(
            "MOBILE APPLICATIONS IN TRAVELS, RURAL TOURISM, TRAVEL EXPERIENCE",
            "Jiaying Lu, Mengbin Wang",
            "Goodbye maps, hello apps? exploring the influential determinants of travel app adoption",
            "TRAVEL EXPERIENCE",
            "18", "9",
            "1059-1079",
            "Sep - Dec 2015",
            "mobile apps, travel, rural tourism, technology adoption",
            true
        ));
        
        // AIRBNB, PEER-TO-PEER ACCOMMODATION
        articles.add(new JournalArticle(
            "AIRBNB, PEER-TO-PEER ACCOMMODATION, SHORT-TERM RENTALS",
            "Daniel Guttentag",
            "Airbnb: disruptive innovation and the rise of an informal tourism accommodation sector",
            "SHORT-TERM RENTALS",
            "18", "9",
            "1192-1217",
            "Sep - Dec 2015",
            "Airbnb, accommodation, peer-to-peer, sharing economy",
            true
        ));
        
        // Add some biography/biographical entries
        articles.add(new JournalArticle(
            "Tourism Pioneers: A Biographical Study",
            "Maria Santos, John Williams",
            "Journal of Tourism History",
            "BIOGRAPHY, TOURISM HISTORY",
            "12", "3",
            "234-256",
            "March 2023",
            "biography, tourism pioneers, history, influential figures",
            false
        ));
        
        articles.add(new JournalArticle(
            "Thomas Cook: The Father of Modern Tourism",
            "Elizabeth Johnson",
            "Biographical Studies in Tourism",
            "BIOGRAPHY, TOURISM PIONEERS",
            "8", "1",
            "12-35",
            "January 2022",
            "Thomas Cook, biography, tourism history, travel agency",
            false
        ));
        
        articles.add(new JournalArticle(
            "Women in Tourism: Biographical Perspectives",
            "Sarah Chen, Linda Brown",
            "Gender and Tourism Journal",
            "BIOGRAPHY, GENDER STUDIES",
            "15", "4",
            "445-467",
            "December 2023",
            "women, tourism, biography, gender, leadership",
            false
        ));
        
        // Add more diverse articles
        articles.add(new JournalArticle(
            "Digital Transformation in Hospitality Industry",
            "Robert Lee, Anna Kim",
            "International Journal of Hospitality Management",
            "DIGITAL TRANSFORMATION, HOSPITALITY",
            "20", "2",
            "156-178",
            "June 2024",
            "digital, hospitality, technology, transformation",
            false
        ));
        
        articles.add(new JournalArticle(
            "Sustainable Tourism Development in Asia",
            "Raj Patel, Ming Zhang",
            "Asia Pacific Journal of Tourism Research",
            "SUSTAINABLE TOURISM, ASIA",
            "19", "7",
            "789-812",
            "July 2024",
            "sustainable, tourism, Asia, development, environment",
            false
        ));
        
        articles.add(new JournalArticle(
            "COVID-19 Impact on Global Tourism",
            "Jennifer Smith, Michael Brown",
            "Tourism Management Perspectives",
            "PANDEMIC, TOURISM CRISIS",
            "21", "1",
            "23-45",
            "January 2024",
            "COVID-19, pandemic, tourism, crisis, recovery",
            false
        ));
        
        articles.add(new JournalArticle(
            "Virtual Reality in Tourism Marketing",
            "David Wilson, Emma Davis",
            "Journal of Travel Research",
            "VIRTUAL REALITY, MARKETING",
            "22", "5",
            "567-589",
            "May 2024",
            "virtual reality, VR, marketing, technology, tourism",
            false
        ));
        
        articles.add(new JournalArticle(
            "Cultural Heritage Tourism in Europe",
            "Francesco Rossi, Marie Dubois",
            "European Journal of Tourism Research",
            "CULTURAL HERITAGE, EUROPE",
            "17", "8",
            "890-915",
            "August 2023",
            "cultural heritage, Europe, tourism, preservation",
            false
        ));
        
        return articles;
    }
}