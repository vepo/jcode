package io.vepo.jcode.workspace;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.WorkspaceOpenEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

public class WorkspaceView extends StackPane {

    /**
     *
     */
    private static final String OPEN_WORKSPACE_PREFERECE_KEY = "open-workspace";
    private final Workbench workbench;

    public WorkspaceView(Workbench workbench) {
        this.workbench = workbench;

        Button btnOpenWorkspace = new Button("Open");
        btnOpenWorkspace.setId(OPEN_WORKSPACE_PREFERECE_KEY);

        workbench.subscribe(WorkspaceOpenEvent.class, event -> {
            TreeView<File> treeView = new TreeView<>(WorkspaceRoot.forRoot(event.workspace()));
            treeView.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
                @Override
                public TreeCell<File> call(TreeView<File> p) {
                    return new TreeCell<>() {

                        @Override
                        public void updateItem(File item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || isNull(item)) {
                                setText(null);
                            } else {
                                setText(item.getName());
                            }
                        }

                    };
                }
            });
            setAlignment(treeView, Pos.TOP_LEFT);
            setMargin(treeView, new Insets(0, 0, 0, 0));
            getChildren().remove(btnOpenWorkspace);
            getChildren().add(treeView);
        });

        setAlignment(btnOpenWorkspace, Pos.TOP_CENTER);
        setMargin(btnOpenWorkspace, new Insets(8, 8, 8, 8));
        getChildren().add(btnOpenWorkspace);
        btnOpenWorkspace.setOnAction(evnt -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select working directory....");
            Preferences fileDialogPreferences = preferences().userRoot().node("file-dialog");
            String lastSelectedWorkspace = fileDialogPreferences.get(OPEN_WORKSPACE_PREFERECE_KEY, null);
            if (nonNull(lastSelectedWorkspace)) {
                directoryChooser.setInitialDirectory(Paths.get(lastSelectedWorkspace).toFile());
            }
            File selectedDirectory = directoryChooser.showDialog(this.getScene().getWindow());

            if (nonNull(selectedDirectory)) {
                fileDialogPreferences.put(OPEN_WORKSPACE_PREFERECE_KEY, selectedDirectory.getAbsolutePath());
                workbench.emit(new WorkspaceOpenEvent(selectedDirectory));
            }
        });
    }

}
