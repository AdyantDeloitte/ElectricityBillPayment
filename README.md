# Electricity Bill Payment System

A simple electricity bill payment system built with Kotlin and Spring Boot.

## Features Implemented

### 1. User Management

- **User Registration**: Register new users with basic information
- **No Authentication Required**: Direct access without login

### 2. Bill Management

- **Get Bill by USN**: Fetch bill details using unique service number
- **List User Bills**: View all bills for a specific user
- **Update Bill Status**: Admin can update bill status (PENDING, PAID, OVERDUE, CANCELLED)
- **Status Tracking**: All bills have status fields

### 3. Payment Processing

- **Get Payment Methods**: List available payment options (Credit Card, Debit Card, UPI, etc.)
- **Process Payment**: Make full payments only (no partial payments)
- **Auto Status Update**: Bill status automatically changes to PAID after successful payment
- **Payment Validation**: Ensures full payment amount matches bill total

### 4. Complaint Management

- **Register Complaint**: Submit complaints with category and subcategory
- **List User Complaints**: View all complaints for a specific user
- **Update Complaint Status**: Admin can update complaint status (OPEN, IN_PROGRESS, RESOLVED, CLOSED, REJECTED)
- **Status Tracking**: All complaints have status fields

## How to Run

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Steps to Run

1. **Clone/Download the project**

   ```bash
   cd ElectricityBillPayment
   ```

2. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

3. **Application will start on**

   ```
   http://localhost:8080
   ```

4. **Test the APIs**
   - Import `ElectricityBillPayment.postman_collection.json` in Postman
   - Set base URL to `http://localhost:8080`
   - Run the API calls

## API Endpoints

### User Management

- `POST /api/v1/users/register` - Register new user

### Security Hints

- `GET /api/v1/hints` - Get all security questions for registration

### Bill Management

- `GET /api/v1/bills/getBillByUSN/{usn}` - Get bill by service number
- `GET /api/v1/bills/user/{userId}` - Get all bills for a user
- `PUT /api/v1/bills/{billId}/status` - Update bill status

### Payment Processing

- `GET /api/v1/payments/methods` - Get available payment methods
- `POST /api/v1/payments` - Process payment

### Complaint Management

- `POST /api/v1/complaints` - Register complaint
- `GET /api/v1/complaints/user/{userId}` - Get all complaints for a user
- `PUT /api/v1/complaints/{complaintId}/status` - Update complaint status

## Sample Data

The application comes with sample data:

- **Sample Bill**: USN2024123456789 (Amount: ₹2170.5)
- **Sample User**: ID 1
- **Payment Methods**: 6 methods available (Credit Card, Debit Card, UPI, etc.)
- **Categories**: Billing Issue, Technical Issue, etc.

## Quick Test

1. **Get security hints**:

   ```
   GET http://localhost:8080/api/v1/hints
   ```

2. **Get a bill**:

   ```
   GET http://localhost:8080/api/v1/bills/getBillByUSN/USN2024123456789
   ```

3. **Get payment methods**:

   ```
   GET http://localhost:8080/api/v1/payments/methods
   ```

4. **Process payment**:
   ```
   POST http://localhost:8080/api/v1/payments
   {
     "billId": 1,
     "paymentMethodId": 1,
     "amount": 2170.5
   }
   ```

## Database

- Uses H2 in-memory database
- Data is automatically loaded on startup
- No database setup required

## Status Values

### Bill Status

- PENDING
- PAID
- OVERDUE
- CANCELLED

### Complaint Status

- OPEN
- IN_PROGRESS
- RESOLVED
- CLOSED
- REJECTED

## Error Handling

- Returns proper HTTP status codes
- Clear error messages for validation failures
- Full payment required (no partial payments)
- Status validation for updates

## Notes

- All payments must be full amount (no partial payments)
- No authentication required
- All responses include status fields
- Bill status automatically updates to PAID after successful payment
