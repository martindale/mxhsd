/*
 * mxhsd - Corporate Matrix Homeserver
 * Copyright (C) 2018 Kamax Sarl
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.mxhsd.core.room;

import io.kamax.mxhsd.api.event.IEvent;
import io.kamax.mxhsd.api.room.IRoomStateSnapshot;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RoomStateSnapshot implements IRoomStateSnapshot {

    private Set<IEvent> stateEvents;
    private Set<IEvent> authChain;

    public RoomStateSnapshot(Collection<? extends IEvent> authChain, Collection<? extends IEvent> stateEvents) {
        this.stateEvents = Collections.unmodifiableSet(new HashSet<>(stateEvents));
        this.authChain = Collections.unmodifiableSet(new HashSet<>(authChain));
    }

    @Override
    public Set<IEvent> getAuthChain() {
        return authChain;
    }

    @Override
    public Set<IEvent> getState() {
        return stateEvents;
    }

}
