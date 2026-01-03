<?php
// Prevent PHP warnings from breaking the JSON response
error_reporting(0);
@ini_set('display_errors', 0);

require_once 'db_connect.php';
header('Content-Type: application/json');

// Get the raw JSON data sent from the app
$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

// Basic validation to ensure all required fields are present
if (
    !isset($data['name']) || !isset($data['email']) || !isset($data['password']) ||
    !isset($data['phone']) || !isset($data['address']) || !isset($data['user_type'])
) {
    echo json_encode(['status' => 'error', 'message' => 'All fields are required.']);
    exit;
}

$name = $data['name'];
$email = $data['email'];
$password = $data['password'];
$phone = $data['phone'];
$address = $data['address'];
$userType = $data['user_type'];

// Check if email already exists in either table
$stmt = $conn->prepare("SELECT email FROM customers_table WHERE email = ? UNION SELECT email FROM service_provider WHERE email = ?");
$stmt->bind_param("ss", $email, $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(['status' => 'error', 'message' => 'An account with this email already exists.']);
    $stmt->close();
    $conn->close();
    exit;
}
$stmt->close();

// Insert into the correct table based on user_type
if ($userType === 'customer') {
    $stmt = $conn->prepare("INSERT INTO customers_table (name, email, phone_number, adress, password) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("sssss", $name, $email, $phone, $address, $password);
} elseif ($userType === 'provider') {
    // A provider must submit a service_id and a price
    if (!isset($data['service_id']) || !isset($data['price_per_hour'])) {
        echo json_encode(['status' => 'error', 'message' => 'Please select a service and set your price.']);
        exit;
    }
    $serviceId = $data['service_id'];
    $price = $data['price_per_hour'];

    // Server-side validation for the price
    if (!is_numeric($price) || $price < 0 || $price > 1000) {
        echo json_encode(['status' => 'error', 'message' => 'Price must be a number between 0 and 1000.']);
        exit;
    }

    // Insert the new provider with their selected service and price
    $stmt = $conn->prepare("INSERT INTO service_provider (provider_name, email, phone_number, service_id, price_per_hour, password) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sssids", $name, $email, $phone, $serviceId, $price, $password);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid user type specified.']);
    exit;
}

if ($stmt->execute()) {
    echo json_encode(['status' => 'success', 'message' => 'Registration successful!']);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Registration failed. Please try again.']);
}

$stmt->close();
$conn->close();
?>