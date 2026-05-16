#!/bin/bash

SESSION_ID="a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb"
QUESTION_ID="ddf75918-e752-4fb1-b316-cb4574da81de"

echo "Testing POST /sessions/{id}/questions/{qid}/upvote"
echo "=================================================="
echo "Session: $SESSION_ID"
echo "Question: $QUESTION_ID"

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curl -k -c cookies.txt -H "Content-Type: application/json" \
  -d '{"email": "admin@eventsync.com", "password": "test"}' \
  https://localhost:8443/auth/login

# Step 2: Valid upvote (should return 200)
echo ""
echo "--- Test 1: Valid upvote (should return 200) ---"
curl -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  POST "https://localhost:8443/sessions/${SESSION_ID}/questions/${QUESTION_ID}/upvote"

# Step 3: Upvote again (should return 200)
echo ""
echo "--- Test 2: Upvote same question again (should return 200) ---"
curl -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  POST "https://localhost:8443/sessions/${SESSION_ID}/questions/${QUESTION_ID}/upvote"

# Step 4: Non-existent session (should return 404)
echo ""
echo "--- Test 3: Non-existent session (should return 404) ---"
curl -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  POST "https://localhost:8443/sessions/26685214-58cb-4e5e-bf7a-1712cca2825d/questions/${QUESTION_ID}/upvote"

# Step 5: Non-existent question (should return 404)
echo ""
echo "--- Test 4: Non-existent question (should return 404) ---"
curl -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  POST "https://localhost:8443/sessions/${SESSION_ID}/questions/26685214-58cb-4e5e-bf7a-1712cca2825d/upvote"

# Step 6: Invalid session UUID format (should return 400)
echo ""
echo "--- Test 5: Invalid session UUID format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  POST "https://localhost:8443/sessions/not-a-uuid/questions/${QUESTION_ID}/upvote"

# Step 7: Invalid question UUID format (should return 400)
echo ""
echo "--- Test 6: Invalid question UUID format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" \
  POST "https://localhost:8443/sessions/${SESSION_ID}/questions/not-a-uuid/upvote"

# Step 8: No authentication (should return 401 or 403)
echo ""
echo "--- Test 7: No authentication (should return 401 or 403) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" \
  POST "https://localhost:8443/sessions/${SESSION_ID}/questions/${QUESTION_ID}/upvote"

# Cleanup
rm -f cookies.txt

echo ""
echo "==================="
echo "All tests completed!"
