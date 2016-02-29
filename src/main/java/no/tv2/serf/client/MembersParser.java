/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static no.tv2.serf.client.ResponseBase.valueConverter;
import org.msgpack.type.Value;

class MembersParser {

    public List<Member> parse(Map<String, Value> body) {
        
        List<Member> members = new ArrayList<>();

        Map<String, Value> map = (Map<String, Value>) body;
        
        if(map.containsKey("Members")) {
            
            for(Value v : map.get("Members").asArrayValue()) {
                if(v.isMapValue()) {
                    members.add(parseMember(v));
                }
            }
        }
        
        return Collections.unmodifiableList(members);
    }

    private Member parseMember(Value v) {

        String name = null;
        InetAddress addr = null;
        int port = Integer.MIN_VALUE;
        Map<String, String> tags = null;
        String status = null;
        int protocolMin = Integer.MIN_VALUE;
        int protocolMax = Integer.MIN_VALUE;
        int protocolCur = Integer.MIN_VALUE;
        int delegateMin = Integer.MIN_VALUE;
        int delegateMax = Integer.MIN_VALUE;
        int delegateCur = Integer.MIN_VALUE;

        for (Map.Entry<Value, Value> entry1 : v.asMapValue().entrySet()) {
            String key = entry1.getKey().asRawValue().getString();
            Value value = entry1.getValue();
            switch(key) {
                case "Name": name = valueConverter().asString(value); break;
                case "Addr": 
                    try {
                        addr = InetAddress.getByAddress(value.asRawValue().getByteArray());
                    } catch (UnknownHostException ex) {
                        throw new IllegalArgumentException(ex);
                    }
                    break;
                case "Port": port = valueConverter().asInteger(value); break;
                case "Status": status = valueConverter().asString(value); break;
                case "ProtocolMin": protocolMin = valueConverter().asInteger(value); break;
                case "ProtocolMax": protocolMax = valueConverter().asInteger(value); break;
                case "ProtocolCur": protocolCur = valueConverter().asInteger(value); break;
                case "DelegateMin": delegateMin = valueConverter().asInteger(value); break;
                case "DelegateMax": delegateMax = valueConverter().asInteger(value); break;
                case "DelegateCur": delegateCur = valueConverter().asInteger(value); break;
                case "Tags": tags = valueConverter().getStringMap(value);
            }
        }
        return new Member(name, addr, port, tags, status, protocolMin, protocolMax, protocolCur, delegateMin, delegateMax, delegateCur);
    }

}
