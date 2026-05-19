#!/bin/bash

SPEAKER_ID="27dfb67f-b1c5-4f71-b0b2-2190674eefa8"

echo "Testing DELETE /speakers/{id}"

# Step 1: Authenticate to get JWT cookie
echo -e "\n"
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Valid delete (should return 204)
echo -e "\n"
echo "--- Test 1: Valid delete (should return 204) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/speakers/$SPEAKER_ID

# Step 3: Delete already deleted speaker (should return 404)
echo -e "\n"
echo "--- Test 2: Delete already deleted speaker (should return 404) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/speakers/$SPEAKER_ID

# Step 4: Delete with non-existent UUID (should return 404)
echo -e "\n"
echo "--- Test 3: Delete non-existent UUID (should return 404) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/speakers/00000000-0000-0000-0000-000000000000

# Step 5: Delete without authentication (should return 401)
echo -e "\n"
echo "--- Test 4: Delete without authentication (should return 401) ---"
curlie -k -H "Content-Type: application/json" -X DELETE https://localhost:443/speakers/$SPEAKER_ID

# Step 6: Delete with invalid UUID format (should return 400)
echo -e "\n"
echo "--- Test 5: Delete with invalid UUID format (should return 400) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/speakers/not-a-uuid

# Step 7: Delete with malformed UUID (should return 400)
echo -e "\n"
echo "--- Test 6: Delete with malformed UUID (should return 400) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/speakers/12345

# Cleanup
rm -f cookies.txt
