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

package examples.webcam; 
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * A Camel route that streams images from the webcam to web-socket, to be viewed from a web page
 */
//@SpringBootApplication
//@ComponentScan("examples.webcam")
    //todo fails due to jetty clash in spring and camel
public class WebcamWebSocketRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        from("webcam:cam").marshal().base64().convertBodyTo(String.class).
            to("websocket://camel-webcam?port=9090&sendToAll=true&staticResources=classpath:webapp");
    }
}