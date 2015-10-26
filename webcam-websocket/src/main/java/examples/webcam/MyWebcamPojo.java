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

import org.apache.commons.codec.binary.Base64;

public class MyWebcamPojo {
    
    private int lat;
    private int lng;
    private String message;
    private String image;
    private String barcode;

    public MyWebcamPojo(byte[] image, String barcode, String message) {
        this.message = message;
        this.image = Base64.encodeBase64String(image);
        this.barcode = barcode;
    }
    public MyWebcamPojo(byte[] image, String message) {
        this.message = message;
        this.image = Base64.encodeBase64String(image);
    }

    public MyWebcamPojo(int lat, int lng, byte[] image) {
        this.lat = lat;
        this.lng = lng;
        this.image = Base64.encodeBase64String(image);;
    }
    
    public MyWebcamPojo(String message) {

        this.message = message;
    }
    
    public int getLat() {
        return lat;
    }

    public int getLng() {
        return lng;
    }

    public String getImage() {
        return image;
    }

    public String getMessage() {
        return message;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
