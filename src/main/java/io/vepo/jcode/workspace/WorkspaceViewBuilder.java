package io.vepo.jcode.workspace;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static io.vepo.jcode.utils.FileId.idFromFile;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javafx.scene.layout.StackPane.setAlignment;
import static javafx.scene.layout.StackPane.setMargin;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;
import java.util.prefs.Preferences;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.themify.Themify;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.CloseWorkspaceEvent;
import io.vepo.jcode.events.FileLoadEvent;
import io.vepo.jcode.events.SelectWorkspaceEvent;
import io.vepo.jcode.events.WorkspaceOpenEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;

public interface WorkspaceViewBuilder {

    /**
     *
     */
    public static final String ID = "workspace-tree";
    static final String FILE_DIALOG_KEY = "file-dialog";
    static final String OPEN_WORKSPACE_PREFERECE_KEY = "open-workspace";
    static final String LAST_WORKSPACE_KEY = "last-workspace";
    static final String EXPANDED_NODES_KEY = "expanded-nodes";

    private static TreeCell<File> cellCreator(TreeView<File> view) {
        return new TreeCell<File>() {
            @Override
            public void updateItem(File item, boolean empty) {
                if (nonNull(item)) {
                    setId(idFromFile(item));
                }
                super.updateItem(item, empty);
                if (empty || isNull(item)) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                    setGraphic(FontIcon.of(item.isDirectory() ? Themify.FOLDER : Themify.FILE, Color.WHITE));
                }
            }

        };
    }

    static StackPane build(Workbench workbench) {
        StackPane pane = new StackPane();

        Button btnOpenWorkspace = new Button("Open");
        btnOpenWorkspace.setId(OPEN_WORKSPACE_PREFERECE_KEY);

        setAlignment(btnOpenWorkspace, Pos.TOP_CENTER);
        setMargin(btnOpenWorkspace, new Insets(8, 8, 8, 8));
        pane.getChildren().add(btnOpenWorkspace);
        btnOpenWorkspace.setOnAction(evnt -> workbench.emit(new SelectWorkspaceEvent()));

        workbench.subscribe(WorkspaceOpenEvent.class, event -> openWorkspace(pane, btnOpenWorkspace, workbench, event));
        workbench.subscribe(SelectWorkspaceEvent.class, event -> selectWorkspace(pane, workbench, event));
        workbench.subscribe(CloseWorkspaceEvent.class, evnt -> closeWorkspace(pane, btnOpenWorkspace, evnt));

        // Try to open the last workspace on startup
        openLastWorkspace(pane, btnOpenWorkspace, workbench);

        return pane;
    }

    private static void openLastWorkspace(StackPane pane, Button btnOpenWorkspace, Workbench workbench) {
        Preferences workspacePreferences = preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY);
        String lastWorkspacePath = workspacePreferences.get(LAST_WORKSPACE_KEY, null);

        if (nonNull(lastWorkspacePath)) {
            File lastWorkspace = new File(lastWorkspacePath);
            if (lastWorkspace.exists() && lastWorkspace.isDirectory()) {
                workbench.emit(new WorkspaceOpenEvent(lastWorkspace));
            }
        }
    }

    private static void closeWorkspace(StackPane pane, Button btnOpenWorkspace, CloseWorkspaceEvent event) {
        pane.getChildren().removeIf(node -> node instanceof TreeView);
        pane.getChildren().add(btnOpenWorkspace);
        
        // Save expanded nodes if any tree is present
        saveWorkspaceState(pane);
        
        // Clear the last workspace and expanded state preference when closing (user-initiated)
        Preferences workspacePreferences = preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY);
        workspacePreferences.remove(LAST_WORKSPACE_KEY);
        workspacePreferences.remove(EXPANDED_NODES_KEY);
    }

    private static void openWorkspace(StackPane pane,
            Button btnOpenWorkspace,
            Workbench workbench,
            WorkspaceOpenEvent event) {
        TreeView<File> treeView = new TreeView<>(WorkspaceRoot.forRoot(event.workspace(), workbench.fileFilter()));
        treeView.setId(ID);
        treeView.setCellFactory(WorkspaceViewBuilder::cellCreator);
        treeView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                File selectedFile = treeView.getSelectionModel().getSelectedItem().getValue();
                if (selectedFile.isFile()) {
                    workbench.emit(new FileLoadEvent(selectedFile));
                }
            }
        });
        setAlignment(treeView, Pos.TOP_LEFT);
        setMargin(treeView, new Insets(0, 0, 0, 0));
        pane.getChildren().remove(btnOpenWorkspace);
        pane.getChildren().add(treeView);

        // Store the workspace path for next startup
        Preferences workspacePreferences = preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY);
        workspacePreferences.put(LAST_WORKSPACE_KEY, event.workspace().getAbsolutePath());

        // Restore expanded nodes
        String expandedNodes = workspacePreferences.get(EXPANDED_NODES_KEY, "");
        if (!expandedNodes.isEmpty()) {
            var expandedSet = Set.of(expandedNodes.split(";"));
            restoreExpandedState(treeView.getRoot(), "", expandedSet);
        }
    }

    private static void selectWorkspace(StackPane pane, Workbench workbench, SelectWorkspaceEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select working directory....");
        Preferences fileDialogPreferences = preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY);
        String lastSelectedWorkspace = fileDialogPreferences.get(FILE_DIALOG_KEY, null);
        if (nonNull(lastSelectedWorkspace)) {
            directoryChooser.setInitialDirectory(Paths.get(lastSelectedWorkspace).toFile());
        }
        File selectedDirectory = directoryChooser.showDialog(pane.getScene().getWindow());

        if (nonNull(selectedDirectory)) {
            fileDialogPreferences.put(FILE_DIALOG_KEY, selectedDirectory.getAbsolutePath());
            workbench.emit(new WorkspaceOpenEvent(selectedDirectory));
        }
    }

    private static void collectExpandedPaths(TreeItem<File> node, String path, StringBuilder expandedPaths) {
        if (node == null)
            return;
        String currentPath = path;
        if (node.getValue() != null) {
            currentPath = path.isEmpty() ? node.getValue().getAbsolutePath() : path + "/" + node.getValue().getName();
        }
        if (node.isExpanded()) {
            if (expandedPaths.length() > 0)
                expandedPaths.append(";");
            expandedPaths.append(currentPath);
        }
        for (TreeItem<File> child : node.getChildren()) {
            collectExpandedPaths(child, currentPath, expandedPaths);
        }
    }

    private static void restoreExpandedState(TreeItem<File> node, String path, Set<String> expandedSet) {
        if (node == null)
            return;
        String currentPath = path;
        if (node.getValue() != null) {
            currentPath = path.isEmpty() ? node.getValue().getAbsolutePath() : path + "/" + node.getValue().getName();
        }
        if (expandedSet.contains(currentPath)) {
            node.setExpanded(true);
        }
        for (TreeItem<File> child : node.getChildren()) {
            restoreExpandedState(child, currentPath, expandedSet);
        }
    }

    static void saveWorkspaceState(StackPane pane) {
        // Save expanded nodes if any tree is present
        Preferences workspacePreferences = preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY);
        pane.getChildren().stream()
            .filter(node -> node instanceof TreeView)
            .map(node -> (TreeView<File>) node)
            .findFirst()
            .ifPresent(treeView -> {
                var expandedPaths = new StringBuilder();
                collectExpandedPaths(treeView.getRoot(), "", expandedPaths);
                workspacePreferences.put(EXPANDED_NODES_KEY, expandedPaths.toString());
            });
    }
}
