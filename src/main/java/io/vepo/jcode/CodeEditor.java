package io.vepo.jcode;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import javafx.scene.layout.AnchorPane;

public class CodeEditor extends VirtualizedScrollPane<CodeArea> {

    public CodeEditor() {
        super(new CodeArea());
        setup();
    }

    private void setup() {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(this);

        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setTopAnchor(this, 0.0);

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
