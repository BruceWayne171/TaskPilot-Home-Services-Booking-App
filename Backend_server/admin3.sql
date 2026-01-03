-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 07, 2025 at 02:28 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `admin`
--

-- --------------------------------------------------------

--
-- Table structure for table `admin_users`
--

CREATE TABLE `admin_users` (
  `admin_id` int(10) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password_hash` varchar(225) NOT NULL,
  `email` varchar(50) NOT NULL,
  `last_login` datetime(6) NOT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `admin_users`
--

INSERT INTO `admin_users` (`admin_id`, `username`, `password_hash`, `email`, `last_login`, `is_active`) VALUES
(1, 'Meet', 'meet123', 'meet123@gmail.com', '0000-00-00 00:00:00.000000', 1),
(2, 'Dhiraj', '$2y$10$W2/fthxpcsjr1yr0889P2O2Q7yzfVsp4IoqzYPjwz/TJVqtkPyMdu', 'dhiraj123@gmail.com', '0000-00-00 00:00:00.000000', 1);

-- --------------------------------------------------------

--
-- Table structure for table `bookings`
--

CREATE TABLE `bookings` (
  `booking_id` int(10) NOT NULL,
  `customer_id` int(10) NOT NULL,
  `provider_id` int(10) NOT NULL,
  `booking_date_time` datetime NOT NULL,
  `price_quoted` decimal(10,2) NOT NULL,
  `status` tinyint(4) NOT NULL,
  `provider_description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bookings`
--

INSERT INTO `bookings` (`booking_id`, `customer_id`, `provider_id`, `booking_date_time`, `price_quoted`, `status`, `provider_description`) VALUES
(1, 1, 1, '2025-11-09 00:00:00', 620.00, 1, NULL),
(2, 3, 1, '2025-11-11 00:00:00', 620.00, 1, NULL),
(3, 2, 1, '2025-11-07 00:00:00', 620.00, 2, 'Add new pipe for washing machine to add & remove water, and done this work in 1hr.'),
(4, 1, 4, '2025-11-08 16:00:00', 158.00, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `customers_table`
--

CREATE TABLE `customers_table` (
  `customer_id` int(10) NOT NULL,
  `name` varchar(20) NOT NULL,
  `email` varchar(50) NOT NULL,
  `phone_number` varchar(10) NOT NULL,
  `adress` varchar(60) NOT NULL,
  `password` varchar(20) NOT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers_table`
--

INSERT INTO `customers_table` (`customer_id`, `name`, `email`, `phone_number`, `adress`, `password`, `is_active`) VALUES
(1, 'Balkrushn', 'balkrushn1234@gmail.com', '1231231231', '', 'balkrushn1234', 1),
(2, 'Piyush', 'piyush1234@gmail.com', '9865329865', '13, mayur nagar, pal gam', 'piyush1234', 1),
(3, 'Mohit', 'mohit123@gmail.com', '9537895378', 'Sant Tukaram Soc-2 Palanpur Jakatnaka\r\n150', 'mohit123', 1);

-- --------------------------------------------------------

--
-- Table structure for table `request`
--

CREATE TABLE `request` (
  `request_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `service_id` int(11) NOT NULL,
  `provider_id` int(11) DEFAULT NULL,
  `request_date` datetime NOT NULL DEFAULT current_timestamp(),
  `preferred_date` datetime DEFAULT NULL,
  `problem_description` text DEFAULT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `request`
--

INSERT INTO `request` (`request_id`, `customer_id`, `service_id`, `provider_id`, `request_date`, `preferred_date`, `problem_description`, `status`) VALUES
(1, 1, 1, 1, '2025-11-06 15:38:55', '2025-11-09 00:00:00', 'i want to Install new pipes', 1),
(2, 2, 1, 1, '2025-11-07 10:47:14', '2025-11-07 00:00:00', 'Add new pipe for washing machine', 3),
(3, 3, 1, 1, '2025-11-07 10:58:39', '2025-11-11 00:00:00', 'new pipe add', 1),
(4, 1, 2, 2, '2025-11-07 14:44:25', '2025-11-08 00:00:00', 'Add new tap in my washroom', 0),
(5, 3, 2, 2, '2025-11-07 14:51:27', '2025-11-13 00:00:00', 'new tap', 2),
(8, 1, 2, 4, '2025-11-07 15:13:35', '2025-11-08 16:00:00', 'add new tap', 1);

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `review_id` int(10) NOT NULL,
  `booking_id` int(10) NOT NULL,
  `customer_id` int(10) NOT NULL,
  `provider_id` int(10) NOT NULL,
  `rating` int(11) NOT NULL,
  `comment` text NOT NULL,
  `review_date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`review_id`, `booking_id`, `customer_id`, `provider_id`, `rating`, `comment`, `review_date`) VALUES
(1, 3, 2, 1, 4, 'Good work', '2025-11-07 11:20:59');

-- --------------------------------------------------------

--
-- Table structure for table `services`
--

CREATE TABLE `services` (
  `service_id` int(10) NOT NULL,
  `service_name` varchar(20) NOT NULL,
  `description` text NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `category_id` int(10) NOT NULL,
  `is_active` tinyint(4) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `services`
--

INSERT INTO `services` (`service_id`, `service_name`, `description`, `price`, `category_id`, `is_active`) VALUES
(1, 'New Pipe Installatio', 'Installing new water supply and drainage pipe systems in new construction or during remodels.', 399.00, 1, 1),
(2, 'New Tap Installation', 'Installation Tap', 129.00, 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `service_categories`
--

CREATE TABLE `service_categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_categories`
--

INSERT INTO `service_categories` (`category_id`, `category_name`, `description`, `is_active`) VALUES
(1, 'Plumbing', 'We provide complete plumbing services including water supply installation, drainage systems, sanitary fittings, leak repairs, and maintenance for residential and commercial buildings.', 1);

-- --------------------------------------------------------

--
-- Table structure for table `service_provider`
--

CREATE TABLE `service_provider` (
  `provider_id` int(11) NOT NULL,
  `provider_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `service_id` int(11) NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `price_per_hour` decimal(10,2) NOT NULL DEFAULT 0.00,
  `password` varchar(50) DEFAULT NULL,
  `is_booked` tinyint(4) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_provider`
--

INSERT INTO `service_provider` (`provider_id`, `provider_name`, `email`, `phone_number`, `service_id`, `is_active`, `price_per_hour`, `password`, `is_booked`) VALUES
(1, 'Vivek', 'vivek123@gamil.com', '9726697266', 1, 1, 95.00, 'vivek123', 0),
(2, 'Ronak', 'ronak123@gmail.com', '9235592366', 2, 1, 39.00, 'ronak123', 0),
(3, 'Dev', 'dev123@gmail.com', '9727997279', 1, 1, 79.00, 'dev123', 0),
(4, 'Om', 'om123@gmail.com', '1234567891', 2, 1, 29.00, 'om123', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin_users`
--
ALTER TABLE `admin_users`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `provider_id` (`provider_id`);

--
-- Indexes for table `customers_table`
--
ALTER TABLE `customers_table`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `phone_number` (`phone_number`);

--
-- Indexes for table `request`
--
ALTER TABLE `request`
  ADD PRIMARY KEY (`request_id`),
  ADD KEY `fk_request_service` (`service_id`),
  ADD KEY `fk_request_client` (`customer_id`) USING BTREE,
  ADD KEY `fk_request_provider` (`provider_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD KEY `booking_id` (`booking_id`),
  ADD KEY `customer_id` (`customer_id`),
  ADD KEY `provider_id` (`provider_id`);

--
-- Indexes for table `services`
--
ALTER TABLE `services`
  ADD PRIMARY KEY (`service_id`),
  ADD UNIQUE KEY `service_name` (`service_name`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `service_categories`
--
ALTER TABLE `service_categories`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `service_provider`
--
ALTER TABLE `service_provider`
  ADD PRIMARY KEY (`provider_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `fk_service` (`service_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin_users`
--
ALTER TABLE `admin_users`
  MODIFY `admin_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `booking_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `customers_table`
--
ALTER TABLE `customers_table`
  MODIFY `customer_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `request`
--
ALTER TABLE `request`
  MODIFY `request_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `review_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `services`
--
ALTER TABLE `services`
  MODIFY `service_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `service_categories`
--
ALTER TABLE `service_categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `service_provider`
--
ALTER TABLE `service_provider`
  MODIFY `provider_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `request`
--
ALTER TABLE `request`
  ADD CONSTRAINT `fk_request_client` FOREIGN KEY (`customer_id`) REFERENCES `customers_table` (`customer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_request_provider` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`provider_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_request_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`booking_id`),
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers_table` (`customer_id`),
  ADD CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`provider_id`) REFERENCES `service_provider` (`provider_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `services`
--
ALTER TABLE `services`
  ADD CONSTRAINT `services_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `service_categories` (`category_id`);

--
-- Constraints for table `service_provider`
--
ALTER TABLE `service_provider`
  ADD CONSTRAINT `fk_service` FOREIGN KEY (`service_id`) REFERENCES `services` (`service_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
