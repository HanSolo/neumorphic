/*
 * Copyright (c) 2021 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.neumorphic.event;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;


public abstract class NEvent extends Event {
    public static final EventType<NEvent> N_EVENT  = new EventType(ANY);

    public NEvent(final EventType<? extends NEvent> eventType) {
        super(eventType);
    }
    public NEvent(final Object source, final EventTarget target, final EventType<? extends NEvent> eventType) {
        super(source, target, eventType);
    }

    public abstract void invokeHandler(final EventHandler handler);
}
