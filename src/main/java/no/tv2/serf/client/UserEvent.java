/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

import java.util.Map;
import org.msgpack.type.Value;

public class UserEvent extends Event {
    
    private int lTime;
    private String name;
    private String payload;
    private boolean coalesce;
    
    UserEvent(long seq, String error, Map<String, Value> response) {
        super(seq, error, EventType.User);
        parse(response);
    }

    private void parse(Map<String, Value> response) {
        lTime = valueConverter().asInteger(response.get("LTime"));
        name = valueConverter().asString(response.get("Name"));
        payload = valueConverter().asString(response.get("Payload"));
        coalesce = valueConverter().asBoolean(response.get("Coalesce"));
    }

    public int getlTime() {
        return lTime;
    }

    public String getName() {
        return name;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isCoalesce() {
        return coalesce;
    }
    
    
}
