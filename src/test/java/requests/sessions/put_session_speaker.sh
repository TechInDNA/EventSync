#!/bin/bash

echo "Testing PUT /sessions/{sessionId}/speaker/{speakerId}"
echo "======================================================"

SESSION_D_ID="550e8400-e29b-41d4-a716-446655440033"
SESSION_A_ID="550e8400-e29b-41d4-a716-446655440030"
SESSION_B_ID="550e8400-e29b-41d4-a716-446655440031"
CHARLIE_ID="550e8400-e29b-41d4-a716-446655440022"
ALICE_ID="550e8400-e29b-41d4-a716-446655440020"
BOB_ID="550e8400-e29b-41d4-a716-446655440021"
INVALID_UUID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"

# Step 1: Authenticate as admin
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Test 1: Unauthenticated request (no cookie, should return 401)
echo ""
echo "--- Test 1: Unauthenticated (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 2: Valid update of existing intervene link (Session D + Charlie, 14:00-15:00 -> 10:00-12:00)
echo ""
echo "--- Test 2: Valid update existing link (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 3: Same speaker assigned to same session (Bob already linked to Session B, update 11:00-12:00 -> 12:00-13:00)
echo ""
echo "--- Test 3: Same speaker same session (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T12:00:00Z","endTime":"2026-07-01T13:00:00Z"}' https://localhost:443/sessions/$SESSION_B_ID/speaker/$BOB_ID

# Test 4: Valid update of another existing link (Session B + Alice, 10:00-11:00 -> 09:00-10:00)
echo ""
echo "--- Test 4: Update Session B + Alice existing link (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T09:00:00Z","endTime":"2026-07-01T10:00:00Z"}' https://localhost:443/sessions/$SESSION_B_ID/speaker/$ALICE_ID

# Test 5: Malformed sessionId (should return 400)
echo ""
echo "--- Test 5: Malformed sessionId (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$MALFORMED_UUID/speaker/$CHARLIE_ID

# Test 6: Malformed speakerId (should return 400)
echo ""
echo "--- Test 6: Malformed speakerId (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$MALFORMED_UUID

# Test 7: Missing startTime (should return 400)
echo ""
echo "--- Test 7: Missing startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 8: Missing endTime (should return 400)
echo ""
echo "--- Test 8: Missing endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 9: Empty startTime (should return 400)
echo ""
echo "--- Test 9: Empty startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 10: Empty endTime (should return 400)
echo ""
echo "--- Test 10: Empty endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":""}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 11: Blank startTime (spaces only, should return 400)
echo ""
echo "--- Test 11: Blank startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"   ","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 12: Blank endTime (spaces only, should return 400)
echo ""
echo "--- Test 12: Blank endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"   "}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 13: Invalid startTime format (not ISO, should return 400)
echo ""
echo "--- Test 13: Invalid startTime format (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"20-06-2026","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 14: Invalid endTime format (not ISO, should return 400)
echo ""
echo "--- Test 14: Invalid endTime format (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"not-a-date"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 15: startTime missing Z suffix (should return 400)
echo ""
echo "--- Test 15: startTime without Z suffix (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 16: endTime missing Z suffix (should return 400)
echo ""
echo "--- Test 16: endTime without Z suffix (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 17: Non-existent intervene pair (Session A has no speakers, should return 404)
echo ""
echo "--- Test 17: Non-existent intervene link (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_A_ID/speaker/$CHARLIE_ID

# Test 18: Non-existent session UUID (should return 404)
echo ""
echo "--- Test 18: Non-existent session UUID (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$INVALID_UUID/speaker/$CHARLIE_ID

# Test 19: Non-existent speaker UUID (should return 404)
echo ""
echo "--- Test 19: Non-existent speaker UUID (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T10:00:00Z","endTime":"2026-06-20T12:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$INVALID_UUID

# Test 20: Restore Session D + Charlie original data (14:00-15:00, should return 200)
echo ""
echo "--- Test 20: Restore Session D + Charlie original data (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-06-20T14:00:00Z","endTime":"2026-06-20T15:00:00Z"}' https://localhost:443/sessions/$SESSION_D_ID/speaker/$CHARLIE_ID

# Test 21: Restore Session B + Bob original data (11:00-12:00, should return 200)
echo ""
echo "--- Test 21: Restore Session B + Bob original data (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T11:00:00Z","endTime":"2026-07-01T12:00:00Z"}' https://localhost:443/sessions/$SESSION_B_ID/speaker/$BOB_ID

# Test 22: Restore Session B + Alice original data (10:00-11:00, should return 200)
echo ""
echo "--- Test 22: Restore Session B + Alice original data (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/$SESSION_B_ID/speaker/$ALICE_ID

# Cleanup
rm -f cookies.txt

echo ""
echo "======================================================"
echo "All PUT intervene tests completed!"
