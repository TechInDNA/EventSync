#!/bin/bash

echo "Testing DELETE /sessions/{sessionId}/speaker/{speakerId}"
echo "======================================================"

TOKEN="<insert-admin-token>"
SESSION_ID="f47ac10b-58cc-4372-a567-0e02b2c3d479"
SPEAKER_ID="af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e"
UNKNOWN_UUID="00000000-0000-0000-0000-000000000000"

# Test 1: DELETE - Remove existing speaker from session (should return 204)
echo ""
echo "--- Test 1: Remove existing speaker (should return 204) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/$SESSION_ID/speaker/$SPEAKER_ID"

# Test 2: DELETE - Non-existent intervention (should return 404)
echo ""
echo "--- Test 2: Non-existent intervention (should return 404) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/$SESSION_ID/speaker/$SPEAKER_ID"

# Test 3: DELETE - Non-existent session (should return 404)
echo ""
echo "--- Test 3: Non-existent session (should return 404) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/$UNKNOWN_UUID/speaker/$SPEAKER_ID"

# Test 4: DELETE - Non-existent speaker (should return 404)
echo ""
echo "--- Test 4: Non-existent speaker (should return 404) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/$SESSION_ID/speaker/$UNKNOWN_UUID"

# Test 5: DELETE - No auth token (should return 401)
echo ""
echo "--- Test 5: No auth token (should return 401) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  "http://localhost:8080/sessions/$SESSION_ID/speaker/$SPEAKER_ID"

# Test 6: DELETE - Malformed sessionId (should return 400)
echo ""
echo "--- Test 6: Malformed sessionId (should return 400) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/not-a-uuid/speaker/$SPEAKER_ID"

# Test 7: DELETE - Malformed speakerId (should return 400)
echo ""
echo "--- Test 7: Malformed speakerId (should return 400) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -X DELETE \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/sessions/$SESSION_ID/speaker/invalid-uuid"

echo ""
echo "======================================================"
echo "All tests completed!"
