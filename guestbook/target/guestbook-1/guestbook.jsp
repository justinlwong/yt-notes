<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
</head>
<script src="/jquery-1.11.2.min.js"></script>
<script src="/comments.js"></script>
<script src="/loadYoutube.js"></script>

<body>

<%
    String youtubeLink = request.getParameter("youtubeLink");
    if (youtubeLink == null) {
        youtubeLink  = "dQw4w9WgXcQ";
    }
    pageContext.setAttribute("youtubeLink", youtubeLink);
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
        pageContext.setAttribute("user", user);
%>

<p>Hello, ${fn:escapeXml(user.nickname)}! (You can
    <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
} else {
%>
<p>Hello!
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
    to include your name with greetings you post.</p>
<%
    }
%>

<form action="/guestbook.jsp" method="get">
    <div><input id="link" type="text" name="youtubeLink" value="${fn:escapeXml(youtubeLink)}"/></div>
    <div><input type="submit" value="Load Video"/></div>
</form>

<div id="player"></div>
<div><input id="timestamp" type="hidden" value="0"/></div>
<!--form action="/guestbook.jsp" method="get"-->
<div><input id="comment" type="text"/></div>
<div><button id="commentSubmit" type="button">Add Comment</button></div>
<!--/form-->
<div id="commentList"></div>
<div><button id="commentSave" type="button">Save Comments</button></div>

</body>
</html>