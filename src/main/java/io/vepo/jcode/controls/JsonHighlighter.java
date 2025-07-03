package io.vepo.jcode.controls;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class JsonHighlighter implements LanguageHighlighter {
    
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String NUMBER_PATTERN = "\\b\\d+(\\.\\d+)?([eE][+-]?\\d+)?\\b";
    private static final String BOOLEAN_PATTERN = "\\b(true|false|null)\\b";
    private static final String KEY_PATTERN = "\"([^\"\\\\]|\\\\.)*\"\\s*:";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String COMMA_PATTERN = ",";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<STRING>" + STRING_PATTERN + ")"
        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
        + "|(?<BOOLEAN>" + BOOLEAN_PATTERN + ")"
        + "|(?<KEY>" + KEY_PATTERN + ")"
        + "|(?<BRACE>" + BRACE_PATTERN + ")"
        + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
        + "|(?<COMMA>" + COMMA_PATTERN + ")"
    );

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public String getCssClass() {
        return "json-code";
    }

    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            String styleClass =
                matcher.group("STRING") != null ? "string" :
                matcher.group("NUMBER") != null ? "number" :
                matcher.group("BOOLEAN") != null ? "boolean" :
                matcher.group("KEY") != null ? "key" :
                matcher.group("BRACE") != null ? "brace" :
                matcher.group("BRACKET") != null ? "bracket" :
                matcher.group("COMMA") != null ? "comma" :
                null;
            
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    @Override
    public void configureCodeArea(CodeArea codeArea) {
        // Set up line numbers
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        
        // Set up styling
        codeArea.getStylesheets().add(JsonHighlighter.class.getResource("/css/json-keywords.css").toExternalForm());
        
        // Set font using CSS
        codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px;");
        
        // Set up syntax highlighting
        codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
        
        // Apply initial highlighting
        codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
    }

    @Override
    public boolean supportsFileExtension(String extension) {
        return "json".equalsIgnoreCase(extension);
    }
} 