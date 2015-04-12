package com.example.guestbook;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

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
		try {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
            Key guestbookKey = KeyFactory.createKey("Guestbook", user.getEmail());			
            Entity notes = new Entity("NoteSet", guestbookKey);
			id = jsonObject.getString("youtubeID");
			notes.setProperty("youtubeID", id);
			comments = jsonObject.getString("commentList");
			JSONArray arr = new JSONArray(comments);
			List<Integer> commentTimestampList = new ArrayList<Integer>();
			List<String> commentContentList = new ArrayList<String>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject comment = new JSONObject(arr.getString(i));
                commentTimestampList.add(comment.getInt("timestamp"));
                commentContentList.add(comment.getString("content"));				
				
			}
			notes.setProperty("commentTimeList", commentTimestampList);
			notes.setProperty("commentContentList", commentContentList);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			datastore.put(notes);
			out.println(id);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  
        resp.setContentType("text/html");
		resp.setStatus(resp.SC_OK);
   
	  
	  

	//Iterator it = jObj.keys(); //gets all the keys

	//while(it.hasNext())
	//{
	//	String key = it.next(); // get key
	//	Object o = jObj.get(key); // get value
	//	session.putValue(key, o); // store in session
	//}
    }
}
