/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
public class Configuration {
    
    private String serfAgentHost;
    private Integer serfAgentPort;

    public String getSerfAgentHost() {
        return serfAgentHost;
    }

    public void setSerfAgentHost(String serfAgentHost) {
        this.serfAgentHost = serfAgentHost;
    }

    public Integer getSerfAgentPort() {
        return serfAgentPort;
    }

    public void setSerfAgentPort(Integer serfAgentPort) {
        this.serfAgentPort = serfAgentPort;
    }
    
    
}
