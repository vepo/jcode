package io.vepo.jcode.controls;

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

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import io.vepo.jcode.Workbench;
import io.vepo.jcode.events.LoadedFileEvent;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class CodeEditor extends TabPane {

    private final Map<File, Tab> tabsIndex;

    public CodeEditor(Workbench workbench) {
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
}
