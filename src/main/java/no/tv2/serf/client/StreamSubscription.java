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

public class StreamSubscription extends ResponseBase {

    private final ResponseHandler handler;
    
    StreamSubscription(ResponseHandler handler) {
        super(handler.getSeq(), "");
        this.handler = handler;
    }

    public Event take() throws SerfCommunicationException {
        RawSerfResponse response = handler.take();
        return response == null ? null : parseResponse(response);
    }
    
    private Event parseResponse(RawSerfResponse response) throws SerfCommunicationException {

        Map<String, Value> map = (Map<String, Value>) response.getBody();

        if (map.containsKey("Event")) {
            String eventTypeString = valueConverter().asString(map.get("Event"));

            switch (eventTypeString) {
                case "user":
                    return new UserEvent(response.getSeq(), response.getError(), map);
                case "query":
                    return new QueryEvent(response.getSeq(), response.getError(), map);
                case "member-join":
                case "member-leave":
                case "member-failed":
                case "member-update":
                case "member-reap":
                    return new MembershipEvent(response.getSeq(), response.getError(), EventType.parse(eventTypeString), map);
                default:
                    throw new SerfCommunicationException("Event of type " + eventTypeString + " is not supported.");
            }
        } else {
            throw new SerfCommunicationException("EventResponse: The response did not contain the value \"Event\"");
        }
    }
    

}
