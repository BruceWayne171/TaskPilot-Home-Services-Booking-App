<?php
// These lines prevent any PHP warnings from breaking the JSON response.
error_reporting(0);
@ini_set('display_errors', 0);

require_once 'db_connect.php';
header('Content-Type: application/json');

$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

// The app still sends 'booking_id' from the fragment, but we know it's the request_id.
if (
    !isset($data['booking_id']) ||
    !isset($data['customer_id']) ||
    !isset($data['provider_id']) ||
    !isset($data['rating']) ||
    !isset($data['comment'])
) {
    echo json_encode(['status' => 'error', 'message' => 'Missing required review fields.']);
    exit;
}

$requestId = $data['booking_id']; // Use the correct variable name for clarity
$customerId = $data['customer_id'];
$providerId = $data['provider_id'];
$rating = $data['rating'];
$comment = $data['comment'];

if (!is_numeric($rating) || $rating < 1 || $rating > 5) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid rating. Please provide a star rating between 1 and 5.']);
    exit;
}

// THE FIX: The INSERT statement now uses the correct 'request_id' column name
// to match your newly fixed database schema.
$stmt = $conn->prepare(
    "INSERT INTO reviews (request_id, customer_id, provider_id, rating, comment) 
     VALUES (?, ?, ?, ?, ?)"
);
// The review_date is now handled automatically by the database.

$stmt->bind_param(
    "iiiis",
    $requestId,
    $customerId,
    $providerId,
    $rating,
    $comment
);

if ($stmt->execute()) {
    echo json_encode(['status' => 'success', 'message' => 'Review submitted successfully!']);
} else {
    if ($conn->errno == 1062) { // 1062 is the error code for a duplicate entry
        echo json_encode(['status' => 'error', 'message' => 'You have already submitted a review for this service.']);
    } else {
        // Provide a more detailed error for debugging
        echo json_encode(['status' => 'error', 'message' => 'Failed to submit review. DB Error: ' . $stmt->error]);
    }
}

$stmt->close();
$conn->close();
exit;
?>

