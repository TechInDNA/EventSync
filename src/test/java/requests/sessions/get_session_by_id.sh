#!/bin/bash

echo "Testing GET /sessions/{id}"
echo "=========================="

# Test 1: Get session by valid ID (should return 200)
echo ""
echo "--- Test 1: Get session by valid ID ---"
SESSION_ID="f47ac10b-58cc-4372-a567-0e02b2c3d479"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions/$SESSION_ID"

# Test 2: Get session by non-existent ID (should return 404)
echo ""
echo "--- Test 2: Non-existent ID (should return 404) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions/00000000-0000-0000-0000-000000000000"

# Test 3: Get session with invalid UUID format (should return 400)
echo ""
echo "--- Test 3: Invalid UUID format (should return 400) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions/not-a-uuid"

echo ""
echo "====================="
echo "All tests completed!"
