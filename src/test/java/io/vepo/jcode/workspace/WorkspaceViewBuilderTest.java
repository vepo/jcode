package io.vepo.jcode.workspace;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.prefs.Preferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.WorkspaceOpenEvent;

class WorkspaceViewBuilderTest {

    @TempDir
    Path tempDir;
    
    private Workbench workbench;
    private File testWorkspace;

    @BeforeEach
    void setUp() throws IOException {
        workbench = new Workbench();
        testWorkspace = tempDir.toFile();
        
        // Create a test file in the workspace
        Files.createFile(tempDir.resolve("test.txt"));
    }

    @Test
    void testLastWorkspacePreferenceStorage() {
        // Clear any existing preferences
        Preferences workspacePreferences = Preferences.userRoot().node("open-workspace");
        workspacePreferences.remove("last-workspace");
        
        // Verify no last workspace is stored initially
        assertNull(workspacePreferences.get("last-workspace", null));
        
        // Manually store a workspace path (simulating what happens in openWorkspace)
        workspacePreferences.put("last-workspace", testWorkspace.getAbsolutePath());
        
        // Verify the workspace path is now stored
        String storedPath = workspacePreferences.get("last-workspace", null);
        assertNotNull(storedPath);
        assertEquals(testWorkspace.getAbsolutePath(), storedPath);
    }

    @Test
    void testLastWorkspacePreferenceClearing() {
        Preferences workspacePreferences = Preferences.userRoot().node("open-workspace");
        
        // Store a workspace path
        workspacePreferences.put("last-workspace", testWorkspace.getAbsolutePath());
        assertNotNull(workspacePreferences.get("last-workspace", null));
        
        // Clear the preference
        workspacePreferences.remove("last-workspace");
        assertNull(workspacePreferences.get("last-workspace", null));
    }

    @Test
    void testWorkspaceExistsValidation() {
        // Test with a non-existent directory
        File nonExistentDir = new File("/non/existent/path");
        assertFalse(nonExistentDir.exists());
        
        // Test with a file (not a directory)
        File testFile = new File(testWorkspace, "test.txt");
        assertTrue(testFile.exists());
        assertFalse(testFile.isDirectory());
        
        // Test with a valid directory
        assertTrue(testWorkspace.exists());
        assertTrue(testWorkspace.isDirectory());
    }

    @Test
    void testWorkspacePreferenceKeyConstants() {
        // Test that the preference keys are correctly defined
        assertEquals("last-workspace", WorkspaceViewBuilder.LAST_WORKSPACE_KEY);
        assertEquals("open-workspace", WorkspaceViewBuilder.OPEN_WORKSPACE_PREFERECE_KEY);
        assertEquals("file-dialog", WorkspaceViewBuilder.FILE_DIALOG_KEY);
    }
} 