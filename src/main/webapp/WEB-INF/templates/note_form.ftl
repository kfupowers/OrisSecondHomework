<!DOCTYPE html>
<html>
<head>
    <title><#if note.id??>Редактировать<#else>Создать</#if> заметку</title>
</head>
<body>
<h1><#if note.id??>Редактировать<#else>Создать</#if> заметку</h1>
<form action="<#if note.id??>/notes/${note.id}/edit<#else>/notes/create</#if>" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <div>
        <label>Заголовок:</label>
        <input type="text" name="title" value="${note.title!''}" required/>
    </div>
    <div>
        <label>Содержание:</label>
        <textarea name="content" rows="5" cols="40">${note.content!''}</textarea>
    </div>
    <div>
        <label>Публичная?</label>
        <input type="checkbox" name="public" <#if note.public>checked</#if> />
    </div>
    <button type="submit">Сохранить</button>
    <a href="/notes">Отмена</a>
</form>
</body>
</html>