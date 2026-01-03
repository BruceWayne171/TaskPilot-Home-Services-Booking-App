<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

// We expect data to be sent via POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method.']);
    exit;
}

if (!isset($_POST['request_id']) || !isset($_POST['status'])) {
    echo json_encode(['status' => 'error', 'message' => 'Request ID and status are required.']);
    exit;
}

$requestId = $_POST['request_id'];
$status = $_POST['status'];

// Use a prepared statement to prevent SQL injection
$stmt = $conn->prepare("UPDATE request SET status = ? WHERE request_id = ?");
$stmt->bind_param("ii", $status, $requestId);

if ($stmt->execute()) {
    // Check if any row was actually updated
    if ($stmt->affected_rows > 0) {
        echo json_encode(['status' => 'success', 'message' => 'Request status updated successfully.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Request not found or status is already updated.']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Failed to update request status.']);
}

$stmt->close();
$conn->close();
?>
