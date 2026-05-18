#!/bin/bash

echo "Testing GET /events/{id}"
echo "========================"

BASE_URL="https://localhost:443"

# Known event IDs from seed data
EVENT_WITH_SESSIONS="e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b"
EVENT_TO_UPDATE="c1b957ac-bdd7-481a-b8f7-636d43794f82"
EVENT_TO_DELETE="a7cc7aac-50db-44cd-b8ae-cac7737a4052"

# --- Success Tests ---

echo ""
echo "--- Test 1: Valid event without sessions (should return 200) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_TO_DELETE"

echo ""
echo "--- Test 2: Valid event with sessions (should return 200 with sessions array) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_WITH_SESSIONS"

echo ""
echo "--- Test 3: Another valid event (should return 200, sessions=null) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_TO_UPDATE"

# --- Not Found Tests ---

echo ""
echo "--- Test 4: Non-existent UUID (should return 404) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/00000000-0000-0000-0000-000000000000"

echo ""
echo "--- Test 5: Random valid UUID that doesn't exist (should return 404) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/12345678-abcd-ef01-2345-6789abcdef01"

# --- Bad Request Tests ---

echo ""
echo "--- Test 6: Invalid UUID format - plain string (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/not-a-uuid"

echo ""
echo "--- Test 7: Malformed UUID - short string (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/12345"

echo ""
echo "--- Test 8: Malformed UUID - numeric only (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/1234567890"

echo ""
echo "--- Test 9: Malformed UUID - partial UUID (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/a7cc7aac-50db-44cd"

echo ""
echo "--- Test 10: Empty path segment (should return 404 from Spring routing) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/"

# --- Authentication Tests ---

echo ""
echo "--- Test 11: GET without auth (should return 200 - endpoint is public) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_TO_DELETE"

echo ""
echo "--- Test 12: GET with auth (should return 200) ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' "$BASE_URL/auth/login" > /dev/null 2>&1
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_TO_DELETE"

# --- Method Tests ---

echo ""
echo "--- Test 13: POST to /events/{id} (should return 405 Method Not Allowed) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_TO_DELETE"

echo ""
echo "--- Test 14: DELETE to /events/{id} without auth (should return 401) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_TO_DELETE"

echo ""
echo "--- Test 15: PUT to /events/{id} without auth (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events/$EVENT_TO_DELETE"

# Cleanup
rm -f cookies.txt

echo ""
echo "========================"
echo "All tests completed!"
