#!/bin/bash

echo "Testing POST /sessions/{sessionId}/speaker/{speakerId}"
echo "========================================================"



# Known session IDs from seed data
SESSION_WITHOUT_SPEAKER="8b9c0d1e-2f3a-4b5c-6d7e-8f9a0b1c2d3e"
SESSION_WITH_SPEAKER="3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f"
NONEXISTENT_SESSION="00000000-0000-0000-0000-000000000000"

# Known speaker IDs from seed data
SPEAKER_CHARLIE_BROWN="c2d3e4f5-a6b7-4c8d-9e0f-1a2b3c4d5e6f"
NONEXISTENT_SPEAKER="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"
MALFORMED_SHORT="12345"

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" \
  -d '{"email": "admin@eventsync.com", "password": "test"}' \
  "$BASE_URL/auth/login"

# Step 2: Valid link creation (should return 201)
echo ""
echo "--- Test 1: Valid speaker-to-session link (should return 201) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Content-Type: application/json" \
  -d '{"startTime":"14:00:00+02:00","endTime":"15:30:00+02:00"}' \
  "$BASE_URL/sessions/$SESSION_WITHOUT_SPEAKER/speaker/$SPEAKER_CHARLIE_BROWN"

# Step 3: Non-existent session (should return 404)
echo ""
echo "--- Test 2: Non-existent session (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Content-Type: application/json" \
  -d '{"startTime":"10:00:00+02:00","endTime":"11:00:00+02:00"}' \
  "$BASE_URL/sessions/$NONEXISTENT_SESSION/speaker/$SPEAKER_CHARLIE_BROWN"

# Step 4: Non-existent speaker (should return 404)
echo ""
echo "--- Test 3: Non-existent speaker (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Content-Type: application/json" \
  -d '{"startTime":"10:00:00+02:00","endTime":"11:00:00+02:00"}' \
  "$BASE_URL/sessions/$SESSION_WITHOUT_SPEAKER/speaker/$NONEXISTENT_SPEAKER"

# Step 5: Invalid session UUID format (should return 400)
echo ""
echo "--- Test 4: Malformed session UUID (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Content-Type: application/json" \
  -d '{"startTime":"10:00:00+02:00","endTime":"11:00:00+02:00"}' \
  "$BASE_URL/sessions/$MALFORMED_UUID/speaker/$SPEAKER_CHARLIE_BROWN"

# Step 6: Invalid speaker UUID format (should return 400)
echo ""
echo "--- Test 5: Malformed speaker UUID (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Content-Type: application/json" \
  -d '{"startTime":"10:00:00+02:00","endTime":"11:00:00+02:00"}' \
  "$BASE_URL/sessions/$SESSION_WITHOUT_SPEAKER/speaker/$MALFORMED_SHORT"

# Step 7: No authentication (should return 401 or 403)
echo ""
echo "--- Test 6: No authentication (should return 401) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Content-Type: application/json" \
  -d '{"startTime":"10:00:00+02:00","endTime":"11:00:00+02:00"}' \
  "$BASE_URL/sessions/$SESSION_WITHOUT_SPEAKER/speaker/$SPEAKER_CHARLIE_BROWN"

# Step 8: Link the same speaker-session pair again (still valid, should return 201)
echo ""
echo "--- Test 7: Duplicate link (no unique constraint, should return 201) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Content-Type: application/json" \
  -d '{"startTime":"09:00:00+02:00","endTime":"10:00:00+02:00"}' \
  "$BASE_URL/sessions/$SESSION_WITHOUT_SPEAKER/speaker/$SPEAKER_CHARLIE_BROWN"

# Cleanup
rm -f cookies.txt

echo ""
echo "========================================================"
echo "All tests completed!"
