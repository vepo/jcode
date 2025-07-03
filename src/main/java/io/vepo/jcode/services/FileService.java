package io.vepo.jcode.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vepo.jcode.events.LoadedFileEvent;
import io.vepo.jcode.events.TaskStartedEvent;
import io.vepo.jcode.Workbench;
import javafx.concurrent.Task;

/**
 * Service responsible for file operations like loading and processing files.
 * Centralizes file-related operations to improve code organization.
 */
public class FileService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final Workbench workbench;
    
    public FileService(Workbench workbench) {
        this.workbench = workbench;
    }
    
    /**
     * Loads a file asynchronously and emits events for progress tracking and completion.
     * 
     * @param file The file to load
     */
    public void loadFile(File file) {
        Task<String> loadFileTask = createLoadFileTask(file);
        
        loadFileTask.setOnSucceeded(workerStateEvent -> {
            try {
                workbench.emit(new LoadedFileEvent(file, loadFileTask.get()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("File loading was interrupted", e);
            } catch (ExecutionException e) {
                logger.error("Failed to load file: {}", file.getAbsolutePath(), e);
            }
        });
        
        loadFileTask.setOnFailed(workerStateEvent -> {
            logger.error("File loading task failed for: {}", file.getAbsolutePath());
        });
        
        // Emit task started event for progress tracking
        workbench.emit(new TaskStartedEvent(loadFileTask.progressProperty()));
        
        // Start the task
        loadFileTask.run();
    }
    
    /**
     * Creates a Task for loading a file with progress tracking.
     * 
     * @param file The file to load
     * @return A Task that loads the file content
     */
    private Task<String> createLoadFileTask(File file) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    // Calculate total lines for progress tracking
                    long lineCount;
                    try (Stream<String> stream = Files.lines(file.toPath())) {
                        lineCount = stream.count();
                    }
                    
                    // Load file content line by line
                    StringBuilder totalFile = new StringBuilder();
                    long linesLoaded = 0;
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        totalFile.append(line);
                        totalFile.append("\n");
                        updateProgress(++linesLoaded, lineCount);
                    }
                    
                    return totalFile.toString();
                }
            }
        };
    }
    
    /**
     * Validates if a file can be loaded.
     * 
     * @param file The file to validate
     * @return true if the file is valid and can be loaded
     */
    public boolean isValidFile(File file) {
        return file != null && file.exists() && file.isFile() && file.canRead();
    }
    
    /**
     * Gets the file extension from a file path.
     * 
     * @param file The file to get extension from
     * @return The file extension (without dot) or empty string if no extension
     */
    public String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }
} 