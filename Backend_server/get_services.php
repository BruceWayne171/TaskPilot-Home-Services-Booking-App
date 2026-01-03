<?php
// Make sure you connect to your 'admin' database
require_once 'db_connect.php'; 

// Set the header to return JSON
header('Content-Type: application/json');

// SQL query to get all active services from your 'services' table
$sql = "SELECT service_id, service_name, description FROM services WHERE is_active = 1";
$result = $conn->query($sql);

$services = array();
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $services[] = $row;
    }
}

// Encode the result into JSON and print it
echo json_encode($services);

$conn->close();
?>