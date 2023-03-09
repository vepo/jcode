package io.vepo.jcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.vepo.jcode.events.Event;

public class Workbench {

    private HashMap<Class<? extends Event>, List<Consumer<? extends Event>>> eventSubscribers;

    public Workbench() {
        this.eventSubscribers = new HashMap<>();
    }

    public <T extends Event> void subscribe(Class<T> eventClass, Consumer<T> callback) {
        eventSubscribers.computeIfAbsent(eventClass, __ -> new ArrayList<>(1))
                        .add(callback);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void emit(T event) {
        eventSubscribers.computeIfAbsent(event.getClass(), __ -> new ArrayList<>(1))
                        .forEach(callback -> ((Consumer<T>) callback).accept(event));
    }

}
