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
public interface SerfEndpoint {

    void close();
    boolean isClosed();
    Map<String, Value> readNextMap() throws SerfCommunicationException;
    void send(Map<String, Object> header, Map<String, Object> body) throws SerfCommunicationException;
}
