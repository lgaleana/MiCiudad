var xmlhttp;

function setRequest(method, url, body, func) {
	if (window.XMLHttpRequest) {
		// code for IE7+, Firefox, Chrome, Opera, Safari
  		xmlhttp = new XMLHttpRequest();
  	} else {
  		// code for IE6, IE5
  		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
  	}
	
	xmlhttp.open(method, url, true);
	xmlhttp.onreadystatechange = func;
	xmlhttp.send(body);
}

function web_getEvents() {
	var url = "events";
	
	setRequest("GET", url, null, function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			var jObject = JSON.parse(xmlhttp.responseText);
			console.log(jObject);
		}
	});
}

function web_getViewportEvents(bounds) {
	var northEast = bounds.getNorthEast();
	var southWest = bounds.getSouthWest();
	
	var upperLat = northEast.lat();
	var upperLng = northEast.lng();
	var lowerLat = southWest.lat();
	var lowerLng = southWest.lng();
	
	var url = "events/" + upperLat + "," + upperLng + "," + lowerLat + "," +
			  lowerLng;
	
	setRequest("GET", url, null, function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			var jObject = JSON.parse(xmlhttp.responseText).events;
			handleViewportEvents(jObject);
		}
	});
}

function web_saveEvent(pos) {
	var url = "events";
	var datetime = currentTime();
	var description = "Evento de prueba";
	var type = "Otro";
	
	var body = pos.lat() + "|" + pos.lng() + "|" + datetime + "|" + description +
			   "|" + type;
	
	setRequest("POST", url, body, function() {
		if (xmlhttp.readyState==4 && xmlhttp.status==200) {
			getViewportEvents(map.getBounds());
		}
	});
}

function web_determineIntersection(path) {
	var bounds = map.getBounds();
	for(var i=0; i<eventsContainer.length; i++) {
		var latLng = eventsContainer[i].getPosition();
		if(bounds.contains(latLng))
			for(var j=1; j<path.length; j++)
				if(distanceToPath(latLng, path[j-1], path[j]) <= 5)
					alert("¡Evento en tu ruta!");
	}
}