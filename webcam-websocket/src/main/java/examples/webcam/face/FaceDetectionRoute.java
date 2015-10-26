/*
 * *
 *  * Licensed to the Rhiot under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  
 *  
 */

package examples.webcam.face;

import examples.webcam.MyWebcamPojo;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;


/**
 * A Camel route that streams images from the webcam, and if a face is detected it is cropped and sent to the web-socket to be viewed from a web page
 */
public class FaceDetectionRoute extends RouteBuilder {

    private final HaarCascadeDetector detector = new HaarCascadeDetector();
    @Override
    public void configure() throws Exception {
        //1. Read the image from the input stream and detect faces
        from("seda:face").process(e -> {  
            e.getOut().setBody(detector.detectFaces(ImageUtilities.createFImage(ImageIO.read(e.getIn().getBody(InputStream.class))))); 
        });

        //2. Take an image from the webcam every half second and pass it to the face detection route in 1. 
        from("webcam:cam?consumer.delay=500&width=320&height=240").routeId("face").streamCaching().enrich("seda:face", (oldExchange, newExchange) -> {
            oldExchange.getOut().setHeader("faces", newExchange.getIn().getBody());
            oldExchange.getOut().setBody(oldExchange.getIn().getBody());

            return oldExchange;
        }).process(e -> {
            
            // 3. Try crop the face from the image, if we're confident enough about the face
            List<DetectedFace> faces = e.getIn().getHeader("faces", List.class);
            if (faces != null && faces.size() > 0) {
                LOG.info("Looks like we've got a live one");
                DetectedFace detectedFace = faces.get(0); //We're only interested in the first face in this example

                if (detectedFace.getConfidence() < 4) {
                    String message = "Confidence [" + detectedFace.getConfidence() + "] is too low";
                    LOG.warn(message);
                    e.getOut().setBody(new MyWebcamPojo(message));
                } else {

                    //4. Write the cropped image with only the face, or do whatever you need to do with it like pipe to a face recognition route
                    BufferedImage src = ImageIO.read(e.getIn().getBody(InputStream.class));
                    Rectangle bounds = detectedFace.getBounds();
                    BufferedImage face = src.getSubimage((int) bounds.getTopLeft().getX(), (int) bounds.getTopLeft().getY(),
                            (int) bounds.getWidth(), (int) bounds.getHeight());


                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ImageIO.write(face, "png", output);

                    e.getOut().setBody(new MyWebcamPojo(output.toByteArray(), 
                            "Face detected with confidence : [" + detectedFace.getConfidence() + "]"));
                }
            } else {
                LOG.warn("Face undetected!");
                e.getOut().setBody(new MyWebcamPojo("Face undetected"));
            }
            //5. Marshal to json and send to websocket
        }).marshal().json(JsonLibrary.Jackson).to("websocket://camel-webcam?port=9090&sendToAll=true&staticResources=classpath:webapp");
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(FaceDetectionRoute.class);
}