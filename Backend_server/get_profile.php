<?php
require_once 'db_connect.php';
header('Content-Type: application/json');

if (!isset($_GET['user_id']) || !isset($_GET['user_type'])) {
    echo json_encode(['status' => 'error', 'message' => 'User ID and User Type are required.']);
    exit;
}

$userId = $_GET['user_id'];
$userType = $_GET['user_type'];

$user_data = null;

if ($userType == 'customer') {
    // Note: We use 'AS' to standardize the column names to match the Android 'User' data class
    $stmt = $conn->prepare("SELECT customer_id as id, name, email, phone_number, adress as address, NULL as price_per_hour FROM customers_table WHERE customer_id = ?");
    $stmt->bind_param("i", $userId);
} elseif ($userType == 'provider') {
    // Fetches provider data and includes price_per_hour, all matching the 'User' data class
    $stmt = $conn->prepare("SELECT provider_id as id, provider_name as name, email, phone_number, price_per_hour FROM service_provider WHERE provider_id = ?");
    $stmt->bind_param("i", $userId);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid user type.']);
    exit;
}

$stmt->execute();
$result = $stmt->get_result();
if ($result->num_rows > 0) {
    $user_data = $result->fetch_assoc();
    // Ensure 'address' field exists for providers, even if it's null, to match the data class
    if ($userType == 'provider') {
        $user_data['address'] = null;
    }
}

$stmt->close();
$conn->close();

if ($user_data) {
    // Send the user data back (it will match the 'User' data class in your app)
    echo json_encode($user_data);
} else {
    echo json_encode(['status' => 'error', 'message' => 'User not found.']);
}
?>