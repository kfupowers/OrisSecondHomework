<!DOCTYPE html>
<html>
<head>
    <title>Публичная история чата</title>
</head>
<body>
<h2>Публичная история (последние 50)</h2>
<#list messages as msg>
    <p><strong>${msg.author.username}</strong> [${msg.sentAt?substring(11, 19)}]: ${msg.content}</p>
</#list>
<a href="/login">Войти</a>
</body>
</html>