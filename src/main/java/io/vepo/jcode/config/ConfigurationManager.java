package io.vepo.jcode.config;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;

import java.util.List;
import java.util.prefs.Preferences;

import io.vepo.jcode.constants.AppConstants;

/**
 * Manages application configuration and preferences.
 * Provides a centralized way to access and modify application settings.
 */
public class ConfigurationManager {
    
    private static ConfigurationManager instance;
    private final Preferences rootPreferences;
    
    private ConfigurationManager() {
        this.rootPreferences = preferences().userRoot();
    }
    
    /**
     * Gets the singleton instance of ConfigurationManager.
     * 
     * @return The ConfigurationManager instance
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    // Window Configuration
    
    /**
     * Gets the window preferences node.
     * 
     * @return Window preferences
     */
    public Preferences getWindowPreferences() {
        return rootPreferences.node(AppConstants.WINDOW_PREFS_NODE);
    }
    
    /**
     * Gets the window X position.
     * 
     * @return Window X position or Double.NEGATIVE_INFINITY if not set
     */
    public double getWindowX() {
        return getWindowPreferences().getDouble("x", Double.NEGATIVE_INFINITY);
    }
    
    /**
     * Sets the window X position.
     * 
     * @param x The X position
     */
    public void setWindowX(double x) {
        getWindowPreferences().putDouble("x", x);
    }
    
    /**
     * Gets the window Y position.
     * 
     * @return Window Y position or Double.NEGATIVE_INFINITY if not set
     */
    public double getWindowY() {
        return getWindowPreferences().getDouble("y", Double.NEGATIVE_INFINITY);
    }
    
    /**
     * Sets the window Y position.
     * 
     * @param y The Y position
     */
    public void setWindowY(double y) {
        getWindowPreferences().putDouble("y", y);
    }
    
    /**
     * Gets the window width.
     * 
     * @param defaultWidth Default width if not set
     * @return Window width
     */
    public double getWindowWidth(double defaultWidth) {
        return getWindowPreferences().getDouble("width", defaultWidth);
    }
    
    /**
     * Sets the window width.
     * 
     * @param width The width
     */
    public void setWindowWidth(double width) {
        getWindowPreferences().putDouble("width", width);
    }
    
    /**
     * Gets the window height.
     * 
     * @param defaultHeight Default height if not set
     * @return Window height
     */
    public double getWindowHeight(double defaultHeight) {
        return getWindowPreferences().getDouble("height", defaultHeight);
    }
    
    /**
     * Sets the window height.
     * 
     * @param height The height
     */
    public void setWindowHeight(double height) {
        getWindowPreferences().putDouble("height", height);
    }
    
    /**
     * Checks if the window is maximized.
     * 
     * @return true if maximized
     */
    public boolean isWindowMaximized() {
        return getWindowPreferences().getBoolean("maximized", AppConstants.DEFAULT_MAXIMIZED);
    }
    
    /**
     * Sets the window maximized state.
     * 
     * @param maximized The maximized state
     */
    public void setWindowMaximized(boolean maximized) {
        getWindowPreferences().putBoolean("maximized", maximized);
    }
    
    // Editor Configuration
    
    /**
     * Gets the editor preferences node.
     * 
     * @return Editor preferences
     */
    public Preferences getEditorPreferences() {
        return rootPreferences.node(AppConstants.EDITOR_PREFS_NODE);
    }
    
    /**
     * Gets the open tabs configuration.
     * 
     * @return Open tabs string
     */
    public String getOpenTabs() {
        return getEditorPreferences().get(AppConstants.OPEN_TABS_KEY, "");
    }
    
    /**
     * Sets the open tabs configuration.
     * 
     * @param openTabs The open tabs string
     */
    public void setOpenTabs(String openTabs) {
        getEditorPreferences().put(AppConstants.OPEN_TABS_KEY, openTabs);
    }
    
    // Recent Workspaces Configuration
    
    /**
     * Gets the recent workspaces preferences node.
     * 
     * @return Recent workspaces preferences
     */
    public Preferences getRecentWorkspacesPreferences() {
        return rootPreferences.node(AppConstants.RECENT_WORKSPACES_NODE);
    }
    
    /**
     * Gets the list of recent workspaces.
     * 
     * @return List of recent workspace paths
     */
    public List<String> getRecentWorkspaces() {
        var prefs = (io.vepo.jcode.preferences.JsonPreferences) getRecentWorkspacesPreferences();
        return prefs.getList(AppConstants.WORKSPACES_KEY);
    }
    
    /**
     * Sets the list of recent workspaces.
     * 
     * @param workspaces The list of workspace paths
     */
    public void setRecentWorkspaces(List<String> workspaces) {
        var prefs = (io.vepo.jcode.preferences.JsonPreferences) getRecentWorkspacesPreferences();
        prefs.putList(AppConstants.WORKSPACES_KEY, workspaces);
    }
    
    // Workspace Configuration
    
    /**
     * Gets the workspace preferences node.
     * 
     * @return Workspace preferences
     */
    public Preferences getWorkspacePreferences() {
        return getWindowPreferences().node(AppConstants.WORKSPACE_PREFS_NODE);
    }
    
    /**
     * Gets the workspace width.
     * 
     * @return Workspace width or Double.NEGATIVE_INFINITY if not set
     */
    public double getWorkspaceWidth() {
        return getWorkspacePreferences().getDouble("width", Double.NEGATIVE_INFINITY);
    }
    
    /**
     * Sets the workspace width.
     * 
     * @param width The workspace width
     */
    public void setWorkspaceWidth(double width) {
        getWorkspacePreferences().putDouble("width", width);
    }
} 