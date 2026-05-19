let stompClient = null;

function connectWebSocket() {
    const socket = new SockJS('/ws-notes');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/new-note', function (message) {
            const note = JSON.parse(message.body);
            alert(`Новая заметка: "${note.title}" от ${note.author}`);
            $('#notification-area').prepend(`<div class="alert alert-info">${note.author} создал(а) заметку "${note.title}"</div>`);
        });
    }, function (error) {
        console.error('STOMP error', error);
    });
}

$(document).ready(function () {
    connectWebSocket();
});