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
class Response {
    
    private final long seq;
    private final String error;
    private Map<String, Value> body;
    
    public Response(long seq, String error) {
        this.seq = seq;
        this.error = error;
    }
    public Response(long seq, String error, Map<String, Value> body) {
        this(seq, error);
        this.body = body;
    }

    public long getSeq() {
        return seq;
    }

    public String getError() {
        return error;
    }

    public Map<String, Value> getBody() {
        return body;
    }
    
}
