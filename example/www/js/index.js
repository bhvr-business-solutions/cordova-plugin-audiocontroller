/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);


        document.getElementById('with-scrubbing').addEventListener('click', function() {
            window.AudioController.setControls({
                track: 'with-scrubbing',
                artist: 'Artist name',
                cover: 'http://prodstoreacc4187.blob.core.windows.net/prod-20170818/profile_1503069410276170_ori.jpeg',
                hasScrubbing: true,
                   duration: 60,
                   elapsed: 0,
            });
        });

        document.getElementById('with-skip-forward').addEventListener('click', function() {
            window.AudioController.setControls({
                track: 'with-skip-forward',
                artist: 'Artist name',
                cover: 'http://prodstoreacc4187.blob.core.windows.net/prod-20170818/profile_1503069410276170_ori.jpeg',
                hasSkipForward: true,
                hasSkipBackward: true,
                                               duration: 1000,
                                               elapsed: 500,
            });
        });
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');

        window.AudioController.setControls({
            track: 'DEFAULT',
            artist: 'Artist name',
            cover: 'http://prodstoreacc4187.blob.core.windows.net/prod-20170818/profile_1503069410276170_ori.jpeg',

            hasPrev: false,
            hasNext: false,
            duration: 300,
            elapsed: 5,
        });

        // var media = new Media('https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', function() {}, function() {}, function(a) {
        //     if (a == 2) {
                
        //     }
        // });

        // media.play();
        
        window.AudioController.subscribe(function(eventName) {
            alert(eventName);
        });
        
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

app.initialize();
