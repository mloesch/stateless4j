package com.github.oxo42.stateless4j;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TransitionTests {

    @Test
    public void testIsReentry() {
        Transition<Integer, Integer> t = new Transition<>(1, 1, 0);
        assertTrue(t.isReentry());

        t = new Transition<>(1, 2, 0);
        assertFalse(t.isReentry());
    }

    @Test
    public void testConstructors() {
        Behavior b = new Behavior() {
        };

        Transition<State, Trigger> t = new Transition<>(State.A, State.B, Trigger.X, GuardTrue.INSTANCE, b);
        assertEquals(State.A, t.getSource());
        assertEquals(State.B, t.getDestination());
        assertEquals(Trigger.X, t.getTrigger());
        assertTrue(t.canEnable());
        assertEquals(b, t.getBehavior());

        t = new Transition<>(State.B, State.C, Trigger.Y, GuardFalse.INSTANCE, b);
        assertEquals(State.B, t.getSource());
        assertEquals(State.C, t.getDestination());
        assertEquals(Trigger.Y, t.getTrigger());
        assertFalse(t.canEnable());
        assertEquals(b, t.getBehavior());

        t = new Transition<>(State.B, State.C, null, null, null);
        assertEquals(State.B, t.getSource());
        assertEquals(State.C, t.getDestination());
        assertNull(t.getTrigger());
        assertTrue(t.canEnable());
        assertNull(t.getBehavior());

        t = new Transition<>(State.B, State.C, null, null);
        assertEquals(State.B, t.getSource());
        assertEquals(State.C, t.getDestination());
        assertNull(t.getTrigger());
        assertTrue(t.canEnable());
        assertNull(t.getBehavior());

        t = new Transition<>(State.A, State.C, null);
        assertEquals(State.A, t.getSource());
        assertEquals(State.C, t.getDestination());
        assertNull(t.getTrigger());
        assertTrue(t.canEnable());
        assertNull(t.getBehavior());

        t = new Transition<>(State.C, State.A);
        assertEquals(State.C, t.getSource());
        assertEquals(State.A, t.getDestination());
        assertNull(t.getTrigger());
        assertTrue(t.canEnable());
        assertNull(t.getBehavior());
    }
}
