<?php
// Prevent PHP warnings from breaking the JSON response
error_reporting(0);
@ini_set('display_errors', 0);

require_once 'db_connect.php';
header('Content-Type: application/json');

$json_data = file_get_contents('php://input');
$data = json_decode($json_data, true);

// Basic validation
if (
    !isset($data['user_id']) || !isset($data['user_type']) ||
    !isset($data['name']) || !isset($data['email']) || !isset($data['phone'])
) {
    echo json_encode(['status' => 'error', 'message' => 'Missing required fields.']);
    exit;
}

$userId = $data['user_id'];
$userType = $data['user_type'];
$name = $data['name'];
$email = $data['email'];
$phone = $data['phone'];

try {
    if ($userType == 'customer') {
        $address = $data['address']; // Customers have an address
        $stmt = $conn->prepare("UPDATE customers_table SET name = ?, email = ?, phone_number = ?, adress = ? WHERE customer_id = ?");
        $stmt->bind_param("ssssi", $name, $email, $phone, $address, $userId);
    } elseif ($userType == 'provider') {
        $stmt = $conn->prepare("UPDATE service_provider SET provider_name = ?, email = ?, phone_number = ? WHERE provider_id = ?");
        $stmt->bind_param("sssi", $name, $email, $phone, $userId);
    } else {
        throw new Exception('Invalid user type.');
    }

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            echo json_encode(['status' => 'success', 'message' => 'Profile updated successfully!']);
        } else {
            echo json_encode(['status' => 'info', 'message' => 'No changes were made.']);
        }
    } else {
        throw new Exception('Failed to execute query.');
    }

    $stmt->close();
    $conn->close();

} catch (Exception $e) {
    // Handle potential duplicate email errors
    if ($conn->errno == 1062) {
        echo json_encode(['status' => 'error', 'message' => 'This email is already in use by another account.']);
    } else {
        echo json_encode(['status' => 'error', 'message' => 'An error occurred.', 'details' => $e->getMessage()]);
    }
}
?>
