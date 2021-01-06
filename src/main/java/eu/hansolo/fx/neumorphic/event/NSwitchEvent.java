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

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;


public class NSwitchEvent extends NEvent {
    public static final EventType<NEvent> ON  = new EventType(N_EVENT, "ON");
    public static final EventType<NEvent> OFF = new EventType(N_EVENT, "OFF");


    public NSwitchEvent(final EventType<? extends NEvent> eventType) {
        super(eventType);
    }
    public NSwitchEvent(final Object source, final EventTarget target, final EventType<? extends NEvent> eventType) {
        super(source, target, eventType);
    }


    @Override public void invokeHandler(final EventHandler handler) {
        handler.handle(this);
    }
}
