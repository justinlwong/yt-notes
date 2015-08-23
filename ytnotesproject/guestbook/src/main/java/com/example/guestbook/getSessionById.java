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

public class getSessionById extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

            String key = "default";
			Long sessionId = Long.parseLong(req.getParameter("sessionId"));

			
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
		    JSONArray timeArray = new JSONArray();
			JSONArray commentArray = new JSONArray();
			JSONArray authorArray = new JSONArray();
			int count = 0;
			try {
				for (NoteSet note : notes) {

						if ((long)note.id == (long)sessionId)
						{
                            out.println("note id in getsession" + String.valueOf(note.id) + " size " + note.commentTimeList.size());
							for (int i=0; i<note.commentTimeList.size(); i++)
							{
								timeArray.put(i, note.commentTimeList.get(i));
								commentArray.put(i, note.commentContentList.get(i));
								authorArray.put(i, note.commentAuthorList.get(i));
							}
							jsonObj.put("commentTimeList", timeArray);
							jsonObj.put("commentContentList", commentArray);
							jsonObj.put("commentAuthorList", authorArray);						
							jsonObj.put("youtubeID", note.youtubeID);
							jsonObj.put("sessionID", note.id);
							//jsonSession.put("sessionId", note.id);
							//jsonArr.put(count, jsonSession);
							//count ++;
						
                        }

				}     
				
			} catch (JSONException e) {
			// TODO Auto-generated catch block
			}						
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.write(jsonObj.toString());
			//out.flush();
			//resp.setStatus(resp.SC_OK);
			
		} 
    
}
