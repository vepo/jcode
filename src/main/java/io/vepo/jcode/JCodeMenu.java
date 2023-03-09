package io.vepo.jcode;

import java.io.File;

import io.vepo.jcode.events.FileLoadEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

public class JCodeMenu extends MenuBar {
    private static Menu createFileMenu(Workbench workbench) {
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem();
        openItem.setText("Open");
        openItem.setOnAction(evnt -> {
            FileChooser fileChooser = new FileChooser();
            // only allow text files to be selected using chooser
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java File (*.java)", "*.java"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java C (*.c)", "*.c"));
            // set initial directory somewhere user will recognise
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            // let user select file
            File fileToLoad = fileChooser.showOpenDialog(null);
            // if file has been chosen, load it using asynchronous method (define later)
            if (fileToLoad != null) {
                workbench.emit(new FileLoadEvent(fileToLoad));
            }
        });
        fileMenu.getItems().addAll(openItem);
        MenuItem saveItem = new MenuItem();
        saveItem.setText("Save");
        saveItem.setOnAction(evnt -> {
            // try {
            // try (FileWriter myWriter = new FileWriter(loadedFileReference)) {
            // myWriter.write(codeEditor.getTextContent());
            // lastModifiedTime = FileTime.fromMillis(System.currentTimeMillis() + 3000);
            // System.out.println("Successfully wrote to the file.");
            // }
            // } catch (IOException e) {
            // Logger.getLogger(getClass().getName()).log(SEVERE, null, e);
            // }
        });
        fileMenu.getItems().addAll(saveItem);

        MenuItem closeItem = new MenuItem();
        closeItem.setText("Close");
        fileMenu.getItems().addAll(closeItem);
        return fileMenu;
    }

    public JCodeMenu(Workbench workbench) {
        super(createFileMenu(workbench));
    }

}
