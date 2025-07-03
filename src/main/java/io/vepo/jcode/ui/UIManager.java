package io.vepo.jcode.ui;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;

import java.util.prefs.Preferences;

import io.vepo.jcode.controls.CodeEditor;
import io.vepo.jcode.controls.FixedSplitPaneBuilder;
import io.vepo.jcode.controls.WelcomeScreen;
import io.vepo.jcode.workspace.WorkspaceViewBuilder;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

/**
 * Manages the main UI components and their lifecycle.
 * Centralizes UI-related operations to improve code organization.
 */
public class UIManager {
    
    private final Stage primaryStage;
    private final BorderPane rootPane;
    private final Scene scene;
    
    private final CodeEditor codeEditor;
    private final WelcomeScreen welcomeScreen;
    private final StackPane workspace;
    
    private final ProgressBar progressBar;
    private final Label statusMessage;
    
    public UIManager(Stage primaryStage, io.vepo.jcode.Workbench workbench) {
        this.primaryStage = primaryStage;
        this.rootPane = new BorderPane();
        
        // Initialize UI components
        this.codeEditor = new CodeEditor(workbench);
        this.welcomeScreen = new WelcomeScreen(workbench, primaryStage);
        this.workspace = WorkspaceViewBuilder.build(workbench);
        
        // Initialize status bar components
        this.progressBar = new ProgressBar();
        this.statusMessage = new Label("Checking for Changes...");
        
        // Setup scene
        this.scene = createScene();
        setupWindowProperties();
        setupEventHandlers();
        
        // Set initial view
        showWelcomeScreen();
    }
    
    private Scene createScene() {
        Preferences windowPrefs = preferences().userRoot().node("window");
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        
        Scene scene = new Scene(rootPane,
                               windowPrefs.getDouble("width", screenBounds.getWidth() - 50),
                               windowPrefs.getDouble("height", screenBounds.getHeight() - 50));
        
        // Apply JMetro theme
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);
        
        // Load custom CSS
        scene.getStylesheets().add(getClass().getResource("/css/welcome-screen.css").toExternalForm());
        
        return scene;
    }
    
    private void setupWindowProperties() {
        Preferences windowPrefs = preferences().userRoot().node("window");
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        
        // Set window position
        double x = windowPrefs.getDouble("x", Double.NEGATIVE_INFINITY);
        double y = windowPrefs.getDouble("y", Double.NEGATIVE_INFINITY);
        if (x > 0) {
            primaryStage.setX(x);
        }
        if (y > 0) {
            primaryStage.setY(y);
        }
        
        // Set window size and maximized state
        primaryStage.setMaximized(windowPrefs.getBoolean("maximized", true));
        
        // Setup property listeners for persistence
        setupWindowPropertyListeners(windowPrefs);
    }
    
    private void setupWindowPropertyListeners(Preferences windowPrefs) {
        primaryStage.maximizedProperty()
                    .addListener((observable, oldValue, newValue) -> 
                        windowPrefs.putBoolean("maximized", newValue));
        
        primaryStage.xProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (!primaryStage.isMaximized()) {
                            windowPrefs.putDouble("x", newValue.doubleValue());
                        }
                    });
        
        primaryStage.yProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (!primaryStage.isMaximized()) {
                            windowPrefs.putDouble("y", newValue.doubleValue());
                        }
                    });
        
        scene.widthProperty()
             .addListener((observable, oldValue, newValue) -> {
                 if (!primaryStage.isMaximized()) {
                     windowPrefs.putDouble("width", newValue.doubleValue());
                 }
             });
        
        scene.heightProperty()
             .addListener((observable, oldValue, newValue) -> {
                 if (!primaryStage.isMaximized()) {
                     windowPrefs.putDouble("height", newValue.doubleValue());
                 }
             });
    }
    
    private void setupEventHandlers() {
        // Setup status bar
        HBox statusBar = new HBox();
        statusBar.getChildren().add(new HBox(statusMessage, progressBar));
        rootPane.setBottom(statusBar);
        
        // Setup window close handler
        primaryStage.setOnCloseRequest(event -> {
            WorkspaceViewBuilder.saveWorkspaceState(workspace);
            codeEditor.saveOpenTabs();
        });
    }
    
    public void showWelcomeScreen() {
        rootPane.setCenter(welcomeScreen);
        updateWindowTitle(null);
    }
    
    public void showWorkspace() {
        rootPane.setCenter(FixedSplitPaneBuilder.build(workspace, codeEditor));
    }
    
    public void updateWindowTitle(String workspacePath) {
        if (workspacePath != null) {
            primaryStage.setTitle("jCode - " + workspacePath);
        } else {
            primaryStage.setTitle("jCode");
        }
    }
    
    public void setStatusMessage(String message) {
        statusMessage.setText(message);
    }
    
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    
    public CodeEditor getCodeEditor() {
        return codeEditor;
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }
} 