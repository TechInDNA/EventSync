#!/bin/bash

echo "Testing GET /sessions"
echo "====================="

# Test 1: Get all sessions (default pagination: page=1, size=5)
echo ""
echo "--- Test 1: Get all sessions (default pagination) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET https://localhost:443/sessions

# Test 2: Get sessions with custom page and size
echo ""
echo "--- Test 2: Get sessions with page=2&size=5 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=2&size=5"

# Test 3: Get sessions with page=1&size=1 (first session only)
echo ""
echo "--- Test 3: Get sessions with page=1&size=1 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=1&size=1"

# Test 4: Invalid page (negative)
echo ""
echo "--- Test 4: Invalid page (negative) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=-1&size=10"

# Test 5: Invalid size (zero)
echo ""
echo "--- Test 5: Invalid size (zero) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=1&size=0"

# Test 6: Invalid size (negative)
echo ""
echo "--- Test 6: Invalid size (negative) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=1&size=-5"

# Test 7: Page beyond total (should return empty data array)
echo ""
echo "--- Test 7: Page beyond total (should return empty data array) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=9999&size=10"

# Test 8: Very large size (should return all 8 sessions from session_data.sql)
echo ""
echo "--- Test 8: Very large size (should return all 8 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=1&size=9999"

# Test 9: Non-numeric page parameter
echo ""
echo "--- Test 9: Non-numeric page parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=abc&size=10"

# Test 10: Non-numeric size parameter
echo ""
echo "--- Test 10: Non-numeric size parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=1&size=abc"

# Test 11: Unknown parameters (should be ignored)
echo ""
echo "--- Test 11: Unknown parameters (should be ignored) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?page=1&size=10&unknown=param"

# Test 12: Filter by eventTitle (GET Sessions Event Alpha → 5 sessions)
echo ""
echo "--- Test 12: Filter by eventTitle=GET Sessions Event Alpha (should return 5 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=GET%20Sessions%20Event%20Alpha"

# Test 13: Filter by eventTitle (GET Sessions Event Beta → 3 sessions)
echo ""
echo "--- Test 13: Filter by eventTitle=GET Sessions Event Beta (should return 3 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=GET%20Sessions%20Event%20Beta"

# Test 14: Combined eventTitle + roomName with no overlap (Event Alpha + Room Beta → 2 sessions: Valid 3, Valid 4)
echo ""
echo "--- Test 14: Combined eventTitle=GET Sessions Event Alpha&roomName=GET Sessions Room Beta (→ 2 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=GET%20Sessions%20Event%20Alpha&roomName=GET%20Sessions%20Room%20Beta"

# Test 15: Filter by roomName (GET Sessions Room Alpha → 4 sessions)
echo ""
echo "--- Test 15: Filter by roomName=GET Sessions Room Alpha (should return 4 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?roomName=GET%20Sessions%20Room%20Alpha"

# Test 16: Filter by roomName (GET Sessions Room Beta → 3 sessions)
echo ""
echo "--- Test 16: Filter by roomName=GET Sessions Room Beta (should return 3 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?roomName=GET%20Sessions%20Room%20Beta"

# Test 17: Filter by speakerName (John → 2 sessions: No Room + Multiple Speakers)
echo ""
echo "--- Test 17: Filter by speakerName=John (should return 2 sessions with John Doe) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?speakerName=John"

# Test 18: Filter by speakerName (Jane → 1 session: Multiple Speakers)
echo ""
echo "--- Test 18: Filter by speakerName=Jane (should return 1 session with Jane Smith) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?speakerName=Jane"

# Test 19: Filter by isLive=true (should return 2 sessions: Valid 1 + Multiple Speakers)
echo ""
echo "--- Test 19: Filter isLive=true (should return 2 live sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?isLive=true"

# Test 20: Combined filters (eventTitle + roomName → GET Sessions Event Alpha + Room Alpha = 2 sessions)
echo ""
echo "--- Test 20: Combined filters (eventTitle=GET Sessions Event Alpha&roomName=GET Sessions Room Alpha → 2 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=GET%20Sessions%20Event%20Alpha&roomName=GET%20Sessions%20Room%20Alpha"

# Test 21: Combined filters with pagination (GET Sessions Event Alpha, page 1 of 5, size 5)
echo ""
echo "--- Test 21: Filter by eventTitle=GET Sessions Event Alpha&page=1&size=5 (page 1 of 5, size 5) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=GET%20Sessions%20Event%20Alpha&page=1&size=5"

# Test 22: Combined eventTitle + speakerName (Event Alpha + John → 1 session: No Room)
echo ""
echo "--- Test 22: Combined eventTitle=GET Sessions Event Alpha&speakerName=John (→ 1 session) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=GET%20Sessions%20Event%20Alpha&speakerName=John"

# Test 23: Combined roomName + speakerName (Room Alpha + John → 0 sessions, no overlap)
echo ""
echo "--- Test 23: Combined roomName=GET Sessions Room Alpha&speakerName=John (→ 0 sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?roomName=GET%20Sessions%20Room%20Alpha&speakerName=John"

# Test 24: Combined eventTitle + isLive (Event Alpha → 1 live session: Valid 1)
echo ""
echo "--- Test 24: Combined eventTitle=GET Sessions Event Alpha&isLive=true (→ 1 session) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=GET%20Sessions%20Event%20Alpha&isLive=true"

# Test 25: Filter by non-existent speakerName
echo ""
echo "--- Test 25: Filter by non-existent speakerName (should return empty list) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?speakerName=NonExistent"

# Test 26: Filter by non-existent eventTitle
echo ""
echo "--- Test 26: Filter by non-existent eventTitle (should return empty list) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?eventTitle=NonExistent"

# Test 27: Filter by non-existent roomName
echo ""
echo "--- Test 27: Filter by non-existent roomName (should return empty list) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?roomName=NonExistent"

# Test 28: isLive=false (should return all sessions, no filter applied)
echo ""
echo "--- Test 28: isLive=false (should return all sessions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" GET "https://localhost:443/sessions?isLive=false"

# Test 29: POST method (should fail - 401)
echo ""
echo "--- Test 29: POST method (should return 403 or 405) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/sessions

# Test 30: PUT method (should fail - 401)
echo ""
echo "--- Test 30: PUT method (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/sessions

# Test 31: DELETE method (should fail - 401)
echo ""
echo "--- Test 31: DELETE method (should return 403 or 405) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/sessions

echo ""
echo "===================="
echo "All tests completed!"
