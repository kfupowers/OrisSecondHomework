<!DOCTYPE html>
<html>
<head>
    <title>Публичные заметки</title>
</head>
<body>
<h1>Публичные заметки</h1>
<#if notes?size == 0>
    <p>Нет публичных заметок.</p>
<#else>
    <ul>
        <#list notes as note>
            <li>
                <strong>${note.title}</strong><br/>
                ${note.content}<br/>
                <small>Автор: ${note.author.username} | Создано: ${note.createdAt}</small>
            </li>
        </#list>
    </ul>
</#if>
<a href="/notes">Мои заметки</a>

<script src="/webjars/jquery/jquery.min.js"></script>
<script src="/webjars/sockjs-client/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/stomp.min.js"></script>
<script src="/websocket.js"></script>
<div id="notification-area" style="position: fixed; top: 10px; right: 10px; width: 300px; z-index: 9999;"></div>
</body>
</html>