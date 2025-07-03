package io.vepo.jcode.controls;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static io.vepo.jcode.utils.FileId.idFromFile;
import static java.util.stream.Collectors.toList;
import static javafx.scene.layout.AnchorPane.setBottomAnchor;
import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setRightAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.FileLoadEvent;
import io.vepo.jcode.events.LoadedFileEvent;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class CodeEditor extends TabPane {

    private final Map<File, Tab> tabsIndex;
    private final Workbench workbench;
    private static final String OPEN_TABS_KEY = "open-tabs";

    public CodeEditor(Workbench workbench) {
        this.workbench = workbench;
        tabsIndex = new HashMap<>();
        workbench.subscribe(LoadedFileEvent.class, this::createEditor);
        getTabs().addListener(new ListChangeListener<Tab>() {
            @Override
            public void onChanged(Change<? extends Tab> c) {
                while (c.next()) {
                    if (c.wasRemoved()) {
                        c.getRemoved()
                         .forEach(tab -> {
                             tabsIndex.entrySet()
                                      .stream()
                                      .filter(entry -> entry.getValue() == tab)
                                      .map(Entry::getKey)
                                      .collect(toList())
                                      .forEach(tabsIndex::remove);
                         });
                    }
                }
            }
        });
    }

    private void createEditor(LoadedFileEvent event) {
        if (!tabsIndex.containsKey(event.file())) {
            var codeArea = new CodeArea(event.content());
            var pane = new VirtualizedScrollPane<CodeArea>(codeArea);
            
            // Configure syntax highlighting based on file extension
            HighlighterFactory.getHighlighterForFile(event.file().getName())
                .ifPresentOrElse(
                    highlighter -> highlighter.configureCodeArea(codeArea),
                    () -> SyntaxHighlighter.configureCodeArea(codeArea) // Default Java highlighting
                );
            
            // Move caret to the first line
            codeArea.moveTo(0);
            
            // Scroll to the top to show the first line
            codeArea.showParagraphAtTop(0);
            
            pane.getContent().setWrapText(true);
            pane.getContent().setId("codeArea-" + idFromFile(event.file()));
            pane.getContent().setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

            var anchorPane = new AnchorPane();
            anchorPane.getChildren().add(pane);
            setLeftAnchor(pane, 0.0);
            setRightAnchor(pane, 0.0);
            setBottomAnchor(pane, 0.0);
            setTopAnchor(pane, 0.0);
            pane.getContent().prefWidthProperty().bind(anchorPane.widthProperty());
            pane.getContent().prefHeightProperty().bind(anchorPane.heightProperty());

            var tab = new Tab(event.file().getName());
            tab.setContent(anchorPane);
            getTabs().add(tab);
            tabsIndex.put(event.file(), tab);
        }
        getSelectionModel().select(tabsIndex.get(event.file()));
    }
    
    public void saveOpenTabs() {
        Preferences editorPrefs = preferences().userRoot().node("editor");
        String openTabs = tabsIndex.keySet().stream()
                .map(File::getAbsolutePath)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ";" + b);
        editorPrefs.put(OPEN_TABS_KEY, openTabs);
    }
    
    public void restoreOpenTabs() {
        Preferences editorPrefs = preferences().userRoot().node("editor");
        String openTabs = editorPrefs.get(OPEN_TABS_KEY, "");
        if (!openTabs.isEmpty()) {
            String[] filePaths = openTabs.split(";");
            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    workbench.emit(new FileLoadEvent(file));
                }
            }
        }
    }
}
