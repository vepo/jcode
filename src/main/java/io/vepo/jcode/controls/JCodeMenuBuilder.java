package io.vepo.jcode.controls;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.CloseWorkspaceEvent;
import io.vepo.jcode.events.SelectWorkspaceEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public interface JCodeMenuBuilder {
    public static MenuBar build(Workbench workbench) {
        var fileMenu = new Menu("File");
        var openItem = new MenuItem();
        openItem.setText("Open Workspace");
        openItem.setOnAction(evnt -> workbench.emit(new SelectWorkspaceEvent()));
        fileMenu.getItems().addAll(openItem);

        var closeItem = new MenuItem();
        closeItem.setText("Close Workspace");
        closeItem.setOnAction(evnt -> workbench.emit(new CloseWorkspaceEvent()));
        fileMenu.getItems().addAll(closeItem);
        return new MenuBar(fileMenu);
    }

}
