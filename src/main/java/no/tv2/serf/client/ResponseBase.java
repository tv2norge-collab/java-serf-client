/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

public abstract class ResponseBase {

    private static final ValueConverter VALUE_CONVERTER = new ValueConverter();
    
    private final String error;
    private final Long seq;

    ResponseBase(Long seq, String error) {
        this.error = error;
        this.seq = seq;
    }

    public String getError() {
        return error;
    }

    public boolean isError() {
        return error != null;
    }

    public Long getSeq() {
        return seq;
    }
    
    static ValueConverter valueConverter() {
        return VALUE_CONVERTER;
    }
}
