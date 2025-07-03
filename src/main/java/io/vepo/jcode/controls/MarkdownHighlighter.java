package io.vepo.jcode.controls;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;

public class MarkdownHighlighter implements LanguageHighlighter {
    private static final List<HighlightRule> RULES = new ArrayList<>();
    static {
        // Headers: #, ##, ###, etc. (must be at start of line)
        RULES.add(new HighlightRule(Pattern.compile("^(#{1,6})\\s+.*$", Pattern.MULTILINE), "md-header"));
        // Bold: **text** or __text__ (non-greedy)
        RULES.add(new HighlightRule(Pattern.compile("\\*\\*(.*?)\\*\\*|__(.*?)__"), "md-bold"));
        // Italic: *text* or _text_ (non-greedy, but not part of bold)
        RULES.add(new HighlightRule(Pattern.compile("(?<!\\*)\\*(?!\\*)(.*?)(?<!\\*)\\*(?!\\*)|(?<!_)_(?!_)(.*?)(?<!_)_(?!_)"), "md-italic"));
        // Inline code: `code` (non-greedy)
        RULES.add(new HighlightRule(Pattern.compile("`([^`]+)`"), "md-code"));
        // Code block: ```...``` (non-greedy)
        RULES.add(new HighlightRule(Pattern.compile("```[\\s\\S]*?```"), "md-code"));
        // Links: [text](url) (non-greedy)
        RULES.add(new HighlightRule(Pattern.compile("\\[([^\\]]+)\\]\\(([^\\)]+)\\)"), "md-link"));
    }

    @Override
    public Pattern getPattern() {
        // Not used, but required by interface
        return Pattern.compile("");
    }

    @Override
    public String getCssClass() {
        return "markdown-code-area";
    }

    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        int last = 0;
        org.fxmisc.richtext.model.StyleSpansBuilder<Collection<String>> spansBuilder = new org.fxmisc.richtext.model.StyleSpansBuilder<>();
        List<Match> matches = new ArrayList<>();
        for (HighlightRule rule : RULES) {
            Matcher matcher = rule.pattern.matcher(text);
            while (matcher.find()) {
                matches.add(new Match(matcher.start(), matcher.end(), rule.styleClass));
            }
        }
        matches.sort((a, b) -> Integer.compare(a.start, b.start));
        for (Match match : matches) {
            if (match.start > last) {
                spansBuilder.add(List.of(), match.start - last);
            }
            spansBuilder.add(List.of(match.styleClass), match.end - match.start);
            last = match.end;
        }
        if (last < text.length()) {
            spansBuilder.add(List.of(), text.length() - last);
        }
        return spansBuilder.create();
    }

    @Override
    public void configureCodeArea(CodeArea codeArea) {
        // Set up styling
        codeArea.getStylesheets().add(MarkdownHighlighter.class.getResource("/css/css-keywords.css").toExternalForm());
        
        // Apply CSS class to the CodeArea
        codeArea.getStyleClass().add(getCssClass());
        
        // Set font using CSS
        codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px;");
        
        // Set up syntax highlighting
        codeArea.textProperty().addListener((obs, oldText, newText) -> codeArea.setStyleSpans(0, computeHighlighting(newText)));
        codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
    }

    @Override
    public boolean supportsFileExtension(String extension) {
        return "md".equalsIgnoreCase(extension) || "markdown".equalsIgnoreCase(extension);
    }

    private static class HighlightRule {
        final Pattern pattern;
        final String styleClass;
        HighlightRule(Pattern pattern, String styleClass) {
            this.pattern = pattern;
            this.styleClass = styleClass;
        }
    }
    private static class Match {
        final int start, end;
        final String styleClass;
        Match(int start, int end, String styleClass) {
            this.start = start;
            this.end = end;
            this.styleClass = styleClass;
        }
    }
} 