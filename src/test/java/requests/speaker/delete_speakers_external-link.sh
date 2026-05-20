#!/bin/bash

echo "Testing DELETE /speakers/{id}/external-link"

TEST_SPEAKER_ID="27dfb67f-b1c5-4f71-b0b2-2190674eefa8"
INVALID_UUID="00000000-0000-0000-0000-000000000000"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -X POST -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Valid delete of existing external link (should return 204)
echo ""
echo "--- Test 1: Valid delete of existing external link (should return 204) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Personal Website"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 3: Delete already deleted link (should return 404)
echo ""
echo "--- Test 2: Delete already deleted link (should return 404) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Personal Website"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 4: Speaker not found (should return 404)
echo ""
echo "--- Test 3: Non-existent speaker ID (should return 404) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" -d '{"name":"GitHub"}' "https://localhost:443/speakers/$INVALID_UUID/external-link"

# Step 5: Invalid UUID format (should return 400)
echo ""
echo "--- Test 4: Invalid UUID format (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" -d '{"name":"GitHub"}' "https://localhost:443/speakers/not-a-uuid/external-link"

# Step 6: Missing name field (should return 400)
echo ""
echo "--- Test 5: Missing name field (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" -d '{}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 7: Empty name field (should return 400)
echo ""
echo "--- Test 6: Empty name field (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" -d '{"name":""}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 8: Delete without authentication (should return 401)
echo ""
echo "--- Test 7: Delete without authentication (should return 401) ---"
curlie -k -X DELETE -H "Content-Type: application/json" -d '{"name":"GitHub"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Cleanup
rm -f cookies.txt
