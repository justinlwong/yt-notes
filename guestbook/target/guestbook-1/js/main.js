(function (window, $) {
	'use strict';

	// Cache document for fast access.
	var document = window.document;


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
			$.get('/getCommentSessions')
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
	function addChat() {
		$("#ChatContainer").append("<div>whatever</div>");
		setTimeout(addChat, 1000);
	}	
	$("#chatSubmit").click(function() {

		var text = $('#chatBox').val();
		addChat();

		//alert(text);
		// do post
		//$.post('/sendMessage', {message: text})
		// .done(function(data) {
		//	 alert("Comments Saved!");
		//});
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


})(window, jQuery);

