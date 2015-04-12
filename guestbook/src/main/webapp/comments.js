// when submit button is pressed, get comment and add a div
$( document ).ready(function() {
	$("#commentSubmit").click(function() {
		var minute = Math.floor(Math.round($("#timestamp").val()) / 60);
		var second = $("#timestamp").val() % 60;
		if (second < 10)
		{
			second = '0' + second
		}
		var commentContent = minute + ':' + second + ' ' + $("#comment").val();
		$("#commentList").append("<div timestamp='"+$("#timestamp").val() +"' content='"+$("#comment").val()+"'>"+commentContent+"</div>");
		// clear commentbox
		$("#comment").val("");
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
			commentArray.push(commentObj);
		});

		var commArrayObj = new Object();
		commArrayObj.youtubeID = $("#link").val();
		commArrayObj.commentList = commentArray;
		var fullString = JSON.stringify(commArrayObj);
		
		alert(fullString);
		// do post
		$.post('/postCommentSession', fullString )
		 .done(function(data) {
			 alert("POST RESPONDED!");
		});
	});
		

		
});