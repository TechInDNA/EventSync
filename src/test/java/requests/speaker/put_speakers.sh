#!/bin/bash

echo "Testing PUT /speakers/{id}"

SPEAKER_ID="27dfb67f-b1c5-4f71-b0b2-2190674eefa8"
NONEXISTENT_ID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"
MALFORMED_SHORT="12345"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (no cookie, should return 403)
echo ""
echo "--- Test 1: Unauthenticated request (should return 403) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"unauth@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 3: Malformed UUID (should return 400)
echo ""
echo "--- Test 2: Malformed UUID format (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"malformed.uuid@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$MALFORMED_UUID

# Step 4: Malformed UUID - short (should return 400)
echo ""
echo "--- Test 3: Malformed UUID short (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"short.uuid@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$MALFORMED_SHORT

# Step 5: Speaker not found (should return 404)
echo ""
echo "--- Test 4: Non-existent UUID (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"not.found@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$NONEXISTENT_ID

# Step 6: Duplicate email (should return 409)
echo ""
echo "--- Test 5: Duplicate email (should return 409) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"john.speaker@eventsync.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 7: Missing firstName (should return 400)
echo ""
echo "--- Test 6: Missing firstName (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"lastName":"Doe","email":"missing.first@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 8: Missing lastName (should return 400)
echo ""
echo "--- Test 7: Missing lastName (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","email":"missing.last@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 9: Missing email (should return 400)
echo ""
echo "--- Test 8: Missing email (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 10: Missing bio (should return 400)
echo ""
echo "--- Test 9: Missing bio (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"missing.bio@test.com"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 11: Blank firstName (should return 400)
echo ""
echo "--- Test 10: Blank firstName (spaces only, should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"   ","lastName":"Doe","email":"blank.first@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 12: Empty firstName (should return 400)
echo ""
echo "--- Test 11: Empty firstName (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"","lastName":"Doe","email":"empty.first@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 13: Blank lastName (should return 400)
echo ""
echo "--- Test 12: Blank lastName (spaces only, should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"   ","email":"blank.last@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 14: Empty lastName (should return 400)
echo ""
echo "--- Test 13: Empty lastName (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"","email":"empty.last@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 15: Blank bio (should return 400)
echo ""
echo "--- Test 14: Blank bio (spaces only, should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"blank.bio@test.com","bio":"   "}' https://localhost:443/speakers/$SPEAKER_ID

# Step 16: Empty bio (should return 400)
echo ""
echo "--- Test 15: Empty bio (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"empty.bio@test.com","bio":""}' https://localhost:443/speakers/$SPEAKER_ID

# Step 17: Blank email (should return 400)
echo ""
echo "--- Test 16: Blank email (spaces only, should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"   ","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 18: Empty email (should return 400)
echo ""
echo "--- Test 17: Empty email (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 19: firstName with invalid character '!' (should return 400)
echo ""
echo "--- Test 18: firstName with invalid character '!' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John!","lastName":"Doe","email":"invalid.char1@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 20: lastName with invalid character '@' (should return 400)
echo ""
echo "--- Test 19: lastName with invalid character '@' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe@","email":"invalid.char2@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 21: bio with invalid character '#' (should return 400)
echo ""
echo "--- Test 20: bio with invalid character '#' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.char3@test.com","bio":"Invalid#Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 22: Invalid email format - missing @ (should return 400)
echo ""
echo "--- Test 21: Invalid email format - missing @ (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalidemail.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 23: Invalid email format - missing domain (should return 400)
echo ""
echo "--- Test 22: Invalid email format - missing domain (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid@","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 24: Invalid email format - invalid characters (should return 400)
echo ""
echo "--- Test 23: Invalid email format - invalid characters '*' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"in*valid@email.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 25: firstName exceeding 50 characters (should return 400)
echo ""
echo "--- Test 24: firstName exceeding 50 characters (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Johnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn","lastName":"Doe","email":"long.first@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 26: lastName exceeding 50 characters (should return 400)
echo ""
echo "--- Test 25: lastName exceeding 50 characters (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee","email":"long.last@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 27: email exceeding 50 characters (should return 400)
echo ""
echo "--- Test 26: email exceeding 50 characters (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"johnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 28: Empty JSON body (should return 400)
echo ""
echo "--- Test 27: Empty JSON body (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{}' https://localhost:443/speakers/$SPEAKER_ID

# Step 29: Valid update with minimal fields (should return 200)
echo ""
echo "--- Test 28: Valid update with minimal fields (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"john.updated@test.com","bio":"Valid Bio"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 30: Valid update with all fields (should return 200)
echo ""
echo "--- Test 29: Valid update with all fields including profilePicture (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Smith","email":"jane.updated@test.com","profilePicture":"https://example.com/pic.jpg","bio":"Updated Bio with valid chars"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 31: Valid update with valid special characters in first name (should return 200)
echo ""
echo "--- Test 30: Valid update with valid special chars .,'':- (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Jean-Marie OConnor","lastName":"Doe","email":"valid.special@test.com","bio":"Valid Bio with standard chars"}' https://localhost:443/speakers/$SPEAKER_ID

# Step 32: Valid update resetting to original (should return 200)
echo ""
echo "--- Test 31: Valid update resetting speaker data (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"john.doe.reset@test.com","bio":"Senior software engineer."}' https://localhost:443/speakers/$SPEAKER_ID

# Cleanup
rm -f cookies.txt
