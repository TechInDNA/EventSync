#!/bin/bash

echo "Testing GET /sessions"
echo "====================="

# Test 1: Get all sessions (default pagination)
echo ""
echo "--- Test 1: Get all sessions (default pagination) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET http://localhost:8080/sessions

# Test 2: Get sessions with custom page and size
echo ""
echo "--- Test 2: Get sessions with page=2&size=5 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=2&size=5"

# Test 3: Get sessions with page=1&size=1 (first session only)
echo ""
echo "--- Test 3: Get sessions with page=1&size=1 (first session only) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=1&size=1"

# Test 4: Invalid page (negative)
echo ""
echo "--- Test 4: Invalid page (negative) - should return 400 Bad request error ---"
    curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=-1&size=10"

# Test 5: Invalid size (zero)
echo ""
echo "--- Test 5: Invalid size (zero) - should return 400 Bad request error ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=1&size=0"

# Test 6: Invalid size (negative)
echo ""
echo "--- Test 6: Invalid size (negative) - should return 400 Bad request error ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=1&size=-5"

# Test 7: Page beyond total (should return empty data array)
echo ""
echo "--- Test 7: Page beyond total (should return empty data array) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=9999&size=10"

# Test 8: Very large size (should return all sessions)
echo ""
echo "--- Test 8: Very large size (should return all sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=1&size=9999"

# Test 9: Non-numeric page parameter
echo ""
echo "--- Test 9: Non-numeric page parameter - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=abc&size=10"

# Test 10: Non-numeric size parameter
echo ""
echo "--- Test 10: Non-numeric size parameter - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=1&size=abc"

# Test 11: Unknown parameters (should be ignored)
echo ""
echo "--- Test 11: Unknown parameters (should be ignored) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?page=1&size=10&unknown=param"

# Test 12: Filter by event title (Summer Tech Fest → 3 sessions: Keynote, Web Dev, AI in Practice)
echo ""
echo "--- Test 12: Filter by eventTitle=Summer (should match 'Summer Tech Fest' → 3 sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=Summer"

# Test 13: Filter by event title (Fall Dev Conference → 3 sessions: Cloud, Testing, PostgreSQL)
echo ""
echo "--- Test 13: Filter by eventTitle=Fall (should match 'Fall Developer Conference' → 3 sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=Fall"

# Test 14: Filter by speaker name (John → sessions with John Doe: Keynote, Cloud Architecture)
echo ""
echo "--- Test 14: Filter by speakerName=John (should match John Doe → 2 sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?speakerName=John"

# Test 15: Filter by room name (Main Hall → 3 sessions: Keynote, Cloud, Advanced PostgreSQL)
echo ""
echo "--- Test 15: Filter by roomName=Main (should match 'Main Hall' → 3 sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?roomName=Main"

# Test 16: Filter by room name (Workshop Room B → 2 sessions: AI in Practice, Testing Strategies)
echo ""
echo "--- Test 16: Filter by roomName=Workshop Room B (should match → 2 sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?roomName=Workshop%20Room%20B"

# Test 17: Filter by isLive=true (Web Dev Basics 09:00-17:00, Testing Strategies 10:00-15:00 today)
echo ""
echo "--- Test 17: Filter isLive=true (should return sessions happening now) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?isLive=true"

# Test 18: Combined filters (Summer Tech Fest + Workshop Room A → Web Dev Basics only)
echo ""
echo "--- Test 18: Combined filters (eventTitle=Summer&roomName=Workshop Room A → Web Dev Basics) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=Summer&roomName=Workshop%20Room%20A"

# Test 19: Combined filters with pagination
echo ""
echo "--- Test 19: Filter by event=Fall + page=1&size=2 (2 of 3 sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=Fall&page=1&size=2"

# Test 20: Filter by speakerName=Jane (Jane Smith → 1 session: Web Dev Basics)
echo ""
echo "--- Test 20: Filter by speakerName=Jane (should match Jane Smith → 1 session) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?speakerName=Jane"

# Test 21: Filter by speakerName=Alice (Alice Johnson → 1 session: AI in Practice)
echo ""
echo "--- Test 21: Filter by speakerName=Alice (should match Alice Williams → AI in Practice) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?speakerName=Alice"

# Test 22: Filter by speakerName=NonExistent (should return empty list)
echo ""
echo "--- Test 22: Filter by non-existent speakerName (should return empty list) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?speakerName=NonExistent"

# Test 23: Filter by non-existent eventTitle (should return empty list)
echo ""
echo "--- Test 23: Filter by non-existent eventTitle (should return empty list) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=NonExistent"

# Test 24: Filter by non-existent roomName (should return empty list)
echo ""
echo "--- Test 24: Filter by non-existent roomName (should return empty list) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?roomName=NonExistent"

# Test 25: Explicit isLive=false (should return all sessions, no filter)
echo ""
echo "--- Test 25: isLive=false (should return all sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?isLive=false"

# Test 26: Combined eventTitle=Summer + speakerName=Jane (→ Web Dev Basics)
echo ""
echo "--- Test 26: Combined eventTitle=Summer&speakerName=Jane (→ Web Dev Basics) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=Summer&speakerName=Jane"

# Test 27: Combined eventTitle=Fall + speakerName=Charlie (→ Testing Strategies)
echo ""
echo "--- Test 27: Combined eventTitle=Fall&speakerName=Charlie (→ Testing Strategies) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=Fall&speakerName=Charlie"

# Test 28: Combined roomName=Main%20Hall + speakerName=John (→ Keynote, Cloud Architecture)
echo ""
echo "--- Test 28: Combined roomName=Main Hall&speakerName=John (→ Keynote, Cloud Architecture) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?roomName=Main%20Hall&speakerName=John"

# Test 29: Combined eventTitle=Summer + isLive=true (→ live sessions in Summer Tech Fest)
echo ""
echo "--- Test 29: Combined eventTitle=Summer&isLive=true (→ live sessions in Summer Tech Fest) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?eventTitle=Summer&isLive=true"

# Test 30: Combined speakerName=Jane + roomName=Workshop%20Room%20A (→ Web Dev Basics)
echo ""
echo "--- Test 30: Combined speakerName=Jane&roomName=Workshop Room A (→ Web Dev Basics) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions?speakerName=Jane&roomName=Workshop%20Room%20A"

# Test 31: POST method (should fail - 403 or 405)
echo ""
echo "--- Test 31: POST method (should return 403 or 405) ---"
curlie -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/sessions

# Test 32: PUT method (should fail - 403 or 405)
echo ""
echo "--- Test 32: PUT method (should return 403 or 405) ---"
curlie -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/sessions

# Test 33: DELETE method (should fail - 403 or 405)
echo ""
echo "--- Test 33: DELETE method (should return 403 or 405) ---"
curlie -X DELETE -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/sessions

echo ""
echo "===================="
echo "All tests completed!"
