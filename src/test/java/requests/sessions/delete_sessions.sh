#!/bin/bash

SESSION_ID="86ee1de3-b078-404e-9ee5-a55f2b3ff4a5"
CASCADE_SESSION_ID="4d70c642-744e-41bf-9140-fae49af31269"

echo "Testing DELETE /sessions/{id}"

# Step 1: Authenticate to get JWT cookie
echo -e "\n"
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Valid delete (should return 204)
echo -e "\n"
echo "--- Test 1: Valid delete (should return 204) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/$SESSION_ID

# Step 3: Delete already deleted session (should return 404)
echo -e "\n"
echo "--- Test 2: Delete already deleted session (should return 404) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/$SESSION_ID

# Step 4: Delete with non-existent UUID (should return 404)
echo -e "\n"
echo "--- Test 3: Delete non-existent UUID (should return 404) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/00000000-0000-0000-0000-000000000000

# Step 5: Delete without authentication (should return 401)
echo -e "\n"
echo "--- Test 4: Delete without authentication (should return 401) ---"
curlie -k -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/$SESSION_ID

# Step 6: Delete with invalid UUID format (should return 400)
echo -e "\n"
echo "--- Test 5: Delete with invalid UUID format (should return 400) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/not-a-uuid

# Step 7: Delete with malformed UUID (should return 400)
echo -e "\n"
echo "--- Test 6: Delete with malformed UUID (should return 400) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/12345

# Step 8: Delete session with related intervene and question records (should return 204 with cascade)
echo -e "\n"
echo "--- Test 7: Delete session with related intervene and question (should return 204) ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/$CASCADE_SESSION_ID

# Step 9: Verify cascade deleted session returns 404
echo -e "\n"
echo "--- Test 8: Verify cascade deleted session returns 404 ---"
curlie -k -b cookies.txt -H "Content-Type: application/json" -X DELETE https://localhost:443/sessions/$CASCADE_SESSION_ID

# Cleanup
rm -f cookies.txt
