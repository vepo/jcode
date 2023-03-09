package io.vepo.jcode.controls;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class FixedSplitPane extends SplitPane {
    public FixedSplitPane(Node... controls) {
        super();
        setResizableWithParent(controls[0], false);
        getItems().addAll(controls);
    }
}
