import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Optimized File I/O Manager for efficient batch operations and buffered reading/writing
 * Features:
 * - Buffered I/O operations for better performance
 * - Batch operations to reduce file system calls
 * - Thread-safe operations with read-write locks
 * - Atomic file operations to prevent data corruption
 * - Memory-efficient streaming for large files
 * - Automatic backup creation before modifications
 */
public class FileIOManager {
    
    private static final int BUFFER_SIZE = 8192; // 8KB buffer
    private static final String BACKUP_SUFFIX = ".backup";
    private static final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Read all lines from a file with buffered I/O - Thread-safe
     */
    public static List<String> readAllLines(File file) throws IOException {
        lock.readLock().lock();
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(file, StandardCharsets.UTF_8), BUFFER_SIZE)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            return lines;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Read file with custom line processor to handle large files efficiently
     */
    public static <T> List<T> readAndProcess(File file, LineProcessor<T> processor) throws IOException {
        lock.readLock().lock();
        try {
            if (!file.exists()) {
                return new ArrayList<>();
            }
            
            List<T> results = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(file, StandardCharsets.UTF_8), BUFFER_SIZE)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    T result = processor.processLine(line, lineNumber++);
                    if (result != null) {
                        results.add(result);
                    }
                }
            }
            return results;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Write all lines to file with atomic operation and backup
     */
    public static void writeAllLines(File file, List<String> lines) throws IOException {
        lock.writeLock().lock();
        try {
            // Create backup if file exists
            if (file.exists()) {
                createBackup(file);
            }
            
            // Create parent directories if they don't exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            
            // Write to temporary file first for atomic operation
            File tempFile = new File(file.getAbsolutePath() + ".tmp");
            
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(tempFile, StandardCharsets.UTF_8), BUFFER_SIZE)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
            }
            
            // Atomic move from temp to actual file
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Append lines to file efficiently
     */
    public static void appendLines(File file, List<String> lines) throws IOException {
        lock.writeLock().lock();
        try {
            // Create parent directories if they don't exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(file, StandardCharsets.UTF_8, true), BUFFER_SIZE)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Append single line to file
     */
    public static void appendLine(File file, String line) throws IOException {
        appendLines(file, Collections.singletonList(line));
    }
    
    /**
     * Update specific lines in a file based on a condition
     */
    public static boolean updateLines(File file, LineUpdater updater) throws IOException {
        lock.writeLock().lock();
        try {
            if (!file.exists()) {
                return false;
            }
            
            List<String> lines = readAllLines(file);
            boolean modified = false;
            
            for (int i = 0; i < lines.size(); i++) {
                String originalLine = lines.get(i);
                String updatedLine = updater.updateLine(originalLine, i);
                if (!originalLine.equals(updatedLine)) {
                    lines.set(i, updatedLine);
                    modified = true;
                }
            }
            
            if (modified) {
                writeAllLines(file, lines);
            }
            
            return modified;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Batch update multiple files atomically
     */
    public static void batchUpdate(Map<File, List<String>> fileUpdates) throws IOException {
        lock.writeLock().lock();
        try {
            // Create backups for all files first
            for (File file : fileUpdates.keySet()) {
                if (file.exists()) {
                    createBackup(file);
                }
            }
            
            // Perform all updates
            for (Map.Entry<File, List<String>> entry : fileUpdates.entrySet()) {
                writeAllLines(entry.getKey(), entry.getValue());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Search for lines matching a pattern
     */
    public static List<SearchResult> searchInFile(File file, String pattern, boolean caseSensitive) throws IOException {
        lock.readLock().lock();
        try {
            List<SearchResult> results = new ArrayList<>();
            if (!file.exists()) {
                return results;
            }
            
            String searchPattern = caseSensitive ? pattern : pattern.toLowerCase();
            
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(file, StandardCharsets.UTF_8), BUFFER_SIZE)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    String searchLine = caseSensitive ? line : line.toLowerCase();
                    if (searchLine.contains(searchPattern)) {
                        results.add(new SearchResult(lineNumber, line));
                    }
                    lineNumber++;
                }
            }
            return results;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get file statistics
     */
    public static FileStats getFileStats(File file) throws IOException {
        lock.readLock().lock();
        try {
            if (!file.exists()) {
                return new FileStats(0, 0, 0);
            }
            
            long size = file.length();
            int lineCount = 0;
            int wordCount = 0;
            
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(file, StandardCharsets.UTF_8), BUFFER_SIZE)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    wordCount += line.split("\\s+").length;
                }
            }
            
            return new FileStats(size, lineCount, wordCount);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Create backup of a file
     */
    private static void createBackup(File file) throws IOException {
        if (!file.exists()) return;
        
        File backupFile = new File(file.getAbsolutePath() + BACKUP_SUFFIX);
        Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Restore file from backup
     */
    public static boolean restoreFromBackup(File file) throws IOException {
        lock.writeLock().lock();
        try {
            File backupFile = new File(file.getAbsolutePath() + BACKUP_SUFFIX);
            if (!backupFile.exists()) {
                return false;
            }
            
            Files.copy(backupFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Delete backup file
     */
    public static boolean deleteBackup(File file) {
        File backupFile = new File(file.getAbsolutePath() + BACKUP_SUFFIX);
        return backupFile.delete();
    }
    
    /**
     * Check if file is locked by another process
     */
    public static boolean isFileLocked(File file) {
        if (!file.exists()) {
            return false;
        }
        
        try (FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.WRITE)) {
            return false; // File is not locked
        } catch (IOException e) {
            return true; // File is locked
        }
    }
    
    /**
     * Safely delete file with retry mechanism
     */
    public static boolean safeDelete(File file, int maxRetries) {
        if (!file.exists()) {
            return true;
        }
        
        for (int i = 0; i < maxRetries; i++) {
            if (file.delete()) {
                return true;
            }
            
            // Wait a bit before retry
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return false;
    }
    
    /**
     * Interface for processing lines during reading
     */
    @FunctionalInterface
    public interface LineProcessor<T> {
        T processLine(String line, int lineNumber);
    }
    
    /**
     * Interface for updating lines
     */
    @FunctionalInterface
    public interface LineUpdater {
        String updateLine(String originalLine, int lineNumber);
    }
    
    /**
     * Search result class
     */
    public static class SearchResult {
        private final int lineNumber;
        private final String line;
        
        public SearchResult(int lineNumber, String line) {
            this.lineNumber = lineNumber;
            this.line = line;
        }
        
        public int getLineNumber() { return lineNumber; }
        public String getLine() { return line; }
        
        @Override
        public String toString() {
            return String.format("Line %d: %s", lineNumber, line);
        }
    }
    
    /**
     * File statistics class
     */
    public static class FileStats {
        private final long sizeInBytes;
        private final int lineCount;
        private final int wordCount;
        
        public FileStats(long sizeInBytes, int lineCount, int wordCount) {
            this.sizeInBytes = sizeInBytes;
            this.lineCount = lineCount;
            this.wordCount = wordCount;
        }
        
        public long getSizeInBytes() { return sizeInBytes; }
        public int getLineCount() { return lineCount; }
        public int getWordCount() { return wordCount; }
        
        @Override
        public String toString() {
            return String.format("Size: %d bytes, Lines: %d, Words: %d", 
                               sizeInBytes, lineCount, wordCount);
        }
    }
}