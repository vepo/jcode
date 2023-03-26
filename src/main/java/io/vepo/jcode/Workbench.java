package io.vepo.jcode;

import static io.vepo.jcode.preferences.JCodePreferencesFactory.preferences;
import static javafx.application.Platform.runLater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.vepo.jcode.events.Event;
import io.vepo.jcode.workspace.FileFilter;

public class Workbench {

    private HashMap<Class<? extends Event>, List<Consumer<? extends Event>>> eventSubscribers;

    public Workbench() {
        this.eventSubscribers = new HashMap<>();
    }

    public <T extends Event> void subscribe(Class<T> eventClass, Consumer<T> callback) {
        eventSubscribers.computeIfAbsent(eventClass, cls -> new ArrayList<>(1))
                        .add(callback);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void emit(T event) {
        eventSubscribers.computeIfAbsent(event.getClass(), cls -> new ArrayList<>(1))
                        .forEach(callback -> runLater(() -> ((Consumer<T>) callback).accept(event)));
    }

    public FileFilter fileFilter() {
        return new FileFilter(preferences().userRoot().getList("workspaceFilder"));
    }

}
