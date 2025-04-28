# Gestion Patisserie - Pastry Management System

## Overview
Gestion Patisserie is a JavaFX-based application designed to help bakeries and pastry shops manage their inventory, orders, clients, and employees. The system provides an intuitive user interface for tracking products, processing customer orders, and managing business operations.

## Features
- **Product Management**: Add, edit, and remove pastry products with details such as name, price, and available quantity
- **Order Processing**: Create and manage customer orders with multiple products
- **Client Management**: Store and access client information for improved customer relationships
- **Employee Administration**: Manage staff accounts with role-based permissions
- **Inventory Tracking**: Monitor product availability and get alerts for low stock
- **Sales Reporting**: Generate reports on sales and popular products

## Project Structure
The project follows a Model-View-Controller (MVC) architecture:

### Model
- `Client`: Stores client information including ID, name, address, and contact details
- `Employee`: Manages employee information including roles and credentials
- `Order`: Represents customer orders with order date, status, and total amount
- `OrderProduct`: Junction class connecting orders with products, including quantity
- `Product`: Stores product information including name, price, and available quantity

### Views (JavaFX)
- Login Screen
- Dashboard
- Product Management
- Order Management
- Client Management
- Employee Management
- Reports

### Controllers
- Controllers for each view to handle user interactions and business logic

## Database Schema
The application uses a relational database with the following tables:
- `client`: Stores client information
- `employee`: Stores employee information
- `product`: Stores product information
- `order`: Stores order information
- `order_product`: Junction table connecting orders and products

## Technologies Used
- Java 17
- JavaFX 17.0.6
- FXML for UI layout
- ControlsFX 11.2.1 for enhanced UI components
- BootstrapFX 0.4.0 for styling
- JUnit 5.10.2 for testing
- Maven for dependency management

## System Requirements
- Java Development Kit (JDK) 17 or later
- Maven 3.6 or later
- Compatible operating system (Windows, macOS, or Linux)

## Setup and Installation
1. Clone the repository:
   ```
   git clone https://github.com/Mouadbouananai/GestionPatisserie.git
   ```

2. Configure the database:
   - Create a new database
   - Run the SQL scripts located in the `database` folder to create tables
   - Update the database configuration in `config.properties`

3. Build the project with Maven:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn javafx:run
   ```
   or
   ```
   java -jar target/GestionPatisserie-1.0-SNAPSHOT.jar
   ```

## Usage

### Login
- Use your employee credentials to log in to the system
- Different permissions are granted based on employee position

### Managing Products
1. Navigate to the Products section
2. Add new products with name, price, and quantity
3. Edit existing products or update inventory counts
4. Remove discontinued products

### Creating Orders
1. Navigate to the Orders section
2. Select a client or create a new one
3. Add products to the order using the product list
4. Specify quantities for each product
5. Finalize the order with status and payment information

### Client Management
1. Navigate to the Clients section
2. Add new clients with contact information
3. View order history for each client
4. Update client information as needed

### Working with Order Products
The system uses ArrayLists to manage products within orders:
1. When creating a new order, products are added to an ArrayList in the Order class
2. Each product in an order is represented by an OrderProduct object
3. The OrderProduct class stores the order ID, product ID, and quantity
4. This in-memory representation is synchronized with the database when saving

## Development
### Running Tests
```
mvn test
```

### Building a Standalone Package
```
mvn clean javafx:jlink
```
This will create a runtime image in the `target/app` directory.

## Contributing
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Contact
For questions or support, please contact [mouadbouananai1@gmail.com]
