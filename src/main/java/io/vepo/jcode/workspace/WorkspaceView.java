package io.vepo.jcode.workspace;

import java.io.File;
import java.util.Objects;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.WorkspaceOpenEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

public class WorkspaceView extends StackPane {

    private final Workbench workbench;
    private final Button btnOpenWorkspace;
    private final TreeView<WorkspaceRoot> treeViewer;

    public WorkspaceView(Workbench workbench) {
        this.workbench = workbench;

        treeViewer = new TreeView<>();
        btnOpenWorkspace = new Button("Open");

        setAlignment(treeViewer, Pos.TOP_LEFT);
        setMargin(treeViewer, new Insets(0, 0, 0, 0));
        workbench.subscribe(WorkspaceOpenEvent.class, event -> {
            treeViewer.setRoot(WorkspaceRoot.forRoot(event.workspace()));
            getChildren().remove(btnOpenWorkspace);
            getChildren().add(treeViewer);
        });

        setAlignment(btnOpenWorkspace, Pos.TOP_CENTER);
        setMargin(btnOpenWorkspace, new Insets(8, 8, 8, 8));
        getChildren().add(btnOpenWorkspace);
        btnOpenWorkspace.setOnAction(evnt -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(this.getScene().getWindow());
            if (Objects.nonNull(selectedDirectory)) {
                workbench.emit(new WorkspaceOpenEvent(selectedDirectory));
            }
        });
    }

}
