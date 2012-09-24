function handleViewportEvents(events) {
	var newEvents = new Array();
	
	for(var i = 0; i < events.length; i++) {
		var text = "<div><b>" + events[i].type + "</b>: Hace " + events[i].time +
		"<br />" + "<p>" + events[i].description + "</p></div>";
		
		var type = OTHER;
		if (events[i].type == "Tráfico")
			type = TRAFFIC;
		if (events[i].type == "Accidente Vial")
			type = ACCIDENT;
		else if (events[i].type == "Falla de Infraestructura")
			type = INFRAESTRUCTURE;
		else if (events[i].type == "Agente Externo")
			type = POLICE;
		
		var marker = createMarker(events[i].lat, events[i].lng, type, text);
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

function createMarker(lat, lng, type, text) {
	var url = "images/";
	
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
	    position: new google.maps.LatLng(lat, lng),
	    icon: image
	});
	
	google.maps.event.addListener(marker, 'click', function() {
		infowindow.setContent(text);
		infowindow.open(map, marker);
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