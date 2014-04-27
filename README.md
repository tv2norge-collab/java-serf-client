java-serf-client
================

A java library that implements the serf RPC protocol as described on the serf project site. 
- Serf: http://www.serfdom.io
- Serf RPC protocol: http://www.serfdom.io/docs/agent/rpc.html

Quick Start
-----------
    //Create a serf socket endpoint
    SerfEndpoint ep = new SocketEndpoint(ip, port);
    
    //Create a serf client and perform handshake
    Client client = new Client(ep);
    client.handshake();

    //Add/delete some tags
    client.tags(ImmutableMap.<String, String>of("tag-to-create", "tag-value"), ImmutableList.<String>of("tag-to-delete"));
    
    //Join a cluster
    JoinResponse response = client.join(ImmutableList.<String>of("10.2.2.11:7946"), false);
    
    //Subscribe to events
    StreamSubscription subscription = client.stream("user:event1");
    
    //Get next event (blocking)
    Event event = subscription.take();
    
    //Query the cluster
    QuerySubscription querySubscription = client.query("my-query", "a payload", ImmutableList.<String>of("serf-agent2"), ImmutableMap.<String, String>of(), true, 0);
    
    //Get the next response (blocking)
    QueryResponse response = querySubscription.take();

Integration tests
-----------------
The project includes a Vagrantfile that creates two VM's with serf agent that can be controlled by the RPC client.Vagrant 1.5 or newer is required.

Run `vagrant up` in the base folder to create the VM's. The VM's are named serf1 and serf2 and you may log into them by using `vagrant ssh serf1` or `vagrant ssh serf2`. When the VM's are up, you may run the integration tests:
    
    mvn -Dtest=no.tv2.serf.client.SerfClientIT -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG surefire:test

    


