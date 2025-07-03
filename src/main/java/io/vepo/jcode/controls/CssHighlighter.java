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

public class CssHighlighter implements LanguageHighlighter {
    
    private static final String[] PROPERTIES = {
        "color", "background", "border", "margin", "padding", "font", "text", "display",
        "position", "width", "height", "top", "left", "right", "bottom", "float", "clear",
        "overflow", "visibility", "opacity", "z-index", "box-shadow", "text-shadow",
        "transform", "transition", "animation", "flex", "grid", "align", "justify"
    };
    
    private static final String[] VALUES = {
        "auto", "none", "inherit", "initial", "unset", "static", "relative", "absolute",
        "fixed", "sticky", "block", "inline", "inline-block", "flex", "grid", "table",
        "hidden", "visible", "scroll", "transparent", "currentColor", "revert"
    };

    private static final String PROPERTY_PATTERN = "\\b(" + String.join("|", PROPERTIES) + ")\\b";
    private static final String VALUE_PATTERN = "\\b(" + String.join("|", VALUES) + ")\\b";
    private static final String SELECTOR_PATTERN = "[.#]?[a-zA-Z][a-zA-Z0-9_-]*";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
    private static final String COMMENT_PATTERN = "/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/";
    private static final String NUMBER_PATTERN = "\\b\\d+(\\.\\d+)?(px|em|rem|%|vh|vw|pt|cm|mm|in)?\\b";
    private static final String COLOR_PATTERN = "#[0-9a-fA-F]{3,6}|rgb\\([^)]*\\)|rgba\\([^)]*\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String COLON_PATTERN = ":";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<PROPERTY>" + PROPERTY_PATTERN + ")"
        + "|(?<VALUE>" + VALUE_PATTERN + ")"
        + "|(?<SELECTOR>" + SELECTOR_PATTERN + ")"
        + "|(?<STRING>" + STRING_PATTERN + ")"
        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
        + "|(?<COLOR>" + COLOR_PATTERN + ")"
        + "|(?<BRACE>" + BRACE_PATTERN + ")"
        + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
        + "|(?<COLON>" + COLON_PATTERN + ")"
    );

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public String getCssClass() {
        return "css-code";
    }

    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            String styleClass =
                matcher.group("PROPERTY") != null ? "property" :
                matcher.group("VALUE") != null ? "value" :
                matcher.group("SELECTOR") != null ? "selector" :
                matcher.group("STRING") != null ? "string" :
                matcher.group("COMMENT") != null ? "comment" :
                matcher.group("NUMBER") != null ? "number" :
                matcher.group("COLOR") != null ? "color" :
                matcher.group("BRACE") != null ? "brace" :
                matcher.group("SEMICOLON") != null ? "semicolon" :
                matcher.group("COLON") != null ? "colon" :
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
        
        // Set up syntax highlighting
        codeArea.multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
        
        // Set up styling
        codeArea.getStylesheets().add(CssHighlighter.class.getResource("/css/css-keywords.css").toExternalForm());
        
        // Set font using CSS
        codeArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px;");
    }

    @Override
    public boolean supportsFileExtension(String extension) {
        return "css".equalsIgnoreCase(extension);
    }
} 