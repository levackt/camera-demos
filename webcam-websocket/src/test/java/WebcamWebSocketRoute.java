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
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.lang.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A Camel route that streams images from the webcam to web-socket, to be viewed from a web page
 */
public class WebcamWebSocketRoute extends RouteBuilder {

    private int port = 9090;
    private int delay = 10000;
    
    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void configure() throws Exception {
        
        // poll webcam for images
        fromF("webcam:cam?consumer.delay=%s", getDelay()).marshal().base64().convertBodyTo(String.class).
            toF("websocket://camel-webcam?port=%s&sendToAll=true&staticResources=classpath:webapp", getPort());
    }
}