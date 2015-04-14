package com.example.guestbook;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
// import com.google.appengine.api.datastore.DatastoreService;
// import com.google.appengine.api.datastore.DatastoreServiceFactory;
// import com.google.appengine.api.datastore.Entity;
// import com.google.appengine.api.datastore.Key;
// import com.google.appengine.api.datastore.KeyFactory;

import com.googlecode.objectify.Key;
import static com.googlecode.objectify.ObjectifyService.ofy;

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

public class postCommentSession extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
		  
		 StringBuffer jb = new StringBuffer();
		 String line = null;
		 try {
		 BufferedReader reader = req.getReader();
		 while ((line = reader.readLine()) != null)
			 jb.append(line);
		 } catch (Exception e) { /*report an error*/ }

		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jb.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String id = null;
		String comments = null;
		long returnId = -1;
		try {

            String key = "default";

			// if sessionId isn't -1, we simply retrieve and overwrite entry in database
			Long sessionId = jsonObject.getLong("sessionId");
			out.println("INPUT SESSION ID : " + String.valueOf(sessionId));											
			Key<Guestbook> theBook = Key.create(Guestbook.class, key);			
			
            //Key guestbookKey = KeyFactory.createKey("Guestbook", key);			
            //Entity notes = new Entity("NoteSet", guestbookKey);
			NoteSet notes = null;
			if (sessionId != -1)
			{
				List<NoteSet> noteList = ofy()
					  .load()
					  .type(NoteSet.class) // We want only notes
					  .ancestor(theBook)    // Anyone in this book
					  .list();

				for (NoteSet curnote : noteList) {
					out.println("CURNOTE ID: " + curnote.id);
					if ((long)curnote.id == (long)sessionId)
					{
						notes = curnote;					
					}
				}
		
			} 
			
			if (notes == null)
			{
				out.println("no match");
				notes = new NoteSet();
			    notes.theBook = theBook;
			}
			
			id = jsonObject.getString("youtubeID");
			notes.youtubeID = id;
			//notes.setProperty("youtubeID", id);
			comments = jsonObject.getString("commentList");
			JSONArray arr = new JSONArray(comments);
			List<Integer> commentTimestampList = new ArrayList<Integer>();
			List<String> commentContentList = new ArrayList<String>();
			List<String> commentAuthorList = new ArrayList<String>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject comment = new JSONObject(arr.getString(i));
                commentTimestampList.add(comment.getInt("timestamp"));
                commentContentList.add(comment.getString("content"));		
                commentAuthorList.add(comment.getString("user"));				
				
			}
			notes.commentTimeList = commentTimestampList;
			notes.commentContentList = commentContentList;
			notes.commentAuthorList = commentAuthorList;

            ofy().save().entity(notes).now();
		    returnId = (long)notes.id;
			
				  
			resp.setContentType("application/json");
			JSONObject sid = new JSONObject();
			sid.put("sessionId", String.valueOf(returnId));
			PrintWriter out = resp.getWriter();
			out.write(sid.toString());		

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


   
	  
	  

	//Iterator it = jObj.keys(); //gets all the keys

	//while(it.hasNext())
	//{
	//	String key = it.next(); // get key
	//	Object o = jObj.get(key); // get value
	//	session.putValue(key, o); // store in session
	//}
    }
}
