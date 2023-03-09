package io.vepo.jcode.events;

import java.io.File;

public record WorkspaceOpenEvent(File workspace) implements Event {

}
