<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.example.guestbook.NoteSet" %>
<%@ page import="com.example.guestbook.Guestbook" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>

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
	
    String guestbookName = request.getParameter("guestbookName");
    if (guestbookName == null) {
        guestbookName = "default";
    }
    pageContext.setAttribute("guestbookName", guestbookName);
	
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
        pageContext.setAttribute("user", user);
		pageContext.setAttribute("guestbookName", user.getEmail());
		guestbookName = user.getEmail();
		
%>

<p>Hello, ${fn:escapeXml(user.nickname)}! (You can
    <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
<%
} else {
%>
<p>Hello!
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
    to include your name with notes you post.</p>
<%
    }
	
    // Create the correct Ancestor key
      Key<Guestbook> theBook = Key.create(Guestbook.class, guestbookName);
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the Notes belonging to the selected Guestbook.
      List<NoteSet> notes = ObjectifyService.ofy()
          .load()
          .type(NoteSet.class) // We want only notes
          .ancestor(theBook)    // Anyone in this book
          .limit(5)             // Only show 5 of them.
          .list();

    if (notes.isEmpty()) {
%>
<p>Guestbook '${fn:escapeXml(guestbookName)}' has no messages.</p>
<%
    } else {
%>
<p>Messages in Guestbook '${fn:escapeXml(guestbookName)}'.</p>
<%
      // Look at all of our notes
        for (NoteSet note : notes) {
            pageContext.setAttribute("note_content", note.commentContentList.get(0));
            pageContext.setAttribute("note_youtubeID", note.youtubeID);
%>
<p><b>${fn:escapeXml(note_youtubeID)}</b> :</p>
<blockquote>${fn:escapeXml(note_content)}</blockquote>
<%
        }
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