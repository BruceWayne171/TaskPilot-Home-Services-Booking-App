<?php
// Include the database connection
require_once 'db_connect.php';

// Set the header to tell the app it's receiving JSON
header('Content-Type: application/json');

// This query selects all active categories from your 'service_categories' table
$sql = "SELECT category_id, category_name FROM service_categories WHERE is_active = 1";

$result = $conn->query($sql);

$categories = array();
if ($result && $result->num_rows > 0) {
    // Loop through each row and add it to the $categories array
    while ($row = $result->fetch_assoc()) {
        $categories[] = $row;
    }
}

// Encode the array into JSON and send it back to the app
echo json_encode($categories);

$conn->close();
?>

