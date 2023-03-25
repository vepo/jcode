package io.vepo.jcode.controls;

import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import javafx.geometry.Insets;
import javafx.scene.layout.AnchorPane;

public class CodeEditor extends VirtualizedScrollPane<CodeArea> {

    public CodeEditor() {
        super(new CodeArea());
        setup();
    }

    private void setup() {
        getContent().setId("codeArea");
        getContent().setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(this);
        setLeftAnchor(this, 0.0);
        setRightAnchor(this, 0.0);
        setBottomAnchor(this, 0.0);
        setTopAnchor(this, 0.0);

        getContent().prefWidthProperty().bind(anchorPane.widthProperty());
        getContent().prefHeightProperty().bind(anchorPane.heightProperty());
    }

    public void setTextContent(String textContent) {
        getContent().replaceText(textContent);
    }

    public String getTextContent() {
        return getContent().getText();
    }

}
