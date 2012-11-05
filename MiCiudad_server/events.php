<?php
include "connection.php";

header('Content-Type: application/json');

$method = $_SERVER['REQUEST_METHOD'];

switch($method) {
	case "GET":
		echo handleGet();
		break;
	case "POST":
		echo handlePost();
		break;
}

function handleGet() {
	global $mysqli;
	
	$query = "SELECT e.type, d.lat, d.lng, d.time, e.description, NOW() as now " .
			"FROM event e, event_data d " .
			"WHERE e.id = d.event_id";
	
	if($_SERVER['QUERY_STRING']) {
		$positionComparison = positionComparison();
		if(!is_null($positionComparison))
			$query .= $positionComparison;
		else
			return "{\"event\":[]}";
	}
	
	$query .= " ORDER BY d.time DESC";
	
	$output = "{\"events\":[";
	$result = $mysqli->query($query);
	while($row = $result->fetch_assoc()) {
		$time = processTime($row['time'], $row['now']);
		
		$output .= "{";
		$output .= "\"type\":\"" . $row['type'] . "\",";
		$output .= "\"lat\":\"" . $row['lat'] . "\",";
		$output .= "\"lng\":\"" . $row['lng'] . "\",";
		$output .= "\"time\":\"" . $time . "\",";
		$output .= "\"description\":\"" . $row['description'] ."\"";
		$output .= "},";
	}
	if(strcmp($output[strlen($output) - 1], ",") == 0)
		$output = substr($output, 0, -1);
	$output .= "]}";

	return utf8_encode($output);
}

function handlePost() {
	global $mysqli;
	
	$body = readBody();
	$parts = explode("|", $body);
	
	$mysqli->query("INSERT INTO event (description, type) VALUES ('" . $parts[2] .
			"', '" . $parts[3] . "')");
	$mysqli->query("INSERT INTO event_data VALUES ((SELECT LAST_INSERT_ID()), (SELECT NOW()), '".
			$parts[0] . "', '" . $parts[1] . "')");
	
	$query = "SELECT e.type, d.lat, d.lng, d.time, e.description " .
			"FROM event e, event_data d " .
			"WHERE e.id = d.event_id AND e.id = (SELECT LAST_INSERT_ID())";
	
	$output = "{";
	$result = $mysqli->query($query);
	if($row = $result->fetch_assoc()) {
		$output .= "\"type\":\"" . $row['type'] . "\",";
		$output .= "\"lat\":\"" . $row['lat'] . "\",";
		$output .= "\"lng\":\"" . $row['lng'] . "\",";
		$output .= "\"time\":\"" . $row['time'] . "\",";
		$output .= "\"description\":\"" . $row['description'] ."\"";
	}
	$output .= "}";
	
	return utf8_encode($output);
}

function positionComparison() {
	$upperLat = $_GET['upperLat'];
	$upperLng = $_GET['upperLng'];
	$lowerLat = $_GET['lowerLat'];
	$lowerLng = $_GET['lowerLng'];
	
	if(!$upperLat || !$upperLng || !$lowerLat || !$lowerLng)
		return NULL;
	
	$pattern = "/(-?[0-9]+(.[0-9]+)?)/";
	if(!preg_match($pattern, $upperLat) || !preg_match($pattern, $upperLng) ||
	   !preg_match($pattern, $lowerLat) || !preg_match($pattern, $lowerLng))
		return NULL;
	
	$positionComparison = " AND d.lat <= " . $upperLat . " AND d.lng <= " .
			$upperLng . " AND d.lat >= ". $lowerLat .
			" AND d.lng >= " . $lowerLng;
	
	return $positionComparison;
}

function processTime($previous, $current) {
	// Times in seconds.
	$twoDays = 172800; // 48 hours.
	$hour = 3600;
	$day = 86400;
	
	$previousStamp = strtotime($previous);
	$currentStamp = strtotime($current);
	$difference = $currentStamp - $previousStamp;
	
	$result = "";
	
	if($difference < $twoDays)
		return ((int) ($difference / $hour)) . " horas";
	else
		return ((int) ($difference / $day)) . " d\u00EDas";
}

function readBody() {
	$body = "";
	$putData = fopen("php://input", "r");
	while($block = fread($putData, 1024)) {
		$body = $body . $block;
	}
	fclose($putData);
	return $body;
}
?>