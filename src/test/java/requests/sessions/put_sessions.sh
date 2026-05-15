#!/bin/bash

echo "Testing PUT /sessions/{id}"
echo "========================"

SESSION_ID="4e64d605-7a24-40aa-aea1-87c183fa4036"
SESSION_WITH_SPEAKERS="3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f"
INVALID_UUID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (no cookie, should return 401 or 403)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401 or 403) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{"title":"Updated Session","description":"Updated description","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":50,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 3: Valid update (should return 200)
echo ""
echo "--- Test 2: Valid update (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Session Updated via PUT","description":"Updated description for PUT test","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":50,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 4: Duplicate title (another session already has this title, should return 409)
echo ""
echo "--- Test 3: Duplicate title (should return 409) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Session to Delete","description":"Trying duplicate title","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":50,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 5: Session not found with non-existent UUID (should return 404)
echo ""
echo "--- Test 4: Session not found with non-existent UUID (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Non Existent","description":"Session not found","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":50,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$INVALID_UUID

# Step 6: Malformed UUID (should return 400)
echo ""
echo "--- Test 5: Malformed UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Malformed UUID","description":"UUID format invalid","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":50,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$MALFORMED_UUID

# Step 7: Missing title (should return 400)
echo ""
echo "--- Test 6: Missing title (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"description":"No title","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 8: Empty title (should return 400)
echo ""
echo "--- Test 7: Empty title (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"","description":"Empty title","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 9: Blank title (spaces only, should return 400)
echo ""
echo "--- Test 8: Blank title (spaces only, should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"   ","description":"Blank title","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 10: Missing description (should return 400)
echo ""
echo "--- Test 9: Missing description (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"No Desc","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 11: Empty description (should return 400)
echo ""
echo "--- Test 10: Empty description (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Empty Desc","description":"","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 12: Missing startDate (should return 400)
echo ""
echo "--- Test 11: Missing startDate (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"No Start","description":"Session without startDate","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 13: Missing endDate (should return 400)
echo ""
echo "--- Test 12: Missing endDate (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"No End","description":"Session without endDate","startDate":"2026-06-16T10:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 14: Invalid startDate format (should return 400)
echo ""
echo "--- Test 13: Invalid startDate format (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Bad Start Date","description":"Invalid startDate","startDate":"not-a-date","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 15: Invalid endDate format (should return 400)
echo ""
echo "--- Test 14: Invalid endDate format (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Bad End Date","description":"Invalid endDate","startDate":"2026-06-16T10:00:00Z","endDate":"16-06-2026","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 16: Title with invalid character '!' (should return 400)
echo ""
echo "--- Test 15: Title with invalid character '!' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Session!","description":"Invalid char in title","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 17: Title with invalid character '@' (should return 400)
echo ""
echo "--- Test 16: Title with invalid character '@' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Session@","description":"Invalid char in title","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 18: Title exceeding 50 characters (should return 400)
echo ""
echo "--- Test 17: Title exceeding 50 characters (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"This is a very long session title that definitely exceeds fifty characters","description":"Title too long","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 19: Valid title with valid special characters (should return 200)
echo ""
echo "--- Test 18: Valid title with period, colon, apostrophe, hyphen (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Session: PUT - Test","description":"Valid special chars in title","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","capacity":75,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 20: Non-existent room (should return 404)
echo ""
echo "--- Test 19: Non-existent room (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Unknown Room","description":"Trying non-existent room","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"NonExistentRoomXYZ","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 21: Non-existent event (should return 404)
echo ""
echo "--- Test 20: Non-existent event (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Unknown Event","description":"Trying non-existent event","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"NonExistentEventXYZ"}' https://localhost:443/sessions/$SESSION_ID

# Step 22: Description with invalid character '!' (should return 400)
echo ""
echo "--- Test 21: Description with invalid character '!' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Bad Desc","description":"Invalid description!","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 23: Missing capacity (defaults to null, should return 400)
echo ""
echo "--- Test 22: Missing capacity (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"No Cap","description":"Session without capacity","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 24: Capacity zero (should return 400)
echo ""
echo "--- Test 23: Capacity set to 0 (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Zero Cap","description":"Session with capacity 0","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","capacity":0,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 25: Capacity negative (should return 400)
echo ""
echo "--- Test 24: Negative capacity (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Neg Cap","description":"Negative capacity","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","capacity":-5,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 26: Capacity as string "abc" (should return 400)
echo ""
echo "--- Test 25: Capacity as string \"abc\" (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Str Cap","description":"String capacity","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","capacity":"abc","eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 27: Missing roomName (should return 400)
echo ""
echo "--- Test 26: Missing roomName (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"No Room","description":"Without roomName","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","eventTitle":"Session PUT Event","capacity":10}' https://localhost:443/sessions/$SESSION_ID

# Step 28: Missing eventTitle (should return 400)
echo ""
echo "--- Test 27: Missing eventTitle (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"No Event","description":"Without eventTitle","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","capacity":10}' https://localhost:443/sessions/$SESSION_ID

# Step 29: Empty roomName (should return 400)
echo ""
echo "--- Test 28: Empty roomName (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Empty Room","description":"Empty roomName","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 30: Empty eventTitle (should return 400)
echo ""
echo "--- Test 29: Empty eventTitle (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Empty Event","description":"Empty eventTitle","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":""}' https://localhost:443/sessions/$SESSION_ID

# Step 31: RoomName with invalid character '!' (should return 400)
echo ""
echo "--- Test 30: RoomName with invalid character '!' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Bad RoomName","description":"Invalid roomName","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Bad!Room","capacity":10,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 32: EventTitle with invalid character '#' (should return 400)
echo ""
echo "--- Test 31: EventTitle with invalid character '#' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Bad Event","description":"Invalid eventTitle","startDate":"2026-06-16T14:00:00Z","endDate":"2026-06-16T16:00:00Z","roomName":"Session PUT Room","capacity":10,"eventTitle":"Bad#Event"}' https://localhost:443/sessions/$SESSION_ID

# Step 33: PUT on session with speakers (should return 200 with non-null speakers)
echo ""
echo "--- Test 33: PUT on session with speakers (should return 200 with speakers) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Keynote: Future of Tech","description":"Updated keynote description for speakers test","startDate":"2026-06-16T10:00:00Z","endDate":"2026-06-16T12:00:00Z","roomName":"Main Hall","capacity":200,"eventTitle":"Summer Tech Fest"}' https://localhost:443/sessions/$SESSION_WITH_SPEAKERS

# Step 34: Restore session with speakers original data (should return 200)
echo ""
echo "--- Test 34: Restore speakers session original data (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Keynote: Future of Tech","description":"Opening keynote about technology trends","startDate":"2026-05-10T10:00:00Z","endDate":"2026-05-10T12:00:00Z","roomName":"Main Hall","capacity":200,"eventTitle":"Summer Tech Fest"}' https://localhost:443/sessions/$SESSION_WITH_SPEAKERS

# Step 35: Restore original data (should return 200)
echo ""
echo "--- Test 35: Restore original test data (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"title":"Session to Update via PUT","description":"Session for testing PUT endpoint","startDate":"2026-06-15T10:00:00Z","endDate":"2026-06-15T12:00:00Z","roomName":"Session PUT Room","capacity":30,"eventTitle":"Session PUT Event"}' https://localhost:443/sessions/$SESSION_ID

# Cleanup
rm -f cookies.txt

echo ""
echo "========================"
echo "All PUT sessions tests completed!"
