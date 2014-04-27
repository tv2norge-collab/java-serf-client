/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
class ResponseHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
    private static final long SHUTDOWN_SEQ = Long.MIN_VALUE;

    private final long seq;
    private final boolean firstResponseEmpty;
    private final BlockingQueue<Response> responseQueue = new LinkedBlockingQueue<>(1000);
    private boolean firstResponse = true;
    private boolean stopped = false;
    
    public ResponseHandler(long seq, boolean firstResponseEmpty) {
        this.seq = seq;
        this.firstResponseEmpty = firstResponseEmpty;
    }

    public Response take() throws SerfCommunicationException {
        if (stopped) {
            throw new SerfCommunicationException("Response handler has been stopped");
        }
        try {
            Response response = responseQueue.take();
            return response.getSeq() == SHUTDOWN_SEQ ? null : response;
        } catch (InterruptedException ex) {
            logger.error("ResponseHandler interrupted.", ex);
            throw new SerfCommunicationException(ex);
        } 
    }

    // Will be called from a single threaded executor. 
    public void addResponse(Response response) {
        firstResponse = false;
        if (!stopped) {
            boolean addedToQueue = responseQueue.offer(response);
            if (!addedToQueue) {
                logger.error("ResponseQueue is full. Dropping response.");
            } 
        }
    }

    public long getSeq() {
        return seq;
    }

    //Response will have a body unless the first response is empty
    public boolean hasBody() {
        return !(firstResponse && firstResponseEmpty);
    }

    public void stop() {
        // Return from a blocking queue, using "Poison Pill Shutdown"
        stopped = true;
        responseQueue.add(new Response(SHUTDOWN_SEQ, "Poison Pill Shutdown"));
    }

}
