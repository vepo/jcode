package io.vepo.jcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vepo.jcode.controls.JCodeMenuBuilder;
import io.vepo.jcode.controllers.ApplicationController;
import io.vepo.jcode.ui.UIManager;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main application class for jCode - a lightweight JavaFX code editor.
 * Uses a clean architecture with separation of concerns through services and controllers.
 */
public class JCode extends Application {
    private static final Logger logger = LoggerFactory.getLogger(JCode.class);

    private Workbench workbench;
    private UIManager uiManager;
    private ApplicationController applicationController;

    public JCode() {
        this.workbench = new Workbench();
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting jCode application...");
        
        try {
            // Initialize UI components
            uiManager = new UIManager(primaryStage, workbench);
            
            // Setup menu
            BorderPane rootPane = (BorderPane) uiManager.getScene().getRoot();
            rootPane.setTop(JCodeMenuBuilder.build(workbench));
            
            // Initialize application controller
            applicationController = new ApplicationController(workbench, uiManager);
            
            // Set scene and show stage
            primaryStage.setScene(uiManager.getScene());
            primaryStage.show();
            
            // Initialize application
            applicationController.initialize();
            
            logger.info("jCode application started successfully");
            
        } catch (Exception e) {
            logger.error("Failed to start jCode application", e);
            throw new RuntimeException("Application startup failed", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
