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

package examples.webcam; import org.apache.camel.main.Main;

/**
 * A main to start this example.
 */
public final class WebSocketMain {

    private WebSocketMain() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n\n\n\n");
        System.out.println("===============================================");
        System.out.println("Open your web browser on http://localhost:9090/index.html");
        System.out.println("Press ctrl+c to stop this example");
        System.out.println("===============================================");
        System.out.println("\n\n\n\n");

        // create a new Camel Main so we can easily start Camel
        Main main = new Main();

        // enable hangup support which mean we detect when the JVM terminates, and stop Camel graceful
        main.enableHangupSupport();

        WebcamWebSocketRouter route = new WebcamWebSocketRouter();

        // add our routes to Camel
        main.addRouteBuilder(route);

        // and run, which keeps blocking until we terminate the JVM (or stop CamelContext)
        main.run();
    }

}
