<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

if (!isset($_GET['provider_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Provider ID is required.']);
    exit;
}

$providerId = $_GET['provider_id'];

// Prepare a secure SQL query to fetch reviews for the given provider
$stmt = $conn->prepare(
    "SELECT 
        r.review_id, 
        r.rating, 
        r.comment, 
        r.review_date,
        c.name as customer_name
    FROM reviews r
    JOIN customers_table c ON r.customer_id = c.customer_id
    WHERE r.provider_id = ?
    ORDER BY r.review_date DESC" // Show the most recent reviews first
);

$stmt->bind_param("i", $providerId);
$stmt->execute();
$result = $stmt->get_result();

$reviews = array();
if ($result) {
    while ($row = $result->fetch_assoc()) {
        $reviews[] = $row;
    }
}

echo json_encode($reviews);
$stmt->close();
$conn->close();
?>
