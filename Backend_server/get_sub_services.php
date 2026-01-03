<?php
// Include the database connection
require_once 'db_connect.php';

// Set the header to tell the app it's receiving JSON
header('Content-Type: application/json');

// This script expects a category_id to be sent as a URL parameter
// For example: get_sub_services.php?category_id=1
if (!isset($_GET['category_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Category ID is required.']);
    exit;
}

$categoryId = $_GET['category_id'];

// Use a prepared statement to securely fetch services (which are your sub-services)
// that match the given category_id
$stmt = $conn->prepare("SELECT service_id, service_name, description FROM services WHERE category_id = ? AND is_active = 1");
$stmt->bind_param("i", $categoryId);
$stmt->execute();
$result = $stmt->get_result();

$sub_services = array();
if ($result && $result->num_rows > 0) {
    // Loop through each row and add it to the $sub_services array
    while ($row = $result->fetch_assoc()) {
        $sub_services[] = $row;
    }
}

// Encode the array into JSON and send it back to the app
echo json_encode($sub_services);

$stmt->close();
$conn->close();
?>

