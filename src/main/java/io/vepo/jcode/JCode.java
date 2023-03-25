package io.vepo.jcode;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import io.vepo.jcode.controls.CodeEditor;
import io.vepo.jcode.controls.FixedSplitPaneBuilder;
import io.vepo.jcode.controls.JCodeMenuBuilder;
import io.vepo.jcode.events.FileLoadEvent;
import io.vepo.jcode.events.TaskStartedEvent;
import io.vepo.jcode.workspace.WorkspaceViewBuilder;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

// https://edencoding.com/how-to-open-edit-sync-and-save-a-text-file-in-javafx/
public class JCode extends Application {

    private ProgressBar progressBar;
    private Label statusMessage;
    private CodeEditor codeEditor;

    Workbench workbench;
    private StackPane workspace;

    public JCode() {
        workbench = new Workbench();
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane pane = new BorderPane();
        pane.setTop(JCodeMenuBuilder.build(workbench));

        Preferences windowPrefs = preferences().userRoot().node("window");
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        double x = windowPrefs.getDouble("x", Double.NEGATIVE_INFINITY);
        double y = windowPrefs.getDouble("y", Double.NEGATIVE_INFINITY);
        if (x > 0) {
            primaryStage.setX(x);
        }

        if (y > 0) {
            primaryStage.setY(y);
        }

        Scene scene = new Scene(pane,
                                windowPrefs.getDouble("width", screenBounds.getWidth() - 50),
                                windowPrefs.getDouble("height", screenBounds.getHeight() - 50));
        primaryStage.setMaximized(windowPrefs.getBoolean("maximized", true));

        primaryStage.maximizedProperty()
                    .addListener((observable, oldValue, newValue) -> windowPrefs.putBoolean("maximized", newValue));
        primaryStage.xProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (!primaryStage.isMaximized()) {
                            windowPrefs.putDouble("x", newValue.doubleValue());
                        }
                    });
        primaryStage.yProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (!primaryStage.isMaximized()) {
                            windowPrefs.putDouble("y", newValue.doubleValue());
                        }
                    });
        scene.widthProperty()
             .addListener((observable, oldValue, newValue) -> {
                 if (!primaryStage.isMaximized()) {
                     windowPrefs.putDouble("width", newValue.doubleValue());
                 }
             });
        scene.heightProperty()
             .addListener((observable, oldValue, newValue) -> {
                 if (!primaryStage.isMaximized()) {
                     windowPrefs.putDouble("height", newValue.doubleValue());
                 }
             });

        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(scene);

        codeEditor = new CodeEditor();
        workspace = WorkspaceViewBuilder.build(workbench);
        pane.setCenter(FixedSplitPaneBuilder.build(workspace, codeEditor));

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
            } catch (ExecutionException e) {
                codeEditor.setTextContent(String.format("Could not load file from: %s ",
                                                        event.file().getAbsolutePath()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        // If unsuccessful, set text area with error message and status message to
        // failed
        loadFileTask.setOnFailed(workerStateEvent -> {
            codeEditor.setTextContent(String.format("Could not load file from: %s", event.file().getAbsolutePath()));
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
