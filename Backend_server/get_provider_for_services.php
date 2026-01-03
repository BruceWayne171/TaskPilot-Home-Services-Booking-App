<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

// This script expects a 'service_id' (which is your sub-service ID)
if (!isset($_GET['service_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Service ID is required.']);
    exit;
}

$serviceId = $_GET['service_id'];

// THE FEATURE: This query now:
// 1. Fetches all providers for the specific sub-service.
// 2. Joins with the 'reviews' table.
// 3. Calculates the average_rating and total_reviews for each provider.
$stmt = $conn->prepare(
    "SELECT 
        p.provider_id, 
        p.provider_name, 
        p.email, 
        p.phone_number, 
        p.price_per_hour,
        AVG(r.rating) as average_rating,
        COUNT(r.review_id) as total_reviews
    FROM service_provider p
    LEFT JOIN reviews r ON p.provider_id = r.provider_id
    WHERE p.service_id = ? AND p.is_active = 1
    GROUP BY p.provider_id"
);

$stmt->bind_param("i", $serviceId);
$stmt->execute();
$result = $stmt->get_result();

$providers = array();
if ($result) {
    while ($row = $result->fetch_assoc()) {
        $providers[] = $row;
    }
}

echo json_encode($providers);
$stmt->close();
$conn->close();
?>