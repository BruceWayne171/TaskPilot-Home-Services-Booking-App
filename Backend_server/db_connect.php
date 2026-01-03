<?php

// --- Database Connection Details ---
// These are for your local development server.
// IMPORTANT: Change these details when you deploy to a live server.

$dbHost = "localhost";      // or "127.0.0.1"
$dbUser = "root";           // Default username for XAMPP/WAMP
$dbPass = "";               // Default password for XAMPP/WAMP is empty
$dbName = "admin";          // The name of your database for Task Pilot

// --- Create a new database connection ---
$conn = new mysqli($dbHost, $dbUser, $dbPass, $dbName);

// --- Check for connection errors ---
// If the connection fails, the script will stop and show an error.
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Optional: Set character set to utf8mb4 for full Unicode support
$conn->set_charset("utf8mb4");

?>