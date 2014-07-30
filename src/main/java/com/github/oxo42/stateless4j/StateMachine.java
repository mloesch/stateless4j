package com.github.oxo42.stateless4j;


import com.github.oxo42.stateless4j.delegates.Action1;
import com.github.oxo42.stateless4j.delegates.Func;
import com.github.oxo42.stateless4j.delegates.Func2;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Models behaviour as transitions between a finite set of states
 *
 * @param <S>   The type used to represent the states
 * @param <T> The type used to represent the triggers that cause state transitions
 */
public class StateMachine<S, T> {

    protected final Map<S, StateRepresentation<S, T>> stateConfiguration = new HashMap<>();
    protected final Func<S> stateAccessor;
    protected final Action1<S> stateMutator;

    /**
     * Construct a state machine
     *
     * @param initialState The initial state
     */
    public StateMachine(S initialState) {
        final StateReference<S, T> reference = new StateReference<>();
        reference.setState(initialState);
        stateAccessor = new Func<S>() {
            @Override
            public S call() {
                return reference.getState();
            }
        };
        stateMutator = new Action1<S>() {
            @Override
            public void doIt(S s) {
                reference.setState(s);
            }
        };
    }

    /**
     * The current state
     *
     * @return The current state
     */
    public S getState() {
        return stateAccessor.call();
    }

    private void setState(S value) {
        stateMutator.doIt(value);
    }

    /**
     * The currently-permissible trigger values
     *
     * @return The currently-permissible trigger values
     */
    public List<T> getPermittedTriggers() {
        return getCurrentRepresentation().getPermittedTriggers();
    }

    StateRepresentation<S, T> getCurrentRepresentation() {
        return getRepresentation(getState());
    }

    protected StateRepresentation<S, T> getRepresentation(S state) {
        StateRepresentation<S, T> result = stateConfiguration.get(state);
        if (result == null) {
            result = new StateRepresentation<>(state);
            stateConfiguration.put(state, result);
        }

        return result;
    }

    /**
     * Begin configuration of the entry/exit actions and allowed transitions
     * when the state machine is in a particular state
     *
     * @param state The state to configure
     * @return A configuration object through which the state can be configured
     */
    public StateConfiguration<S, T> configure(S state) {
        return new StateConfiguration<>(getRepresentation(state), new Func2<S, StateRepresentation<S, T>>() {

            @Override
            public StateRepresentation<S, T> call(S arg0) {
                return getRepresentation(arg0);
            }
        });
    }

    /**
     * Transition from the current state via the specified trigger.
     * The target state is determined by the configuration of the current state.
     * Actions associated with leaving the current state and entering the new one
     * will be invoked
     *
     * @param trigger The trigger to fire
     */
    public boolean fire(T trigger) {
        Transition<S, T> transition = getCurrentRepresentation().getHandler(trigger);
        if (transition == null) {
            return false;
        }

        getCurrentRepresentation().exit();
        setState(transition.getDestination());
        getCurrentRepresentation().enter();
        return true;
    }

    /**
     * Determine if the state machine is in the supplied state
     *
     * @param state The state to test for
     * @return True if the current state is equal to, or a substate of, the supplied state
     */
    public boolean isInState(S state) {
        return getCurrentRepresentation().isIncludedIn(state);
    }

    /**
     * Returns true if {@code trigger} can be fired  in the current state
     *
     * @param trigger Trigger to test
     * @return True if the trigger can be fired, false otherwise
     */
    public boolean canFire(T trigger) {
        return getCurrentRepresentation().canHandle(trigger);
    }

    /**
     * A human-readable representation of the state machine
     *
     * @return A description of the current state and permitted triggers
     */
    @Override
    public String toString() {
        List<T> permittedTriggers = getPermittedTriggers();
        List<String> parameters = new ArrayList<>();

        for (T tTrigger : permittedTriggers) {
            parameters.add(tTrigger.toString());
        }

        StringBuilder params = new StringBuilder();
        String delim = "";
        for (String param : parameters) {
            params.append(delim);
            params.append(param);
            delim = ", ";
        }

        return String.format(
                "StateMachine {{ State = %s, PermittedTriggers = {{ %s }}}}",
                getState(),
                params.toString());
    }

    public void generateDotFileInto(final OutputStream dotFile) throws IOException {
        try (OutputStreamWriter w = new OutputStreamWriter(dotFile, "UTF-8")) {
            PrintWriter writer = new PrintWriter(w);
            writer.write("digraph G {\n");
            for (Entry<S, StateRepresentation<S, T>> entry : this.stateConfiguration.entrySet()) {
                Map<T, List<Transition<S, T>>> behaviours = entry.getValue().getTriggerBehaviours();
                for (Entry<T, List<Transition<S, T>>> behaviour : behaviours.entrySet()) {
                    for (Transition<S, T> transition : behaviour.getValue()) {
                        writer.write(String.format("\t%s -> %s;\n", entry.getKey(), transition.getDestination()));
                    }
                }
            }
            writer.write("}");
        }
    }
}
