# Frontend Page to Backend API Mapping (v2)

Base URL: `http://localhost:8080`

## 1. Account Creation Page
- Action: Register customer
- Endpoint: `POST /api/v2/accounts/register`
- Request:
```json
{
  "fullName": "Rahul Sharma",
  "email": "rahul.sharma@example.com",
  "mobileNumber": "9876543210",
  "dateOfBirth": "1998-04-16"
}
```
- Store: `customer.id`, `customer.customerCode`

## 2. KYC Submission Page
- Action: Submit KYC details
- Endpoint: `POST /api/v2/kyc/{customerId}/submit`
- Request:
```json
{
  "aadhaarNumber": "123456789012",
  "panNumber": "ABCDE1234F",
  "addressLine": "12 MG Road",
  "city": "Bengaluru",
  "state": "Karnataka",
  "postalCode": "560001",
  "country": "India"
}
```

## 3. KYC Review/Admin Page
- Approve KYC: `PUT /api/v2/kyc/{customerId}/approve`
- Reject KYC: `PUT /api/v2/kyc/{customerId}/reject`
- Reject request body:
```json
{
  "reason": "Document mismatch"
}
```
- Fetch KYC status: `GET /api/v2/kyc/{customerId}`

## 4. Savings Account Opening Page
- Action: Open account (only after KYC approved)
- Endpoint: `POST /api/v2/savings/{customerId}/open`
- Request:
```json
{
  "initialDeposit": 5000.00,
  "interestRate": 3.50
}
```
- Store: `accountNumber`

## 5. Savings Dashboard Page
- Get account details + recent transactions:
- Endpoint: `GET /api/v2/savings/{accountNumber}`

## 6. Deposit Page
- Endpoint: `POST /api/v2/savings/{accountNumber}/deposit`
- Request:
```json
{
  "amount": 1200.00,
  "remarks": "cash deposit"
}
```

## 7. Withdraw Page
- Endpoint: `POST /api/v2/savings/{accountNumber}/withdraw`
- Request:
```json
{
  "amount": 200.00,
  "remarks": "atm withdrawal"
}
```

## 8. Customer Accounts List Page
- Endpoint: `GET /api/v2/savings/customer/{customerId}`

## 9. User Management List Page
- List users: `GET /api/v2/users`
- Filter by status: `GET /api/v2/users?status=ACTIVE`

## 10. User Details Page
- Endpoint: `GET /api/v2/users/{customerId}`

## 11. User Status Update Page (Admin)
- Endpoint: `PUT /api/v2/users/{customerId}/status/{status}`
- Allowed status: `PENDING_KYC`, `ACTIVE`, `BLOCKED`, `REJECTED`

## Frontend Environment
- Add in frontend `.env`:
```env
VITE_API_BASE_URL=http://localhost:8080
```
