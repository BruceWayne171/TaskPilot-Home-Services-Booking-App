<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

if (!isset($_GET['customer_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Customer ID is required.']);
    exit;
}

$customerId = $_GET['customer_id'];

// Status 0 = Pending
$stmt = $conn->prepare(
    "SELECT 
        r.request_id,
        r.preferred_date,
        r.status,
        s.service_name,
        sp.provider_name,
        s.service_id, -- This is the sub-service ID
        r.provider_id
    FROM request r
    JOIN services s ON r.service_id = s.service_id
    LEFT JOIN service_provider sp ON r.provider_id = sp.provider_id
    WHERE r.customer_id = ? AND r.status = 0
    ORDER BY r.request_date DESC"
);

$stmt->bind_param("i", $customerId);
$stmt->execute();
$result = $stmt->get_result();

$requests = array();
if ($result) {
    while ($row = $result->fetch_assoc()) {
        $row['status_text'] = 'Pending';
        $requests[] = $row;
    }
}

echo json_encode($requests);
$stmt->close();
$conn->close();
?>

