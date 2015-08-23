<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Random" %>
<%@ page import="java.lang.*" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="com.example.guestbook.NoteSet" %>
<%@ page import="com.example.guestbook.ChatLog" %>
<%@ page import="com.example.guestbook.Guestbook" %>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>

<!DOCTYPE html>
<!--[if lt IE 7]><html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]><html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]><html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js"> <!--<![endif]-->
<head>
    <meta charset="utf-8">
    <!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"><![endif]-->
    <title>YT Notes</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <link href="http://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800" rel="stylesheet">

    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/normalize.min.css">
    <link rel="stylesheet" href="css/font-awesome.min.css">
    <link rel="stylesheet" href="css/animate.css">
    <link rel="stylesheet" href="css/templatemo_misc.css">
    <link rel="stylesheet" href="css/templatemo_style.css">

    <script src="js/vendor/modernizr-2.6.2.min.js"></script>
</head>
<body>
    <!--[if lt IE 7]>
    <p class="chromeframe">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">activate Google Chrome Frame</a> to improve your experience.</p>
    <![endif]-->

    
    <div class="bg-overlay"></div>

    <div class="container-fluid">
        <div class="row">
            
            <div class="col-md-4 col-sm-12">
                <div class="sidebar-menu">
                    
                    <div class="logo-wrapper">
                        <h1 class="logo">
                            <a href="#"><img src="images/logo.png" alt="Circle Template">
                            <span>YT Notes : Video Sharing Console</span></a>
                        </h1>
                    </div> <!-- /.logo-wrapper -->
                    
                    <div class="menu-wrapper">
                        <ul class="menu">
                            <li><a class="homebutton" href="#">Add Notes</a></li>
                            <li><a class="show-3" href="#">View Notes</a></li>
                            <li><a class="show-5" href="#">Chat</a></li>

                        </ul> <!-- /.menu -->
                        <a href="#" class="toggle-menu"><i class="fa fa-bars"></i></a>
                    </div> <!-- /.menu-wrapper -->

                    <!--Arrow Navigation-->
                    <a id="prevslide" class="load-item"><i class="fa fa-angle-left"></i></a>
                    <a id="nextslide" class="load-item"><i class="fa fa-angle-right"></i></a>

                </div> <!-- /.sidebar-menu -->
            </div> <!-- /.col-md-4 -->

            <div class="col-md-8 col-sm-12">
                
                <div id="menu-container">

					<div id="notesPage">
                        <div class="row">     
                            <div class="col-md-12">
                                <div class="load-youtube-form">
                                    <div class="row">
										<%
																				
											String youtubeLink = request.getParameter("youtubeLink");
											String setTab = "0";
											if (youtubeLink == null) {
												youtubeLink  = "https://www.youtube.com/watch?v=-CmadmM5cOk";
											}
											pageContext.setAttribute("youtubeLink", youtubeLink);
											
											String chatYoutubeLink = request.getParameter("chatYoutubeLink");
											if (chatYoutubeLink == null) {
												chatYoutubeLink  = "https://www.youtube.com/watch?v=-CmadmM5cOk";
											} else {
												setTab = "1";
											}
											pageContext.setAttribute("chatYoutubeLink", chatYoutubeLink);
											
											
											//String guestbookName = request.getParameter("guestbookName");
											//if (guestbookName == null) {
											//	guestbookName = "default";
											//}
											//pageContext.setAttribute("guestbookName", guestbookName);
											
											UserService userService = UserServiceFactory.getUserService();
											User user = userService.getCurrentUser();
											if (user != null) {
												pageContext.setAttribute("user", user);
												//pageContext.setAttribute("guestbookName", user.getEmail());
												//guestbookName = user.getEmail();
												
										%>

										<p id="userCheck" userNick="${fn:escapeXml(user.nickname)}">Hello, ${fn:escapeXml(user.nickname)}! (You can
											<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>
										<%
										} else {
											Random rand = new Random();
											String randomNum = "Guest-" +  String.valueOf(rand.nextInt(9000)+999);
											
										%>
										<p  id="userCheck" userNick="<%=randomNum%>">Hello!
											<a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
											to include your name with notes you post.</p>
										<%
											}
											
									
										%>
                                    	<form action="/index.jsp" method="get">
                                            <fieldset class="col-md-4">
												<input id="link" type="text" name="youtubeLink" value="${fn:escapeXml(youtubeLink)}"/>												
                                            </fieldset>
                                             <fieldset class="col-md-4">
												<input type="submit" value="Load Video"/>											
                                            </fieldset>
                                             <fieldset class="col-md-4">
												<input type="submit" value="Reset"/>											
                                            </fieldset>
                                        </form>
                                    </div> <!-- /.row -->
                                </div> <!-- /.load-youtube-form -->
                            </div> <!-- /.col-md-12 -->
							

							<div class="toggle-content text-center" id="YoutubeVideoContainer">
								<div id="player"></div>
								<div><input id="timestamp" type="hidden" value="0"/></div>
								<div><input id="comment" type="text"/></div>
								<div><button id="commentSubmit" type="button">Add Comment</button></div>
								<div id="commentList">
									<%
										String sessionId = request.getParameter("sessionId");
										
										if (sessionId != null )
										{
											pageContext.setAttribute("sessionId", sessionId);
			
											String guestKey = "default";
											//if (user != null)
											//{
											//	email = user.getEmail();				
											//}
											
											Key<Guestbook> theBook = Key.create(Guestbook.class, guestKey);	
											List<NoteSet> notes = ObjectifyService.ofy()
												  .load()
												  .type(NoteSet.class) // We want only notes
												  .ancestor(theBook)    // Anyone in this book
                                                  .list();
											
											NoteSet curNote = null;

											for (NoteSet note : notes) {
												if (note.id == Long.parseLong(sessionId))
                                                    curNote = note;													
                                            }
											if (curNote != null)
											{
												for (int i=0; i<curNote.commentContentList.size(); i++)
												{
													int curTime = curNote.commentTimeList.get(i);
													String curComment = curNote.commentContentList.get(i);
													int minute = curTime / 60;
													int second = curTime % 60;
													String minuteString = String.valueOf(minute);
													String secondString = String.valueOf(second);
													if (second < 10)
													{
														secondString = "0" + secondString;
													}
													String time = minuteString + ":" + secondString;
													String content = " " + curComment;
													String userNick = curNote.commentAuthorList.get(i);
							
										%>
										<div class='row' timestamp='<%=String.valueOf(curTime)%>' content='<%=curComment%>' user='<%=userNick%>'><div class='timePart'><a><%=time%></a></div><div class='commentPart'><%=content%></div><div class='userPart'>(<%=userNick%>)</div></div>
										<%
												}
											}
										%>
										</div>
										<div id="sessionCheck" sessionId="${fn:escapeXml(sessionId)}"></div>									
										<%	
										}
										else {
										%>
										</div>
										<div id="sessionCheck" sessionId="-1"></div>												
										<%
										}
                                    %>									
								<div id="test"></div>
								<div><button id="commentSave" type="button">Save Comments</button></div>
								<div class="row">
									<div id="shareCommentBox">Type a message to save and get link to share comment session!</div>
								</div>
							</div>
						</div>
					</div> <!-- /.col-md-12 -->



                    <div id="menu-3" class="gallery content">
                        <div class="row">                           
						<!-- This will be injected with divs from GET call from datastore -->
                        </div> <!-- /.row -->
                    </div> <!-- /.gallery -->
					
                    <div id="menu-5" class="chat content">
						<div class="col-md-12">
							<div class="load-youtube-form">
								<div class="row">
									<form action="/index.jsp" method="get">
											<fieldset class="col-md-4">
												<input id="chatYoutubeLink" type="text" name="chatYoutubeLink" value="${fn:escapeXml(chatYoutubeLink)}"/>												
											</fieldset>
											 <fieldset class="col-md-4">
												<input type="submit" value="Load Video"/>											
											</fieldset>
											 <fieldset class="col-md-4">
												<input type="submit" value="Reset"/>											
											</fieldset>
									</form>
								</div>
							</div>
						</div>
                        <div class="row">
							<div class="toggle-content text-center" id="chatDiv">
								<div id="panel">
									<div class="panelPartition" id="player2"></div>
									
									<div class="panelPartition" id="chatCol">
										<div id="chatContainer">
										<%

											String chatSessionId = request.getParameter("chatSessionId");										
											if (chatSessionId != null )
											{
												pageContext.setAttribute("chatSessionId", chatSessionId);
				
												String guestKey = "default";
												
												Key<Guestbook> theBook = Key.create(Guestbook.class, guestKey);	
												List<ChatLog> chats = ObjectifyService.ofy()
													  .load()
													  .type(ChatLog.class) // We want only chats
													  .ancestor(theBook)    // Anyone in this book
													  .list();
												
												ChatLog curChat = null;

												for (ChatLog note : chats) {
													if (note.id == Long.parseLong(chatSessionId))
														curChat = note;													
												}
												if (curChat != null)
												{
													for (int i=0; i<curChat.chatContentList.size(); i++)
													{
														String curMessage = curChat.chatContentList.get(i);
														String userNick = curChat.chatAuthorList.get(i);
								
											%>
											<div class="messageDiv" author="<%=userNick%>" message="<%=curMessage%>"><%=userNick%> : <%=curMessage%></div>
											<%
													}
												}
											%>
										</div>
										<div id="chatSessionCheck" chatSessionId="${fn:escapeXml(chatSessionId)}" setTab="<%=setTab%>"></div>									
										<%	
											}
											else {
										%>
										</div>
										<div id="chatSessionCheck" chatSessionId="-1" setTab="<%=setTab%>"></div>												
									<%
											}
                                    %>	
									<div><input id="chatBox" type="text"/></div>
									<div><button id="chatSubmit" type="button">Send</button></div>
									</div>

								</div>
								<div class="row">
									<div id="shareChatBox">Type a message to establish a chat session! You will be the owner and can control video synchronization. (Warning: Sharing chat with yourself can cause sync issues.)</div>
								</div>
							</div>

							</div>
								<!--div id="player"></div-->                        	
							<!--/div-->
                        </div> <!-- /.row -->
                    </div> <!-- /.chat -->

                </div> <!-- /#menu-container -->

            </div> <!-- /.col-md-8 -->

        </div> <!-- /.row -->
    </div> <!-- /.container-fluid -->
    


    <script src="js/vendor/jquery-1.10.1.min.js"></script>
    <script>window.jQuery || document.write('<script src="js/vendor/jquery-1.10.1.min.js"><\/script>')</script>
    <script src="js/jquery.easing-1.3.js"></script>
    <script src="js/bootstrap.js"></script>
    <script src="js/plugins.js"></script>
    <script src="js/loadYoutube.js"></script>
    <script src="js/main.js"></script>
    <script src="js/comments.js"></script>
    <script type="text/javascript">
            
			jQuery(function ($) {

                $.supersized({

                    // Functionality
                    slide_interval: 3000, // Length between transitions
                    transition: 1, // 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
                    transition_speed: 700, // Speed of transition

                    // Components                           
                    slide_links: 'blank', // Individual links for each slide (Options: false, 'num', 'name', 'blank')
                    slides: [ // Slideshow Images
						{
                            image: 'images/img6.jpg'
                        }, {
                            image: 'images/img7.jpg'
						}, {
                            image: 'images/img2.jpg'
                        }, {
                            image: 'images/img3.jpg'
                        }, {
                            image: 'images/img4.jpg'
                        }, {
                            image: 'images/img11.jpg'
                        }
                        
                    ]

                });
            });
            
    </script>
    
    	<!-- Google Map -->
        <script src="http://maps.google.com/maps/api/js?sensor=true"></script>
        <script src="js/vendor/jquery.gmap3.min.js"></script>
        <!-- Google Map Init-->
        <script type="text/javascript">
           function templatemo_map() {
                $('.google-map').gmap3({
                    marker:{
                        address: '16.8496189,96.1288854' 
                    },
                        map:{
                        options:{
                        zoom: 15,
                        scrollwheel: false,
                        streetViewControl : true
                        }
                    }
                });
            }
        </script>
</body>
</html>