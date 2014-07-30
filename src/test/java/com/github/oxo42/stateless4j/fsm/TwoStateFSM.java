/*
 * Copyright 2014 Fabien Renaud.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.oxo42.stateless4j.fsm;

import com.github.oxo42.stateless4j.StateMachine;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Fabien Renaud
 */
public class TwoStateFSM {

    enum State {

        A,
        B
    }

    enum Trigger {

        A_TO_B,
    }

    @Test
    public void test1() {
        StateMachine<State, Trigger> builder = new StateMachine<>(State.A);

        builder.configure(State.A)
                .permit(Trigger.A_TO_B, State.B);

        assertEquals(State.A, builder.getState());

        builder.fire(Trigger.A_TO_B);
        assertEquals(State.B, builder.getState());
    }
}
