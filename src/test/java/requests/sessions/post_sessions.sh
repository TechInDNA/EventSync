#!/bin/bash

echo "Testing POST /sessions"
echo "======================"

TOKEN="<insert-admin-token>"

# Test 1: Create a new session (should return 201)
echo ""
echo "--- Test 1: Create a new session ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Intro to Kubernetes",
    "description": "A beginner-friendly Kubernetes workshop",
    "startDate": "2026-09-01T10:00:00Z",
    "endDate": "2026-09-01T12:00:00Z",
    "roomId": "c1c12204-4427-4add-b755-b681719d1685",
    "capacity": 80,
    "eventId": "b3b958ac-bdd7-481a-b8f7-636d43794f83"
  }' http://localhost:8080/sessions

# Test 2: Create with duplicate title (should return 409)
echo ""
echo "--- Test 2: Duplicate title (should return 409) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Intro to Kubernetes",
    "description": "Another attempt",
    "startDate": "2026-09-01T14:00:00Z",
    "endDate": "2026-09-01T16:00:00Z",
    "roomId": "c1c12204-4427-4add-b755-b681719d1685",
    "capacity": 50,
    "eventId": "b3b958ac-bdd7-481a-b8f7-636d43794f83"
  }' http://localhost:8080/sessions

# Test 3: Create with missing fields (should return 400)
echo ""
echo "--- Test 3: Missing required fields (should return 400) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "Incomplete Session"
  }' http://localhost:8080/sessions

# Test 4: Create without auth token (should return 401)
echo ""
echo "--- Test 4: No auth token (should return 401) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Unauthorized Session",
    "description": "Should not be created",
    "startDate": "2026-09-01T10:00:00Z",
    "endDate": "2026-09-01T12:00:00Z",
    "roomId": "c1c12204-4427-4add-b755-b681719d1685",
    "capacity": 30,
    "eventId": "b3b958ac-bdd7-481a-b8f7-636d43794f83"
  }' http://localhost:8080/sessions

echo ""
echo "====================="
echo "All tests completed!"
