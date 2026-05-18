#!/bin/bash

EVENT_ID="a7cc7aac-50db-44cd-b8ae-cac7737a4052"

echo "Testing DELETE /events/{id}"

# Step 1: Authenticate to get JWT cookie
echo -e "\n"
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Valid delete (should return 200)
echo -e "\n"
echo "--- Test 1: Valid delete (should return 200) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/events/$EVENT_ID

# Step 3: Delete already deleted event (should return 404)
echo -e "\n"
echo "--- Test 2: Delete already deleted event (should return 404) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/events/$EVENT_ID

# Step 4: Delete with non-existent UUID (should return 404)
echo -e "\n"
echo "--- Test 3: Delete non-existent UUID (should return 404) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/events/00000000-0000-0000-0000-000000000000

# Step 5: Delete without authentication (should return 401)
echo -e "\n"
echo "--- Test 4: Delete without authentication (should return 403) ---"
curlie -k -H "Content-Type: application/json" -X DELETE https://localhost:443/events/$EVENT_ID

# Step 6: Delete with invalid UUID format (should return 400)
echo -e "\n"
echo "--- Test 5: Delete with invalid UUID format (should return 400) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/events/not-a-uuid

# Step 7: Delete with malformed UUID (should return 400)
echo -e "\n"
echo "--- Test 6: Delete with malformed UUID (should return 400) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/events/12345

# Step 8: Delete event with attached sessions (should return 200 and cascade delete sessions)
EVENT_WITH_SESSIONS_ID="e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b"
echo -e "\n"
echo "--- Test 7: Delete event with attached sessions (should return 200, cascade delete sessions) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/events/$EVENT_WITH_SESSIONS_ID

# Cleanup
rm -f cookies.txt
