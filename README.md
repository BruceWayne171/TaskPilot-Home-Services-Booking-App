# TaskPilot: Home Service Booking App 🛠️

TaskPilot is a full-stack mobile application that connects homeowners with local service providers for tasks like plumbing, cleaning, electrical work, and repairs.

## 📱 Project Overview
This repository contains the complete source code for the system:
* **Android App:** Built with Android Studio (Kotlin/Java) for customers and providers.
* **Backend:** PHP API scripts to handle logic and database communication.
* **Database:** MySQL database (via XAMPP) to store user data and bookings.

## ✨ Key Features
* **User Roles:** Distinct interfaces for Customers and Service Providers.
* **Booking System:** Browse services, view provider details, and book appointments.
* **Job Management:** Providers can accept/reject jobs and update status.
* **Admin Panel:** (If applicable) Web interface to manage users and categories.

## 🛠️ Tech Stack
* **Frontend:** Android SDK, XML
* **Backend:** Core PHP
* **Database:** MySQL (MariaDB)
* **Networking:** Retrofit / Volley (for connecting App to API)
* **Local Server:** XAMPP

## 🚀 How to Run Locally

### 1. Database Setup
1. Download and install **XAMPP**.
2. Start **Apache** and **MySQL** in the XAMPP Control Panel.
3. Open `phpMyAdmin` (http://localhost/phpmyadmin).
4. Create a new database named `taskpilot` (or whatever your DB name is).
5. Import the SQL file located in the `Backend-Server` folder (e.g., `admin3.sql`).

### 2. Backend Setup
1. Copy the `Backend-Server` folder to your XAMPP `htdocs` directory (e.g., `C:\xampp\htdocs\TaskPilot`).
2. Open the database connection file (e.g., `db_connect.php`) and ensure the password/username matches your local MySQL setup.

### 3. Android App Setup
1. Open the `Android-App` folder in **Android Studio**.
2. Find the API configuration file (usually in `Constants.java` or `RetrofitClient.java`).
3. **Crucial:** Change the `BASE_URL` to your computer's IP address.
   * *Example:* `http://192.168.1.5/TaskPilot/` (Do not use `localhost` for physical devices).
4. Sync Gradle and Run the app on an Emulator or Device.
