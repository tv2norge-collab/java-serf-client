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
import static no.tv2.serf.client.ResponseBase.valueConverter;
import org.msgpack.type.Value;

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
public class Log extends Response {
    
    private String text;
    
    Log(long seq, String error, Map<String, Value> response) {
        super(seq, error);
        parse(response);
    }

    private void parse(Map<String, Value> response) {
        text = valueConverter().asString(response.get("Log"));
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
