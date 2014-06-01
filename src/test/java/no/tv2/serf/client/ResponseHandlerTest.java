package no.tv2.serf.client;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Arne M. Størksen <arne.storksen@gmail.com>
 */
public class ResponseHandlerTest {
    
    public ResponseHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testTake() throws Exception {
        
        ResponseHandler responseHandler = new ResponseHandler(1l, true);
        RawSerfResponse expectedResponse = new RawSerfResponse(1l, null);
        responseHandler.addResponse(expectedResponse);
        RawSerfResponse actualResponse = responseHandler.take();
        
        assertEquals(expectedResponse, actualResponse);
    }
    
    @Test(expected = SerfCommunicationException.class)
    public void testAddResponseWithWrongSeq() throws SerfCommunicationException {
        
        ResponseHandler responseHandler = new ResponseHandler(1l, true);
        responseHandler.addResponse(new RawSerfResponse(2l, null));
    }
    
    @Test(expected = SerfCommunicationException.class)
    public void testFullQueue() throws SerfCommunicationException {
        
        ResponseHandler responseHandler = new ResponseHandler(1l, false);
        for(int i=0; i<1001; i++) {
            responseHandler.addResponse(new RawSerfResponse(1l, null));
        }
    }
    

    @Test(expected = SerfCommunicationException.class)
    public void testAddResponseIfStopped() throws SerfCommunicationException {
        ResponseHandler responseHandler = new ResponseHandler(1l, true);
        responseHandler.stop();
        responseHandler.addResponse(new RawSerfResponse(1l, null));
    }

    @Test(expected = SerfCommunicationException.class)
    public void testTakeIfStopped() throws SerfCommunicationException {
        ResponseHandler responseHandler = new ResponseHandler(1l, true);
        responseHandler.addResponse(new RawSerfResponse(1l, null));
        responseHandler.stop();
        responseHandler.take();
    }
    
    @Test
    public void testStop() throws SerfCommunicationException {
        final ResponseHandler responseHandler = new ResponseHandler(1l, true);
        
        Executors.newScheduledThreadPool(1).schedule(new Runnable() {

            @Override
            public void run() {
                responseHandler.stop(); 
            }
        }, 100, TimeUnit.MILLISECONDS);

        System.out.println("Blocking on take");
        responseHandler.take(); //Will block until poison pill received
        
    }
    
    @Test
    public void testGetSeq() {
        final ResponseHandler responseHandler = new ResponseHandler(1l, true);
        assertEquals(1l, responseHandler.getSeq());
    }

    @Test
    public void testHasBody() {
        final ResponseHandler responseHandler = new ResponseHandler(1l, false);
        assertEquals(true, responseHandler.hasBody());
    }
    
}
