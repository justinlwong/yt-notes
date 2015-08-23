// when submit button is pressed, get comment and add a div
$( document ).ready(function() {
	$("#commentSubmit").click(function() {
		comment_sending = 1;
		var minute = Math.floor(Math.round($("#timestamp").val()) / 60);
		var second = $("#timestamp").val() % 60;
		var user = $("#userCheck").attr("userNick");
		if (second < 10)
		{
			second = '0' + second;
		}
		var time = minute + ':' + second;
		var content = ' ' + $("#comment").val();
		$("#commentList").append("<div class='row' timestamp='"+$("#timestamp").val() +"' content='"+$("#comment").val()+"' user='"+user+"'><div class='timePart'><a>"+time+"</a></div><div class='commentPart'>"+content+"</div><div class='userPart'>("+user+")</div></div>");
		// clear commentbox
		$("#comment").val("");
	    $("#commentSave").click();
	});
	
	$("#comment").keypress(function(event){
		if(event.keyCode == 13){
			$("#commentSubmit").click();
		}
	});
	
	$("#commentSave").click(function() {
		var commentArray = [];
		$("#commentList").children().each(function(){
			var commentObj = new Object();
			var $input = $( this );
			commentObj.timestamp = $input.attr("timestamp");
			commentObj.content = $input.attr("content");
			commentObj.user = $input.attr("user");
			commentArray.push(commentObj);
		});

		var commArrayObj = new Object();
		commArrayObj.youtubeID = $("#link").val();
		commArrayObj.sessionId = $("#sessionCheck").attr("sessionId");
		commArrayObj.commentList = commentArray;
		var fullString = JSON.stringify(commArrayObj);
		
		//alert(fullString);
		// do post
		$.post('/postCommentSession', fullString )
		 .done(function(data) {
			 //alert("Comments Saved!");
			 // only refresh the first time
			 $("#sessionCheck").attr("sessionId", data.sessionId);
			 comment_sending = 0;
			 //if (commArrayObj.sessionId != data.sessionId)
			 //{
			//	 alert("First save, created new session!");
			//     window.location.replace("/index.jsp?youtubeLink="+commArrayObj.youtubeID+"&sessionId="+data.sessionId);
			 //}
		});		
	});
		
	$('#commentList').on('click', '.timePart', function() {
		//alert("hi");
		var $input = $( this );
		player.seekTo(parseInt($input.parent().attr("timestamp")));
	});
		
});