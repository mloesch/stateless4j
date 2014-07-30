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
package com.github.oxo42.stateless4j;

import com.github.oxo42.stateless4j.delegates.FuncBoolean;

/**
 *
 * @author Fabien Renaud
 */
public final class GuardFalse implements FuncBoolean {

    public static final GuardFalse INSTANCE = new GuardFalse();

    private GuardFalse() {
    }

    @Override
    public boolean call() {
        return false;
    }

}
