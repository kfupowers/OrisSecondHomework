<!DOCTYPE html>
<html>
<head>
    <title>Вход</title>
</head>
<body>
<h1>Вход в систему</h1>

<#if RequestParameters.error??>
    <p style="color:red;">Неверное имя пользователя или пароль</p>
</#if>

<form action="/login" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <div>
        <label>Имя пользователя:</label>
        <input type="text" name="username" required/>
    </div>
    <div>
        <label>Пароль:</label>
        <input type="password" name="password" required/>
    </div>
    <button type="submit">Войти</button>
</form>

<p>Нет аккаунта? <a href="/register">Зарегистрируйтесь</a></p>
</body>