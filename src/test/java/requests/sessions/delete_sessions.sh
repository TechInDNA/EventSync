#!/bin/bash

SESSION_ID="86ee1de3-b078-404e-9ee5-a55f2b3ff4a5"

echo "Testing DELETE /sessions/{id}"

# Step 1: Authenticate to get JWT cookie
echo -e "\n"
echo "--- Authenticating to get JWT cookie ---"
curlie -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

# Step 2: Valid delete (should return 200)
echo -e "\n"
echo "--- Test 1: Valid delete (should return 200) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/sessions/$SESSION_ID

# Step 3: Delete already deleted session (should return 404)
echo -e "\n"
echo "--- Test 2: Delete already deleted session (should return 404) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/sessions/$SESSION_ID

# Step 4: Delete with non-existent UUID (should return 404)
echo -e "\n"
echo "--- Test 3: Delete non-existent UUID (should return 404) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/sessions/00000000-0000-0000-0000-000000000000

# Step 5: Delete without authentication (should return 403)
echo -e "\n"
echo "--- Test 4: Delete without authentication (should return 403) ---"
curlie -H "Content-Type: application/json" -X DELETE http://localhost:8080/sessions/$SESSION_ID

# Step 6: Delete with invalid UUID format (should return 400)
echo -e "\n"
echo "--- Test 5: Delete with invalid UUID format (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/sessions/not-a-uuid

# Step 7: Delete with malformed UUID (should return 400)
echo -e "\n"
echo "--- Test 6: Delete with malformed UUID (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/sessions/12345

# Cleanup
rm -f cookies.txt
