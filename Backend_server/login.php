<?php
require_once 'db_connect.php';

header('Content-Type: application/json');

// Check if email and password are set
if (!isset($_POST['email']) || !isset($_POST['password'])) {
    echo json_encode(['status' => 'error', 'message' => 'Email and password are required.']);
    exit;
}

$email = $_POST['email'];
$password = $_POST['password'];

// --- Check Customers Table First ---
$stmt = $conn->prepare("SELECT customer_id, name, email, phone_number, adress, password FROM customers_table WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    // Use simple comparison for plain text passwords
    if ($password == $user['password']) {
        
        // THE FIX: Create a standardized user object to send to the app
        $customer_data = [
            'id' => (string)$user['customer_id'], // Always use the key 'id'
            'name' => $user['name'],
            'email' => $user['email'],
            'phone_number' => $user['phone_number'],
            'address' => $user['adress']
        ];

        echo json_encode(['status' => 'success', 'message' => 'Login successful!', 'user' => $customer_data, 'user_type' => 'customer']);
    } else {
        // Incorrect password
        echo json_encode(['status' => 'error', 'message' => 'Invalid email or password.']);
    }
} else {
    // --- If not a customer, Check Providers Table ---
    $stmt = $conn->prepare("SELECT provider_id, provider_name, email, phone_number, password FROM service_provider WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        if ($password == $user['password']) {
            // Standardize the provider data as well
            $provider_data = [
                'id' => (string)$user['provider_id'], // Always use the key 'id'
                'name' => $user['provider_name'],
                'email' => $user['email'],
                'phone_number' => $user['phone_number']
                // 'address' is intentionally omitted as providers don't have one
            ];
            echo json_encode(['status' => 'success', 'message' => 'Login successful!', 'user' => $provider_data, 'user_type' => 'provider']);
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Invalid email or password.']);
        }
    } else {
        // No user found in either table
        echo json_encode(['status' => 'error', 'message' => 'Invalid email or password.']);
    }
}

$stmt->close();
$conn->close();

?>

