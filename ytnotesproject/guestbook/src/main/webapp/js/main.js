// 2. This code loads the IFrame Player API code asynchronously.
"use strict";
var tag = document.createElement('script');
var eventful = 0;
var counter = -1;
var playstatus = 0;
var videoSyncing = 0;
var comment_sending = 0;
var chat_message_sending = 0;
var testVar = 0;
var prevEvent = 0;
var owner = "";

tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
var player;
var player2;

function onYouTubeIframeAPIReady() {
	
	var video_id = $("#link").val().split('v=')[1];
	var ampersandPosition = video_id.indexOf('&');
	if(ampersandPosition != -1) {
	  video_id = video_id.substring(0, ampersandPosition);
	}
	
	var video_id2 = $("#chatYoutubeLink").val().split('v=')[1];
	var ampersandPosition2 = video_id.indexOf('&');
	if(ampersandPosition2 != -1) {
	  video_id2 = video_id2.substring(0, ampersandPosition2);
	}

	player = new YT.Player('player', {
	  height: '390',
	  width: '640',
	  videoId: video_id,
	  events: {
		'onReady': onPlayerReady,
		'onStateChange': onPlayerStateChange
	  }
	});
	
	player2 = new YT.Player('player2', {
	  height: '390',
	  width: '640',
	  videoId: video_id2,
	  events: {
		'onStateChange': chatVideoStateChange
	  }
	});
}

function chatVideoStateChange(event) {

	console.log("state: " + event.data + " video syncing : " + videoSyncing + " preEvent : " + prevEvent);
	
	if ((owner == $("#userCheck").attr("userNick"))&&(event.data == YT.PlayerState.PLAYING || event.data == YT.PlayerState.PAUSED))
	{
		console.log("state change post");
		// send a post
		chat_message_sending = 1;
		// 
		var chatArray = [];
		$("#chatContainer").children().each(function(){
			var chatObj = new Object();
			var $input = $( this );
			chatObj.content = $input.attr("message");
			chatObj.user = $input.attr("author");
			chatArray.push(chatObj);
		});

		var chatArrayObj = new Object();
		chatArrayObj.youtubeID = $("#chatYoutubeLink").val();
		chatArrayObj.chatSessionId = $("#chatSessionCheck").attr("chatSessionId");
		chatArrayObj.contentList = chatArray;
		chatArrayObj.playerEventful = 1;
		chatArrayObj.curState = player2.getPlayerState();
		chatArrayObj.curTime = player2.getCurrentTime();
		chatArrayObj.chatSender = $("#userCheck").attr("userNick");
		var fullString = JSON.stringify(chatArrayObj);
		
        eventful += 1;
		// do post
		 $.post('/postChat', fullString )
		  .done(function(data) {

			 $("#chatSessionCheck").attr("chatSessionId", data.chatSessionId);
			 chat_message_sending = 0;
		 });
	}
	
    // then initiate poll to check for further seek changes	
	if (event.data == YT.PlayerState.PLAYING)
	{
		playstatus = 1;
		checkTime();
	}
	else 
	{
		playstatus = 0;
	}
	
	prevEvent = event.data;
	

}

function checkTime(){
	var actualTime = player2.getCurrentTime();
	counter += 1;
	//alert("yttime: " + actualTime + " counter : " + counter);
	
	if (((Math.abs(counter - actualTime)) > 4))
	{
		console.log("TEST VAR: " + testVar);
		console.log("seek post");
		//alert ("Seeked!");
		//console.log("seeked " + counter + " " + actualTime);
		counter = actualTime-1;
		if ((videoSyncing == 0) && (owner == $("#userCheck").attr("userNick")))
		{
			// send a post
			chat_message_sending = 1;
			// 
			var chatArray = [];
			$("#chatContainer").children().each(function(){
				var chatObj = new Object();
				var $input = $( this );
				chatObj.content = $input.attr("message");
				chatObj.user = $input.attr("author");
				chatArray.push(chatObj);
			});

			var chatArrayObj = new Object();
			chatArrayObj.youtubeID = $("#chatYoutubeLink").val();
			chatArrayObj.chatSessionId = $("#chatSessionCheck").attr("chatSessionId");
			chatArrayObj.contentList = chatArray;
			chatArrayObj.playerEventful = 1;
			chatArrayObj.curState = player2.getPlayerState();
			chatArrayObj.curTime = player2.getCurrentTime();
		    chatArrayObj.chatSender = $("#userCheck").attr("userNick");
			var fullString = JSON.stringify(chatArrayObj);
			
			eventful += 1;
			// do post
			 $.post('/postChat', fullString )
			  .done(function(data) {

				 $("#chatSessionCheck").attr("chatSessionId", data.chatSessionId);
				 chat_message_sending = 0;
			 });
	    }
	}
	if (playstatus == 1)
	{
		setTimeout(checkTime, 1000);
	}

}

// 4. The API will call this function when the video player is ready.
function onPlayerReady(event) {
//event.target.playVideo();
}

function onPlayerStateChange(event) {
if (event.data == YT.PlayerState.PLAYING) {
  updateTime();
}
}

