#!/bin/bash

SESSION_ID="a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb"
QID_DIANA="0067a56e-84d8-443a-a686-b5b124b01342"
QID_ALICE="ddf75918-e752-4fb1-b316-cb4574da81de"
QID_BOB_ANON="20919e99-f2ff-429d-a6af-8cba749af27a"
QID_NONEXISTENT="00000000-0000-0000-0000-000000000000"

echo "Testing POST /sessions/{id}/questions/{qid}/upvote"
echo "=================================================="

# Step 1: Authenticate as Eve (no existing upvotes — clean slate)
echo ""
echo "--- Authenticating as Eve (participant) ---"
EV_RESP=$(curlie -k -s -X POST -H "Content-Type: application/json" -d '{"firstName":"Eve","lastName":"Davis","email":"eve.participant@eventsync.com"}' https://localhost:443/auth/participant)
EV_TOKEN=$(echo "$EV_RESP" | jq -r '.token')
echo ""

# Step 2: Authenticate as Diana (has upvotes on ddf75918 and 403dea58)
echo ""
echo "--- Authenticating as Diana (participant) ---"
DI_RESP=$(curlie -k -s -X POST -H "Content-Type: application/json" -d '{"firstName":"Diana","lastName":"Miller","email":"diana.participant@eventsync.com"}' https://localhost:443/auth/participant)
DI_TOKEN=$(echo "$DI_RESP" | jq -r '.token')
echo ""

echo ""
echo "===================================="
echo "         VALID UP / TOGGLE"
echo "===================================="

# Test 1: Valid upvote — Eve upvotes Diana's question (0 upvotes -> 1)
echo ""
echo "--- Test 1: Valid upvote (should return 200, count=1) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$EV_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_DIANA}/upvote"

# Test 2: Toggle off — same user upvotes same question again (1 -> 0)
echo ""
echo "--- Test 2: Toggle off / un-upvote (should return 200, count=0) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$EV_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_DIANA}/upvote"

# Test 3: Toggle on again (0 -> 1)
echo ""
echo "--- Test 3: Toggle back on / re-upvote (should return 200, count=1) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$EV_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_DIANA}/upvote"

# Test 4: Another user upvotes a different question — Diana upvotes Bob's anonymous Q (1 + 1 = 2)
echo ""
echo "--- Test 4: Diana upvotes Bob's anonymous question (should return 200, count=2) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$DI_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_BOB_ANON}/upvote"

# Test 5: User re-upvotes their own existing upvote — Diana already upvoted Alice's Q, toggling off
echo ""
echo "--- Test 5: Diana toggles off her existing upvote on Alice's question (should return 200, count=2) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$DI_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_ALICE}/upvote"

echo ""
echo "===================================="
echo "        INVALID UUID (400)"
echo "===================================="

# Test 6: Invalid session UUID format
echo ""
echo "--- Test 6: Invalid session UUID format (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$EV_TOKEN" "https://localhost:443/sessions/not-a-uuid/questions/${QID_DIANA}/upvote"

# Test 7: Invalid question UUID format
echo ""
echo "--- Test 7: Invalid question UUID format (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$EV_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/not-a-uuid/upvote"

echo ""
echo "===================================="
echo "        NOT FOUND (404)"
echo "===================================="

# Test 8: Non-existent question UUID (valid format but no such question)
echo ""
echo "--- Test 8: Non-existent question (should return 404) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$EV_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_NONEXISTENT}/upvote"

# Test 9: Question belongs to a different (non-existent) session
echo ""
echo "--- Test 9: Non-existent session UUID (should return 404) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=$EV_TOKEN" "https://localhost:443/sessions/${QID_DIANA}/questions/${QID_ALICE}/upvote"

echo ""
echo "===================================="
echo "     AUTHENTICATION FAILURES (401)"
echo "===================================="

# Test 10: No JWT cookie at all
echo ""
echo "--- Test 10: No authentication (should return 401) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_DIANA}/upvote"

# Test 11: Invalid / malformed JWT token
echo ""
echo "--- Test 11: Invalid JWT token (should return 401) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST -H "Cookie: jwt=invalid.jwt.token" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_DIANA}/upvote"

echo ""
echo "===================================="
echo "           CLEANUP"
echo "===================================="

# Restore state: toggle Diana's upvote back on Alice's question (2 -> 3)
echo ""
echo "--- Cleanup: restore Diana's upvote on Alice's question ---"
curlie -k -s -X POST -H "Cookie: jwt=$DI_TOKEN" "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_ALICE}/upvote" > /dev/null 2>&1

echo ""
echo "===================================="
echo "       ALL TESTS COMPLETED!"
echo "===================================="
