<!DOCTYPE html>
<html>
<head>
    <title>Чат</title>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
</head>
<body>
<h2>Общий чат</h2>
<div id="messages">
    <#list messages as msg>
        <p><strong>${(msg.author.username)!'Аноним'}</strong> [${msg.sentAt?substring(11, 19)}]: ${msg.content}</p>
    </#list>
</div>
<br/>
<input type="text" id="messageInput" placeholder="Введите сообщение..."/>
<button onclick="sendMessage()">Отправить</button>

<script>
    const username = "${currentUser}";
</script>
<script>
    <#noparse>
    const stompClient = Stomp.over(new SockJS('/ws'));

    stompClient.connect({}, () => {
        stompClient.subscribe('/topic/messages', (messageOutput) => {
            const msg = JSON.parse(messageOutput.body);
            const div = document.getElementById('messages');
            const time = new Date(msg.sentAt).toLocaleTimeString();
            const author = msg.author ? msg.author.username : 'Аноним';
            div.innerHTML += `<p><strong>${author}</strong> [${time}]: ${msg.content}</p>`;
        });
    });

    function sendMessage() {
        const input = document.getElementById('messageInput');
        const content = input.value.trim();
        if (content && stompClient) {
            stompClient.send('/app/send', {}, JSON.stringify({ content: content }));
            input.value = '';
        }
    }
    </#noparse>
</script>

<a href="/chat/my">Мои сообщения</a> | <a href="/chat/public">Публичная история</a>
</body>
</html>