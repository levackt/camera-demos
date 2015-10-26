# camera-demos
Playing around with cameras for the cloudlet and/or quickstart to demo camel-webcam 

If you have a webcam on your computer, then build Rhiot and run the CamelWebcamWebSocketMain class in your IDE.

Open your web browser on http://localhost:9090/index.html

Enjoy the spycam, or birdfeed, or barcode scanner...

The gist of each example;

# Webcam to websocket - WebSocketMain.java
Front end is a thinned out version of the webcam-capture example.
What changes notably is the backend, the following line/s take pics from the webcam every 100ms, marshal to base64 before sending it 
to all subscribers. 
camel-websocket is also serving up the static files webapp directory in the classpath, 

    from("webcam:cam?consumer.delay=100").marshal().base64().convertBodyTo(String.class).
        toF("websocket://camel-webcam?port=%s&sendToAll=true&staticResources=classpath:webapp", getPort())
       

# Barcode scanner - BarcodeTest.java
The barcode scanner example takes an image that could be from the 
    from("webcam:barcode").unmarshal(barcode).to("mock:barcode")

# Timelapse

Nothing much to do here, the scheduled consumer is the default consumer and it takes one frame every second, so just fire up a route like so;
  
    from("webcam:mycam").to(myEndpoint)
  
# Motion detection
Motion is detected when the picture changes significantly, you can configure this in the endpoint, see Rhiot.
When motion is detected you want to configure where it goes next, usually this is an email, or an alarm.
Camel makes this really easy and I'll demonstrate a few motion examples, the simplest one is to set motion to true and pipe to the route of your choice. 
  
    from("webcam:spycam?motion=true").to(alarmEndpoint)
    
# GPSD integration - WebcamGpsdIntegrationTest
A device with a webcam that knows it's place in the world is really useful, it can scan barcodes or biometrics and report on the location of
the objects, or individuals, based on it's own position and/or proximity to those objects.
Currently this sample is more complex than it needs to be so it will be updated with gps_enrich, but still quite simple;
First get the device current location, then enrich the payload from the webcam before storing the serialized coordinates
as well as the base64 encoded image in a vanilla java object.
You might imagine enriching with data from several sensors, or first scanning the image and then getting the coordinates,
and that's already planned for Rhiot, meanwhile Camel makes this easy enough. :)
        
        from("gpsd:current-location?host=" + piAddress).pollEnrich("webcam:cam", (oldExchange, newExchange) 
            ClientGpsCoordinates coordinates = oldExchange.getIn().getHeader(GpsdConstants.TPV_HEADER, ClientGpsCoordinates.class);
                byte[] image = newExchange.getIn().getBody(byte[].class);
                
                MyCoolThing pojo = new MyCoolThing();
                pojo.setCoordinates(coordinates.serialize());
                if (image != null) {
                    pojo.setBase64Image(Base64.encodeBase64String(image));
                }
                
                newExchange.getOut().setBody(pojo);
                
                return newExchange;
            }).to("mock:geo-photo");         

# Face detection ?

# Face registration/enrolment ?

# Face recognition ?

