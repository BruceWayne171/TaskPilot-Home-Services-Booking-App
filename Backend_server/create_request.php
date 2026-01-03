<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

// This script expects 'service_id' (which is your sub-service ID)
if (
    !isset($data['customer_id']) ||
    !isset($data['service_id']) || 
    !isset($data['provider_id']) ||
    !isset($data['preferred_date']) ||
    !isset($data['problem_description'])
) {
    echo json_encode(['status' => 'error', 'message' => 'Missing required fields.']);
    exit;
}

$customerId = $data['customer_id'];
$serviceId = $data['service_id']; 
$providerId = $data['provider_id'];
$preferredDate = $data['preferred_date'];
$problemDescription = $data['problem_description'];
$status = 0; // Default status for a new request is 0 (Pending)

// --- SERVER-SIDE DATE VALIDATION ---
$today = new DateTime();
$maxDate = (new DateTime())->modify('+15 days');
$selectedDate = new DateTime($preferredDate);

if ($selectedDate > $maxDate) {
    echo json_encode(['status' => 'error', 'message' => 'Booking date cannot be more than 15 days in the future.']);
    exit;
}
if ($selectedDate < $today->setTime(0,0,0)) {
     echo json_encode(['status' => 'error', 'message' => 'Booking date cannot be in the past.']);
    exit;
}
// ----------------------------------

// Insert into 'request' table using 'service_id' (the sub-service ID)
$stmt = $conn->prepare(
    "INSERT INTO request (customer_id, service_id, provider_id, preferred_date, problem_description, status) 
     VALUES (?, ?, ?, ?, ?, ?)"
);

$stmt->bind_param(
    "iiissi",
    $customerId,
    $serviceId, 
    $providerId,
    $preferredDate,
    $problemDescription,
    $status
);

if ($stmt->execute()) {
    echo json_encode(['status' => 'success', 'message' => 'Service requested successfully!']);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Failed to create request.']);
}

$stmt->close();
$conn->close();
?>