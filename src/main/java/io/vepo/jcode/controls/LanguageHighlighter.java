package io.vepo.jcode.controls;

import java.util.Collection;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;

public interface LanguageHighlighter {
    
    /**
     * Get the pattern for this language's syntax highlighting
     */
    Pattern getPattern();
    
    /**
     * Get the CSS class name for this language
     */
    String getCssClass();
    
    /**
     * Compute highlighting for the given text
     */
    StyleSpans<Collection<String>> computeHighlighting(String text);
    
    /**
     * Configure the code area for this language
     */
    void configureCodeArea(CodeArea codeArea);
    
    /**
     * Check if this highlighter supports the given file extension
     */
    boolean supportsFileExtension(String extension);
} 