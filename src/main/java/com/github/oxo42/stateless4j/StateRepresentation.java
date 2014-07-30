package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.Action;

import java.util.*;

public class StateRepresentation<S, T> {

    private final S state;

    private final Map<T, List<Transition<S, T>>> triggerBehaviours = new HashMap<>();
    private final List<Action> entryActions = new ArrayList<>();
    private final List<Action> exitActions = new ArrayList<>();
    private final List<StateRepresentation<S, T>> substates = new ArrayList<>();
    private StateRepresentation<S, T> superstate; // null

    public StateRepresentation(S state) {
        this.state = state;
    }

    protected Map<T, List<Transition<S, T>>> getTriggerBehaviours() {
        return triggerBehaviours;
    }

    public boolean canHandle(T trigger) {
        return getHandler(trigger) != null;
    }

    public Transition<S, T> getHandler(T trigger) {
        Transition result = tryFindLocalHandler(trigger);
        if (result == null && superstate != null) {
            result = superstate.getHandler(trigger);
        }
        return result;
    }

    Transition<S, T> tryFindLocalHandler(T trigger/*, out TriggerBehaviour handler*/) {
        List<Transition<S, T>> possible = triggerBehaviours.get(trigger);
        if (possible == null) {
            return null;
        }

        List<Transition<S, T>> actual = new ArrayList<>();
        for (Transition<S, T> triggerBehaviour : possible) {
            if (triggerBehaviour.canEnable()) {
                actual.add(triggerBehaviour);
            }
        }

        if (actual.size() > 1) {
            throw new IllegalStateException("Multiple permitted exit transitions are configured from state '" + trigger + "' for trigger '" + state + "'. Guard clauses must be mutually exclusive.");
        }

        return actual.get(0);
    }

    public void addEntryAction(Action action) {
        assert action != null : "action is null";
        entryActions.add(action);
    }

    public void addExitAction(Action action) {
        assert action != null : "action is null";
        exitActions.add(action);
    }

    void enter() {
        for (Action action : entryActions) {
            action.doIt();
        }
    }

    void exit() {
        for (Action action : exitActions) {
            action.doIt();
        }
    }

    public void addTransition(Transition<S, T> triggerBehaviour) {
        List<Transition<S, T>> allowed;
        if (!triggerBehaviours.containsKey(triggerBehaviour.getTrigger())) {
            allowed = new ArrayList<>();
            triggerBehaviours.put(triggerBehaviour.getTrigger(), allowed);
        }
        allowed = triggerBehaviours.get(triggerBehaviour.getTrigger());
        allowed.add(triggerBehaviour);
    }

    public StateRepresentation<S, T> getSuperstate() {
        return superstate;
    }

    public void setSuperstate(StateRepresentation<S, T> value) {
        superstate = value;
    }

    public S getUnderlyingState() {
        return state;
    }

    public void addSubstate(StateRepresentation<S, T> substate) {
        assert substate != null : "substate is null";
        substates.add(substate);
    }

    public boolean includes(S stateToCheck) {
        for (StateRepresentation<S, T> s : substates) {
            if (s.includes(stateToCheck)) {
                return true;
            }
        }
        return this.state.equals(stateToCheck);
    }

    public boolean isIncludedIn(S stateToCheck) {
        return this.state.equals(stateToCheck) || (superstate != null && superstate.isIncludedIn(stateToCheck));
    }

    @SuppressWarnings("unchecked")
    public List<T> getPermittedTriggers() {
        Set<T> result = new HashSet<>();

        for (T t : triggerBehaviours.keySet()) {
            for (Transition<S, T> v : triggerBehaviours.get(t)) {
                if (v.canEnable()) {
                    result.add(t);
                    break;
                }
            }
        }

        if (getSuperstate() != null) {
            result.addAll(getSuperstate().getPermittedTriggers());
        }

        return new ArrayList<>(result);
    }
}
