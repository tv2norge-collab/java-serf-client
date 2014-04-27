/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

import java.util.HashMap;
import java.util.Map;
import org.msgpack.type.Value;

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
class ValueConverter {

    public void verifyArray(String name, Map.Entry<String, Value> entry) {
        if(!name.equals(entry.getKey()) || !entry.getValue().isArrayValue()) {
            throw new IllegalArgumentException("Incoming data doesn't match expected format");
        }
    }
    
    public String asString(Value value) {
        if(value == null) {
            return null;
        }
        if(!value.isRawValue()) {
            throw new IllegalArgumentException("Incoming value is not a String");
        }
        return value.asRawValue().getString();
    }

    public int asInteger(Value value) {
        if(!value.isIntegerValue()) {
            throw new IllegalArgumentException("Incoming value is not an Integer");
        }
        return value.asIntegerValue().getInt();
    }

    public boolean asBoolean(Value value) {
        if(!value.isBooleanValue()) {
            throw new IllegalArgumentException("Incoming value is not a Boolean");
        }
        return value.asBooleanValue().getBoolean();
    }
    
    public Map<String, String> getStringMap(Value value) {
        if(!value.isMapValue()) {
            throw new IllegalArgumentException("Incoming value is not a Map");
        }
        Map<String, String> map = new HashMap<>();
        for(Map.Entry<Value, Value> entry : value.asMapValue().entrySet()) {
            if(!entry.getKey().isRawValue() || !entry.getValue().isRawValue()) {
                throw new IllegalArgumentException("Map entry is not a String");
            }
            map.put(entry.getKey().asRawValue().getString(), entry.getValue().asRawValue().getString());
        }
        return map;
    } 

}
