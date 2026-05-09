#!/bin/bash

echo "Testing POST /events"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

# Step 2: Valid event creation (should return 201)
echo ""
echo "--- Test 1: Valid event creation (should return 201) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidEvent123","description":"ValidDesc123","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc123"}' http://localhost:8080/events

# Step 3: Duplicate event (same title, should return 409)
echo ""
echo "--- Test 2: Duplicate event title (should return 409) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidEvent123","description":"AnotherDesc","startDate":"2026-06-02T10:00:00Z","endDate":"2026-06-02T18:00:00Z","location":"AnotherLoc123"}' http://localhost:8080/events

# Step 4: Bad request tests - missing required fields
echo ""
echo "--- Test 3: Missing title (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 4: Missing description (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 5: Missing startDate (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"ValidDesc","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 6: Missing endDate (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 7: Missing location (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z"}' http://localhost:8080/events

# Step 5: Bad request tests - invalid characters per EventValidator (pattern: ^[a-zA-Z0-9.,':-]+$)
echo ""
echo "--- Test 8: Title with invalid character '!' (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Invalid!Title","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 9: Description with invalid character '@' (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"Invalid@Desc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 10: Location with invalid character '#' (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"Invalid#Loc"}' http://localhost:8080/events

echo ""
echo "--- Test 11: Title with space (valid, should return 201) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Valid Title","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

# Step 6: Bad request tests - invalid date formats
echo ""
echo "--- Test 12: Invalid startDate format (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"ValidDesc","startDate":"not-a-date","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 13: Invalid endDate format (invalid month 13, should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-13-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

# Step 7: Bad request tests - blank/empty fields
echo ""
echo "--- Test 14: Blank title (spaces only, should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"   ","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 15: Empty title (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 16: Title exceeding 50 characters (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ThisIsAVeryLongTitleThatExceedsFiftyCharactersForTestingPurposes","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ValidLoc"}' http://localhost:8080/events

echo ""
echo "--- Test 17: Location exceeding 50 characters (should return 400) ---"
curlie -b cookies.txt -H "Content-Type: application/json" -d '{"title":"ValidTitle","description":"ValidDesc","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"ThisIsAVeryLongLocationNameThatExceedsFiftyCharactersTest"}' http://localhost:8080/events

# Cleanup
rm -f cookies.txt
