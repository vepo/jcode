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

public class HtmlHighlighter implements LanguageHighlighter {
    
    private static final String[] TAGS = {
        "html", "head", "body", "title", "meta", "link", "script", "style",
        "div", "span", "p", "h1", "h2", "h3", "h4", "h5", "h6",
        "a", "img", "table", "tr", "td", "th", "ul", "ol", "li",
        "form", "input", "button", "textarea", "select", "option",
        "header", "footer", "nav", "main", "section", "article", "aside"
    };
    
    private static final String[] ATTRIBUTES = {
        "id", "class", "style", "src", "href", "alt", "title", "type",
        "name", "value", "placeholder", "required", "disabled", "readonly",
        "maxlength", "minlength", "pattern", "autocomplete", "autofocus"
    };

    private static final String TAG_PATTERN = "</?(" + String.join("|", TAGS) + ")\\b[^>]*>";
    private static final String ATTRIBUTE_PATTERN = "\\b(" + String.join("|", ATTRIBUTES) + ")\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";
    private static final String COMMENT_PATTERN = "<!--[^-]*-->";
    private static final String DOCTYPE_PATTERN = "<!DOCTYPE[^>]*>";
    private static final String CDATA_PATTERN = "<!\\[CDATA\\[.*?\\]\\]>";
    private static final String EQUALS_PATTERN = "=";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<TAG>" + TAG_PATTERN + ")"
        + "|(?<ATTRIBUTE>" + ATTRIBUTE_PATTERN + ")"
        + "|(?<STRING>" + STRING_PATTERN + ")"
        + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
        + "|(?<DOCTYPE>" + DOCTYPE_PATTERN + ")"
        + "|(?<CDATA>" + CDATA_PATTERN + ")"
        + "|(?<EQUALS>" + EQUALS_PATTERN + ")"
    );

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public String getCssClass() {
        return "html-code";
    }

    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while (matcher.find()) {
            String styleClass =
                matcher.group("TAG") != null ? "tag" :
                matcher.group("ATTRIBUTE") != null ? "attribute" :
                matcher.group("STRING") != null ? "string" :
                matcher.group("COMMENT") != null ? "comment" :
                matcher.group("DOCTYPE") != null ? "doctype" :
                matcher.group("CDATA") != null ? "cdata" :
                matcher.group("EQUALS") != null ? "equals" :
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
        codeArea.getStylesheets().add(HtmlHighlighter.class.getResource("/css/html-keywords.css").toExternalForm());
        
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
        return "html".equalsIgnoreCase(extension) || "htm".equalsIgnoreCase(extension);
    }
} 