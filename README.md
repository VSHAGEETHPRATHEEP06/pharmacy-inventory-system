# Pharmacy Inventory Management System

A comprehensive web-based system for managing pharmacy inventory, sales, purchases, suppliers, and more.

## Features

- **User Management**: Role-based authentication and authorization with JWT
- **Inventory Management**: Track medicines, batches, and stock levels
- **Supplier Management**: Manage suppliers and rate their performance
- **Purchase Management**: Create and track purchase orders
- **Sales Management**: Process sales and generate invoices
- **Notification System**: Get alerted for low stock and expiring medicines
- **Reports**: Generate reports on sales, inventory, and supplier performance

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.1.5
- Spring Security with JWT Authentication
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI for API Documentation

### Frontend (To be implemented)
- React + Vite
- Material UI or Tailwind CSS
- Axios for API requests
- React Router for routing
- Redux or Context API for state management

## Getting Started

### Prerequisites
- Java 17
- Maven
- PostgreSQL
- Node.js and npm (for frontend development)

### Database Setup
1. Create a PostgreSQL database named `pharmacy_inventory`
2. Update the database configuration in `src/main/resources/application.properties` if needed

### Building and Running the Backend
1. Navigate to the project directory
2. Build the project:
   ```
   mvn clean install
   ```
3. Run the application:
   ```
   mvn spring-boot:run
   ```
4. The application will be available at `http://localhost:8081`
5. API documentation can be accessed at `http://localhost:8081/swagger-ui.html`

### API Endpoints

#### Authentication
- POST `/api/auth/login` - Login and get JWT token
- POST `/api/auth/register` - Register a new user

#### Medicine Management
- GET `/api/medicines` - Get all medicines
- GET `/api/medicines/{id}` - Get medicine by ID
- POST `/api/medicines` - Add a new medicine
- PUT `/api/medicines/{id}` - Update medicine
- DELETE `/api/medicines/{id}` - Delete medicine

#### Batch Management
- GET `/api/batches` - Get all batches
- GET `/api/batches/{id}` - Get batch by ID
- POST `/api/batches` - Add a new batch
- PUT `/api/batches/{id}` - Update batch
- DELETE `/api/batches/{id}` - Delete batch

#### Stock Management
- GET `/api/stock` - Get all stock
- GET `/api/stock/{id}` - Get stock by ID
- GET `/api/stock/low-stock` - Get low stock items
- POST `/api/stock` - Add new stock
- PUT `/api/stock/{id}` - Update stock

#### Supplier Management
- GET `/api/suppliers` - Get all suppliers
- GET `/api/suppliers/{id}` - Get supplier by ID
- POST `/api/suppliers` - Add a new supplier
- PUT `/api/suppliers/{id}` - Update supplier
- DELETE `/api/suppliers/{id}` - Delete supplier

#### Purchase Order Management
- GET `/api/purchase-orders` - Get all purchase orders
- GET `/api/purchase-orders/{id}` - Get purchase order by ID
- POST `/api/purchase-orders` - Create a new purchase order
- PUT `/api/purchase-orders/{id}` - Update purchase order
- PATCH `/api/purchase-orders/{id}/status/{status}` - Update purchase order status
- DELETE `/api/purchase-orders/{id}` - Delete purchase order

#### Sales Management
- GET `/api/sales` - Get all sales
- GET `/api/sales/{id}` - Get sale by ID
- POST `/api/sales` - Create a new sale
- PUT `/api/sales/{id}` - Update sale
- DELETE `/api/sales/{id}` - Delete sale

#### Notification Management
- GET `/api/notifications/user/{userId}` - Get notifications for a user
- GET `/api/notifications/user/{userId}/unread` - Get unread notifications for a user
- PATCH `/api/notifications/{id}/read` - Mark notification as read
- PATCH `/api/notifications/user/{userId}/read-all` - Mark all user notifications as read
- POST `/api/notifications/generate/low-stock` - Generate low stock notifications
- POST `/api/notifications/generate/expiry` - Generate expiry notifications

## Security and Authentication

The system uses JWT (JSON Web Token) for authentication. To access secure endpoints:
1. Obtain a JWT token by logging in through `/api/auth/login`
2. Include the token in the `Authorization` header as `Bearer {token}` for all secure API requests

## Role-Based Access Control

- **ROLE_ADMIN**: Full access to all features
- **ROLE_PHARMACIST**: Manage inventory, process sales, and view reports
- **ROLE_SALESPERSON**: Process sales and view inventory
- **ROLE_MANAGER**: View reports, monitor stock, and manage suppliers

## Next Steps for Frontend Development

The backend APIs are fully prepared to be integrated with a React + Vite frontend. The next steps would be:

1. Initialize a React + Vite project
2. Set up routing with React Router
3. Implement authentication and state management
4. Create UI components for each feature
5. Connect APIs to the frontend
6. Implement responsive design for various devices


## Contact

For any questions or suggestions, please open an issue in this repository.
