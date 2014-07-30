package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;

public final class Transition<S, T> {

    private final S source;
    private final S destination;
    private final T trigger;
    private final FuncBoolean guard;
    private final Behavior behavior;

    Transition(S source, S destination, T trigger, FuncBoolean guard, Behavior behavior) {
        this.source = source;
        this.destination = destination;
        this.trigger = trigger;
        this.guard = guard;
        this.behavior = behavior;
    }

    Transition(S source, S destination, T trigger, FuncBoolean guard) {
        this(source, destination, trigger, guard, null);
    }

    Transition(S source, S destination, T trigger) {
        this(source, destination, trigger, null);
    }

    Transition(S source, S destination) {
        this(source, destination, null);
    }

    public S getSource() {
        return source;
    }

    public S getDestination() {
        return destination;
    }

    public T getTrigger() {
        return trigger;
    }

    public boolean canEnable() {
        return guard == null ? true : guard.call();
    }
    
    public Behavior getBehavior() {
        return behavior;
    }

    public boolean isReentry() {
        return source.equals(destination);
    }

}
