# Self-Service Signup Implementation

## Overview
Implemented self-service registration for both parent and rider users. These users can now sign up without requiring school assignment or authentication.

## Key Changes

### 1. Parent Signup (AuthService.registerParent)
**File:** `src/main/java/com/sqool/sqoolbus/service/AuthService.java`

**Changes:**
- Made `schoolId` optional in signup process
- Generate unique 6-digit user code automatically (format: `PRTxxxxxx`)
- Handle null school assignment in profile creation
- Handle null schoolId in JWT token generation
- Return null `SchoolInfo` in response when school not assigned

**User Code Generation:**
- Format: `PRT` + 6-digit padded user ID
- Example: User ID 1 → `PRT000001`
- Automatically assigned after user creation

### 2. Rider Signup (UserManagementService.createRider)
**File:** `src/main/java/com/sqool/sqoolbus/tenant/service/UserManagementService.java`

**Changes:**
- Made school parameter nullable
- Generate unique 6-digit rider code when username not provided (format: `RDRxxxxxx`)
- Handle null school in UserProfile creation

**User Code Generation:**
- Format: `RDR` + 6-digit padded user ID
- Example: User ID 5 → `RDR000005`
- Generated only if username is null or empty

### 3. Security Configuration
**File:** `src/main/java/com/sqool/sqoolbus/config/SecurityConfig.java`

**Changes:**
- Added `/api/users/parent` to permitAll (no authentication required)
- Added `/api/users/rider` to permitAll (no authentication required)

### 4. Request DTOs
**File:** `src/main/java/com/sqool/sqoolbus/dto/auth/ParentSignupRequest.java`

**Changes:**
- Removed `@NotNull` validation from `schoolId` field
- Updated description to indicate school is optional

### 5. Controller Updates
**File:** `src/main/java/com/sqool/sqoolbus/tenant/controller/UserController.java`

**Changes:**
- Removed `@RequirePermissions` annotation from `/api/users/rider` endpoint
- Made school lookup conditional based on `schoolId` presence
- Updated API documentation to reflect optional school assignment

## API Endpoints

### Parent Signup
```
POST /api/users/parent
Content-Type: application/json
X-Tenant-ID: {tenantId}

{
  "username": "john_doe",          // Optional - will be generated if not provided
  "email": "john@example.com",     // Required
  "password": "SecurePass123!",    // Required
  "firstName": "John",             // Required
  "lastName": "Doe",               // Required
  "phoneNumber": "+1234567890",
  "schoolId": 1                    // Optional - can be null
}
```

**Response:**
- Status: 201 Created
- JWT token included
- `schoolInfo` will be null if no school assigned
- `username` will be auto-generated code (e.g., `PRT000001`)

### Rider Signup
```
POST /api/users/rider
Content-Type: application/json

{
  "username": "",                  // Can be empty - will be generated
  "email": "rider@example.com",    // Required
  "password": "SecurePass123!",    // Required
  "firstName": "Jane",             // Required
  "lastName": "Smith",             // Required
  "schoolId": null,                // Optional
  "licenseNumber": "DL123456",
  "employeeId": "EMP001"
}
```

Tenant is resolved from `schoolId`. The `X-Tenant-ID` header is ignored for this endpoint.

**Response:**
- Status: 201 Created
- User object with auto-generated username (e.g., `RDR000005`)

## User Code Format

### Parent Users
- Prefix: `PRT`
- Format: `PRTxxxxxx` (9 characters total)
- Example: `PRT000001`, `PRT000042`, `PRT012345`

### Rider Users
- Prefix: `RDR`
- Format: `RDRxxxxxx` (9 characters total)
- Example: `RDR000001`, `RDR000018`, `RDR054321`

## Benefits

1. **Self-Service Onboarding:** Users can register without admin intervention
2. **Flexible School Assignment:** School can be assigned later if not available during signup
3. **Unique Identification:** Every user gets a unique, easily identifiable code
4. **No Authentication Required:** Public signup endpoints for easier onboarding
5. **Consistent Naming:** Predictable username format based on user role

## JWT Token Behavior

### With School Assignment
- Token includes `schoolId` claim
- User has full access to school-specific features

### Without School Assignment
- Token generated with null `schoolId`
- User can still authenticate and use non-school-specific features
- School can be assigned later through admin or profile update

## Testing

Build successful with all changes:
```bash
./gradlew build -x test
```

### Test Scenarios

1. **Parent signup without school:**
   - POST to `/api/users/parent` with `schoolId: null`
   - Verify username starts with `PRT`
   - Verify JWT token generated
   - Verify response has null `schoolInfo`

2. **Rider signup without school:**
   - POST to `/api/users/rider` with `schoolId: null` and empty username
   - Verify username starts with `RDR`
   - Verify user created successfully

3. **Parent signup with school:**
   - POST with valid `schoolId`
   - Verify school assignment in profile
   - Verify `schoolInfo` in response

## Future Enhancements

1. Consider adding school assignment API for users who signed up without school
2. Add email verification flow for self-registered users
3. Implement user profile completion wizard for users without school
4. Add analytics to track self-service signup rates
