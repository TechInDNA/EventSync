#!/bin/bash

echo "Testing PUT /rooms/{id}"

ROOM_ID="c1c12204-4427-4add-b755-b681719d1684"
INVALID_UUID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"

# Step 1: Authenticate to get JWT cookie (admin role required)
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (no cookie, should return 401)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{"name":"Updated Room"}' https://localhost:443/rooms/$ROOM_ID

# Step 3: Valid update (should return 200)
echo ""
echo "--- Test 2: Valid update (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Updated Room Name"}' https://localhost:443/rooms/$ROOM_ID

# Step 4: Duplicate name (another room already has this name, should return 409)
echo ""
echo "--- Test 3: Duplicate name (should return 409) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Grand Ballroom"}' https://localhost:443/rooms/$ROOM_ID

# Step 5: Room not found (should return 404)
echo ""
echo "--- Test 4: Room not found with non-existent UUID (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"NonExistent Room"}' https://localhost:443/rooms/$INVALID_UUID

# Step 6: Malformed UUID (should return 400)
echo ""
echo "--- Test 5: Malformed UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Malformed Uuid Room"}' https://localhost:443/rooms/$MALFORMED_UUID

# Step 7: Missing name (should return 400)
echo ""
echo "--- Test 6: Missing name (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{}' https://localhost:443/rooms/$ROOM_ID

# Step 8: Blank name (spaces only, should return 400)
echo ""
echo "--- Test 7: Blank name (spaces only, should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"   "}' https://localhost:443/rooms/$ROOM_ID

# Step 9: Empty name (should return 400)
echo ""
echo "--- Test 8: Empty name (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":""}' https://localhost:443/rooms/$ROOM_ID

# Step 10: Name with invalid character '!' (should return 400)
echo ""
echo "--- Test 9: Name with invalid character '!' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Invalid!Room"}' https://localhost:443/rooms/$ROOM_ID

# Step 11: Name with invalid character '@' (should return 400)
echo ""
echo "--- Test 10: Name with invalid character '@' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Invalid@Room"}' https://localhost:443/rooms/$ROOM_ID

# Step 12: Name with valid space character (should return 200)
echo ""
echo "--- Test 11: Name with valid space (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Valid Updated Room Name"}' https://localhost:443/rooms/$ROOM_ID

# Step 13: Name with valid special characters .,':- (should return 200)
echo ""
echo "--- Test 12: Name with valid special characters (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Valid.Room,Test:Name-Updated"}' https://localhost:443/rooms/$ROOM_ID

# Cleanup
rm -f cookies.txt
