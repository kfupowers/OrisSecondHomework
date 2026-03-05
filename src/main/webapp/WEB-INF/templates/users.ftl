<#ftl output_format="plainText">
<#list users as user>User{id=${user.id}, username='${user.username}'}<#if user_has_next>

</#if></#list>