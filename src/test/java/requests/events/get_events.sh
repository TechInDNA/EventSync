#!/bin/bash

echo "Testing GET /events"
echo "===================="

BASE_URL="https://localhost:443"

# --- Pagination Tests ---

echo ""
echo "--- Test 1: Default pagination (page=1, size=10) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events"

echo ""
echo "--- Test 2: Custom page and size (page=2, size=5) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=2&size=5"

echo ""
echo "--- Test 3: Single result (page=1, size=1) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=1&size=1"

echo ""
echo "--- Test 4: Very large size (should return all 15 events) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=1&size=9999"

echo ""
echo "--- Test 5: Page beyond total (should return empty data array) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=9999&size=10"

# --- Title Filter Tests ---

echo ""
echo "--- Test 6: Filter by exact title 'AI Summit' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?title=AI%20Summit"

echo ""
echo "--- Test 7: Filter by partial title 'Conference' (matches Tech Conference 2026, Mobile App Conference, Blockchain Expo, IoT Conference) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?title=Conference"

echo ""
echo "--- Test 8: Filter by partial title 'Python' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?title=Python"

echo ""
echo "--- Test 9: Filter by title with no match 'NonExistent' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?title=NonExistent"

# --- Location Filter Tests ---

echo ""
echo "--- Test 10: Filter by exact location 'Tech Hub' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?location=Tech%20Hub"

echo ""
echo "--- Test 11: Filter by partial location 'Center' (matches Convention Center, Expo Center, Security Center, Crypto Center) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?location=Center"

echo ""
echo "--- Test 12: Filter by partial location 'Hall' (matches DevOps Hall, Smart City Hall, Session Test Hall) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?location=Hall"

echo ""
echo "--- Test 13: Filter by location with no match 'Mars' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?location=Mars"

# --- Combined Title + Location Filter Tests ---

echo ""
echo "--- Test 14: Combined filter title='Conference' + location='Center' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?title=Conference&location=Center"

echo ""
echo "--- Test 15: Combined filter title='Summit' + location='Tech Hub' (should match AI Summit) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?title=Summit&location=Tech%20Hub"

echo ""
echo "--- Test 16: Combined filter with no match title='Python' + location='Center' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?title=Python&location=Center"

# --- Invalid Parameter Tests ---

echo ""
echo "--- Test 17: Invalid page (negative) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=-1&size=10"

echo ""
echo "--- Test 18: Invalid size (zero) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=1&size=0"

echo ""
echo "--- Test 19: Invalid size (negative) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=1&size=-5"

echo ""
echo "--- Test 20: Non-numeric page parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=abc&size=10"

echo ""
echo "--- Test 21: Non-numeric size parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=1&size=abc"

echo ""
echo "--- Test 22: Both page and size non-numeric - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=foo&size=bar"

# --- Edge Cases ---

echo ""
echo "--- Test 23: Unknown parameters (should be ignored) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events?page=1&size=10&unknown=param"

echo ""
echo "--- Test 24: POST method (should return 403) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events"

echo ""
echo "--- Test 25: PUT method (should return 403) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events"

echo ""
echo "--- Test 26: DELETE method (should return 403) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" "$BASE_URL/events"

echo ""
echo "===================="
echo "All tests completed!"
