<?php
// Make sure you connect to your 'admin' database
require_once 'db_connect.php'; 

// Set the header to return JSON
header('Content-Type: application/json');

// SQL query to get all active customers. Note that we exclude the password field.
$sql = "SELECT customer_id, name, email, phone_number, adress FROM customers_table WHERE is_active = 1";
$result = $conn->query($sql);

$users = array();
if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $users[] = $row;
    }
}

// Encode the result into JSON and print it
echo json_encode($users);

$conn->close();
?>
