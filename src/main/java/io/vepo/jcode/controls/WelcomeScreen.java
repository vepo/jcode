package io.vepo.jcode.controls;

import java.io.File;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.services.WorkspaceService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class WelcomeScreen extends BorderPane {
    
    private final Workbench workbench;
    private final Stage primaryStage;
    private final ListView<String> recentWorkspaces;
    private final WorkspaceService workspaceService;
    
    public WelcomeScreen(Workbench workbench, Stage primaryStage) {
        this.workbench = workbench;
        this.primaryStage = primaryStage;
        this.recentWorkspaces = new ListView<>();
        this.workspaceService = new WorkspaceService(workbench);
        
        setupUI();
        loadRecentWorkspaces();
    }
    
    private void setupUI() {
        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(40, 20, 20, 20));
        
        Label titleLabel = new Label("Welcome to jCode");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #ffffff;");
        
        Label subtitleLabel = new Label("Your lightweight JavaFX code editor");
        subtitleLabel.setFont(Font.font("System", 16));
        subtitleLabel.setStyle("-fx-text-fill: #cccccc;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Center content
        VBox centerContent = new VBox(30);
        centerContent.setAlignment(Pos.CENTER);
        centerContent.setPadding(new Insets(40));
        centerContent.setMaxWidth(600);
        
        // Quick actions
        VBox quickActions = new VBox(15);
        quickActions.setAlignment(Pos.CENTER);
        
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        quickActionsLabel.setStyle("-fx-text-fill: #ffffff;");
        
        Button openWorkspaceBtn = createActionButton("Open Workspace", "Open an existing project folder", this::openWorkspace);
        Button newFileBtn = createActionButton("New File", "Create a new file", this::newFile);
        
        quickActions.getChildren().addAll(quickActionsLabel, openWorkspaceBtn, newFileBtn);
        
        // Recent workspaces
        VBox recentSection = new VBox(15);
        recentSection.setAlignment(Pos.CENTER);
        
        Label recentLabel = new Label("Recent Workspaces");
        recentLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        recentLabel.setStyle("-fx-text-fill: #ffffff;");
        
        recentWorkspaces.setPrefHeight(150);
        recentWorkspaces.setMaxWidth(400);
        recentWorkspaces.getStyleClass().add("welcome-list");
        
        recentWorkspaces.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = recentWorkspaces.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openWorkspaceFromPath(selected);
                }
            }
        });
        
        recentSection.getChildren().addAll(recentLabel, recentWorkspaces);
        
        centerContent.getChildren().addAll(quickActions, recentSection);
        
        // Footer
        VBox footer = new VBox(10);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));
        
        Label footerLabel = new Label("Built with JavaFX • Syntax Highlighting • Workspace Management");
        footerLabel.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
        
        footer.getChildren().add(footerLabel);
        
        // Set layout
        setTop(header);
        setCenter(centerContent);
        setBottom(footer);
        
        // Apply styling
        setStyle("-fx-background-color: linear-gradient(to bottom, #2b2b2b, #1e1e1e);");
    }
    
    private Button createActionButton(String text, String tooltip, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        button.setStyle("""
            -fx-background-color: #007acc;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-background-radius: 5px;
            -fx-cursor: hand;
        """);
        
        button.setOnMouseEntered(e -> 
            button.setStyle("""
                -fx-background-color: #005a9e;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-font-size: 14px;
                -fx-background-radius: 5px;
                -fx-cursor: hand;
            """)
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle("""
                -fx-background-color: #007acc;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-font-size: 14px;
                -fx-background-radius: 5px;
                -fx-cursor: hand;
            """)
        );
        
        button.setOnAction(e -> action.run());
        
        if (tooltip != null) {
            button.setTooltip(new Tooltip(tooltip));
        }
        
        return button;
    }
    
    private void openWorkspace() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Workspace");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            openWorkspaceFromPath(selectedDirectory.getAbsolutePath());
        }
    }
    
    private void newFile() {
        // For now, just open a workspace. In the future, this could create a new file
        openWorkspace();
    }
    
    private void openWorkspaceFromPath(String workspacePath) {
        File workspaceDir = new File(workspacePath);
        if (workspaceService.isValidWorkspace(workspaceDir)) {
            workspaceService.openWorkspace(workspaceDir);
        }
    }
    
    private void loadRecentWorkspaces() {
        var recent = workspaceService.getRecentWorkspaces();
        
        recentWorkspaces.getItems().clear();
        if (recent != null && !recent.isEmpty()) {
            recentWorkspaces.getItems().addAll(recent);
        }
    }
} 