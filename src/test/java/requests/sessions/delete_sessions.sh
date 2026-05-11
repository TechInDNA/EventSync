#!/bin/bash

echo "Testing DELETE /sessions/{id}"
echo "============================="

TOKEN="<insert-admin-token>"

# Test 1: Delete a session by valid ID (should return 200)
echo ""
echo "--- Test 1: Delete existing session ---"
SESSION_ID="f47ac10b-58cc-4372-a567-0e02b2c3d479"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/$SESSION_ID"

# Test 2: Delete non-existent session (should return 404)
echo ""
echo "--- Test 2: Non-existent ID (should return 404) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/00000000-0000-0000-0000-000000000000"

# Test 3: Delete with invalid UUID (should return 400)
echo ""
echo "--- Test 3: Invalid UUID (should return 400) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/not-a-uuid"

# Test 4: Delete without auth token (should return 401)
echo ""
echo "--- Test 4: No auth token (should return 401) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  "http://localhost:8080/sessions/$SESSION_ID"

echo ""
echo "====================="
echo "All tests completed!"
