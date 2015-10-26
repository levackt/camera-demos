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

$(document).ready(function() {

	var ws = new WebSocket("ws://localhost:9090/camel-webcam");

	ws.onopen = function(e) {
		if (typeof console !== 'undefined') {
			console.info('WS open');
		}
	};

	ws.onmessage = function (e) {
		var data = JSON.parse(e.data);
		if (data && data.image) {
			$("#image").attr('src', 'data:image/png;base64,' + data.image);
		} 
		else {
			$("#image").attr('src', 'rhiot.png');
		}
		
		if (data.message) {
			$("#message").text(data.message);
		} else {
			$("#message").text('');
		}
		if (data.barcode) {
			$("#barcode").text(data.barcode);
		} else {
			$("#barcode").text('');
		}
	};

	ws.onclose = function() {
		if (typeof console !== 'undefined') {
			console.info('WS close');
		}
	};

	ws.onerror = function(err) {
		if (typeof console !== 'undefined') {
			console.info('WS error');
		}
	};
});

