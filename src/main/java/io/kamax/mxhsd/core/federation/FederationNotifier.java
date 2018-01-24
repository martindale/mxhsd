/*
 * mxhsd - Corporate Matrix Homeserver
 * Copyright (C) 2018 Maxime Dor
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

package io.kamax.mxhsd.core.federation;

import io.kamax.matrix.MatrixID;
import io.kamax.mxhsd.api.event.ISignedEvent;
import io.kamax.mxhsd.api.federation.IFederationNotifier;
import io.kamax.mxhsd.api.room.IRoomState;
import io.kamax.mxhsd.core.HomeserverState;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class FederationNotifier implements IFederationNotifier {

    private final Logger log = LoggerFactory.getLogger(FederationNotifier.class);

    private final HomeserverState global;

    public FederationNotifier(HomeserverState global) {
        this.global = global;
        init();
    }

    private void init() {
        global.getRoomMgr().forAllRooms().addListener(this);
    }

    @Handler
    private void eventHandler(ISignedEvent ev) {
        if (!global.getDomain().equals(ev.getOrigin())) {
            return;
        }

        // TODO should be done async in a worker pool?
        IRoomState rState = global.getRoomMgr().getRoom(ev.getRoomId()).getStateFor(ev.getId());
        rState.getMemberships().stream()
                .filter(c -> rState.isAccessibleAs(c.getStateKey()))
                .map(c -> MatrixID.asAcceptable(c.getStateKey()).getDomain())
                .filter(id -> !global.getDomain().equals(id))
                .unordered().distinct()
                .forEach(d -> send(ev, d));
    }

    @Override
    public void send(ISignedEvent ev, String destination) {
        Transaction t = new Transaction(
                Long.toString(ev.getTimestamp().toEpochMilli()),
                global.getDomain(),
                ev.getTimestamp(),
                Collections.singletonList(ev));
        try {
            global.getHsMgr().get(destination).pushTransaction(t);
        } catch (RuntimeException e) {
            log.warn("Unable to send Event {} to {}", ev.getId(), destination, e);
        }
    }

}