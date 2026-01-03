<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

// We expect data to be sent via POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method.']);
    exit;
}

if (!isset($_POST['provider_id']) || !isset($_POST['price_per_hour'])) {
    echo json_encode(['status' => 'error', 'message' => 'Provider ID and new price are required.']);
    exit;
}

$providerId = $_POST['provider_id'];
$newPrice = $_POST['price_per_hour'];

// Validate that the price is a valid number
if (!is_numeric($newPrice) || $newPrice < 0) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid price format.']);
    exit;
}

$stmt = $conn->prepare("UPDATE service_provider SET price_per_hour = ? WHERE provider_id = ?");
$stmt->bind_param("di", $newPrice, $providerId); // 'd' for double/decimal

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(['status' => 'success', 'message' => 'Price updated successfully.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Provider not found or price is already the same.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Failed to update price.']);
}

$stmt->close();
$conn->close();
?>