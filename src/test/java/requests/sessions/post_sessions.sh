#!/bin/bash

ROOM_ID="d4e1798f-d3cf-4114-9e74-aa6b091f8ff5"
EVENT_ID="8e354819-2e18-4955-88ce-0ab61e7e8ca3"
INVALID_UUID="00000000-0000-0000-0000-000000000000"

echo "Testing POST /sessions"
echo "======================"

echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 1: Valid session creation (should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Keynote: The Future of AI","description":"Opening keynote on AI advancements","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":200,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 2: Duplicate title (should return 409) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Keynote: The Future of AI","description":"Another session same title","startDate":"2026-06-15T14:00:00Z","endDate":"2026-06-15T15:00:00Z","roomId":"'"$ROOM_ID"'","capacity":100,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 3: Missing title (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"description":"No title","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 4: Null title (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":null,"description":"Null title","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 5: Empty title (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"","description":"Empty title","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 6: Blank title (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"   ","description":"Blank title","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 7: Title with invalid '!' (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Invalid! Title","description":"Exclamation mark","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 8: Title with invalid '@' (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Invalid@Title","description":"At symbol","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 9: Title too long (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"This title is way too long and exceeds the maximum allowed length of fifty characters","description":"Long title","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 10: Title with allowed special chars (should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session 101: Coding Workshop","description":"Hands-on coding session","startDate":"2026-06-15T14:00:00Z","endDate":"2026-06-15T16:00:00Z","roomId":"'"$ROOM_ID"'","capacity":30,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 11: Missing description (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Description Session","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 12: Description with invalid chars (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad Description","description":"Invalid description with <html>","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 13: Missing startDate (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Start Date","description":"Missing startDate","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 14: Invalid startDate format (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad Date","description":"Wrong date format","startDate":"15-06-2026","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 15: Missing endDate (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No End Date","description":"Missing endDate","startDate":"2026-06-15T10:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 16: Invalid endDate format (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad End Date","description":"Wrong endDate format","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15 12:00:00","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 17: Missing roomId (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Room","description":"Missing roomId","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 18: Invalid roomId format (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad Room UUID","description":"Wrong room UUID","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"not-a-uuid","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 19: Non-existent roomId (should return 404) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Ghost Room Session","description":"Room does not exist","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$INVALID_UUID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 20: Missing eventId (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Event","description":"Missing eventId","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50}' http://localhost:8080/sessions

echo ""
echo "--- Test 21: Invalid eventId format (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad Event UUID","description":"Wrong event UUID","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"bad-uuid"}' http://localhost:8080/sessions

echo ""
echo "--- Test 22: Non-existent eventId (should return 404) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Ghost Event Session","description":"Event does not exist","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$INVALID_UUID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 23: No authentication (should return 401 or 403) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Unauthorized Session","description":"No auth","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 24: Capacity = 0 (should return 201) ---"
echo "    Note: capacity is accepted as-is, no lower-bound check in validator"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Zero Capacity Session","description":"Capacity is zero","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":0,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

echo ""
echo "--- Test 25: Title with all allowed punctuation (should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session A.B,C:D-E","description":"All allowed punctuation in title","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomId":"'"$ROOM_ID"'","capacity":50,"eventId":"'"$EVENT_ID"'"}' http://localhost:8080/sessions

rm -f cookies.txt

echo ""
echo "=================="
echo "All tests completed!"
