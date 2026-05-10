#!/bin/bash

EVENT_ID="a7cc7aac-50db-44cd-b8ae-cac7737a4052"

echo "Testing DELETE /events/{id}"

# Step 1: Authenticate to get JWT cookie
echo -e "\n"
echo "--- Authenticating to get JWT cookie ---"
curlie -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

# Step 2: Valid delete (should return 200)
echo -e "\n"
echo "--- Test 1: Valid delete (should return 200) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/events/$EVENT_ID

# Step 3: Delete already deleted event (should return 404)
echo -e "\n"
echo "--- Test 2: Delete already deleted event (should return 404) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/events/$EVENT_ID

# Step 4: Delete with non-existent UUID (should return 404)
echo -e "\n"
echo "--- Test 3: Delete non-existent UUID (should return 404) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/events/00000000-0000-0000-0000-000000000000

# Step 5: Delete without authentication (should return 401)
echo -e "\n"
echo "--- Test 4: Delete without authentication (should return 403) ---"
curlie -H "Content-Type: application/json" -X DELETE http://localhost:8080/events/$EVENT_ID

# Step 6: Delete with invalid UUID format (should return 400)
echo -e "\n"
echo "--- Test 5: Delete with invalid UUID format (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/events/not-a-uuid

# Step 7: Delete with malformed UUID (should return 400)
echo -e "\n"
echo "--- Test 6: Delete with malformed UUID (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -X DELETE http://localhost:8080/events/12345

# Cleanup
rm -f cookies.txt
