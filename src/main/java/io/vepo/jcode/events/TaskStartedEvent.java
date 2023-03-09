package io.vepo.jcode.events;

import javafx.beans.property.ReadOnlyDoubleProperty;

public record TaskStartedEvent(ReadOnlyDoubleProperty progress) implements Event {

}
