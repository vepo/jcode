package io.vepo.jcode;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static io.vepo.jcode.utils.FileId.idFromFile;
import static io.vepo.jcode.workspace.WorkspaceViewBuilder.FILE_DIALOG_KEY;
import static io.vepo.jcode.workspace.WorkspaceViewBuilder.OPEN_WORKSPACE_PREFERECE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import org.fxmisc.richtext.CodeArea;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import io.vepo.jcode.events.CloseWorkspaceEvent;
import io.vepo.jcode.events.FileLoadEvent;
import io.vepo.jcode.workspace.WorkspaceViewBuilder;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class JCodeTest {
    private JCode code;

    @Start
    private void start(Stage stage) {
        code = new JCode();
        code.start(stage);
    }

    @Test
    void shouldStart(FxRobot robot) throws IOException {
        verifyThat("#open-workspace", hasText("Open"));
        robot.clickOn("#open-workspace");
        preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY).put(FILE_DIALOG_KEY,
                                                                        Paths.get(".").toAbsolutePath().toString());
        robot.type(KeyCode.ENTER);
        await().until(() -> robot.lookup("#" + WorkspaceViewBuilder.ID).queryAll().size() == 1);
        File file = find(Paths.get(".").toFile(), ".*\\.java");
        code.workbench.emit(new FileLoadEvent(file));
        CodeArea editor = robot.lookup("#codeArea-" + idFromFile(file))
                               .queryAs(CodeArea.class);
        await().until(() -> !editor.getContent().getText().isEmpty());
        assertThat(editor.getContent()
                         .getText()).isEqualToIgnoringNewLines(new String(Files.readAllBytes(file.toPath())));
        code.workbench.emit(new CloseWorkspaceEvent());
        await().until(() -> robot.lookup("#" + WorkspaceViewBuilder.ID).queryAll().size() == 0);
    }

    private File find(File file, String pattern) {
        if (file.isFile() && file.getName().matches(pattern)) {
            return file;
        } else if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                File found = find(f, pattern);
                if (Objects.nonNull(found)) {
                    return found;
                }
            }
        }
        return null;
    }

}
