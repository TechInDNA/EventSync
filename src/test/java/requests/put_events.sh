#!/bin/bash

echo "Testing PUT /events/{id}"

EVENT_ID="97e52967-0e33-486e-a45e-dc53f3a12df1"
INVALID_UUID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"

# Step 1: Authenticate to get JWT cookie (admin role required)
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

# Step 2: Unauthenticated request (no cookie, should return 401)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401) ---"
curlie -H "Content-Type: application/json" -d '{"title":"UpdatedEvent","description":"UpdatedDesc","startDate":"2026-12-02T10:00:00Z","endDate":"2026-12-02T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 3: Valid update (should return 200)
echo ""
echo "--- Test 2: Valid update (should return 200) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"UpdatedEvent","description":"UpdatedDesc","startDate":"2026-12-02T10:00:00Z","endDate":"2026-12-02T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 4: Duplicate title (another event already has this title, should return 409)
echo ""
echo "--- Test 3: Duplicate title (should return 409) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Tech Conference 2026","description":"UpdatedDesc","startDate":"2026-12-03T10:00:00Z","endDate":"2026-12-03T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 5: Event not found (should return 404)
echo ""
echo "--- Test 4: Event not found with non-existent UUID (should return 404) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"NonExistentUpdate","description":"UpdatedDesc","startDate":"2026-12-04T10:00:00Z","endDate":"2026-12-04T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$INVALID_UUID

# Step 6: Malformed UUID (should return 400)
echo ""
echo "--- Test 5: Malformed UUID (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"MalformedUuidUpdate","description":"UpdatedDesc","startDate":"2026-12-05T10:00:00Z","endDate":"2026-12-05T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$MALFORMED_UUID

# Step 7: Missing title (should return 400)
echo ""
echo "--- Test 6: Missing title (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"description":"UpdatedDesc","startDate":"2026-12-06T10:00:00Z","endDate":"2026-12-06T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 8: Missing description (should return 400)
echo ""
echo "--- Test 7: Missing description (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","startDate":"2026-12-07T10:00:00Z","endDate":"2026-12-07T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 9: Missing startDate (should return 400)
echo ""
echo "--- Test 8: Missing startDate (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"UpdatedDesc","endDate":"2026-12-08T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 10: Missing endDate (should return 400)
echo ""
echo "--- Test 9: Missing endDate (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"UpdatedDesc","startDate":"2026-12-09T10:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 11: Missing location (should return 400)
echo ""
echo "--- Test 10: Missing location (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"UpdatedDesc","startDate":"2026-12-10T10:00:00Z","endDate":"2026-12-10T18:00:00Z"}' http://localhost:8080/events/$EVENT_ID

# Step 12: Title with invalid character '!' (should return 400)
echo ""
echo "--- Test 11: Title with invalid character '!' (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Invalid!Title","description":"UpdatedDesc","startDate":"2026-12-11T10:00:00Z","endDate":"2026-12-11T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 13: Description with invalid character '@' (should return 400)
echo ""
echo "--- Test 12: Description with invalid character '@' (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"Invalid@Desc","startDate":"2026-12-12T10:00:00Z","endDate":"2026-12-12T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 14: Location with invalid character '#' (should return 400)
echo ""
echo "--- Test 13: Location with invalid character '#' (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"UpdatedDesc","startDate":"2026-12-13T10:00:00Z","endDate":"2026-12-13T18:00:00Z","location":"Invalid#Loc"}' http://localhost:8080/events/$EVENT_ID

# Step 15: Invalid startDate format (should return 400)
echo ""
echo "--- Test 14: Invalid startDate format (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"UpdatedDesc","startDate":"not-a-date","endDate":"2026-12-14T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 16: Invalid endDate format (should return 400)
echo ""
echo "--- Test 15: Invalid endDate format (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"UpdatedDesc","startDate":"2026-12-15T10:00:00Z","endDate":"invalid-date","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 17: Blank title (spaces only, should return 400)
echo ""
echo "--- Test 16: Blank title (spaces only, should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"   ","description":"UpdatedDesc","startDate":"2026-12-16T10:00:00Z","endDate":"2026-12-16T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 18: Empty title (should return 400)
echo ""
echo "--- Test 17: Empty title (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"","description":"UpdatedDesc","startDate":"2026-12-17T10:00:00Z","endDate":"2026-12-17T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 19: Empty body (should return 400 or 500)
echo ""
echo "--- Test 18: Empty JSON body (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{}' http://localhost:8080/events/$EVENT_ID

# Step 20: Title with valid space character (should return 200)
echo ""
echo "--- Test 19: Title with valid space (should return 200) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Valid Updated Title","description":"UpdatedDesc","startDate":"2026-12-18T10:00:00Z","endDate":"2026-12-18T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Step 21: Title with valid special characters .,':- (should return 200)
echo ""
echo "--- Test 20: Title with valid special characters (should return 200) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Valid.Title,Test:Desc-Updated","description":"UpdatedDesc","startDate":"2026-12-19T10:00:00Z","endDate":"2026-12-19T18:00:00Z","location":"UpdatedLoc"}' http://localhost:8080/events/$EVENT_ID

# Cleanup
rm -f cookies.txt
