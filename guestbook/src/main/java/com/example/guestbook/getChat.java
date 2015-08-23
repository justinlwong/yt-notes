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

public class getChat extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
            String key = "default";
			Long chatSessionId = Long.parseLong(req.getParameter("chatSessionId"));

			
			// Create the correct Ancestor key
			  Key<Guestbook> theBook = Key.create(Guestbook.class, key);
			// Run an ancestor query to ensure we see the most up-to-date
			// view of the chats belonging to the selected Guestbook.
			  List<ChatLog> chats = ObjectifyService.ofy()
				  .load()
				  .type(ChatLog.class) // We want only chats
				  .ancestor(theBook)    // Anyone in this book
				  //.limit(10)             // Only show 5 of them.
				  .list();		
			

			// Loop through and put chats into json format
			JSONObject jsonObj = new JSONObject();
			JSONArray chatArray = new JSONArray();
			JSONArray authorArray = new JSONArray();
			int count = 0;
			try {
				for (ChatLog chat : chats) {

						if ((long)chat.id == (long)chatSessionId)
						{
                            out.println("chat id in getsession" + String.valueOf(chat.id));
							if (chat.chatAuthorList != null)
							{
								for (int i=0; i<chat.chatAuthorList.size(); i++)
								{
									chatArray.put(i, chat.chatContentList.get(i));
									authorArray.put(i, chat.chatAuthorList.get(i));
								}
							}
							jsonObj.put("chatContentList", chatArray);
							jsonObj.put("chatAuthorList", authorArray);						
							jsonObj.put("youtubeID", chat.youtubeID);
							jsonObj.put("playerEventful", chat.playerEventful);
							jsonObj.put("curTime", chat.curTime);
							jsonObj.put("curState", chat.curState);		
						    jsonObj.put("owner", chat.owner);
							//jsonSession.put("chatSessionId", chat.id);
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
