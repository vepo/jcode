package io.vepo.jcode.workspace;

import java.io.File;

import javafx.scene.control.TreeItem;

public class WorkspaceRoot {
    private File root;

    public WorkspaceRoot(File root) {
        this.root = root;
    }

    public static TreeItem<WorkspaceRoot> empty() {
        return new TreeItem<>();
    }

    public static TreeItem<WorkspaceRoot> forRoot(File root) {
        return new TreeItem<>(new WorkspaceRoot(root));
    }

}
