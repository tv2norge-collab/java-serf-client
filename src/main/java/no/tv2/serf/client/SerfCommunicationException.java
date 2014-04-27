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
public class SerfCommunicationException extends Exception {
    
    public SerfCommunicationException(Throwable cause) {
        super(cause);
    }
    public SerfCommunicationException(String message) {
        super(message);
    }
    public SerfCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
