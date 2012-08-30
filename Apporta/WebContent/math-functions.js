/* 
		    * Although distances should be meassured by finding great circle intersections,
		    * I assume that because the distances meassured are too small, we can treat the
		    * paths (great circle arcs) as segments (finite piece of a line).
		    */
		    function distanceToPath(ePoint, sPoint, enPoint) {
		    	var eventLat = toRad(ePoint.lat());
		    	var eventLng = toRad(ePoint.lng());
		    	var startLat = toRad(sPoint.lat());
		    	var startLng = toRad(sPoint.lng());
		    	var endLat = toRad(enPoint.lat());
		    	var endLng = toRad(enPoint.lng());
		    	
		    	var eventPoint = convertLatLngToCartesian(eventLat, eventLng);
		    	var startPoint = convertLatLngToCartesian(startLat, startLng);
		    	var endPoint = convertLatLngToCartesian(endLat, endLng);
		    	
		    	return distanceFromPointToSegment(eventPoint, startPoint, endPoint);
		    }
		    
		    /*
		     * Copyright 2001, softSurfer (www.softsurfer.com)
		     * Source: Algorithm is at the very end of the page, in http://www.softsurfer.com/Archive/algorithm_0102/algorithm_0102.htm
		     * 
		     * @param eventPoint	point of the event.
		     * @param startPoint	starting point of the segment.
		     * @param endPoint		ending point of the segment.
		     *
		     * @return	the distance from the event point to the closest point in the segment, which can
		     * 		either be the start point, the end point or a point in between.
		     */
		    function distanceFromPointToSegment(eventPoint, startPoint, endPoint) {
		    	// The segment.
		    	var startEndVector = toVector(startPoint, endPoint);
		    	// Line from the start of the segment to the event - V(P0, E).
		    	var startEventVector = toVector(startPoint, eventPoint);
		    	
		    	// If the angle of the V(P0, E) with the segment is greater than 90° it means that the
		    	// closest point to the event point is the start point of the segment.
		    	var dotEventSegment = dotProduct(startEndVector, startEventVector);
		    	if(dotEventSegment <= 0)
		    		return vectorDistance(startEventVector);
		    	
		    	// The angle of the end point is verified.
		    	var dotSegmentItself = dotProduct(startEndVector, startEndVector);
		    	if(dotSegmentItself <= dotEventSegment)
		    		return vectorDistance(toVector(endPoint, eventPoint));
		    	
		    	// This amount will scale the vector to become the displacement from the
		    	// start point of the segment to the closest one to the event point.
		    	var scale = dotEventSegment / dotSegmentItself;
		    	var closestPoint = {
		    			x: startPoint.x + (scale * startEndVector.x),
		    			y: startPoint.y + (scale * startEndVector.y),
		    			z: startPoint.z + (scale * startEndVector.z)
		    	};
		    	
		    	return vectorDistance(toVector(eventPoint, closestPoint));
		    }
		    
		    /* 
		     * The dot product of two vectors returns the angle between them.
		     * The dot product of a vector with itself returns the double of its length.
		     */
		    function dotProduct(a, b) {
		    	return (a.x * b.x) + (a.y * b.y) + (a.z + b.z);
		    }
		    
		    function vectorDistance(a) {
		    	return Math.sqrt((a.x * a.x) + (a.y * a.y) + (a.z * a.z));
		    }
		    
		    function toVector(a, b) {
		    	return {
		    		x: b.x - a.x,
		    		y: b.y - a.y,
		    		z: b.z - a.z
		    	};
		    }
		    
		    function convertLatLngToCartesian(lat, lng) {
		    	var point = {
		    			x: toX(lat, lng),
		    			y: toY(lat, lng),
		    			z: toZ(lat)
		    	};
		    	return point;
		    }
		    
		    function toX(lat, lng) {
		    	return earthRadius * Math.cos(lat) * Math.cos(lng);
		    }
		    
		    function toY(lat, lng) {
		    	return earthRadius * Math.cos(lat) * Math.sin(lng);
		    }
		    
		    function toZ(lat) {
		    	return earthRadius * Math.sin(lat);
		    }
		    
		    function toRad(number) {
		    	return number * Math.PI / 180;
		    }
