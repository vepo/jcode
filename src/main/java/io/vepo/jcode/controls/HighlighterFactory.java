package io.vepo.jcode.controls;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HighlighterFactory {
    
    private static final Map<String, LanguageHighlighter> HIGHLIGHTERS = new HashMap<>();
    
    static {
        // Register built-in highlighters
        registerHighlighter(new JavaHighlighter());
        registerHighlighter(new XmlHighlighter());
        registerHighlighter(new JsonHighlighter());
        registerHighlighter(new CssHighlighter());
        registerHighlighter(new HtmlHighlighter());
        registerHighlighter(new MarkdownHighlighter());
    }
    
    /**
     * Register a new language highlighter
     */
    public static void registerHighlighter(LanguageHighlighter highlighter) {
        // Register for common extensions
        if (highlighter.supportsFileExtension("java")) {
            HIGHLIGHTERS.put("java", highlighter);
        }
        if (highlighter.supportsFileExtension("xml")) {
            HIGHLIGHTERS.put("xml", highlighter);
        }
        if (highlighter.supportsFileExtension("json")) {
            HIGHLIGHTERS.put("json", highlighter);
        }
        if (highlighter.supportsFileExtension("css")) {
            HIGHLIGHTERS.put("css", highlighter);
        }
        if (highlighter.supportsFileExtension("html")) {
            HIGHLIGHTERS.put("html", highlighter);
            HIGHLIGHTERS.put("htm", highlighter);
        }
        if (highlighter.supportsFileExtension("md")) {
            HIGHLIGHTERS.put("md", highlighter);
        }
        if (highlighter.supportsFileExtension("markdown")) {
            HIGHLIGHTERS.put("markdown", highlighter);
        }
    }
    
    /**
     * Get the appropriate highlighter for the given file extension
     */
    public static Optional<LanguageHighlighter> getHighlighter(String fileExtension) {
        return Optional.ofNullable(HIGHLIGHTERS.get(fileExtension.toLowerCase()));
    }
    
    /**
     * Get the appropriate highlighter for the given file name
     */
    public static Optional<LanguageHighlighter> getHighlighterForFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return Optional.empty();
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return Optional.empty();
        }
        
        String extension = fileName.substring(lastDotIndex + 1);
        switch (extension) {
            case "java":
                return Optional.of(new JavaHighlighter());
            case "xml":
                return Optional.of(new XmlHighlighter());
            case "json":
                return Optional.of(new JsonHighlighter());
            case "css":
                return Optional.of(new CssHighlighter());
            case "html":
                return Optional.of(new HtmlHighlighter());
            case "md":
            case "markdown":
                return Optional.of(new MarkdownHighlighter());
            default:
                return Optional.empty();
        }
    }
} 