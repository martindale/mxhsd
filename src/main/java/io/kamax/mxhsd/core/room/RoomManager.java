/*
 * mxhsd - Corporate Matrix Homeserver
 * Copyright (C) 2017 Maxime Dor
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

import io.kamax.matrix.hs.RoomMembership;
import io.kamax.mxhsd.api.room.*;
import io.kamax.mxhsd.api.room.event.RoomCreateEvent;
import io.kamax.mxhsd.api.room.event.RoomMembershipEvent;
import io.kamax.mxhsd.api.room.event.RoomPowerLevelEvent;
import io.kamax.mxhsd.core.HomeserverState;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RoomManager implements IRoomManager {

    private Logger log = LoggerFactory.getLogger(RoomManager.class);

    private HomeserverState state;
    private Map<String, IRoom> rooms;

    public RoomManager(HomeserverState state) {
        this.state = state;
        rooms = new HashMap<>();
    }

    private boolean hasRoom(String id) {
        return rooms.containsKey(id);
    }

    private String getId() {
        String id;
        do {
            id = "!" + RandomStringUtils.randomAlphanumeric(16) + ":" + state.getDomain();
        } while (hasRoom(id));

        log.info("Generated Room ID {}", id);
        return id;
    }

    // TODO make it configurable via JSON data
    private RoomPowerLevels getPowerLevelEvent(IRoomCreateOptions options) {
        return new RoomPowerLevels.Builder()
                // Default state PL, moderator is a good compromise
                .setStateDefault(PowerLevel.Moderator)

                // Anyone can send any message events by default
                .setEventsDefault(PowerLevel.None)
                .addEvent(RoomEventType.HistoryVisiblity.get(), PowerLevel.Admin)
                .addEvent(RoomEventType.PowerLevels.get(), PowerLevel.Admin)

                // Users don't get any PL by default, adding creator
                .setUsersDefault(PowerLevel.None)
                .addUser(options.getCreator().getId(), PowerLevel.Admin)

                // Define some basic room management, anyone can invite
                .setBan(PowerLevel.Moderator)
                .setInvite(PowerLevel.None)
                .setKick(PowerLevel.Moderator)
                .setRedact(PowerLevel.Moderator)
                .build();
    }

    @Override
    public synchronized IRoom createRoom(IRoomCreateOptions options) { // FIXME use RWLock
        String creator = options.getCreator().getId();
        String id = getId();
        Room room = new Room(state, id);
        room.inject(new RoomCreateEvent(creator, id));
        room.inject(new RoomMembershipEvent(creator, id, RoomMembership.Join.get(), creator));
        room.inject(new RoomPowerLevelEvent(creator, id, getPowerLevelEvent(options)));
        rooms.put(id, room);

        log.info("Room {} created", id);
        return room;
    }

    @Override
    public synchronized Optional<IRoom> findRoom(String id) { // FIXME use RWLock
        return Optional.ofNullable(rooms.get(id));
    }

    @Override
    public synchronized List<IRoom> listRooms() { // FIXME use RWLock
        return new ArrayList<>(rooms.values());
    }

}
