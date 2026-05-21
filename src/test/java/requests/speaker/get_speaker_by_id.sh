#!/bin/bash

echo "Testing GET /speakers/{id}"
echo "========================"

BASE_URL="https://localhost:443"

# Known speaker IDs from seed data
JOHN_DOE="af1bf5f5-96cd-4ad3-b06c-faa3bfdfe56e"           # 2 links + 2 sessions
ALICE_WILLIAMS="b1c2d3e4-f5a6-4b7c-8d9e-0f1a2b3c4d5e"    # 2 links + 1 session
ALICE_EXTERNAL="45047c18-1984-4d1e-bcbc-a7407c227292"      # 0 links + 0 sessions
BOB_EXTERNAL="be8bfef5-d76f-4b54-a7aa-8209b17b5a54"       # 1 link + 0 sessions
SPEAKER_TO_UPDATE="27dfb67f-b1c5-4f71-b0b2-2190674eefa8"  # 2 links + 0 sessions

# --- Success Tests ---

echo ""
echo "--- Test 1: Speaker with external links and sessions (should return 200) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$JOHN_DOE"

echo ""
echo "--- Test 2: Speaker with external links and one session (should return 200) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$ALICE_WILLIAMS"

echo ""
echo "--- Test 3: Speaker with external links but no sessions (should return 200, sessions=null) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$SPEAKER_TO_UPDATE"

echo ""
echo "--- Test 4: Speaker with one external link but no sessions (should return 200) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$BOB_EXTERNAL"

echo ""
echo "--- Test 5: Speaker with no external links and no sessions (should return 200, sessions=null, externalLinks=null) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$ALICE_EXTERNAL"

# --- Not Found Tests ---

echo ""
echo "--- Test 6: Non-existent UUID (should return 404) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/00000000-0000-0000-0000-000000000000"

echo ""
echo "--- Test 7: Random valid UUID that doesn't exist (should return 404) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/12345678-abcd-ef01-2345-6789abcdef01"

# --- Bad Request Tests ---

echo ""
echo "--- Test 8: Invalid UUID format - plain string (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/not-a-uuid"

echo ""
echo "--- Test 9: Malformed UUID - short string (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/12345"

echo ""
echo "--- Test 10: Malformed UUID - numeric only (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/1234567890"

echo ""
echo "--- Test 11: Malformed UUID - partial UUID (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/af1bf5f5-96cd-4ad3"

echo ""
echo "--- Test 12: UUID with invalid characters (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/zzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz"

# --- Authentication & Authorization Tests ---

echo ""
echo "--- Test 13: GET without auth (should return 200 - endpoint is public) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$JOHN_DOE"

echo ""
echo "--- Test 14: GET with admin auth (should return 200) ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' "$BASE_URL/auth/login" > /dev/null 2>&1
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$JOHN_DOE"

# --- Method Tests ---

echo ""
echo "--- Test 15: POST to /speakers/{id} (should return 401) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$JOHN_DOE"

echo ""
echo "--- Test 16: DELETE to /speakers/{id} without auth (should return 401) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$JOHN_DOE"

echo ""
echo "--- Test 17: PUT to /speakers/{id} without auth (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/speakers/$SPEAKER_TO_UPDATE"

# Cleanup
rm -f cookies.txt

echo ""
echo "========================"
echo "All tests completed!"
