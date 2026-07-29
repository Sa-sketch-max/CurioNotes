package com.editor.markdown;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownEngine {

    private final Parser parser;
    private final HtmlRenderer renderer;

    public MarkdownEngine() {
        parser = Parser.builder().build();
        renderer = HtmlRenderer.builder().build();
    }

    public String parseToHtml(String markdown) {

        if (markdown == null || markdown.isBlank()) {
            return "";
        }

        Node document = parser.parse(markdown);

        return renderer.render(document);
    }

}