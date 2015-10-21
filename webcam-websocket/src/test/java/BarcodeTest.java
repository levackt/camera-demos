/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.barcode.BarcodeDataFormat;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


@SuppressWarnings("ClassInDefaultPackage")
public class BarcodeTest extends CamelTestSupport {

    DataFormat barcode = new BarcodeDataFormat();
    
    @Test
    public void smokeTest() throws Exception {
        Thread.sleep(2000);
    }
    
    @Test
    public void testBarcode() throws InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:barcode");
        mock.setMinimumExpectedMessageCount(1);
        
        template.sendBody("seda:barcode", BarcodeTest.class.getResourceAsStream("qrcode-zxing.png"));

        mock.expectedBodiesReceived("http://www.qrstuff.com/");
        assertMockEndpointsSatisfied(10, TimeUnit.SECONDS);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                
                from("seda:barcode").unmarshal(barcode).to("mock:barcode");
            }
        };
    }

}
