#!/bin/bash

echo "Testing PUT /sessions/{sessionId}/speaker/{speakerId}"
echo "====================================================="

SESSION_ID="1e7f6a5b-8c9d-0e1f-2a3b-4c5d6e7f8a9b"
SPEAKER_ID="9c5d4e3f-6a7b-8c9d-0e1f-2a3b4c5d6e7f"
UNLINKED_SPEAKER_ID="af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e"
NONEXISTENT_ID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"
MALFORMED_SHORT="12345"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (no cookie, should return 401)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 3: Malformed session UUID (should return 400)
echo ""
echo "--- Test 2: Malformed session UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$MALFORMED_UUID/speaker/$SPEAKER_ID

# Step 4: Short malformed session UUID (should return 400)
echo ""
echo "--- Test 3: Short malformed session UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$MALFORMED_SHORT/speaker/$SPEAKER_ID

# Step 5: Malformed speaker UUID (should return 400)
echo ""
echo "--- Test 4: Malformed speaker UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$MALFORMED_UUID

# Step 6: Short malformed speaker UUID (should return 400)
echo ""
echo "--- Test 5: Short malformed speaker UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$MALFORMED_SHORT

# Step 7: Session not found (should return 404)
echo ""
echo "--- Test 6: Non-existent session (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$NONEXISTENT_ID/speaker/$SPEAKER_ID

# Step 8: Speaker not found (should return 404)
echo ""
echo "--- Test 7: Non-existent speaker (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$NONEXISTENT_ID

# Step 9: Speaker not linked to this session (should return 404)
echo ""
echo "--- Test 8: Speaker not linked to session (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$UNLINKED_SPEAKER_ID

# Step 10: Missing startTime (should return 400)
echo ""
echo "--- Test 9: Missing startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 11: Missing endTime (should return 400)
echo ""
echo "--- Test 10: Missing endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 12: Empty startTime (should return 400)
echo ""
echo "--- Test 11: Empty startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"","endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 13: Empty endTime (should return 400)
echo ""
echo "--- Test 12: Empty endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":""}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 14: Blank startTime (should return 400)
echo ""
echo "--- Test 13: Blank startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"   ","endTime":"13:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 15: Blank endTime (should return 400)
echo ""
echo "--- Test 14: Blank endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"12:00:00+02","endTime":"   "}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 16: Empty JSON body (should return 400)
echo ""
echo "--- Test 15: Empty JSON body (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 17: Valid update (should return 200 with "Speaker link updated")
echo ""
echo "--- Test 16: Valid update (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"14:00:00+02","endTime":"15:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Step 18: Restore original data (should return 200)
echo ""
echo "--- Test 17: Restore original data (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"10:00:00+02","endTime":"11:00:00+02"}' https://localhost:443/sessions/$SESSION_ID/speaker/$SPEAKER_ID

# Cleanup
rm -f cookies.txt

echo ""
echo "====================================================="
echo "All PUT sessions speaker tests completed!"
