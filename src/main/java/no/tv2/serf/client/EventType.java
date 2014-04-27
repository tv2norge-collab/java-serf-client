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
public enum EventType {

    User,
    Query,
    MemberJoin,
    MemberLeave,
    MemberFailed,
    MemberUpdate,
    MemberReap;

    static EventType parse(String eventType) {

        switch (eventType) {
            case "user":
                return User;
            case "query":
                return Query;
            case "member-join":
                return MemberJoin;
            case "member-leave":
                return MemberLeave;
            case "member-failed":
                return MemberFailed;
            case "member-update":
                return MemberUpdate;
            case "member-reap":
                return MemberReap;
            default:
                throw new IllegalArgumentException("Event type " + eventType + " is not supported.");
        }
    }
}
