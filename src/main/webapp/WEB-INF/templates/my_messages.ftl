<!DOCTYPE html>
<html>
<head>
    <title>Мои сообщения</title>
</head>
<body>
<h2>Мои сообщения</h2>
<#list messages as msg>
    <p>
        [${msg.sentAt?substring(0, 19)}] ${msg.content}
    <form action="/chat/${msg.id}/delete" method="post" style="display:inline;">
        <input type="submit" value="Удалить"/>
    </form>
    </p>
</#list>
<a href="/chat">Вернуться в чат</a>
</body>
</html>