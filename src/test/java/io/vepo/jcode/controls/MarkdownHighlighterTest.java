package io.vepo.jcode.controls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MarkdownHighlighterTest {

    private final MarkdownHighlighter highlighter = new MarkdownHighlighter();

    @Test
    void shouldSupportMarkdownExtensions() {
        assertTrue(highlighter.supportsFileExtension("md"));
        assertTrue(highlighter.supportsFileExtension("markdown"));
        assertFalse(highlighter.supportsFileExtension("java"));
    }

    @Test
    void shouldReturnCorrectCssClass() {
        assertEquals("markdown-code-area", highlighter.getCssClass());
    }

    @Test
    void shouldHaveValidPattern() {
        assertNotNull(highlighter.getPattern());
    }

    @Test
    void shouldComputeHighlighting() {
        String markdownText = """
            # Header 1
            ## Header 2
            This is **bold** and *italic* text.
            Use `code` for inline code.
            Visit [Google](https://google.com) for search.
            """;
        
        var spans = highlighter.computeHighlighting(markdownText);
        assertNotNull(spans);
        assertTrue(spans.getSpanCount() > 0);
    }

    @Test
    void shouldBeRegisteredInFactory() {
        var highlighter = HighlighterFactory.getHighlighterForFile("test.md");
        assertTrue(highlighter.isPresent());
        assertTrue(highlighter.get() instanceof MarkdownHighlighter);
        
        highlighter = HighlighterFactory.getHighlighterForFile("README.markdown");
        assertTrue(highlighter.isPresent());
        assertTrue(highlighter.get() instanceof MarkdownHighlighter);
    }
} 