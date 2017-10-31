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

package io.kamax.mxhsd.api.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.Collection;

public interface IEventBuilder {

    IEventBuilder setTimestamp(Instant instant);

    IEventBuilder setType(String type);

    IEventBuilder addParent(ISignedEvent parent);

    default IEventBuilder addParents(Collection<ISignedEvent> parents) {
        parents.forEach(this::addParent);
        return this;
    }

    Collection<String> getParents();

    JsonArray getAuthEvents();

    JsonObject getContent();

    JsonObject getPrevState();

    JsonObject getJson();

    IEvent build(String id);

}
