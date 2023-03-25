package io.vepo.jcode.workspace;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.File;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.themify.Themify;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.CloseWorkspaceEvent;
import io.vepo.jcode.events.FileLoadEvent;
import io.vepo.jcode.events.SelectWorkspaceEvent;
import io.vepo.jcode.events.WorkspaceOpenEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

public class WorkspaceView extends StackPane {

    private class WorkspaceTreeCell extends TreeCell<File> {

        @Override
        public void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || isNull(item)) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getName());
                setGraphic(FontIcon.of(item.isDirectory() ? Themify.FOLDER : Themify.FILE));
            }
        }

    }

    /**
     *
     */
    private static final String OPEN_WORKSPACE_PREFERECE_KEY = "open-workspace";
    private final Workbench workbench;
    private final Button btnOpenWorkspace;

    public WorkspaceView(Workbench workbench) {
        this.workbench = workbench;

        btnOpenWorkspace = new Button("Open");
        btnOpenWorkspace.setId(OPEN_WORKSPACE_PREFERECE_KEY);

        setAlignment(btnOpenWorkspace, Pos.TOP_CENTER);
        setMargin(btnOpenWorkspace, new Insets(8, 8, 8, 8));
        getChildren().add(btnOpenWorkspace);
        btnOpenWorkspace.setOnAction(evnt -> workbench.emit(new SelectWorkspaceEvent()));

        workbench.subscribe(WorkspaceOpenEvent.class, this::openWorkspace);
        workbench.subscribe(SelectWorkspaceEvent.class, this::selectWorkspace);
        workbench.subscribe(CloseWorkspaceEvent.class, this::closeWorkspace);
    }

    private void closeWorkspace(CloseWorkspaceEvent event) {
        getChildren().removeIf(node -> node instanceof TreeView);
        getChildren().add(btnOpenWorkspace);
    }

    private void openWorkspace(WorkspaceOpenEvent event) {
        TreeView<File> treeView = new TreeView<>(WorkspaceRoot.forRoot(event.workspace(), workbench.fileFilter()));
        treeView.setCellFactory(view -> new WorkspaceTreeCell());
        treeView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() == 2) {
                    File selectedFile = treeView.getSelectionModel().getSelectedItem().getValue();
                    if (selectedFile.isFile()) {
                        workbench.emit(new FileLoadEvent(selectedFile));
                    }
                }
            }
        });
        setAlignment(treeView, Pos.TOP_LEFT);
        setMargin(treeView, new Insets(0, 0, 0, 0));
        getChildren().remove(btnOpenWorkspace);
        getChildren().add(treeView);
    }

    private void selectWorkspace(SelectWorkspaceEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select working directory....");
        Preferences fileDialogPreferences = preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY);
        String lastSelectedWorkspace = fileDialogPreferences.get("file-dialog", null);
        if (nonNull(lastSelectedWorkspace)) {
            directoryChooser.setInitialDirectory(Paths.get(lastSelectedWorkspace).toFile());
        }
        File selectedDirectory = directoryChooser.showDialog(this.getScene().getWindow());

        if (nonNull(selectedDirectory)) {
            fileDialogPreferences.put("file-dialog", selectedDirectory.getAbsolutePath());
            workbench.emit(new WorkspaceOpenEvent(selectedDirectory));
        }
    }

}
