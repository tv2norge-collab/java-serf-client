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
import java.util.concurrent.Callable;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
class ListenerTask implements Callable<Boolean> {

    private final static Logger logger = LoggerFactory.getLogger(ListenerTask.class);
    private final Map<Long, ResponseHandler> handlers;
    private final SerfEndpoint serfEndpoint;
    private boolean closed = false;

    public ListenerTask(Map<Long, ResponseHandler> handlers, SerfEndpoint serfEndpoint) {
        this.handlers = handlers;
        this.serfEndpoint = serfEndpoint;
    }

    @Override
    public Boolean call() throws SerfCommunicationException {
        try {
            while (!serfEndpoint.isClosed() && !closed) {
                //Read header of next message
                Map<String, Value> dstMap = serfEndpoint.readNextMap();
                logger.debug("Reading header: {}", dstMap.toString());
                Long seq = decodeSeq(dstMap);
                String error = decodeError(dstMap);

                //Find handler
                if (handlers.containsKey(seq)) {
                    readResponseAndNotifyHandler(seq, error);
                } else {
                    throw new SerfCommunicationException("No handler for seq=" + seq);
                }
            }
        } catch (SerfCommunicationException ex) {
            if (closed) {
                //Closing the socket will throw exception. This prevents a stacktrace when it is closed properly.
                logger.debug("Socket closed");
            } else {
                throw new SerfCommunicationException(ex);
            }
        }
        //Return true means returns without exceptions
        return true;
    }

    private void readResponseAndNotifyHandler(Long seq, String error) throws SerfCommunicationException {
        Map<String, Value> dstMap;
        ResponseHandler handler = handlers.get(seq);
        if (error == null && handler.hasBody()) {
            dstMap = serfEndpoint.readNextMap();
            logger.debug("Reading body: {}", dstMap.toString());
        } else {
            dstMap = null;
        }
        RawSerfResponse response = new RawSerfResponse(seq, error, dstMap);
        handler.addResponse(response);
    }

    private Long decodeSeq(Map<String, Value> dstMap) {
        Long seq = dstMap.get("Seq").asIntegerValue().getLong();
        return seq;
    }

    private String decodeError(Map<String, Value> dstMap) {
        String error = dstMap.get("Error").asRawValue().getString();
        if (error.length() == 0) {
            error = null;
        }
        return error;
    }

    public void close() {
        this.closed = true;
    }
}
