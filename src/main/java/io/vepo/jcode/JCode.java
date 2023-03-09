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

import io.vepo.jcode.controls.FixedSplitPane;
import io.vepo.jcode.events.FileLoadEvent;
import io.vepo.jcode.events.TaskStartedEvent;
import io.vepo.jcode.workspace.WorkspaceRoot;
import io.vepo.jcode.workspace.WorkspaceView;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// https://edencoding.com/how-to-open-edit-sync-and-save-a-text-file-in-javafx/
public class JCode extends Application {

    private ProgressBar progressBar;
    private Label statusMessage;
    private File loadedFileReference;
    private FileTime lastModifiedTime;
    private CodeEditor codeEditor;

    private Workbench workbench;
    private WorkspaceView workspace;

    public JCode() {
        workbench = new Workbench();
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        pane.setTop(new JCodeMenu(workbench));
        Scene scene = new Scene(pane, 300, 250);

        codeEditor = new CodeEditor();
        workspace = new WorkspaceView(workbench);
        pane.setCenter(new FixedSplitPane(workspace, codeEditor));

        HBox rule = new HBox();
        progressBar = new ProgressBar();
        statusMessage = new Label("Checking for Changes...");
        rule.getChildren().add(new HBox(statusMessage, progressBar));

        // Definir a cena no palco
        primaryStage.setScene(scene);
        primaryStage.setTitle("jCode");
        primaryStage.show();

        workbench.subscribe(TaskStartedEvent.class, this::progressReporter);
        workbench.subscribe(FileLoadEvent.class, this::loadFile);
    }

    private void loadFile(FileLoadEvent event) {
        // Create a task to load the file asynchronously
        Task<String> loadFileTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                try (BufferedReader reader = new BufferedReader(new FileReader(event.file()))) {
                    // Use Files.lines() to calculate total lines - used for progress
                    long lineCount;
                    try (Stream<String> stream = Files.lines(event.file().toPath())) {
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
            }
        };
        // If successful, update the text area, display a success message and store the
        // loaded file reference
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                codeEditor.setTextContent(loadFileTask.get());
                statusMessage.setText("File loaded: " + event.file().getName());
                loadedFileReference = event.file();
            } catch (InterruptedException | ExecutionException e) {
                Logger.getLogger(getClass().getName()).log(SEVERE, null, e);
                codeEditor.setTextContent("Could not load file from:\n " + event.file().getAbsolutePath());
            }
        });
        // If unsuccessful, set text area with error message and status message to
        // failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            codeEditor.setTextContent("Could not load file from:\n " + event.file().getAbsolutePath());
            statusMessage.setText("Failed to load file");
        });
        workbench.emit(new TaskStartedEvent(loadFileTask.progressProperty()));
        loadFileTask.run();
    }

    private void progressReporter(TaskStartedEvent event) {
        progressBar.progressProperty().bind(event.progress());
    }

    public static void main(String[] args) {
        launch(args);
    }

}
