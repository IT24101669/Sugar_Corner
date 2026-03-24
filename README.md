<<<<<<< HEAD
# Payment Handling Project

## Database Setup
Run the following SQL scripts in your MySQL database:

CREATE DATABASE IF NOT EXISTS brownies_db;

USE brownies_db;

CREATE TABLE IF NOT EXISTS orders (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
customer_id BIGINT NOT NULL,
total_amount DECIMAL(10, 2) NOT NULL,
status VARCHAR(50) DEFAULT 'PENDING',
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payments (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
order_id BIGINT NOT NULL,
method ENUM('ONLINE', 'COD') NOT NULL,
status ENUM('PENDING', 'PAID', 'FAILED') DEFAULT 'PENDING',
amount DECIMAL(10, 2) NOT NULL,
transaction_id VARCHAR(100),
paid_at TIMESTAMP,
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

## Running the Project
1. Update application.properties with your DB credentials.
2. Run mvn clean install
3. Run the application via IntelliJ or mvn spring-boot:run
4. Access frontend at http://localhost:8080/payment.html
=======
# Sugar_Corner
Web Based Brownies sales and order   management System 
>>>>>>> 9c292c30f0b11330ebf22cda28831be0375c41a8
