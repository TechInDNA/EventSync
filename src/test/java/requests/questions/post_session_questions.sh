#!/bin/bash

SESSION_ID="a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb"
SESSION_FUTURE_ID="a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb"

echo "Testing POST /sessions/{id}/questions"
echo "====================================="

# Step 1: Authenticate as Eve (participant)
echo ""
echo "--- Authenticating as Eve (participant) ---"
EV_RESP=$(curlie -k -s -X POST -H "Content-Type: application/json" \
  -d '{"firstName":"Eve","lastName":"Davis","email":"eve.participant@eventsync.com"}' \
  https://localhost:443/auth/participant)
EV_TOKEN=$(echo "$EV_RESP" | jq -r '.token')
echo ""

echo ""
echo "===================================="
echo "      VALIDATION FAILURES (400)"
echo "===================================="

# Test 1: Invalid session UUID format
echo ""
echo "--- Test 1: Invalid session UUID format (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Cookie: jwt=$EV_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"Test question"}' \
  "https://localhost:443/sessions/not-a-uuid/questions"

# Test 2: Empty content field
echo ""
echo "--- Test 2: Empty content field (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Cookie: jwt=$EV_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":""}' \
  "https://localhost:443/sessions/${SESSION_ID}/questions"

# Test 3: Missing content field
echo ""
echo "--- Test 3: Missing content field (should return 400) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Cookie: jwt=$EV_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My question"}' \
  "https://localhost:443/sessions/${SESSION_ID}/questions"

echo ""
echo "===================================="
echo "       NOT FOUND (404)"
echo "===================================="

# Test 4: Non-existent session UUID
echo ""
echo "--- Test 4: Non-existent session UUID (should return 404) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Cookie: jwt=$EV_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"Test question"}' \
  "https://localhost:443/sessions/00000000-0000-0000-0000-000000000000/questions"

echo ""
echo "===================================="
echo "   SESSION NOT LIVE (400)"
echo "===================================="

# Test 5: The test session date is in the future, so it's not live yet
echo ""
echo "--- Test 5: Session not live (should return 400) ---"
echo "NOTE: test session starts 2026-06-10, current date is $(date +%Y-%m-%d)"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Cookie: jwt=$EV_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content":"Will the session cover microservices?"}' \
  "https://localhost:443/sessions/${SESSION_ID}/questions"

echo ""
echo "===================================="
echo "   AUTHENTICATION FAILURES (401)"
echo "===================================="

# Test 6: No JWT cookie
echo ""
echo "--- Test 6: No authentication (should return 401) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Content-Type: application/json" \
  -d '{"content":"Test question"}' \
  "https://localhost:443/sessions/${SESSION_ID}/questions"

# Test 7: Invalid JWT token
echo ""
echo "--- Test 7: Invalid JWT token (should return 401) ---"
curlie -k -w "\nHTTP Status: %{http_code}\n" -X POST \
  -H "Cookie: jwt=invalid.jwt.token" \
  -H "Content-Type: application/json" \
  -d '{"content":"Test question"}' \
  "https://localhost:443/sessions/${SESSION_ID}/questions"

echo ""
echo "===================================="
echo "   VALID REQUEST (ANONYMOUS)"
echo "===================================="

# Test 8: Valid anonymous request — only achievable when a session is live
echo ""
echo "--- Test 8: Anonymous question (requires live session, skip if 400) ---"
ANON_RESP=$(curlie -k -s -w "\nHTTP_STATUS:%{http_code}" -X POST \
  -H "Cookie: jwt=$EV_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Anonymous query","content":"Is there a recording?","isAnonymous":true}' \
  "https://localhost:443/sessions/${SESSION_ID}/questions")
ANON_HTTP=$(echo "$ANON_RESP" | grep "HTTP_STATUS:" | cut -d: -f2)
echo "HTTP Status: $ANON_HTTP"
if [ "$ANON_HTTP" = "201" ]; then
  echo "SUCCESS: Anonymous question created."
  echo "Response body:"
  echo "$ANON_RESP" | sed '/^HTTP_STATUS:/d'
fi

echo ""
echo "===================================="
echo "   VALID REQUEST (NAMED)"
echo "===================================="

# Test 9: Valid named question — only achievable when a session is live
echo ""
echo "--- Test 9: Named question (requires live session, skip if 400) ---"
NAMED_RESP=$(curlie -k -s -w "\nHTTP_STATUS:%{http_code}" -X POST \
  -H "Cookie: jwt=$EV_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Framework question","content":"Which framework do you recommend for this?"}' \
  "https://localhost:443/sessions/${SESSION_ID}/questions")
NAMED_HTTP=$(echo "$NAMED_RESP" | grep "HTTP_STATUS:" | cut -d: -f2)
echo "HTTP Status: $NAMED_HTTP"
if [ "$NAMED_HTTP" = "201" ]; then
  echo "SUCCESS: Named question created."
  echo "Response body:"
  echo "$NAMED_RESP" | sed '/^HTTP_STATUS:/d'
fi

echo ""
echo "===================================="
echo "       ALL TESTS COMPLETED!"
echo "===================================="
