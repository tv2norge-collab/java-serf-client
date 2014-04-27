/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A java implementation of the Serf RPC protocol.
 * http://www.serfdom.io/docs/agent/rpc.html
 * 
 * 
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
public class Client {

    private final AtomicLong seqCounter = new AtomicLong(0);

    private final Map<Long, ResponseHandler> handlers = new ConcurrentHashMap<>();
    private final ExecutorService listenerService;
    private final ListenerTask listenerTask;
    private final SerfEndpoint serfEndpoint;

    public Client(SerfEndpoint serfEndpoint) throws SerfCommunicationException {
        this.serfEndpoint = serfEndpoint;
        this.listenerService = Executors.newSingleThreadExecutor();
        this.listenerTask = new ListenerTask(handlers, serfEndpoint);

        listenerService.submit(listenerTask);
    }
    
    public EmptyResponse handshake() throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("handshake", ImmutableMap.<String, Object>builder()
                .put("Version", 1).build(), true);
        return new EmptyResponse(response.getSeq(), response.getError());
    }
    
    public void leave() throws SerfCommunicationException {
        serfEndpoint.send(header("leave", nextSeq()), ImmutableMap.<String, Object>of()); //Not expecting response.
        close();
    }

    public MembersResponse members() throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("members", ImmutableMap.<String, Object>of(), false);
        return new MembersResponse(response.getSeq(), response.getError(), response.getBody());
    }

    public MembersResponse membersFiltered(Map<String, String> tags, String status, String name) throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("members-filtered", ImmutableMap.<String, Object>builder()
                .put("Tags", tags)
                .put("Status", status)
                .put("Name", name).build(), false);
        return new MembersResponse(response.getSeq(), response.getError(), response.getBody());
    }

    public JoinResponse join(List<String> servers, boolean replay) throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("join", ImmutableMap.<String, Object>builder()
                .put("Existing", servers)
                .put("Replay", replay).build(), false);
        return new JoinResponse(response.getSeq(), response.getError(), response.getBody());
    }

    public EmptyResponse event(String name, String payload, boolean coalesce) throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("event", ImmutableMap.<String, Object>builder()
                .put("Name", name)
                .put("Payload", payload)
                .put("Coalesce", coalesce).build(), true);
        return new EmptyResponse(response.getSeq(), response.getError());
    }

    public EmptyResponse forceLeave(String node) throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("force-leave", ImmutableMap.<String, Object>builder()
                .put("Node", node).build(), true);
        return new EmptyResponse(response.getSeq(), response.getError());

    }

    public EmptyResponse tags(Map<String, String> tags, List<String> deleteTags) throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("tags", ImmutableMap.<String, Object>builder()
                .put("Tags", tags)
                .put("DeleteTags", deleteTags).build(), true);
        return new EmptyResponse(response.getSeq(), response.getError());
    }

    public StreamSubscription stream(String type) throws SerfCommunicationException {
        ResponseHandler handler = executeCommandAndReturnHandler("stream", ImmutableMap.<String, Object>builder()
                .put("Type", type).build());

        return new StreamSubscription(handler);
    }
    
    public EmptyResponse stop(long seq) throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("stop", ImmutableMap.<String, Object>builder()
                .put("Stop", seq).build(), true);
        if(handlers.containsKey(seq)) {
            ResponseHandler handler = handlers.remove(seq);
            handler.stop();
        }
        return new EmptyResponse(response.getSeq(), response.getError());
    }
    
    public MonitorSubscription monitor(LogLevel logLevel) throws SerfCommunicationException {
        ResponseHandler handler = executeCommandAndReturnHandler("monitor", ImmutableMap.<String, Object>builder()
                .put("LogLevel", logLevel.name().toUpperCase()).build());

        return new MonitorSubscription(handler);
    }
    
    public QuerySubscription query(String name, String payload, List<String> filterNodes, Map<String, String> filterTags, boolean requestAck, int timeout) throws SerfCommunicationException {
        ResponseHandler handler = executeCommandAndReturnHandler("query", ImmutableMap.<String, Object>builder()
                .put("FilterNodes", filterNodes)
                .put("FilterTags", filterTags)
                .put("RequestAck", requestAck)
                .put("Timeout", timeout)
                .put("Name", name)
                .put("Payload", payload).build());

        return new QuerySubscription(handler);
    }

    public EmptyResponse respond(int id, String payload) throws SerfCommunicationException {
        Response response = executeCommandAndWaitForSingleResponse("respond", ImmutableMap.<String, Object>builder()
                .put("ID", id)
                .put("Payload", payload).build(), true);
        return new EmptyResponse(response.getSeq(), response.getError());
    }


    public void close() {
        closeHandlers();
        listenerTask.close();
        serfEndpoint.close();
        listenerService.shutdown();
    }
    
    private void closeHandlers() {
        for (Long seq : handlers.keySet()) {
            ResponseHandler handler = handlers.remove(seq);
            handler.stop();
        }
    }

    private Map<String, Object> header(String header, long seq) {
        return ImmutableMap.<String, Object>builder()
                .put("Command", header)
                .put("Seq", seq).build();
    }

    private ResponseHandler createHandler(long seq, boolean isFirstResponseEmpty) throws SerfCommunicationException {
        ResponseHandler handler = new ResponseHandler(seq, isFirstResponseEmpty);
        handlers.put(seq, handler);
        return handler;
    }

    private Response waitForSingleResponseAndRemoveHandler(ResponseHandler handler) throws SerfCommunicationException {

        Response response = handler.take();
        handlers.remove(handler.getSeq());
        return response;
    }

    private long nextSeq() {
        return seqCounter.incrementAndGet();
    }

    private ResponseHandler executeCommand(String command, Map<String, Object> body, boolean isFirstResponseEmpty) throws SerfCommunicationException {
        long seq = nextSeq();
        ResponseHandler handler = createHandler(seq, isFirstResponseEmpty);
        serfEndpoint.send(header(command, seq), body);
        return handler;
    }

    private void handleError(Response response) throws SerfCommunicationException {

        if (response.getError() != null) {
            throw new SerfCommunicationException("Got error from Serf Agent: " + response.getError());
        }
    }

    private Response executeCommandAndWaitForSingleResponse(String command, Map<String, Object> map, boolean isFirstResponseEmpty) throws SerfCommunicationException {
        ResponseHandler handler = executeCommand(command, map, isFirstResponseEmpty);
        Response response = waitForSingleResponseAndRemoveHandler(handler);
        handleError(response);
        return response;
    }
    
    private ResponseHandler executeCommandAndReturnHandler(String command, Map<String, Object> map) throws SerfCommunicationException {
        ResponseHandler handler = executeCommand(command, map, true);
        Response response = handler.take();
        handleError(response);
        return handler;
    }
}
