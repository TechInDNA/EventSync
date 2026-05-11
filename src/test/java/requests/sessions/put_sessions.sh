#!/bin/bash

echo "Testing PUT /sessions/{id}"
echo "=========================="

TOKEN="<insert-admin-token>"
SESSION_ID="f47ac10b-58cc-4372-a567-0e02b2c3d479"

# Test 1: Update an existing session (should return 200)
echo ""
echo "--- Test 1: Update existing session ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X PUT \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Updated Session Title",
    "description": "Updated description for the session",
    "startDate": "2026-06-15T10:00:00Z",
    "endDate": "2026-06-15T12:30:00Z",
    "roomId": "c1c12204-4427-4add-b755-b681719d1685",
    "capacity": 75,
    "eventId": "b3b958ac-bdd7-481a-b8f7-636d43794f83"
  }' "http://localhost:8080/sessions/$SESSION_ID"

# Test 2: Update non-existent session (should return 404)
echo ""
echo "--- Test 2: Non-existent session (should return 404) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X PUT \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Ghost Session",
    "description": "Does not exist",
    "startDate": "2026-06-15T10:00:00Z",
    "endDate": "2026-06-15T12:00:00Z",
    "roomId": "c1c12204-4427-4add-b755-b681719d1685",
    "capacity": 50,
    "eventId": "b3b958ac-bdd7-481a-b8f7-636d43794f83"
  }' "http://localhost:8080/sessions/00000000-0000-0000-0000-000000000000"

# Test 3: Update with invalid data (should return 400)
echo ""
echo "--- Test 3: Invalid data (should return 400) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X PUT \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": ""
  }' "http://localhost:8080/sessions/$SESSION_ID"

# Test 4: Update without auth token (should return 401)
echo ""
echo "--- Test 4: No auth token (should return 401) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Unauthorized Update",
    "description": "Should not work",
    "startDate": "2026-06-15T10:00:00Z",
    "endDate": "2026-06-15T12:00:00Z",
    "roomId": "c1c12204-4427-4add-b755-b681719d1685",
    "capacity": 50,
    "eventId": "b3b958ac-bdd7-481a-b8f7-636d43794f83"
  }' "http://localhost:8080/sessions/$SESSION_ID"

echo ""
echo "====================="
echo "All tests completed!"
