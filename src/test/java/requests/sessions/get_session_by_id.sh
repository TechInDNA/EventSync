#!/bin/bash

echo "Testing GET /sessions/{id}"
echo "=========================="

BASE_URL="https://localhost:443"

# Known session IDs from seed data
FULL_SESSION="7d1b2c1f-2887-4b33-94f9-c3d204c4ab56"
NO_SPEAKER_NO_QUESTIONS="afb87ca5-1265-44b8-8ae3-dc59bec528a8"
NO_ROOM_WITH_SPEAKER="e1fbfb54-3e09-4a00-bc64-9a6dc2112b73"

# --- Success Tests ---

echo ""
echo "--- Test 1: Session with speakers and questions (should return 200 with speakers+questions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$FULL_SESSION"

echo ""
echo "--- Test 2: Session without speakers or questions (should return 200, speakers=null, questions=null) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$NO_SPEAKER_NO_QUESTIONS"

echo ""
echo "--- Test 3: Session without room but with speaker and question (should return 200, room=null) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$NO_ROOM_WITH_SPEAKER"

# --- Not Found Tests ---

echo ""
echo "--- Test 4: Non-existent UUID (should return 404) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/00000000-0000-0000-0000-000000000000"

echo ""
echo "--- Test 5: Random valid UUID that doesn't exist (should return 404) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/12345678-abcd-ef01-2345-6789abcdef01"

# --- Bad Request Tests ---

echo ""
echo "--- Test 6: Invalid UUID format - plain string (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/not-a-uuid"

echo ""
echo "--- Test 7: Malformed UUID - short string (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/12345"

echo ""
echo "--- Test 8: Malformed UUID - numeric only (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/1234567890"

echo ""
echo "--- Test 9: Malformed UUID - partial UUID (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/a7cc7aac-50db-44cd"

# --- Authentication Tests ---

echo ""
echo "--- Test 10: GET without auth (should return 200 - endpoint is public) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$FULL_SESSION"

echo ""
echo "--- Test 11: GET with auth (should return 200) ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' "$BASE_URL/auth/login" > /dev/null 2>&1
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$FULL_SESSION"

# --- Method Tests ---

echo ""
echo "--- Test 12: POST to /sessions/{id} (should return 401) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$FULL_SESSION"

echo ""
echo "--- Test 13: DELETE to /sessions/{id} without auth (should return 401) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$FULL_SESSION"

echo ""
echo "--- Test 14: PUT to /sessions/{id} without auth (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/sessions/$FULL_SESSION"

# Cleanup
rm -f cookies.txt

echo ""
echo "=========================="
echo "All tests completed!"
