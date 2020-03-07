'use strict';

let stompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
}

function connect() {
    let socket = new SockJS('/secured/chat');
    stompClient = Stomp.over(socket);
    let sessionId = "";

    stompClient.connect({}, function(frame) {
        let url = stompClient.ws._transport.url;
        url.replace( "ws://localhost:8080/app/secured/chat/", "");
        url.replace("/websocket", "");
        url = url.replace(/^[0-9]+\//, "");
        console.log("Your current session is: " + url);

        sessionId = url;

        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/secured/user/queue/specific-user'
            + '-user' + this.sessionId, function(messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
        });
    });
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendMessage() {
    let from = document.getElementById('from').value;
    let text = document.getElementById('text').value;
    // let to = document.getElementById('to').value;
    // let time = document.getElementById('time');
    stompClient.send("/app/chat", {}, JSON.stringify({'from':from, 'text':text}));
}

function showMessageOutput(messageOutput) {
    let response = document.getElementById('response');
    let p = document.createElement('p');

    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(messageOutput.from + ": "
        + messageOutput.text + " (" + messageOutput.time + ")"));
    response.appendChild(p);
}