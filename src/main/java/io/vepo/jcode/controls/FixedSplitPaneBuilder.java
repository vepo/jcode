package io.vepo.jcode.controls;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static javafx.scene.control.SplitPane.setResizableWithParent;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public interface FixedSplitPaneBuilder {
    public static SplitPane build(Node... controls) {
        var pane = new SplitPane();
        setResizableWithParent(controls[0], false);
        pane.getItems().addAll(controls);

        var workspacePref = preferences().userRoot()
                                         .node("window")
                                         .node("workspace");

        var width = workspacePref.getDouble("width", Double.NEGATIVE_INFINITY);
        if (width > 0) {
            pane.getDividers()
                .get(0)
                .setPosition(width);
        }
        pane.getDividers()
            .get(0)
            .positionProperty()
            .addListener((observable, oldValue, newValue) -> workspacePref.putDouble("width", newValue.doubleValue()));

        return pane;
    }
}
