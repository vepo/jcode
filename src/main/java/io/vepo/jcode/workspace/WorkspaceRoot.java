package io.vepo.jcode.workspace;

import static java.util.Objects.nonNull;

import java.io.File;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.themify.Themify;

import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

public class WorkspaceRoot extends TreeItem<File> {

    public static TreeItem<File> empty() {
        return new TreeItem<>(null);
    }

    public static TreeItem<File> forRoot(File root, FileFilter fileFilter) {
        return new WorkspaceRoot(root, fileFilter);
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

    private static void fillTree(File file, TreeItem<File> item, FileFilter fileFilter) {
        if (nonNull(file) && file.isDirectory()) {
            for (var child : file.listFiles()) {
                if (!fileFilter.ignore(child)) {
                    var childItem = new TreeItem<File>(child);
                    item.getChildren().add(childItem);
                    fillTree(child, childItem, fileFilter);
                }
            }
        }
    }

    public WorkspaceRoot(File root, FileFilter fileFilter) {
        super(root, FontIcon.of(Themify.FOLDER, Color.WHITE));
        fillTree(root, this, fileFilter);
    }

    public String getFilename() {
        if (nonNull(this.getValue())) {
            return this.getValue().getName();
        } else {
            return "";
        }
    }

}
