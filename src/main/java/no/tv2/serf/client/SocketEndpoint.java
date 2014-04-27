/*
 * Copyright (c) 2014 Arne M. Størksen <arne.storksen@tv2.no>.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package no.tv2.serf.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import static org.msgpack.template.Templates.TString;
import static org.msgpack.template.Templates.TValue;
import static org.msgpack.template.Templates.tMap;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arne M. Størksen <arne.storksen@tv2.no>
 */
public class SocketEndpoint implements SerfEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(SocketEndpoint.class);
    
    private final Socket socket;
    private final MessagePack msgpack = new MessagePack();
    private final Template<Map<String, Value>> mapTmpl = tMap(TString, TValue);
    private final Unpacker unpacker;

    public SocketEndpoint(String host, int port) throws IOException {
        socket = new Socket(host, port);
        unpacker = msgpack.createUnpacker(socket.getInputStream());
    }
    
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException ex) {
            //Exceptions are expected.
            logger.debug("Closing SocketEndpoint", ex);
        }
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed() || socket.isInputShutdown();
    }
    
    @Override
    public Map<String, Value> readNextMap() throws SerfCommunicationException {
        try {
            return unpacker.read(mapTmpl);
        } catch (IOException ex) {
            throw new SerfCommunicationException(ex);
        }
    }

    @Override
    public void send(Map<String, Object> header, Map<String, Object> body) throws SerfCommunicationException {

        //Currently generating the whole byte array before sending it. Not sure if this is necessary.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try(Packer packer = msgpack.createPacker(out)) {
            logger.debug("Sending header: {}", header.toString());
            packer.write(header);
            if (body != null) {
                logger.debug("Sending body: {}", body.toString());
                packer.write(body);
            }
            out.flush();

            socket.getOutputStream().write(out.toByteArray());
        } catch(IOException ex) {
            throw new SerfCommunicationException(ex);
        }
    }

}
