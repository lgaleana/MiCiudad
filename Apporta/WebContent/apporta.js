function handleViewportEvents(events) {
	emptyEventsContainer();
	
	for(var i = 0; i < events.length; i++) {
		var text = "<div><b>" + events[i].type + "</b>: " + events[i].time +
		"<br />" + "<p>" + events[i].description + "</p></div>";
		var marker = createMarker(events[i].lat, events[i].lng, text);
		eventsContainer.push(marker);
	}
	
	displayEvents();
}

function displayEvents() {
	for(var i = 0; i < eventsContainer.length; i++)
		eventsContainer[i].setMap(map);
}

function emptyEventsContainer() {
	for(var i = 0; i < eventsContainer.length; i++)
		eventsContainer[i].setMap(null);
	eventsContainer.length = 0;
}

function currentTime() {
	var date = new Date();
	return date.getFullYear() + "-" + date.getMonth() + "-" +
	   date.getDate() + " " + date.getHours() + ":" +
	   date.getMinutes() + ":" + date.getSeconds();
}