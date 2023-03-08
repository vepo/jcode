package io.vepo.jcode;

import static java.util.logging.Level.SEVERE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.fxmisc.richtext.CodeArea;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

// https://edencoding.com/how-to-open-edit-sync-and-save-a-text-file-in-javafx/
public class JCode extends Application {

    private CodeArea textArea;
    private ProgressBar progressBar;
    private Label statusMessage;
    private File loadedFileReference;
    private FileTime lastModifiedTime;

    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem();
        openItem.setText("Open");
        openItem.setOnAction(evnt -> {
            FileChooser fileChooser = new FileChooser();
            // only allow text files to be selected using chooser
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
            // set initial directory somewhere user will recognise
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            // let user select file
            File fileToLoad = fileChooser.showOpenDialog(null);
            // if file has been chosen, load it using asynchronous method (define later)
            if (fileToLoad != null) {
                loadFileToTextArea(fileToLoad);
            }
        });
        fileMenu.getItems().addAll(openItem);

        MenuItem saveItem = new MenuItem();
        saveItem.setText("Save");
        fileMenu.getItems().addAll(saveItem);

        MenuItem closeItem = new MenuItem();
        closeItem.setText("Close");
        fileMenu.getItems().addAll(closeItem);
        MenuBar menuBar = new MenuBar(fileMenu);

        pane.setTop(menuBar);
        Scene scene = new Scene(pane, 300, 250);

        textArea = new CodeArea();
        pane.setCenter(textArea);

        HBox rule = new HBox();
        progressBar = new ProgressBar();
        statusMessage = new Label("Checking for Changes...");
        rule.getChildren().add(new HBox(statusMessage, progressBar));

        // Definir a cena no palco
        primaryStage.setScene(scene);
        primaryStage.setTitle("jCode");
        primaryStage.show();
    }

    private Task<String> fileLoaderTask(File fileToLoad) {
        // Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                BufferedReader reader = new BufferedReader(new FileReader(fileToLoad));
                // Use Files.lines() to calculate total lines - used for progress
                long lineCount;
                try (Stream<String> stream = Files.lines(fileToLoad.toPath())) {
                    lineCount = stream.count();
                }
                // Load in all lines one by one into a StringBuilder separated by "\n" -
                // compatible with TextArea
                String line;
                StringBuilder totalFile = new StringBuilder();
                long linesLoaded = 0;
                while ((line = reader.readLine()) != null) {
                    totalFile.append(line);
                    totalFile.append("\n");
                    updateProgress(++linesLoaded, lineCount);
                }
                return totalFile.toString();
            }
        };
        // If successful, update the text area, display a success message and store the
        // loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                textArea.replaceText(loadFileTask.get());
                statusMessage.setText("File loaded: " + fileToLoad.getName());
                loadedFileReference = fileToLoad;
            } catch (InterruptedException | ExecutionException e) {
                Logger.getLogger(getClass().getName()).log(SEVERE, null, e);
                textArea.replaceText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            }
        });
        // If unsuccessful, set text area with error message and status message to
        // failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            textArea.replaceText("Could not load file from:\n " + fileToLoad.getAbsolutePath());
            statusMessage.setText("Failed to load file");
        });
        return loadFileTask;
    }

    private void loadFileToTextArea(File fileToLoad) {
        Task<String> loadFileTask = fileLoaderTask(fileToLoad);
        progressBar.progressProperty().bind(loadFileTask.progressProperty());
        loadFileTask.run();
    }

    public static void main(String[] args) {
        launch(args);
    }

}