package com.example.guestbook;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.text.ParseException;
import java.io.*;
import java.lang.*;
import java.util.Properties;
import java.util.*;

import static java.lang.System.out;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;
//import com.google.gson.*;

public class getCommentSessions extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
		
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
            String key = "default";
			String nickname = "Guest";
			if (user != null)
			{
				nickname = user.getNickname();				
			}
			
			// Create the correct Ancestor key
			  Key<Guestbook> theBook = Key.create(Guestbook.class, key);
			// Run an ancestor query to ensure we see the most up-to-date
			// view of the Notes belonging to the selected Guestbook.
			  List<NoteSet> notes = ObjectifyService.ofy()
				  .load()
				  .type(NoteSet.class) // We want only notes
				  .ancestor(theBook)    // Anyone in this book
				  //.limit(10)             // Only show 5 of them.
				  .list();		
			

			// Loop through and put notes into json format
			JSONObject jsonObj = new JSONObject();
		    JSONArray jsonArr = new JSONArray();
			int count = 0;
			for (NoteSet note : notes) {
				try {
					// only load ones they contributed to
					if (note.commentAuthorList.contains(nickname))
					{
						JSONObject jsonSession = new JSONObject();
						jsonSession.put("youtubeID", note.youtubeID);
						jsonSession.put("sessionId", note.id);
						jsonArr.put(count, jsonSession);
						count ++;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}

			}     
			
			try {			
				jsonObj.put("sessionList", jsonArr);
			}
			catch (JSONException e) {
					// TODO Auto-generated catch block
			}
				
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.write(jsonObj.toString());
			//out.flush();
			//resp.setStatus(resp.SC_OK);
			
		} 
    
}
