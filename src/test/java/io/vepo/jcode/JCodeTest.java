package io.vepo.jcode;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static io.vepo.jcode.workspace.WorkspaceViewBuilder.FILE_DIALOG_KEY;
import static io.vepo.jcode.workspace.WorkspaceViewBuilder.OPEN_WORKSPACE_PREFERECE_KEY;
import static org.awaitility.Awaitility.await;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import io.vepo.jcode.events.CloseWorkspaceEvent;
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
    void shouldStart(FxRobot robot) throws InterruptedException {
        verifyThat("#open-workspace", hasText("Open"));
        robot.clickOn("#open-workspace");
        preferences().userRoot().node(OPEN_WORKSPACE_PREFERECE_KEY).put(FILE_DIALOG_KEY,
                                                                        Paths.get(".").toAbsolutePath().toString());
        robot.type(KeyCode.ENTER);
        await().until(() -> robot.lookup("#" + WorkspaceViewBuilder.ID).queryAll().size() == 1);

        code.workbench.emit(new CloseWorkspaceEvent());
        await().until(() -> robot.lookup("#" + WorkspaceViewBuilder.ID).queryAll().size() == 0);
    }

}
