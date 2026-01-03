<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

if (!isset($_GET['customer_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Customer ID is required.']);
    exit;
}

$customerId = $_GET['customer_id'];

// Status 1 = Accepted, 2 = Completed
$stmt = $conn->prepare(
    "SELECT 
        r.request_id,
        r.provider_id, -- THE FIX: Added this line to the query
        r.preferred_date,
        r.status,
        s.service_name,
        sp.provider_name
    FROM request r
    JOIN services s ON r.service_id = s.service_id
    JOIN service_provider sp ON r.provider_id = sp.provider_id
    WHERE r.customer_id = ? AND r.status IN (1, 2)"
);

$stmt->bind_param("i", $customerId);
$stmt->execute();
$result = $stmt->get_result();

$bookings = array();
if ($result) {
    while ($row = $result->fetch_assoc()) {
        $row['status_text'] = ($row['status'] == 1) ? 'Accepted' : 'Completed';
        $bookings[] = $row;
    }
}

echo json_encode($bookings);
$stmt->close();
$conn->close();
?>

