package io.vepo.jcode.controls;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.CloseWorkspaceEvent;
import io.vepo.jcode.events.SelectWorkspaceEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public interface JCodeMenuBuilder {
    public static MenuBar build(Workbench workbench) {
        var fileMenu = new Menu("File");
        var openItem = new MenuItem();
        openItem.setText("Open Workspace");
        openItem.setOnAction(evnt -> workbench.emit(new SelectWorkspaceEvent()));
        fileMenu.getItems().addAll(openItem);

        var closeItem = new MenuItem();
        closeItem.setText("Close Workspace");
        closeItem.setOnAction(evnt -> {
            // User-initiated close: emit event to clear preferences
            workbench.emit(new CloseWorkspaceEvent());
        });
        fileMenu.getItems().addAll(closeItem);
        
        fileMenu.getItems().add(new SeparatorMenuItem());
        
        var clearLastWorkspaceItem = new MenuItem();
        clearLastWorkspaceItem.setText("Clear Last Workspace");
        clearLastWorkspaceItem.setOnAction(evnt -> {
            var workspacePreferences = preferences().userRoot().node("open-workspace");
            workspacePreferences.remove("last-workspace");
        });
        fileMenu.getItems().addAll(clearLastWorkspaceItem);
        
        return new MenuBar(fileMenu);
    }

}
