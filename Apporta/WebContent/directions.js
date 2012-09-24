function keyHandler(e) {
	if(e.keyCode == 13)
		routePlanner();
}

function routePlanner() {
	destinyName = document.getElementById("destiny").value;
	if(destinyName != "")
		generateRoute(destinyName);
}

function generateRoute(destiny) {
	var destinyGeocoder = new google.maps.Geocoder();

	destinyGeocoder.geocode({'bounds': map.getBounds(), 'address': destiny}, function(destinyResults, destinyStatus) {
		if(destinyStatus == google.maps.GeocoderStatus.OK) {
			finalLatLng = new google.maps.LatLng(destinyResults[0].geometry.location.lat(), destinyResults[0].geometry.location.lng());
			processRoute(new google.maps.LatLng(getUserLat(), getUserLng()), finalLatLng);
		}
		else if(destinyStatus == google.maps.GeocoderStatus.ZERO_RESULTS)
			directionsDisplay.setMap(null);
	});
}

function processRoute(originLatLng, destinyLatLng) {
	directionsDisplay.setMap(map);

	var request = {
			origin: originLatLng,
			destination: destinyLatLng,
			travelMode: google.maps.TravelMode.DRIVING
	};

	var directionsService = new google.maps.DirectionsService();
	directionsService.route(request, function(result, status) {
		if (status == google.maps.DirectionsStatus.OK) {
			directionsDisplay.setDirections(result);
			determineIntersection(result.routes[0].overview_path);
		}
	});
}

function determineIntersection(path) {
	var bounds = map.getBounds();
	for(var i=0; i<eventsContainer.length; i++) {
		var latLng = eventsContainer[i].getPosition();
		if(bounds.contains(latLng))
			for(var j=1; j<path.length; j++)
				if(distanceToPath(latLng, path[j-1], path[j]) <= 5)
					alert("¡Evento en tu ruta!");
	}
}