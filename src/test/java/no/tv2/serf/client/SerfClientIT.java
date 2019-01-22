/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import org.junit.After;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
public class SerfClientIT {

    private static final Logger logger = LoggerFactory.getLogger(SerfClientIT.class);

    private static final String SERF1_RPC_IP = "10.2.2.1";
    private static final int SERF1_RPC_PORT = 7373;

    private static final String SERF2_RPC_IP = "10.2.2.1";
    private static final int SERF2_RPC_PORT = 7374;

    private static final String SERF1_IP = "10.2.2.10";
    private static final int SERF1_PORT = 7946;
    private static final String SERF2_IP = "10.2.2.11";
    private static final int SERF2_PORT = 7946;

    private Client client1;
    private Client client2;
    private SerfEndpoint endpoint;

    @Before
    public void before() throws IOException, SerfCommunicationException {
        logger.info("before");
        client1 = createClient(SERF1_RPC_IP, SERF1_RPC_PORT);
        client2 = createClient(SERF2_RPC_IP, SERF2_RPC_PORT);
    }

    @After
    public void after() throws SerfCommunicationException {
        logger.info("after");
        client1.close();
        client2.close();
    }

    public Client createClient(String ip, int port) throws SerfCommunicationException, IOException {
        endpoint = new SocketEndpoint(ip, port);
        Client client = new Client(endpoint);
        client.handshake();
        return client;
    }


    @Test
    public void testMembers() throws SerfCommunicationException {
        logger.info("testMembers");
        System.out.println("testMembers");
        client1.join(ImmutableList.<String>of(SERF2_IP + ":" + SERF2_PORT), false);
        MembersResponse response = client1.members();
        assertEquals(2, response.getMembers().size());
    }

    @Test
    public void testFilteredMembers() throws SerfCommunicationException {
        logger.info("testFilteredMembers");
        Map<String, String> expected = ImmutableMap.<String, String>builder().put("test-tag", "tag-value").build();

        client1.tags(expected, ImmutableList.<String>of());

        MembersResponse membersFiltered = client1.membersFiltered(expected, "", "");
        assertEquals(1, membersFiltered.getMembers().size());

        // clean up
        client1.tags(ImmutableMap.<String, String>of(), ImmutableList.<String>of("test-tag"));
    }

    @Test
    public void testJoin() throws SerfCommunicationException {
        logger.info("join");
        JoinResponse response = client1.join(ImmutableList.<String>of(SERF2_IP + ":" + SERF2_PORT), false);
        assertEquals(1, response.getNum());
    }

    @Test
    public void testSendAndReceiveEvent() throws SerfCommunicationException {
        logger.info("testSendAndReceiveEvent");
        JoinResponse response = client1.join(ImmutableList.<String>of(SERF2_IP + ":" + SERF2_PORT), false);
        assertEquals(1, response.getNum());
        StreamSubscription subscription = client2.stream("user:event1");
        client1.event("event1", "payload1", true);
        Event event = subscription.take();
        client2.stop(subscription.getSeq());

        assertEquals(EventType.User, event.getEventType());
    }

    @Test
    public void testLeaveAndReceiveMemberEvent() throws SerfCommunicationException {
        logger.info("testLeaveAndReceiveMemberEvent");
        JoinResponse response = client1.join(ImmutableList.<String>of(SERF2_IP + ":" + SERF2_PORT), false);
        assertEquals(1, response.getNum());
        StreamSubscription subscription = client1.stream("member-update");
        client2.leave();
        Event event = subscription.take();

        assertEquals(EventType.MemberUpdate, event.getEventType());
    }

    @Test
    public void testForceLeave() throws SerfCommunicationException {
        logger.info("testForceLeave");
        JoinResponse response = client1.join(ImmutableList.<String>of(SERF2_IP + ":" + SERF2_PORT), false);
        client1.forceLeave("serf2");
    }

    @Test
    public void testCreateTags() throws SerfCommunicationException {
        logger.info("testCreateTags");
        Map<String, String> expected = ImmutableMap.<String, String>of("test-tag", "tag-value");

        client1.tags(expected, ImmutableList.<String>of());

        MembersResponse membersFiltered = client1.membersFiltered(expected, "", "");
        assertTrue(Maps.difference(expected, membersFiltered.getMembers().get(0).getTags()).areEqual());

        // clean up
        client1.tags(ImmutableMap.<String, String>of(), ImmutableList.<String>of("test-tag"));
    }

    @Test
    public void testDeleteTags() throws SerfCommunicationException {
        logger.info("testDeleteTags");
        Map<String, String> tags = ImmutableMap.<String, String>builder().put("test-tag", "tag-value").build();
        List<String> deletedTags = ImmutableList.<String>of("test-tag");

        client1.tags(tags, ImmutableList.<String>of());
        MembersResponse membersFiltered1 = client1.membersFiltered(tags, "", "");
        assertTrue(Maps.difference(tags, membersFiltered1.getMembers().get(0).getTags()).areEqual());

        client1.tags(ImmutableMap.<String, String>of(), deletedTags);

        MembersResponse membersFiltered2 = client1.membersFiltered(tags, "", "");
        assertEquals(0, membersFiltered2.getMembers().size());
    }

    @Test
    public void testLog() throws SerfCommunicationException {
        logger.info("testLog");
        JoinResponse response = client1.join(ImmutableList.<String>of(SERF2_IP + ":" + SERF2_PORT), false);

        MonitorSubscription subscription = client1.monitor(LogLevel.info);
        client2.event("test-monitor", "test-monitor-payload", true);
        Log log = subscription.take();
        assertTrue(log.toString().contains("INFO"));
    }

    @Test
    public void testQueryResponse() throws SerfCommunicationException {
        logger.info("testQueryResponse");

        JoinResponse response = client1.join(ImmutableList.<String>of(SERF2_IP + ":" + SERF2_PORT), false);
        assertEquals(1, response.getNum());

        StreamSubscription streamSubscription = client2.stream("query");
        QuerySubscription querySubscription = client1.query("test-query", "test-payload", ImmutableList.<String>of("serf2"), ImmutableMap.<String, String>of(), true, 0);

        Event event = streamSubscription.take();
        assertEquals(EventType.Query, event.getEventType());

        QueryEvent queryEvent = (QueryEvent) event;
        assertEquals("test-query", queryEvent.getName());
        assertEquals("test-payload", queryEvent.getPayload());

        int id = queryEvent.getId();
        client2.respond(id, "test-response");

        QueryResponse response1 = querySubscription.take();
        assertEquals(QueryResponseType.ack, response1.getType());
        assertEquals("serf2", response1.getFrom());

        QueryResponse response2 = querySubscription.take();
        assertEquals(QueryResponseType.response, response2.getType());
        assertEquals("serf2", response2.getFrom());
        assertEquals("test-response", response2.getPayload());

        QueryResponse response3 = querySubscription.take();
        assertEquals(QueryResponseType.done, response3.getType());

    }

    @Test
    public void testStats() throws SerfCommunicationException {
        logger.info("testStats");

        Map<String, String> expected = ImmutableMap.<String, String>of("teststat", "valstat");

        client1.tags(expected, ImmutableList.<String>of());
        StatsResponse statsResponse = client1.stats();
        assertTrue(Integer.valueOf(statsResponse.getStats().getSerf().getMembers()) > 0);
        assertTrue(Maps.difference(expected, statsResponse.getStats().getTags()).areEqual());

        // clean up
        client1.tags(ImmutableMap.<String, String>of(), ImmutableList.<String>of("teststat"));

    }
    
}
