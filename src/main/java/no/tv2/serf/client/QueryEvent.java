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

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
public class QueryEvent extends Event {

    private int id;
    private int lTime;
    private String name;
    private String payload;
    
    QueryEvent(long seq, String error, Map<String, Value> response) {
        super(seq, error, EventType.Query);
        parse(response);
    }
    
    private void parse(Map<String, Value> response) {
        id = valueConverter().asInteger(response.get("ID"));
        lTime = valueConverter().asInteger(response.get("LTime"));
        name = valueConverter().asString(response.get("Name"));
        payload = valueConverter().asString(response.get("Payload"));
    }

    public int getId() {
        return id;
    }

    public int getLTime() {
        return lTime;
    }

    public String getName() {
        return name;
    }

    public String getPayload() {
        return payload;
    }
    
    

}
