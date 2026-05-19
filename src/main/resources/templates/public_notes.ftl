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
</body>
</html>