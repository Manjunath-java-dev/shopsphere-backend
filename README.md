ShopSphere — E-Commerce Backend
ShopSphere is a full-stack E-Commerce application with a Java Spring Boot backend and a separate frontend application.
This repository contains the backend REST APIs, business logic, database integration, authentication, authorization, and admin/customer functionality.

🚀 Features
 Authentication & Security
* User registration
* User login
* JWT authentication
* BCrypt password encryption
* Role-based authorization
* CUSTOMER and ADMIN roles
* Protected admin APIs

 Customer Features
* Customer profile management
* Delivery address management
* Browse products
* Search products
* Product pagination
* Product category filtering
* Shopping cart
* Wishlist
* Order management
* Order tracking
* Product reviews and ratings

 Admin Features
* Admin dashboard
* Product management
* Category management
* Customer management
* Order management
* Update order status
* Customer search
* Product search
* Product pagination
* Order statistics
* Sales statistics

 Product Management
* Add product
* Get product by ID
* Get all products
* Update product
* Delete product
* Search products by name
* Filter products by category
* Pagination
* Stock management
* Product ratings

 Order Management
* Create orders
* View customer orders
* View order details
* Admin order management
* Update order status
* Shipping address snapshot
* Order item management

 Reviews
* Add reviews
* Product ratings
* View reviews
* Customer review management

 🛠️ Tech Stack

| Technology        | Purpose                        |
| ----------------- | ------------------------------ |
| Java 17           | Programming Language           |
| Spring Boot       | Backend Framework              |
| Spring MVC        | REST APIs                      |
| Spring Data JPA   | Database Access                |
| Spring Security   | Authentication & Authorization |
| JWT               | Authentication                 |
| Hibernate         | ORM                            |
| MySQL             | Database                       |
| Maven             | Build Tool                     |
| Lombok            | Boilerplate Reduction          |
| Swagger / OpenAPI | API Documentation              |
| SLF4J / Logback   | Logging                        |

 🏗️ Architecture

The application follows a layered architecture:

Controller
    ↓
Service
    ↓
Repository
    ↓
Database


Controller Layer
Handles HTTP requests and responses.

Service Layer
Contains business logic and application operations.

Repository Layer
Handles database operations using Spring Data JPA.

Entity Layer
Represents database tables.

DTO Layer
Handles API request and response objects.

Security Layer
Handles JWT authentication and role-based authorization.

Exception Layer
Contains custom exceptions and centralized exception handling.


📁 Project Structure
shopsphere-backend/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/shopsphere/
│       │       ├── controller/
│       │       ├── service/
│       │       ├── repositoy/
│       │       ├── entity/
│       │       ├── dto/
│       │       │   ├── request/
│       │       │   └── response/
│       │       ├── security/
│       │       ├── exception/
│       │       └── enums/
│       │
│       └── resources/
│
├── pom.xml
├── README.md
└── .gitignore


🔑 Main API Modules
Authentication
POST /api/v1/auth/register
POST /api/v1/auth/login

Customer Products
GET /api/v1/customer/products
GET /api/v1/customer/products/{id}
GET /api/v1/customer/products/search
GET /api/v1/customer/products/category/{categoryId}


Cart
GET    /api/v1/customer/cart
POST   /api/v1/customer/cart
PUT    /api/v1/customer/cart/{cartItemId}
DELETE /api/v1/customer/cart/{cartItemId}

Wishlist
GET    /api/v1/customer/wishlist
POST   /api/v1/customer/wishlist/{productId}
DELETE /api/v1/customer/wishlist/{productId}

Orders
POST /api/v1/customer/orders
GET  /api/v1/customer/orders
GET  /api/v1/customer/orders/{orderId}

Customer Profile
GET /api/v1/customer
PUT /api/v1/customer

Addresses
GET    /api/v1/customer/addresses
POST   /api/v1/customer/addresses
PUT    /api/v1/customer/addresses/{id}
DELETE /api/v1/customer/addresses/{id}

Reviews
POST /api/v1/customer/reviews
GET  /api/v1/customer/reviews

👨‍💼 Admin APIs
Admin Dashboard
GET /api/v1/admin/dashboard

Admin Products
POST   /api/v1/admin/products
GET    /api/v1/admin/products
GET    /api/v1/admin/products/{id}
PUT    /api/v1/admin/products/{id}
DELETE /api/v1/admin/products/{id}
GET /api/v1/admin/products/search
GET /api/v1/admin/products/category/{categoryId}

Admin Categories
POST   /api/v1/admin/categories
GET    /api/v1/admin/categories
GET    /api/v1/admin/categories/{id}
PUT    /api/v1/admin/categories/{id}
DELETE /api/v1/admin/categories/{id}

Admin Customers
GET /api/v1/admin/customers
GET /api/v1/admin/customers/{id}
GET /api/v1/admin/customers/search

Admin Orders
GET /api/v1/admin/orders
GET /api/v1/admin/orders/{orderId}
PUT /api/v1/admin/orders/{orderId}/status

🔒 JWT Authentication
After successful login, the backend returns a JWT token.
Protected APIs require:
Authorization: Bearer <JWT_TOKEN>
Admin APIs additionally require the authenticated user to have the:
ADMIN
role.

🗄️ Database
ShopSphere uses MySQL with Spring Data JPA and Hibernate.
Main entities include:
User
Category
Product
Cart
CartItem
Wishlist
Order
OrderItem
Address
Review

⚙️ Configuration
Configure your local database settings in:
src/main/resources/application.properties

Example:
spring.datasource.url=jdbc:mysql://localhost:3306/shopsphere
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
server.port=8080
Never commit real passwords, JWT secrets, AWS credentials, or other sensitive information to GitHub.

▶️ How to Run
1. Clone the repository
git clone https://github.com/Manjunath-java-dev/shopsphere-backend.git

2. Create the database
CREATE DATABASE shopsphere;

3. Configure MySQL
Update your local database credentials in `application.properties`.

4. Build the project
mvn clean install

6. Run the application
mvn spring-boot:run

Backend runs on:
http://localhost:8080


📚 API Documentation
Swagger / OpenAPI is integrated into the project for API documentation and testing.
The APIs can be tested using Swagger UI or Postman.

🧪 Testing
The backend APIs can be tested using:
* Swagger UI
* Postman
* ShopSphere frontend
Protected APIs require a valid JWT token.

📊 Admin Dashboard
The admin dashboard provides:
* Total customers
* Total products
* Total orders
* Pending orders
* Confirmed orders
* Shipped orders
* Delivered orders
* Cancelled orders
* Total sales

🎯 Learning & Development Goals
This project demonstrates practical experience with:
* Java
* Spring Boot
* REST API development
* Spring Security
* JWT
* JPA/Hibernate
* MySQL
* DTOs
* Validation
* Exception handling
* Pagination
* Searching
* Role-based authorization
* Swagger
* Logging
* Git & GitHub

🔮 Future Improvements
Possible future enhancements:
* Payment gateway integration
* Email notifications
* Product image upload
* AWS deployment
* Docker
* Automated testing
* Advanced filtering
* Advanced analytics
* Reporting


👨‍💻 Author
Manjunath HV
Java Backend Developer

Technologies
Java
Spring Boot
Spring Security
JWT
Hibernate
JPA
MySQL
REST API
Maven
Git
GitHub

⭐ Project
ShopSphere — E-Commerce Application
A full-stack E-Commerce project built using Java, Spring Boot, MySQL, HTML, CSS and JavaScript.
