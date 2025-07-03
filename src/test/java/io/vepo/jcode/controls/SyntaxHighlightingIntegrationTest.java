package io.vepo.jcode.controls;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class SyntaxHighlightingIntegrationTest {

    @Test
    void testJavaSyntaxHighlightingIntegration() {
        // Test that Java highlighter is correctly selected
        Optional<LanguageHighlighter> highlighter = HighlighterFactory.getHighlighterForFile("Test.java");
        assertTrue(highlighter.isPresent());
        assertTrue(highlighter.get() instanceof JavaHighlighter);
        
        // Test that the highlighter computes highlighting correctly
        String javaCode = "public class Test { private String message = \"Hello\"; }";
        var spans = highlighter.get().computeHighlighting(javaCode);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }

    @Test
    void testXmlSyntaxHighlightingIntegration() {
        // Test that XML highlighter is correctly selected
        Optional<LanguageHighlighter> highlighter = HighlighterFactory.getHighlighterForFile("config.xml");
        assertTrue(highlighter.isPresent());
        assertTrue(highlighter.get() instanceof XmlHighlighter);
        
        // Test that the highlighter computes highlighting correctly
        String xmlCode = "<root><element attribute=\"value\">Content</element></root>";
        var spans = highlighter.get().computeHighlighting(xmlCode);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }

    @Test
    void testJsonSyntaxHighlightingIntegration() {
        // Test that JSON highlighter is correctly selected
        Optional<LanguageHighlighter> highlighter = HighlighterFactory.getHighlighterForFile("data.json");
        assertTrue(highlighter.isPresent());
        assertTrue(highlighter.get() instanceof JsonHighlighter);
        
        // Test that the highlighter computes highlighting correctly
        String jsonCode = "{\"key\": \"value\", \"number\": 123, \"boolean\": true}";
        var spans = highlighter.get().computeHighlighting(jsonCode);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }

    @Test
    void testCssSyntaxHighlightingIntegration() {
        // Test that CSS highlighter is correctly selected
        Optional<LanguageHighlighter> highlighter = HighlighterFactory.getHighlighterForFile("style.css");
        assertTrue(highlighter.isPresent());
        assertTrue(highlighter.get() instanceof CssHighlighter);
        
        // Test that the highlighter computes highlighting correctly
        String cssCode = ".selector { color: #ff0000; font-size: 14px; }";
        var spans = highlighter.get().computeHighlighting(cssCode);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }

    @Test
    void testHtmlSyntaxHighlightingIntegration() {
        // Test that HTML highlighter is correctly selected
        Optional<LanguageHighlighter> highlighter = HighlighterFactory.getHighlighterForFile("index.html");
        assertTrue(highlighter.isPresent());
        assertTrue(highlighter.get() instanceof HtmlHighlighter);
        
        // Test that the highlighter computes highlighting correctly
        String htmlCode = "<html><head><title>Test</title></head><body>Content</body></html>";
        var spans = highlighter.get().computeHighlighting(htmlCode);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }

    @Test
    void testUnknownFileTypeUsesDefaultHighlighter() {
        // Test that unknown file types use the default highlighter
        Optional<LanguageHighlighter> highlighter = HighlighterFactory.getHighlighterForFile("unknown.txt");
        assertFalse(highlighter.isPresent());
    }

    @Test
    void testHighlighterCssClassNames() {
        // Test that all highlighters have correct CSS class names
        assertEquals("java-code", new JavaHighlighter().getCssClass());
        assertEquals("xml-code", new XmlHighlighter().getCssClass());
        assertEquals("json-code", new JsonHighlighter().getCssClass());
        assertEquals("css-code", new CssHighlighter().getCssClass());
        assertEquals("html-code", new HtmlHighlighter().getCssClass());
    }
} 