package com.example.guestbook;

import java.io.IOException;
import javax.servlet.http.*;
import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.MessageType;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import static java.lang.System.out;

@SuppressWarnings("serial")
public class sendMessage extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
          throws IOException {
			  
			JID jid = new JID("example@gmail.com");
			String msgBody = req.getParameter("message");//"Someone has sent you a gift on Example.com. To view: http://example.com/gifts/";
			Message msg = new MessageBuilder()
				.withRecipientJids(jid)
				.withBody(msgBody)
				.build();

			boolean messageSent = false;
			XMPPService xmpp = XMPPServiceFactory.getXMPPService();
			SendResponse status = xmpp.sendMessage(msg);
			messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);
            out.println("messageSent: " + messageSent);
			if (!messageSent) {
				// Send an email message instead...
			}
    }
}