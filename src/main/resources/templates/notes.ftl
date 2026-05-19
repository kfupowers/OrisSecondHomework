<!DOCTYPE html>
<html>
<head>
    <title>Мои заметки</title>
</head>
<body>
<h1>Мои заметки</h1>
<a href="/notes/create">Создать заметку</a>
<#if notes?size == 0>
    <p>У вас пока нет заметок.</p>
<#else>
    <ul>
        <#list notes as note>
            <li>
                <strong>${note.title}</strong><br/>
                ${note.content}<br/>
                <small>Создано: ${note.createdAt}</small>
                <#if note.public>
                    <span style="color:green;">(Публичная)</span>
                <#else>
                    <span style="color:gray;">(Приватная)</span>
                </#if>
                <a href="/notes/${note.id}/edit">Редактировать</a>
                <form action="/notes/${note.id}/delete" method="post" style="display:inline;">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" onclick="return confirm('Удалить?');">Удалить</button>
                </form>
            </li>
        </#list>
    </ul>
</#if>
<a href="/notes/public">Публичные заметки</a>

<script src="/webjars/jquery/jquery.min.js"></script>
<script src="/webjars/sockjs-client/sockjs.min.js"></script>
<script src="/webjars/stomp-websocket/stomp.min.js"></script>
<script src="/websocket.js"></script>
<div id="notification-area" style="position: fixed; top: 10px; right: 10px; width: 300px; z-index: 9999;"></div>
</body>
</html>