package io.vepo.jcode.controllers;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vepo.jcode.events.CloseWorkspaceEvent;
import io.vepo.jcode.events.FileLoadEvent;
import io.vepo.jcode.events.LoadedFileEvent;
import io.vepo.jcode.events.TaskStartedEvent;
import io.vepo.jcode.events.WorkspaceOpenEvent;
import io.vepo.jcode.services.FileService;
import io.vepo.jcode.services.WorkspaceService;
import io.vepo.jcode.ui.UIManager;
import io.vepo.jcode.Workbench;
import javafx.application.Platform;

/**
 * Main application controller that coordinates between services and UI components.
 * Handles application-level logic and event routing.
 */
public class ApplicationController {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
    
    private final Workbench workbench;
    private final UIManager uiManager;
    private final FileService fileService;
    private final WorkspaceService workspaceService;
    
    public ApplicationController(Workbench workbench, UIManager uiManager) {
        this.workbench = workbench;
        this.uiManager = uiManager;
        this.fileService = new FileService(workbench);
        this.workspaceService = new WorkspaceService(workbench);
        
        setupEventHandlers();
    }
    
    /**
     * Sets up event handlers for the application.
     */
    private void setupEventHandlers() {
        workbench.subscribe(TaskStartedEvent.class, this::handleTaskStarted);
        workbench.subscribe(FileLoadEvent.class, this::handleFileLoad);
        workbench.subscribe(LoadedFileEvent.class, this::handleFileLoaded);
        workbench.subscribe(WorkspaceOpenEvent.class, this::handleWorkspaceOpen);
        workbench.subscribe(CloseWorkspaceEvent.class, this::handleWorkspaceClose);
    }
    
    /**
     * Initializes the application and attempts to restore the last workspace.
     */
    public void initialize() {
        logger.info("Initializing application...");
        
        // Try to restore last workspace
        Platform.runLater(() -> {
            if (!workspaceService.restoreLastWorkspace()) {
                logger.info("No valid workspace to restore, showing welcome screen");
            }
        });
    }
    
    /**
     * Handles task started events for progress tracking.
     */
    private void handleTaskStarted(TaskStartedEvent event) {
        uiManager.getProgressBar().progressProperty().bind(event.progress());
    }
    
    /**
     * Handles file load requests.
     */
    private void handleFileLoad(FileLoadEvent event) {
        logger.debug("Loading file: {}", event.file().getAbsolutePath());
        fileService.loadFile(event.file());
    }
    
    /**
     * Handles file loaded events.
     */
    private void handleFileLoaded(LoadedFileEvent event) {
        logger.debug("File loaded successfully: {}", event.file().getAbsolutePath());
        // The CodeEditor automatically handles LoadedFileEvent through its subscription
    }
    
    /**
     * Handles workspace open events.
     */
    private void handleWorkspaceOpen(WorkspaceOpenEvent event) {
        logger.info("Workspace opened: {}", event.workspace().getAbsolutePath());
        
        uiManager.updateWindowTitle(event.workspace().getAbsolutePath());
        uiManager.getCodeEditor().restoreOpenTabs();
        uiManager.showWorkspace();
    }
    
    /**
     * Handles workspace close events.
     */
    private void handleWorkspaceClose(CloseWorkspaceEvent event) {
        logger.info("Workspace closed");
        
        uiManager.updateWindowTitle(null);
        uiManager.showWelcomeScreen();
    }
    
    /**
     * Opens a workspace using the workspace service.
     * 
     * @param workspaceDir The workspace directory to open
     * @return true if the workspace was successfully opened
     */
    public boolean openWorkspace(File workspaceDir) {
        return workspaceService.openWorkspace(workspaceDir);
    }
    
    /**
     * Loads a file using the file service.
     * 
     * @param file The file to load
     */
    public void loadFile(File file) {
        if (fileService.isValidFile(file)) {
            fileService.loadFile(file);
        } else {
            logger.warn("Invalid file: {}", file.getAbsolutePath());
            uiManager.setStatusMessage("Invalid file: " + file.getName());
        }
    }
    
    /**
     * Gets the workspace service.
     * 
     * @return The workspace service instance
     */
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }
    
    /**
     * Gets the file service.
     * 
     * @return The file service instance
     */
    public FileService getFileService() {
        return fileService;
    }
    
    /**
     * Gets the UI manager.
     * 
     * @return The UI manager instance
     */
    public UIManager getUIManager() {
        return uiManager;
    }
} 