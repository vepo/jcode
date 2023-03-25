package io.vepo.jcode;

import io.vepo.jcode.events.CloseWorkspaceEvent;
import io.vepo.jcode.events.SelectWorkspaceEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class JCodeMenu extends MenuBar {
    private static Menu createFileMenu(Workbench workbench) {
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem();
        openItem.setText("Open Workspace");
        openItem.setOnAction(evnt -> workbench.emit(new SelectWorkspaceEvent()));
        fileMenu.getItems().addAll(openItem);

        MenuItem closeItem = new MenuItem();
        closeItem.setText("Close Workspace");
        closeItem.setOnAction(evnt -> workbench.emit(new CloseWorkspaceEvent()));
        fileMenu.getItems().addAll(closeItem);
        return fileMenu;
    }

    public JCodeMenu(Workbench workbench) {
        super(createFileMenu(workbench));
    }

}
