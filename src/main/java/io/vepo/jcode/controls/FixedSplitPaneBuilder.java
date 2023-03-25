package io.vepo.jcode.controls;

import static javafx.scene.control.SplitPane.setResizableWithParent;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public interface FixedSplitPaneBuilder {
    public static SplitPane build(Node... controls) {
        SplitPane pane = new SplitPane();
        setResizableWithParent(controls[0], false);
        pane.getItems().addAll(controls);
        return pane;
    }
}
