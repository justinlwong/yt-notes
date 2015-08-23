package com.example.guestbook;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

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

public class postChat extends HttpServlet {
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
		int curState = -5;
		int curTime = -5;
		int playerEventful = -5;
		long returnId = -1;
		//String chatSender = null;
		try {

            String key = "default";

			// if sessionId isn't -1, we simply retrieve and overwrite entry in database
			Long chatSessionId = jsonObject.getLong("chatSessionId");
			//out.println("INPUT SESSION ID : " + String.valueOf(sessionId));											
			Key<Guestbook> theBook = Key.create(Guestbook.class, key);			
			
			ChatLog chat = null;
			if (chatSessionId != -1)
			{
				List<ChatLog> chatList = ofy()
					  .load()
					  .type(ChatLog.class) // We want only notes
					  .ancestor(theBook)    // Anyone in this book
					  .list();

				for (ChatLog curLog : chatList) {
					//out.println("CURNOTE ID: " + curnote.id);
					if ((long)curLog.id == (long)chatSessionId)
					{
						chat = curLog;					
					}
				}
		
			} 
			
			if (chat == null)
			{
				out.println("no match");
				chat = new ChatLog();
			    chat.theBook = theBook;
				chat.playerEventful = 0;
				chat.owner = jsonObject.getString("chatSender");
				
			}
			
			id = jsonObject.getString("youtubeID");
			curTime = jsonObject.getInt("curTime");
            curState = jsonObject.getInt("curState");
			playerEventful = jsonObject.getInt("playerEventful");
			chat.youtubeID = id;
			chat.curTime = curTime;
			chat.curState = curState;
			// only edit eventful if it was an eventful thing, or else just leave it as is
			if (playerEventful == 1)
			{	
				chat.playerEventful++;// = playerEventful;
			}
			//notes.setProperty("youtubeID", id);
			comments = jsonObject.getString("contentList");
			JSONArray arr = new JSONArray(comments);
			//List<Integer> commentTimestampList = new ArrayList<Integer>();
			List<String> chatContentList = new ArrayList<String>();
			List<String> chatAuthorList = new ArrayList<String>();
			for (int i = 0; i < arr.length(); i++) {
				JSONObject comment = new JSONObject(arr.getString(i));
                //commentTimestampList.add(comment.getInt("timestamp"));
                chatContentList.add(comment.getString("content"));		
                chatAuthorList.add(comment.getString("user"));				
			}
			if (arr.length() == 0)
			{
				chatContentList.add("");
				chatAuthorList.add("");
			}

			chat.chatContentList = chatContentList;
			chat.chatAuthorList = chatAuthorList;

			
            ofy().save().entity(chat).now();
		    returnId = (long)chat.id;
			out.println(returnId);
				  
			resp.setContentType("application/json");
			JSONObject cid = new JSONObject();
			cid.put("chatSessionId", String.valueOf(returnId));
			PrintWriter out = resp.getWriter();
			out.write(cid.toString());		

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
