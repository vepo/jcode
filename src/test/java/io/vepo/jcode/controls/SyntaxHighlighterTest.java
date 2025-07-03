package io.vepo.jcode.controls;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class SyntaxHighlighterTest {

    @Test
    void testJavaHighlighter() {
        JavaHighlighter highlighter = new JavaHighlighter();
        
        assertTrue(highlighter.supportsFileExtension("java"));
        assertFalse(highlighter.supportsFileExtension("xml"));
        assertEquals("java-code", highlighter.getCssClass());
        assertNotNull(highlighter.getPattern());
    }

    @Test
    void testXmlHighlighter() {
        XmlHighlighter highlighter = new XmlHighlighter();
        
        assertTrue(highlighter.supportsFileExtension("xml"));
        assertFalse(highlighter.supportsFileExtension("java"));
        assertEquals("xml-code", highlighter.getCssClass());
        assertNotNull(highlighter.getPattern());
    }

    @Test
    void testJsonHighlighter() {
        JsonHighlighter highlighter = new JsonHighlighter();
        
        assertTrue(highlighter.supportsFileExtension("json"));
        assertFalse(highlighter.supportsFileExtension("java"));
        assertEquals("json-code", highlighter.getCssClass());
        assertNotNull(highlighter.getPattern());
    }

    @Test
    void testCssHighlighter() {
        CssHighlighter highlighter = new CssHighlighter();
        
        assertTrue(highlighter.supportsFileExtension("css"));
        assertFalse(highlighter.supportsFileExtension("java"));
        assertEquals("css-code", highlighter.getCssClass());
        assertNotNull(highlighter.getPattern());
    }

    @Test
    void testHtmlHighlighter() {
        HtmlHighlighter highlighter = new HtmlHighlighter();
        
        assertTrue(highlighter.supportsFileExtension("html"));
        assertTrue(highlighter.supportsFileExtension("htm"));
        assertFalse(highlighter.supportsFileExtension("java"));
        assertEquals("html-code", highlighter.getCssClass());
        assertNotNull(highlighter.getPattern());
    }

    @Test
    void testHighlighterFactory() {
        // Test Java file
        Optional<LanguageHighlighter> javaHighlighter = HighlighterFactory.getHighlighterForFile("Test.java");
        assertTrue(javaHighlighter.isPresent());
        assertTrue(javaHighlighter.get() instanceof JavaHighlighter);
        
        // Test XML file
        Optional<LanguageHighlighter> xmlHighlighter = HighlighterFactory.getHighlighterForFile("config.xml");
        assertTrue(xmlHighlighter.isPresent());
        assertTrue(xmlHighlighter.get() instanceof XmlHighlighter);
        
        // Test JSON file
        Optional<LanguageHighlighter> jsonHighlighter = HighlighterFactory.getHighlighterForFile("data.json");
        assertTrue(jsonHighlighter.isPresent());
        assertTrue(jsonHighlighter.get() instanceof JsonHighlighter);
        
        // Test CSS file
        Optional<LanguageHighlighter> cssHighlighter = HighlighterFactory.getHighlighterForFile("style.css");
        assertTrue(cssHighlighter.isPresent());
        assertTrue(cssHighlighter.get() instanceof CssHighlighter);
        
        // Test HTML file
        Optional<LanguageHighlighter> htmlHighlighter = HighlighterFactory.getHighlighterForFile("index.html");
        assertTrue(htmlHighlighter.isPresent());
        assertTrue(htmlHighlighter.get() instanceof HtmlHighlighter);
        
        // Test unknown file
        Optional<LanguageHighlighter> unknownHighlighter = HighlighterFactory.getHighlighterForFile("unknown.txt");
        assertFalse(unknownHighlighter.isPresent());
        
        // Test null and empty filenames
        assertFalse(HighlighterFactory.getHighlighterForFile(null).isPresent());
        assertFalse(HighlighterFactory.getHighlighterForFile("").isPresent());
        assertFalse(HighlighterFactory.getHighlighterForFile("noextension").isPresent());
    }

    @Test
    void testJavaSyntaxHighlighting() {
        JavaHighlighter highlighter = new JavaHighlighter();
        String javaCode = """
            public class Test {
                private String message = "Hello World";
                public void test() {
                    // This is a comment
                    System.out.println(message);
                }
            }
            """;
        
        var spans = highlighter.computeHighlighting(javaCode);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }

    @Test
    void testXmlSyntaxHighlighting() {
        XmlHighlighter highlighter = new XmlHighlighter();
        String xmlCode = """
            <?xml version="1.0" encoding="UTF-8"?>
            <root>
                <!-- This is a comment -->
                <element attribute="value">Content</element>
            </root>
            """;
        
        var spans = highlighter.computeHighlighting(xmlCode);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }
} 