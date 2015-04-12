package com.example.guestbook;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SignGuestbookServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    String guestbookName = req.getParameter("guestbookName");
    Key guestbookKey = KeyFactory.createKey("Guestbook", guestbookName);
    String content = req.getParameter("content");
    Date date = new Date();
    Entity notes = new Entity("NoteSet", guestbookKey);
    if (user != null) {
      notes.setProperty("author_id", user.getUserId());
      notes.setProperty("author_email", user.getEmail());
    }
    notes.setProperty("date", date);
    notes.setProperty("content", content);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(notes);

    resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);
  }
}