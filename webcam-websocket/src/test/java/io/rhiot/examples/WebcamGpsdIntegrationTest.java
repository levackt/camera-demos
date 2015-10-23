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

package io.rhiot.examples; import com.github.sarxos.webcam.Webcam;
import io.rhiot.component.gpsd.ClientGpsCoordinates;
import io.rhiot.component.gpsd.GpsdConstants;
import io.rhiot.deployer.detector.Device;
import io.rhiot.deployer.detector.DeviceDetector;
import io.rhiot.deployer.detector.SimplePortScanningDeviceDetector;
import io.rhiot.utils.Networks;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.codec.binary.Base64;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assume.assumeTrue;

/**
 * Testing webcam and GPS scenarios.
 */
public class WebcamGpsdIntegrationTest extends CamelTestSupport {

    private static Webcam webcam;
    static boolean isRpiAvailable;
    static DeviceDetector deviceDetector = new SimplePortScanningDeviceDetector();
    static String piAddress;
    static List<Device> devices;
    
    @BeforeClass
    public static void before(){
        try {
            webcam = Webcam.getDefault(15000L);
        } catch (Exception e) {
            // webcam is unavailable
        }
        assumeTrue(webcam != null && webcam.open());
        webcam.close();

        devices = deviceDetector.detectDevices();
        piAddress = devices.size() == 1 ? devices.get(0).address().getHostAddress() : null;
        isRpiAvailable = devices.size() == 1 && devices.get(0).type().equals(Device.DEVICE_RASPBERRY_PI_2) &&
                Networks.available(piAddress, GpsdConstants.DEFAULT_PORT);

        assumeTrue(isRpiAvailable);
    }

    @Test
    @Ignore
    public void testWebcamAndGpsd() throws Exception {
        
        MockEndpoint mock = getMockEndpoint("mock:geo-photo");
        mock.expectedMinimumMessageCount(3);

        assertMockEndpointsSatisfied(20, TimeUnit.SECONDS);

        MyCoolThing myCoolThing = mock.getExchanges().get(0).getIn().getBody(MyCoolThing.class);
        
        // Lets assert that the aggregation worked 
        assertNotNull(myCoolThing);
        assertNotNull(myCoolThing.getCoordinates());
        assertNotNull(myCoolThing.getBase64Image());
    }
    
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                
                // This route aggregates the coordinates from a remote Pi with the image from a webcam on the local computer, or even on the Pi
                from("gpsd:current-location?host=" + piAddress).pollEnrich("webcam:cam", (oldExchange, newExchange) -> {
                    
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
            }
        };
    }
    
    class MyCoolThing{
        private String coordinates;
        private String base64Image;

        public String getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(String coordinates) {
            this.coordinates = coordinates;
        }

        public String getBase64Image() {
            return base64Image;
        }

        public void setBase64Image(String base64Image) {
            this.base64Image = base64Image;
        }
    }
}
