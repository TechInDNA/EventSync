#!/bin/bash

echo "Testing POST /sessions"
echo "======================"

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Valid session creation (should return 201)
echo ""
echo "--- Test 1: Valid session creation (should return 201) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session POST","description":"Session created via POST test","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":100,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 3: Duplicate session title (should return 409)
echo ""
echo "--- Test 2: Duplicate session title (should return 409) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session POST","description":"Another session with same title","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","capacity":50,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 4: Missing title (should return 400)
echo ""
echo "--- Test 3: Missing title (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"description":"No title","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 5: Empty title (should return 400)
echo ""
echo "--- Test 4: Empty title (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"","description":"Empty title","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 6: Blank title (spaces only, should return 400)
echo ""
echo "--- Test 5: Blank title (spaces only, should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"   ","description":"Blank title","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 7: Missing description (should return 400)
echo ""
echo "--- Test 6: Missing description (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Desc Session","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 8: Empty description (should return 400)
echo ""
echo "--- Test 7: Empty description (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Empty Desc Session","description":"","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 9: Missing startDate (should return 400)
echo ""
echo "--- Test 8: Missing startDate (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Start","description":"Session without startDate","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 10: Missing endDate (should return 400)
echo ""
echo "--- Test 9: Missing endDate (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No End","description":"Session without endDate","startDate":"2026-05-14T10:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 11: Invalid startDate format (should return 400)
echo ""
echo "--- Test 10: Invalid startDate format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad Date","description":"Invalid date","startDate":"not-a-date","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 12: Invalid endDate format (should return 400)
echo ""
echo "--- Test 11: Invalid endDate format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad End Date","description":"Invalid endDate","startDate":"2026-05-14T10:00:00Z","endDate":"14-05-2026","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 13: Title with invalid character '!' (should return 400)
echo ""
echo "--- Test 12: Title with invalid character '!' (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session!","description":"Invalid char in title","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 14: Title with invalid character '@' (should return 400)
echo ""
echo "--- Test 13: Title with invalid character '@' (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session@","description":"Invalid char in title","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 15: Title with invalid character '#' (should return 400)
echo ""
echo "--- Test 14: Title with invalid character '#' (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session#","description":"Invalid char in title","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 16: Title exceeding 50 characters (should return 400)
echo ""
echo "--- Test 15: Title exceeding 50 characters (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"This is a very long session title that definitely exceeds fifty characters","description":"Title too long","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 17: Valid title with valid special characters (should return 201)
echo ""
echo "--- Test 16: Valid title with period, colon, apostrophe, hyphen (should return 201) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Session: POST - Test","description":"Valid special chars in title","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","capacity":75,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 18: Non-existent room (should return 404)
echo ""
echo "--- Test 17: Non-existent room (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Unknown Room Session","description":"Trying non-existent room","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"NonExistentRoomXYZ","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 19: Non-existent event (should return 404)
echo ""
echo "--- Test 18: Non-existent event (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Unknown Event Session","description":"Trying non-existent event","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"NonExistentEventXYZ"}' https://localhost:443/sessions

# Step 20: No authentication (should return 401 or 403)
echo ""
echo "--- Test 19: No authentication (should return 401 or 403) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Unauthorized Session","description":"No auth token","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 21: Description with invalid character '!' (should return 400)
echo ""
echo "--- Test 20: Description with invalid character '!' (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Bad Desc","description":"Invalid description!","startDate":"2026-05-14T10:00:00Z","endDate":"2026-05-14T12:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 22: Missing capacity (defaults to 0, should return 400)
echo ""
echo "--- Test 21: Missing capacity (defaults to 0, should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Capacity","description":"Session without capacity","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 23: Capacity zero (should return 400)
echo ""
echo "--- Test 22: Capacity set to 0 (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Zero Capacity","description":"Session with capacity 0","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","capacity":0,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 24: Capacity negative (should return 400)
echo ""
echo "--- Test 23: Negative capacity (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Negative Capacity","description":"Session with negative capacity","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","capacity":-5,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 25: Missing roomName (should return 400)
echo ""
echo "--- Test 24: Missing roomName (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Room Name","description":"Session without roomName","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","eventTitle":"Session POST Event","capacity":10}' https://localhost:443/sessions

# Step 26: Missing eventTitle (should return 400)
echo ""
echo "--- Test 25: Missing eventTitle (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"No Event Title","description":"Session without eventTitle","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","capacity":10}' https://localhost:443/sessions

# Step 27: Empty roomName (should return 400)
echo ""
echo "--- Test 26: Empty roomName (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Empty Room","description":"Session with empty roomName","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"","capacity":10,"eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Step 28: Empty eventTitle (should return 400)
echo ""
echo "--- Test 27: Empty eventTitle (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"Empty Event","description":"Session with empty eventTitle","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","capacity":10,"eventTitle":""}' https://localhost:443/sessions

# Step 29: Capacity as a string (should return 400)
echo ""
echo "--- Test 28: Capacity as string \"abc\" (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"title":"String Capacity","description":"Session with string capacity","startDate":"2026-05-14T14:00:00Z","endDate":"2026-05-14T16:00:00Z","roomName":"Session POST Room","capacity":"abc","eventTitle":"Session POST Event"}' https://localhost:443/sessions

# Cleanup
rm -f cookies.txt

echo ""
echo "======================"
echo "All tests completed!"
