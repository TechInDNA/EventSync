#!/bin/bash

SESSION_ID="a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb"
QID_ALICE="ddf75918-e752-4fb1-b316-cb4574da81de"
QID_BOB_ANON="20919e99-f2ff-429d-a6af-8cba749af27a"
QID_CHARLIE="2110b88d-b648-44fb-a2c0-6a2f5a674eb7"
QID_DIANA="0067a56e-84d8-443a-a686-b5b124b01342"
QID_EVE_ANON="a6631e77-eeeb-4551-aefc-11b17b681e46"
QID_ALICE2="403dea58-0f1b-4c6f-81dd-5d6cb7dcd790"
QID_NONEXISTENT="00000000-0000-0000-0000-000000000000"

echo "Testing GET /sessions/{id}/questions/{qid}/upvote"
echo "================================================="

echo ""
echo "===================================="
echo "   VALID SESSION + QUESTION (200)"
echo "===================================="

# Test 1: Question with 3 upvotes (Alice's Q — Bob, Charlie, Diana)
echo ""
echo "--- Test 1: Question with 3 upvotes (should return 200, count=3) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_ALICE}/upvote"

# Test 2: Question with 2 upvotes (Charlie's Q — Alice, Bob)
echo ""
echo "--- Test 2: Question with 2 upvotes (should return 200, count=2) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_CHARLIE}/upvote"

# Test 3: Question with 1 upvote (anonymous Bob's Q — Charlie)
echo ""
echo "--- Test 3: Anonymous question with 1 upvote (should return 200, count=1) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_BOB_ANON}/upvote"

# Test 4: Question with 1 upvote (Alice's second Q — Diana)
echo ""
echo "--- Test 4: Question with 1 upvote (should return 200, count=1) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_ALICE2}/upvote"

# Test 5: Question with 0 upvotes (Diana's Q — no upvotes)
echo ""
echo "--- Test 5: Question with 0 upvotes (should return 200, count=0) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_DIANA}/upvote"

# Test 6: Anonymous question with 0 upvotes (Eve's Q)
echo ""
echo "--- Test 6: Anonymous question with 0 upvotes (should return 200, count=0) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_EVE_ANON}/upvote"

echo ""
echo "===================================="
echo "      INVALID UUID FORMAT (400)"
echo "===================================="

# Test 7: Invalid session UUID format
echo ""
echo "--- Test 7: Invalid session UUID format (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/not-a-uuid/questions/${QID_ALICE}/upvote"

# Test 8: Invalid question UUID format
echo ""
echo "--- Test 8: Invalid question UUID format (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/not-a-uuid/upvote"

# Test 9: Both UUIDs invalid
echo ""
echo "--- Test 9: Both UUIDs invalid format (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/bad-uuid-1/questions/bad-uuid-2/upvote"

echo ""
echo "===================================="
echo "       NOT FOUND (404)"
echo "===================================="

# Test 10: Non-existent session UUID
echo ""
echo "--- Test 10: Non-existent session UUID (should return 404) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/26685214-58cb-4e5e-bf7a-1712cca2825d/questions/${QID_ALICE}/upvote"

# Test 11: Non-existent question UUID
echo ""
echo "--- Test 11: Non-existent question UUID (should return 404) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${SESSION_ID}/questions/${QID_NONEXISTENT}/upvote"

# Test 12: Question belongs to a different session (session UUID doesn't match question's session)
echo ""
echo "--- Test 12: Mismatched session/question (should return 404) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions/${QID_ALICE}/questions/${QID_ALICE2}/upvote"


echo ""
echo "===================="
echo " All tests completed!"
echo "===================="
