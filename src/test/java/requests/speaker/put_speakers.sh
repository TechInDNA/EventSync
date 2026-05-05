#!/bin/bash

echo "Testing PUT /speakers/{id}"

SPEAKER_ID="27dfb67f-b1c5-4f71-b0b2-2190674eefa8"
INVALID_UUID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"

# Step 1: Authenticate to get JWT cookie (admin role required)
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

# Step 2: Unauthenticated request (no cookie, should return 403)
echo ""
echo "--- Test 1: Unauthenticated request (should return 403) ---"
curlie -X PUT -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 3: Malformed UUID (should return 400)
echo ""
echo "--- Test 2: Malformed UUID (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$MALFORMED_UUID

# Step 4: Speaker not found (should return 404)
echo ""
echo "--- Test 3: Speaker not found with non-existent UUID (should return 404) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"nonexistent@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$INVALID_UUID

# Step 6: Valid update - minimal required fields only (should return 200)
echo ""
echo "--- Test 5: Valid update with only required fields (should return 200) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Minimal","lastName":"Update","email":"test.speaker@eventsync.com","bio":"Minimal bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 7: Duplicate email (another speaker has this email, should return 409)
echo ""
echo "--- Test 6: Duplicate email (should return 409) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"john.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 8: Missing firstName (should return 400)
echo ""
echo "--- Test 7: Missing firstName (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 9: Missing lastName (should return 400)
echo ""
echo "--- Test 8: Missing lastName (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 10: Missing email (should return 400)
echo ""
echo "--- Test 9: Missing email (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 11: Missing bio (should return 400)
echo ""
echo "--- Test 10: Missing bio (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 12: Empty body (should return 400)
echo ""
echo "--- Test 11: Empty JSON body (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 13: Blank firstName (spaces only, should return 400)
echo ""
echo "--- Test 12: Blank firstName (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"   ","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 14: Empty firstName (should return 400)
echo ""
echo "--- Test 13: Empty firstName (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 15: Blank lastName (should return 400)
echo ""
echo "--- Test 14: Blank lastName (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"   ","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 16: Blank email (should return 400)
echo ""
echo "--- Test 15: Blank email (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"   ","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 17: Blank bio (should return 400)
echo ""
echo "--- Test 16: Blank bio (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"   "}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 18: firstName with invalid character '!' (should return 400)
echo ""
echo "--- Test 17: firstName with invalid character '!' (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Upda!ed","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 19: lastName with invalid character '@' (should return 400)
echo ""
echo "--- Test 18: lastName with invalid character '@' (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Sp@aker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 20: bio with invalid character '#' (should return 400)
echo ""
echo "--- Test 19: bio with invalid character '#' (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Invalid#Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 21: firstName with valid special characters .,':- (should return 200)
echo ""
echo "--- Test 20: firstName with valid special characters (should return 200) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Upda.ted","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 22: firstName with valid space (should return 200)
echo ""
echo "--- Test 21: firstName with valid space (should return 200) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated Test","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 23: Invalid email format - missing @ (should return 400)
echo ""
echo "--- Test 22: Invalid email format - missing @ (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"invalidemail.com","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 24: Invalid email format - missing domain (should return 400)
echo ""
echo "--- Test 23: Invalid email format - missing domain (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"invalid@","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 25: Invalid profilePicture URL - no http/https (should return 400)
echo ""
echo "--- Test 24: Invalid profilePicture URL - ftp protocol (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","profilePicture":"ftp://example.com/pic.jpg","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 26: Invalid profilePicture URL - forbidden characters (should return 400)
echo ""
echo "--- Test 25: Invalid profilePicture URL - with spaces (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","profilePicture":"https://example.com/pic with spaces.jpg","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 27: Valid profilePicture URL (should return 200)
echo ""
echo "--- Test 26: Valid profilePicture URL (should return 200) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","profilePicture":"https://i.pravatar.cc/150?img=30","bio":"Updated Bio"}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 28: Invalid externalLink name - forbidden characters (should return 400)
echo ""
echo "--- Test 27: Invalid externalLink name - forbidden characters (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"Web#site","url":"https://example.com"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 29: Invalid externalLink URL - forbidden characters (should return 400)
echo ""
echo "--- Test 28: Invalid externalLink URL - with spaces (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"Website","url":"https://example.com/path with spaces"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 30: Invalid externalLink URL - wrong protocol (should return 400)
echo ""
echo "--- Test 29: Invalid externalLink URL - ftp protocol (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"Website","url":"ftp://example.com"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 31: Invalid externalLink URL - no protocol (should return 400)
echo ""
echo "--- Test 30: Invalid externalLink URL - no protocol (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"Website","url":"example.com"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 32: Valid externalLinks update (should return 200)
echo ""
echo "--- Test 31: Valid externalLinks update (should return 200) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"Personal Website","url":"https://testspeaker.dev"},{"name":"GitHub","url":"https://github.com/testspeaker"},{"name":"LinkedIn","url":"https://linkedin.com/in/testspeaker"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 33: ExternalLink with blank name (should return 400)
echo ""
echo "--- Test 32: ExternalLink with blank name (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"   ","url":"https://example.com"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 34: ExternalLink with empty name (should return 400)
echo ""
echo "--- Test 33: ExternalLink with empty name (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"","url":"https://example.com"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 35: ExternalLink with blank URL (should return 400)
echo ""
echo "--- Test 34: ExternalLink with blank URL (should return 400) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"Website","url":"   "}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Step 36: Duplicate externalLink URL (another speaker has this URL, should return 409)
echo ""
echo "--- Test 35: Duplicate externalLink URL (should return 409) ---"
curlie -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Updated","lastName":"Speaker","email":"test.speaker@eventsync.com","bio":"Updated Bio","externalLinks":[{"name":"Website","url":"https://johndoe.dev"}]}' http://localhost:8080/speakers/$SPEAKER_ID

# Cleanup
rm -f cookies.txt
