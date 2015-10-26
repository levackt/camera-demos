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

package examples.webcam.barcode;

import com.google.zxing.NotFoundException;
import examples.webcam.MyWebcamPojo;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.barcode.BarcodeDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spi.DataFormat;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public class BarcodeScannerRoute  extends RouteBuilder {

    private DataFormat barcode = new BarcodeDataFormat();

    @Override
    public void configure() throws Exception {

        from("webcam:cam?consumer.delay=500&width=640&height=480").routeId("barcode").
                doTry().unmarshal(barcode).doCatch(NotFoundException.class).setBody(constant("Barcode not found")).process(e -> {
                        e.getOut().setBody(new MyWebcamPojo(e.getIn().getBody(String.class)));
        }).marshal().json(JsonLibrary.Jackson).log("${body}").
                to("websocket://camel-webcam?port=9090&sendToAll=true&staticResources=classpath:webapp");
    }
    
}
