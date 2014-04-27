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
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
public class Member {
    
    private final String name;
    private final InetAddress addr;
    private final int port;
    private final Map<String, String> tags;
    private final String status;
    private final int protocolMin;
    private final int protocolMax;
    private final int protocolCur;
    private final int delegateMin;
    private final int delegateMax;
    private final int delegateCur;    
    
    Member(String name, 
            InetAddress addr, 
            int port, 
            Map<String, String> tags, 
            String status, 
            int protocolMin, 
            int protocolMax,
            int protocolCur,
            int delegateMin,
            int delegateMax,
            int delegateCur) {
        
        this.name = name;
        this.addr = addr;
        this.port = port;
        this.tags = Collections.unmodifiableMap(tags);
        this.status = status;
        this.protocolMin = protocolMin;
        this.protocolMax = protocolMax;
        this.protocolCur = protocolCur;
        this.delegateMin = delegateMin;
        this.delegateMax = delegateMax;
        this.delegateCur = delegateCur;
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddr() {
        return addr;
    }

    public int getPort() {
        return port;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public String getStatus() {
        return status;
    }

    public int getProtocolMin() {
        return protocolMin;
    }

    public int getProtocolMax() {
        return protocolMax;
    }

    public int getProtocolCur() {
        return protocolCur;
    }

    public int getDelegateMin() {
        return delegateMin;
    }

    public int getDelegateMax() {
        return delegateMax;
    }

    public int getDelegateCur() {
        return delegateCur;
    }
    
}