function updateTime() {
    var time = player.getCurrentTime();
	$("#timestamp").val(Math.round(time));
    setTimeout(updateTime, 1000);
}

$( document ).ready(function() {
	
    $(".menu").slideToggle(0);
	
	// set tab on load
	setTab();

	function setTab()
	{
        //alert(parseInt($("#chatSessionCheck").attr("setTab")));
		if ((parseInt($("#chatSessionCheck").attr("chatSessionId")) != -1 )|| (parseInt($("#chatSessionCheck").attr("setTab")) == 1))
		{
			//alert("Entered this");
		    $("#menu-container .content, #menu-container #notesPage").hide();
			$("#menu-container #menu-5").addClass("animated fadeInDown").show();			
		}			
	}
	
	updateContent();
	function updateContent() {
		if (($("#sessionCheck").attr("sessionId") != -1) && (comment_sending == 0))
		{
			testVar += 1;
			$.get('/getSessionById', {sessionId : $("#sessionCheck").attr("sessionId")})
			 .done(function(data) {
				$("#commentList").empty();

                for (var i=0; i< data.commentTimeList.length; i++)
                {
					var curTime = data.commentTimeList[i];
					var curComment = data.commentContentList[i];
					var minute = Math.floor(Math.round(curTime) / 60);
					var second = curTime % 60;
					//String minuteString = String.valueOf(minute);
					//String secondString = String.valueOf(second);
					if (second < 10)
					{
						second = "0" + second;
					}
					var time = minute + ":" + second;
					var content = " " + curComment;
					var userNick = data.commentAuthorList[i];				
					$("#commentList").append("<div class='row' timestamp='"+curTime +"' content='"+curComment+"' user='"+userNick+"'><div class='timePart'><a>"+time+"</a></div><div class='commentPart'>"+content+"</div><div class='userPart'>("+userNick+")</div></div>");
				}
				if ($("#shareCommentBox > *").length == 0)
				{
					// share into sharebox
					$("#shareCommentBox").val("");
					$("#shareCommentBox").empty();
					var link = "http://" + window.location.host + "/index.jsp?youtubeLink="+data.youtubeID+"&sessionId="+data.sessionID;
					$("#shareCommentBox").append("<div>Share comments with friends : </div><input class='commentLinkInput' type='text' size='75' value='"+link+"' onclick='this.select()'>");	
                }					
			 });
		}
		if (($("#chatSessionCheck").attr("chatSessionId") != -1) && (chat_message_sending == 0))
		{
			// if this is not null default to show chat tab
			
			$.get('/getChat', {chatSessionId : $("#chatSessionCheck").attr("chatSessionId")})
			 .done(function(data) {
				 owner = data.owner;
				 
				 // check for sync
				console.log("EVENT log: " + data.playerEventful + " " + eventful);			 
				if ( data.playerEventful > eventful)
                {

					videoSyncing = 1;
					console.log("Received event...syncing: 0" + videoSyncing);
					if(data.curState == 1)
					{
						console.log("State on server is playing");
						player2.seekTo(data.curTime);
						player2.playVideo();
					} else if (data.curState == 2)
					{
						console.log("State on server is paused. ")
						player2.pauseVideo();
						
					}
					
					// update seq number
					eventful = data.playerEventful;
					// set a time period to let the video sync
					setTimeout(videoSyncing = 0, 1000);
				}					
				 
				$("#chatContainer").empty();
				

                for (var i=0; i< data.chatAuthorList.length; i++)
                {
					var message = data.chatContentList[i];
					var user = data.chatAuthorList[i];				
					$("#chatContainer").append("<div class='messageDiv' author='"+user+"' message='"+message+"'>"+user + " : "+message+"</div>");

				}	
				$("#chatContainer").scrollTop($("#chatContainer").prop('scrollHeight'));				
				// share into sharebox
				if ($("#shareChatBox > *").length == 0)
				{
					$("#shareChatBox").val("");
					$("#shareChatBox").empty();
					var link = "http://" + window.location.host + "/index.jsp?chatSessionId="+$("#chatSessionCheck").attr("chatSessionId")+"&chatYoutubeLink=" + data.youtubeID;
					$("#shareChatBox").append("<div>Share chat link with friends : </div><input class='linkInput' type='text' size='75' value='"+link+"' onclick='this.select()'>");
				}				
			 });
			 
			 
		}
		//$("#test").empty();		
		//$("#test").append("<div class='whatever'>whatever</div>");
		setTimeout(updateContent, 2000);
	}


	/************** Toggle Menu *********************/
	$('a.toggle-menu').click(function(){
        $(".menu").slideToggle(400);
		return false;
    });




    /************** Open Different Pages *********************/
	$(".menu a").click(function(){
		var id =  $(this).attr('class');
		id = id.split('-');
		$("#menu-container .content, #menu-container #notesPage").hide();
		// If gallery, then reload it before doing the show
		if (id[1]=="3")
		{
			$.get('/getCommentSessions', {user : $("#userCheck").attr("userNick")})
			 .done(function(data) {
				// clear before reloading
				$("#menu-container #menu-3 .row").empty();
				for (var i=0; i< data.sessionList.length; i++)
				{
					var link = data.sessionList[i].youtubeID;
					var sessionId = data.sessionList[i].sessionId;
					 
					var video_id = link.split('v=')[1];
					var ampersandPosition = video_id.indexOf('&');
					if(ampersandPosition != -1) {
					  video_id = video_id.substring(0, ampersandPosition);
					}
					
					var thumbLink = "http://img.youtube.com/vi/" + video_id + "/hqdefault.jpg";
					var homeLink = "/index.jsp?youtubeLink="+link+"&sessionId="+sessionId;
					//alert(homeLink);

					$("#menu-container #menu-3 .row").append("<div class='col-md-4 col-ms-6'><div class='g-item'><img src='"+thumbLink+"' alt=''><a data-rel='lightbox' class='overlay' href='"+homeLink+"'><span>+</span></a></div> <!-- /.g-item --></div> <!-- /.col-md-4 -->");
			    }
				$("#menu-container #menu-"+id[1]).addClass("animated fadeInDown").show();				
			}, "json");			
		} else 
		{
			$("#menu-container #menu-"+id[1]).addClass("animated fadeInDown").show();			
		}

		return false;
	});

	$(".menu a.homebutton").click(function(){
		$("#menu-container #notesPage").addClass("animated fadeInDown").show();
	});

	//$("#menu-container #menu-3 .row").on('click', '.overlay', function() {
	//	alert("hi");
	//});

	$(window).resize(function(){
		if ($(window).width() <= 769){	
			$(".menu a").click(function(){
				$(".menu").hide();
				return false;
			});
		}	
	});

	/*
	var dt = window.atob('IC0gPGEgcmVsPSJub2ZvbGxvdyIgaHJlZj0iaHR0cDovL3d3dy50ZW1wbGF0ZW1vLmNvbS9wcmV2aWV3L3RlbXBsYXRlbW9fNDEwX2NpcmNsZSI+Q2lyY2xlPC9hPiBieSA8YSByZWw9Im5vZm9sbG93IiBocmVmPSJodHRwOi8vd3d3LnRlbXBsYXRlbW8uY29tIj5GcmVlIFRlbXBsYXRlczwvYT4='); 
	var y = document.getElementById('footer-text');
	y.innerHTML += dt;
	*/

	/************** Tabs *********************/
	$('ul.tabs').each(function(){
		// For each set of tabs, we want to keep track of
		// which tab is active and it's associated content
		var $active, $content, $links = $(this).find('a');

		// If the location.hash matches one of the links, use that as the active tab.
		// If no match is found, use the first link as the initial active tab.
		$active = $($links.filter('[href="'+location.hash+'"]')[0] || $links[0]);
		$active.addClass('active');

		$content = $($active[0].hash);

		// Hide the remaining content
		$links.not($active).each(function () {
		$(this.hash).hide();  
		});

		// Bind the click event handler
		$(this).on('click', 'a', function(e){
		// Make the old tab inactive.
		$active.removeClass('active');
		$content.hide();

		// Update the variables with the new link and content
		$active = $(this);
		$content = $(this.hash);

		// Make the tab active.
		$active.addClass('active');
		$content.slideToggle();

		// Prevent the anchor's default click action
		e.preventDefault();
		});
	});

	$("#chatSubmit").click(function() {
        chat_message_sending = 1;
		var message = $("#chatBox").val();
		var user = $("#userCheck").attr("userNick");
		$("#chatContainer").append("<div class='messageDiv' author='"+user+"' message='"+message+"'>"+user + " : "+message+"</div>");
		$("#chatBox").val("");
		$("#chatContainer").scrollTop($("#chatContainer").prop('scrollHeight'));
		// 
		var chatArray = [];
		$("#chatContainer").children().each(function(){
			var chatObj = new Object();
			var $input = $( this );
			chatObj.content = $input.attr("message");
			chatObj.user = $input.attr("author");
			chatArray.push(chatObj);
		});

		var chatArrayObj = new Object();
		chatArrayObj.youtubeID = $("#chatYoutubeLink").val();
		chatArrayObj.chatSessionId = $("#chatSessionCheck").attr("chatSessionId");
		chatArrayObj.contentList = chatArray;
		chatArrayObj.playerEventful = 0;
		chatArrayObj.curState = player2.getPlayerState();
		chatArrayObj.curTime = player2.getCurrentTime();
		chatArrayObj.chatSender = $("#userCheck").attr("userNick");
		var fullString = JSON.stringify(chatArrayObj);
		

		// do post
		 $.post('/postChat', fullString )
		  .done(function(data) {

			 $("#chatSessionCheck").attr("chatSessionId", data.chatSessionId);
			 chat_message_sending = 0;
		 });		

	});

	$("#chatBox").keypress(function(event){
		if(event.keyCode == 13){
			$("#chatSubmit").click();
		}
	});

	/************** LightBox *********************/
	$(function(){
		$('[data-rel="lightbox"]').lightbox();
	});


});

