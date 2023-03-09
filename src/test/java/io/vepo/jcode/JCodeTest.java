package io.vepo.jcode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class JCodeTest {
    private JCode code;

    @Start
    private void start(Stage stage) {
        code = new JCode();
        code.start(stage);
    }

    @Test
    void shouldStart(FxRobot robot) {
        FxAssert.verifyThat("#open-workspace", LabeledMatchers.hasText("Open"));
    }
}
