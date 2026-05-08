#!/bin/bash

echo "Testing POST /rooms"
echo "=================="

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

# Step 2: Valid room creation (should return 201)
echo ""
echo "--- Test 1: Valid room creation (should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Main Hall"}' http://localhost:8080/rooms

# Step 3: Duplicate room name (should return 409)
echo ""
echo "--- Test 2: Duplicate room name (should return 409) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Main Hall"}' http://localhost:8080/rooms

# Step 4: Another valid room creation
echo ""
echo "--- Test 3: Another valid room (should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Conference Room A"}' http://localhost:8080/rooms

# Step 5: Missing name field (should return 400)
echo ""
echo "--- Test 4: Missing name field (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{}' http://localhost:8080/rooms

# Step 6: Null name (should return 400)
echo ""
echo "--- Test 5: Null name (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":null}' http://localhost:8080/rooms

# Step 7: Empty name (should return 400)
echo ""
echo "--- Test 6: Empty name (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":""}' http://localhost:8080/rooms

# Step 8: Blank name (spaces only, should return 400)
echo ""
echo "--- Test 7: Blank name (spaces only, should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"   "}' http://localhost:8080/rooms

# Step 9: Invalid characters in name - exclamation mark
echo ""
echo "--- Test 8: Name with invalid character '!' (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Room!123"}' http://localhost:8080/rooms

# Step 10: Invalid characters in name - at symbol
echo ""
echo "--- Test 9: Name with invalid character '@' (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Room@123"}' http://localhost:8080/rooms

# Step 11: Invalid characters in name - hash symbol
echo ""
echo "--- Test 10: Name with invalid character '#' (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Room#123"}' http://localhost:8080/rooms

# Step 12: Valid characters - with space
echo ""
echo "--- Test 11: Name with space (valid, should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Room 101"}' http://localhost:8080/rooms

# Step 13: Valid characters - with dot
echo ""
echo "--- Test 12: Name with dot (valid, should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Room.102"}' http://localhost:8080/rooms

# Step 14: Valid characters - with comma
echo ""
echo "--- Test 13: Name with comma (valid, should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Hall A,B"}' http://localhost:8080/rooms

# Step 15: Valid characters - with apostrophe
echo ""
echo "--- Test 14: Name with apostrophe (valid, should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Organizer'\''s Room"}' http://localhost:8080/rooms

# Step 16: Valid characters - with colon
echo ""
echo "--- Test 15: Name with colon (valid, should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Room:101"}' http://localhost:8080/rooms

# Step 17: Valid characters - with hyphen
echo ""
echo "--- Test 16: Name with hyphen (valid, should return 201) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Room-101"}' http://localhost:8080/rooms

# Step 18: No authentication (should return 401 or 403)
echo ""
echo "--- Test 17: No authentication (should return 401 or 403) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"Unauthorized Room"}' http://localhost:8080/rooms

# Step 19: Test with very long name
echo ""
echo "--- Test 18: Very long name (should return 400) ---"
curlie -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"name":"VeryLongRoomNameThatMightExceedDatabaseConstraintsButLetsSeeWhatHappensWithThisStringWhichIsQuiteLong"}' http://localhost:8080/rooms

# Cleanup
rm -f cookies.txt

echo ""
echo "=================="
echo "All tests completed!"
