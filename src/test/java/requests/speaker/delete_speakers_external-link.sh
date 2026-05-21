#!/bin/bash

SPEAKER_ID="27dfb67f-b1c5-4f71-b0b2-2190674eefa8"
NONEXISTENT_ID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"
MALFORMED_SHORT="12345"

echo "Testing DELETE /speakers/{id}/external-link?urlName={name}"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (should return 401)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401) ---"
curlie -k -X DELETE -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName=GitHub"

# Step 3: Valid delete (should return 204)
echo ""
echo "--- Test 2: Valid delete (should return 204) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName=GitHub"

# Step 4: Delete already deleted external link (should return 404)
echo ""
echo "--- Test 3: Delete already deleted external link (should return 404) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName=GitHub"

# Step 5: Delete non-existent urlName for existing speaker (should return 404)
echo ""
echo "--- Test 4: Non-existent urlName (should return 404) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName=NonexistentLink"

# Step 6: Delete with non-existent speaker UUID (should return 404)
echo ""
echo "--- Test 5: Non-existent speaker (should return 404) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$NONEXISTENT_ID/external-link?urlName=GitHub"

# Step 7: Delete with invalid UUID format (should return 400)
echo ""
echo "--- Test 6: Invalid UUID format (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$MALFORMED_UUID/external-link?urlName=GitHub"

# Step 8: Delete with malformed UUID (should return 400)
echo ""
echo "--- Test 7: Malformed UUID (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$MALFORMED_SHORT/external-link?urlName=GitHub"

# Step 9: Delete with empty urlName (should return 400)
echo ""
echo "--- Test 8: Empty urlName (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName="

# Step 10: Delete with blank urlName (spaces, should return 400)
echo ""
echo "--- Test 9: Blank urlName (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName=%20%20%20"

# Step 11: Delete with urlName containing invalid char '@' (should return 400)
echo ""
echo "--- Test 10: urlName with invalid char '@' (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName=Git@Hub"

# Step 12: Delete with urlName containing invalid char '!' (should return 400)
echo ""
echo "--- Test 11: urlName with invalid char '!' (should return 400) ---"
curlie -k -X DELETE -b cookies.txt -H "Content-Type: application/json" "https://localhost:443/speakers/$SPEAKER_ID/external-link?urlName=GitHub!"

# Cleanup
rm -f cookies.txt
