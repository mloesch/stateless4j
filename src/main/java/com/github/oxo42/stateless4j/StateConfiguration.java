package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.*;

public class StateConfiguration<S, T> {

    private final StateRepresentation<S, T> representation;
    private final Func2<S, StateRepresentation<S, T>> lookup;

    public StateConfiguration(final StateRepresentation<S, T> representation, final Func2<S, StateRepresentation<S, T>> lookup) {
        assert representation != null : "representation is null";
        assert lookup != null : "lookup is null";
        this.representation = representation;
        this.lookup = lookup;
    }

    public StateConfiguration<S, T> permit(T trigger, S destinationState) {
        enforceNotIdentityTransition(destinationState);
        return externalTransition(destinationState, trigger);
    }

    public StateConfiguration<S, T> permitIf(T trigger, S destinationState, FuncBoolean guard) {
        enforceNotIdentityTransition(destinationState);
        return externalTransition(destinationState, trigger, guard);
    }

    public StateConfiguration<S, T> permitReentry(T trigger) {
        return externalTransition(representation.getUnderlyingState(), trigger);
    }

    public StateConfiguration<S, T> permitReentryIf(T trigger, FuncBoolean guard) {
        return externalTransition(representation.getUnderlyingState(), trigger, guard);
    }

    public StateConfiguration<S, T> externalTransition(S destinationState) {
        return externalTransition(destinationState, null);
    }

    public StateConfiguration<S, T> externalTransition(S destinationState, T trigger) {
        return externalTransition(destinationState, trigger, null);
    }

    public StateConfiguration<S, T> externalTransition(S destinationState, T trigger, FuncBoolean guard) {
        return externalTransition(destinationState, trigger, guard, null);
    }

    public StateConfiguration<S, T> externalTransition(S destinationState, T trigger, FuncBoolean guard, Behavior behavior) {
        representation.addTransition(new Transition<>(representation.getUnderlyingState(), destinationState, trigger, guard, behavior));
        return this;
    }

    /**
     * Specify an action that will execute when transitioning into the configured state
     *
     * @param entryAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<S, T> onEntry(final Action entryAction) {
        assert entryAction != null : "entryAction is null";
        representation.addEntryAction(entryAction);
        return this;
    }

    /**
     * Specify an action that will execute when transitioning from the configured state
     *
     * @param exitAction Action to execute
     * @return The receiver
     */
    public StateConfiguration<S, T> onExit(Action exitAction) {
        assert exitAction != null : "exitAction is null";
        representation.addExitAction(exitAction);
        return this;
    }

    /**
     * Sets the superstate that the configured state is a substate of
     * <p>
     * Substates inherit the allowed transitions of their superstate.
     * When entering directly into a substate from outside of the superstate,
     * entry actions for the superstate are executed.
     * Likewise when leaving from the substate to outside the supserstate,
     * exit actions for the superstate will execute.
     *
     * @param superstate The superstate
     * @return The receiver
     */
    public StateConfiguration<S, T> substateOf(S superstate) {
        StateRepresentation<S, T> superRepresentation = lookup.call(superstate);
        representation.setSuperstate(superRepresentation);
        superRepresentation.addSubstate(representation);
        return this;
    }

    void enforceNotIdentityTransition(S destination) {
        if (destination.equals(representation.getUnderlyingState())) {
            throw new IllegalStateException("Permit() (and PermitIf()) require that the destination state is not equal to the source state. To accept a trigger without changing state, use either Ignore() or PermitReentry().");
        }
    }
}
