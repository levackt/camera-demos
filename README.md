# camera-demos
Playing around with cameras for the cloudlet and/or quickstart to demo camel-webcam 

If you have a webcam on your computer, then build Rhiot and run the CamelWebcamWebSocketMain class in your IDE.

Open your web browser on http://localhost:9090/index.html

Enjoy the spycam, or birdfeed, or barcode scanner...

The gist is this;

    fromF("webcam:cam?consumer.delay=100").marshal().base64().convertBodyTo(String.class).
        toF("websocket://camel-webcam?port=%s&sendToAll=true&staticResources=classpath:webapp", getPort());
            
That's taking pics from the webcam every 100ms, marshalling to base64 before sending it to all subscribers.
camel-websocket is also serving up the static files webapp directory in the classpath.