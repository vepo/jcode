package io.vepo.jcode.constants;

/**
 * Application constants for jCode.
 * Centralizes configuration values and constants used throughout the application.
 */
public final class AppConstants {
    
    private AppConstants() {
        // Prevent instantiation
    }
    
    // Application Information
    public static final String APP_NAME = "jCode";
    public static final String APP_VERSION = "1.0-SNAPSHOT";
    public static final String APP_DESCRIPTION = "Your lightweight JavaFX code editor";
    
    // Window Configuration
    public static final double DEFAULT_WINDOW_WIDTH_OFFSET = 50.0;
    public static final double DEFAULT_WINDOW_HEIGHT_OFFSET = 50.0;
    public static final boolean DEFAULT_MAXIMIZED = true;
    
    // Recent Workspaces
    public static final int MAX_RECENT_WORKSPACES = 10;
    
    // File Operations
    public static final String OPEN_TABS_KEY = "open-tabs";
    public static final String WORKSPACES_KEY = "workspaces";
    
    // Preferences Nodes
    public static final String WINDOW_PREFS_NODE = "window";
    public static final String EDITOR_PREFS_NODE = "editor";
    public static final String RECENT_WORKSPACES_NODE = "recentWorkspaces";
    public static final String WORKSPACE_PREFS_NODE = "workspace";
    
    // UI Configuration
    public static final double WELCOME_SCREEN_MAX_WIDTH = 600.0;
    public static final double RECENT_WORKSPACES_MAX_WIDTH = 400.0;
    public static final double RECENT_WORKSPACES_HEIGHT = 150.0;
    public static final double ACTION_BUTTON_WIDTH = 200.0;
    public static final double ACTION_BUTTON_HEIGHT = 40.0;
    
    // Spacing and Padding
    public static final double HEADER_PADDING = 40.0;
    public static final double CENTER_PADDING = 40.0;
    public static final double FOOTER_PADDING = 20.0;
    public static final double SECTION_SPACING = 30.0;
    public static final double COMPONENT_SPACING = 15.0;
    public static final double SMALL_SPACING = 10.0;
    
    // Colors (for reference - actual styling is in CSS)
    public static final String PRIMARY_COLOR = "#007acc";
    public static final String PRIMARY_HOVER_COLOR = "#005a9e";
    public static final String BACKGROUND_GRADIENT_START = "#2b2b2b";
    public static final String BACKGROUND_GRADIENT_END = "#1e1e1e";
    public static final String TEXT_COLOR_PRIMARY = "#ffffff";
    public static final String TEXT_COLOR_SECONDARY = "#cccccc";
    public static final String TEXT_COLOR_MUTED = "#888888";
} 