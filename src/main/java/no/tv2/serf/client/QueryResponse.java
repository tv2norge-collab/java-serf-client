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

public class QueryResponse extends ResponseBase {
    
    private QueryResponseType type;
    private String from;
    private String payload;
    
    QueryResponse(Long seq, String error, Map<String, Value> response) {
        super(seq, error);
        parse(response);
    }

    private void parse(Map<String, Value> response) {
        type = QueryResponseType.valueOf(valueConverter().asString(response.get("Type")));
        from = valueConverter().asString(response.get("From"));
        payload = valueConverter().asString(response.get("Payload"));
    }

    public QueryResponseType getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public String getPayload() {
        return payload;
    }
}
