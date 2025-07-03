package io.vepo.jcode.services;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vepo.jcode.events.WorkspaceOpenEvent;
import io.vepo.jcode.Workbench;

/**
 * Service responsible for workspace operations like opening, closing, and managing recent workspaces.
 * Centralizes workspace-related operations to improve code organization.
 */
public class WorkspaceService {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkspaceService.class);
    private final Workbench workbench;
    
    public WorkspaceService(Workbench workbench) {
        this.workbench = workbench;
    }
    
    /**
     * Opens a workspace and emits the appropriate event.
     * 
     * @param workspaceDir The workspace directory to open
     * @return true if the workspace was successfully opened
     */
    public boolean openWorkspace(File workspaceDir) {
        if (!isValidWorkspace(workspaceDir)) {
            logger.warn("Invalid workspace directory: {}", workspaceDir.getAbsolutePath());
            return false;
        }
        
        try {
            workbench.emit(new WorkspaceOpenEvent(workspaceDir));
            addToRecentWorkspaces(workspaceDir.getAbsolutePath());
            logger.info("Workspace opened successfully: {}", workspaceDir.getAbsolutePath());
            return true;
        } catch (Exception e) {
            logger.error("Failed to open workspace: {}", workspaceDir.getAbsolutePath(), e);
            return false;
        }
    }
    
    /**
     * Validates if a directory is a valid workspace.
     * 
     * @param workspaceDir The directory to validate
     * @return true if the directory is a valid workspace
     */
    public boolean isValidWorkspace(File workspaceDir) {
        return workspaceDir != null && 
               workspaceDir.exists() && 
               workspaceDir.isDirectory() && 
               workspaceDir.canRead();
    }
    
    /**
     * Gets the list of recent workspaces.
     * 
     * @return List of recent workspace paths
     */
    public List<String> getRecentWorkspaces() {
        var prefs = (io.vepo.jcode.preferences.JsonPreferences) preferences().userRoot().node("recentWorkspaces");
        return prefs.getList("workspaces");
    }
    
    /**
     * Gets the most recently opened workspace.
     * 
     * @return The path of the most recent workspace, or null if none exists
     */
    public String getLastWorkspace() {
        List<String> recent = getRecentWorkspaces();
        return recent != null && !recent.isEmpty() ? recent.get(0) : null;
    }
    
    /**
     * Adds a workspace path to the recent workspaces list.
     * 
     * @param workspacePath The workspace path to add
     */
    public void addToRecentWorkspaces(String workspacePath) {
        var prefs = (io.vepo.jcode.preferences.JsonPreferences) preferences().userRoot().node("recentWorkspaces");
        List<String> recent = prefs.getList("workspaces");
        
        // Remove if already exists
        if (recent != null) {
            recent.remove(workspacePath);
        } else {
            recent = new java.util.ArrayList<>();
        }
        
        // Add to beginning
        recent.add(0, workspacePath);
        
        // Keep only last 10
        if (recent.size() > 10) {
            recent = recent.subList(0, 10);
        }
        
        prefs.putList("workspaces", recent);
    }
    
    /**
     * Removes a workspace from the recent workspaces list.
     * 
     * @param workspacePath The workspace path to remove
     */
    public void removeFromRecentWorkspaces(String workspacePath) {
        var prefs = (io.vepo.jcode.preferences.JsonPreferences) preferences().userRoot().node("recentWorkspaces");
        List<String> recent = prefs.getList("workspaces");
        
        if (recent != null) {
            recent.remove(workspacePath);
            prefs.putList("workspaces", recent);
        }
    }
    
    /**
     * Clears all recent workspaces.
     */
    public void clearRecentWorkspaces() {
        var prefs = (io.vepo.jcode.preferences.JsonPreferences) preferences().userRoot().node("recentWorkspaces");
        prefs.putList("workspaces", new java.util.ArrayList<>());
    }
    
    /**
     * Attempts to restore the last opened workspace.
     * 
     * @return true if a workspace was successfully restored
     */
    public boolean restoreLastWorkspace() {
        String lastWorkspace = getLastWorkspace();
        if (lastWorkspace != null) {
            File workspaceDir = new File(lastWorkspace);
            if (isValidWorkspace(workspaceDir)) {
                return openWorkspace(workspaceDir);
            } else {
                // Remove invalid workspace from recent list
                removeFromRecentWorkspaces(lastWorkspace);
                logger.warn("Removed invalid workspace from recent list: {}", lastWorkspace);
            }
        }
        return false;
    }
} 