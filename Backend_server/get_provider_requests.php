<?php
require_once 'db_connect.php';

header('Content-Type: application/json');

// This script expects a provider_id to be sent as a URL parameter.
// For example: get_provider_requests.php?provider_id=4
if (!isset($_GET['provider_id'])) {
    echo json_encode(['status' => 'error', 'message' => 'Provider ID is required.']);
    exit;
}

$providerId = $_GET['provider_id'];

// Use a prepared statement to prevent SQL injection and securely fetch data.
$stmt = $conn->prepare(
    "SELECT 
        r.request_id, 
        r.preferred_date, 
        r.problem_description, 
        r.status,
        c.name as customer_name,
        c.adress as customer_address,
        c.phone_number as customer_phone,
        s.service_name
    FROM request r
    JOIN customers_table c ON r.customer_id = c.customer_id
    JOIN services s ON r.service_id = s.service_id
    WHERE r.provider_id = ?"
);

$stmt->bind_param("i", $providerId);
$stmt->execute();
$result = $stmt->get_result();

$requests = array();
if ($result && $result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        // This switch statement converts the status number into text for the app.
        switch ($row['status']) {
            case 0:
                $row['status_text'] = 'Pending';
                break;
            case 1:
                $row['status_text'] = 'Accepted';
                break;
            case 2:
                $row['status_text'] = 'Completed';
                break;
            default:
                $row['status_text'] = 'Unknown';
        }
        $requests[] = $row;
    }
}

// Send the final JSON response back to the Android app.
echo json_encode($requests);

$stmt->close();
$conn->close();
?>

