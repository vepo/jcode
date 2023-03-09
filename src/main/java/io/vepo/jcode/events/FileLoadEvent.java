package io.vepo.jcode.events;

import java.io.File;

public record FileLoadEvent(File file) implements Event {

}
