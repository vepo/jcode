package io.vepo.jcode.events;

import java.io.File;

public record LoadedFileEvent(File file, String content) implements Event {

}
