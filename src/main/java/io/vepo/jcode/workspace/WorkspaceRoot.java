package io.vepo.jcode.workspace;

import static java.util.Objects.nonNull;

import java.io.File;

import javafx.scene.control.TreeItem;
import javafx.util.StringConverter;

public class WorkspaceRoot extends TreeItem<File> {

    public static TreeItem<File> empty() {
        return new TreeItem<>(null);
    }

    public static TreeItem<File> forRoot(File root) {
        return new WorkspaceRoot(root);
    }

    public static StringConverter<File> converter() {
        return new StringConverter<File>() {

            @Override
            public String toString(File item) {
                if (nonNull(item)) {
                    return item.getName();
                } else {
                    return "";
                }
            }

            @Override
            public File fromString(String value) {
                throw new UnsupportedOperationException("Unimplemented method 'fromString'");
            }

        };
    }

    private static void fillTree(File file, TreeItem<File> item) {
        if (nonNull(file) && file.isDirectory()) {
            for (File child : file.listFiles()) {
                TreeItem<File> childItem = new TreeItem<File>(child);
                item.getChildren().add(childItem);
                fillTree(child, childItem);
            }
        }
    }

    public WorkspaceRoot(File root) {
        super(root);
        fillTree(root, this);
    }

    public String getFilename() {
        if (nonNull(this.getValue())) {
            return this.getValue().getName();
        } else {
            return "";
        }
    }

}
