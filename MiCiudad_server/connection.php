<?php
$server = "localhost";
$user = "mytripx1_apporta";
$password = "apporta";
$database = "mytripx1_apporta";

$mysqli = new mysqli($server, $user, $password, $database);
if ($mysqli->connect_errno) {
    echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
}
?>