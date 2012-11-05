function handleViewportEvents(events) {
	var newEvents = new Array();
	
	for(var i = 0; i < events.length; i++) {
		var marker = createEventMarker(events[i]);
		newEvents.push(marker);
	}
	
	displayEvents(newEvents);
	emptyEventsContainer();
	eventsContainer = newEvents;
}

function displayEvents(events) {
	for(var i = 0; i < events.length; i++)
		events[i].setMap(map);
}

function emptyEventsContainer() {
	for(var i = 0; i < eventsContainer.length; i++)
		eventsContainer[i].setMap(null);
	eventsContainer.length = 0;
}

function createEventMarker(event) {
	var url = "images/";
	
	var type = OTHER;
	if (event.type == "Tráfico")
		type = TRAFFIC;
	if (event.type == "Accidente Vial")
		type = ACCIDENT;
	else if (event.type == "Falla de Infraestructura")
		type = INFRAESTRUCTURE;
	else if (event.type == "Policía")
		type = POLICE;
	
	switch(type) {
		case 0: url += "trafico.png"; break;
		case 1: url += "accidente_vial.png"; break;
		case 2: url += "falla_infraestructura.png"; break;
		case 3: url += "policia.png"; break;
		case 4: url += "otro.png"; break;
	}
	
	var image = new google.maps.MarkerImage(url,
			new google.maps.Size(31, 33),
			new google.maps.Point(0,0),
			new google.maps.Point(0, 33));
	
	var marker = new google.maps.Marker({
	    position: new google.maps.LatLng(event.lat, event.lng),
	    icon: image
	});
	
	google.maps.event.addListener(marker, 'click', function() {
		if(android) {
			Android.showEventInfoDialog(event.type, event.time, event.description);
		}
		else {
			var text = "<div><b>" + event.type + "</b>: Hace " + event.time +
			"<br />" + "<p>" + event.description + "</p></div>";
			infowindow.setContent(text);
			infowindow.open(map, marker);
		}
	});
	
	return marker;
}

function createUserMarker(lat, lng) {
	var url = "images/usuario.png";
	
	var image = new google.maps.MarkerImage(url,
			new google.maps.Size(16, 15),
			new google.maps.Point(0,0),
			new google.maps.Point(8, 8));
	
	var marker = new google.maps.Marker({
	    position: new google.maps.LatLng(lat, lng),
	    icon: image
	});
	
	return marker;
}