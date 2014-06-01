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
    private final BlockingQueue<RawSerfResponse> responseQueue = new LinkedBlockingQueue<>(1000);
    private boolean firstResponse = true;
    private boolean stopped = false;
    
    public ResponseHandler(long seq, boolean firstResponseEmpty) {
        this.seq = seq;
        this.firstResponseEmpty = firstResponseEmpty;
    }

    public RawSerfResponse take() throws SerfCommunicationException {
        if (stopped) {
            throw new SerfCommunicationException("Response handler has been stopped");
        }
        try {
            RawSerfResponse response = responseQueue.take();
            return response.getSeq() == SHUTDOWN_SEQ ? null : response;
        } catch (InterruptedException ex) {
            logger.error("ResponseHandler interrupted.", ex);
            throw new SerfCommunicationException(ex);
        } 
    }

    // Will be called from a single threaded executor. 
    public void addResponse(RawSerfResponse response) throws SerfCommunicationException {
        if(seq != response.getSeq()) {
            throw new SerfCommunicationException("Seq of response not matching seq of this handler");
        }
        if(stopped) {
            throw new SerfCommunicationException("ResponseHandler has been stopped and is not exepting new responses.");
        }
        firstResponse = false;
        boolean addedToQueue = responseQueue.offer(response);
        if (!addedToQueue) {
            throw new SerfCommunicationException("ResponseQueue is full");
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
        responseQueue.add(new RawSerfResponse(SHUTDOWN_SEQ, "Poison Pill Shutdown"));
    }

}
