#!/bin/bash

echo "Testing POST /speakers"

# Step1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -X POST -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

# Step 2: Valid speaker creation (should return 201)
echo ""
echo "--- Test 1: Valid speaker creation (should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"john.doe@valid.com","profilePicture":"https://example.com/pic.jpg","bio":"Valid Bio 123","externalLinks":[{"name":"Website","url":"https://example.com"}]}' http://localhost:8080/speakers

# Step 3: Duplicate email (should return 409)
echo ""
echo "--- Test 2: Duplicate email (should return 409) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Smith","email":"john.doe@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

# Step 4: Bad request tests - missing required fields
echo ""
echo "--- Test 3: Missing firstName (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"lastName":"Doe","email":"missing.first@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 4: Missing lastName (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","email":"missing.last@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 5: Missing email (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 6: Missing bio (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"missing.bio@valid.com"}' http://localhost:8080/speakers

# Step 5: Bad request tests - invalid characters in names and bio (pattern: ^[a-zA-Z0-9 .,':-]+$)
echo ""
echo "--- Test 7: firstName with invalid character '!' (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John!","lastName":"Doe","email":"invalid.char@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 8: lastName with invalid character '@' (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe@","email":"invalid.char2@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 9: bio with invalid character '#' (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.char3@valid.com","bio":"Invalid#Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 10: firstName with space (valid, should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John Michael","lastName":"Doe","email":"valid.space@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

# Step 6: Bad request tests - invalid email format
echo ""
echo "--- Test 11: Invalid email format - missing @ (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalidemail.com","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 12: Invalid email format - missing domain (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid@","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 13: Invalid email format - invalid characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"in*valid@email.com","bio":"Valid Bio"}' http://localhost:8080/speakers

# Step 7: Bad request tests - invalid profilePicture URL
echo ""
echo "--- Test 14: Invalid profilePicture URL - no http/https (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.url@valid.com","profilePicture":"ftp://example.com/pic.jpg","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 15: Invalid profilePicture URL - forbidden characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.url2@valid.com","profilePicture":"https://example.com/pic with spaces.jpg","bio":"Valid Bio"}' http://localhost:8080/speakers

# Step 8: Bad request tests - invalid externalLinks
echo ""
echo "--- Test 16: Invalid externalLink name - forbidden characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Web#site","url":"https://example.com"}]}' http://localhost:8080/speakers

echo ""
echo "--- Test 17: Invalid externalLink URL - forbidden characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link2@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Website","url":"https://example.com/path with spaces"}]}' http://localhost:8080/speakers

# Step 8b: Bad request tests - more invalid externalLinks URLs (based on validateUrl pattern)
echo ""
echo "--- Test 26: Invalid externalLink URL - wrong protocol ftp:// (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link3@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Website","url":"ftp://example.com"}]}' http://localhost:8080/speakers

echo ""
echo "--- Test 27: Invalid externalLink URL - no protocol (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link4@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Website","url":"example.com"}]}' http://localhost:8080/speakers

echo ""
echo "--- Test 28: Invalid externalLink URL - with port number (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link5@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Website","url":"https://example.com:8080"}]}' http://localhost:8080/speakers

echo ""
echo "--- Test 29: Invalid externalLink URL - with query string (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link6@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Website","url":"https://example.com?q=test"}]}' http://localhost:8080/speakers

echo ""
echo "--- Test 30: Invalid externalLink URL - with @ symbol (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link7@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Website","url":"https://user@example.com"}]}' http://localhost:8080/speakers

# Step 8c: Valid externalLink URL - same as Test 1 (duplicates allowed, should return 201)
echo ""
echo "--- Test 31: Valid externalLink URL - same as Test 1 duplicate allowed (should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Alice","lastName":"Brown","email":"duplicate.url@valid.com","bio":"Valid Bio","externalLinks":[{"name":"Website","url":"https://example.com"}]}' http://localhost:8080/speakers

# Step 9: Bad request tests - blank/empty fields
echo ""
echo "--- Test 18: Blank firstName (spaces only, should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"   ","lastName":"Doe","email":"blank.field@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 19: Empty firstName (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"","lastName":"Doe","email":"empty.field@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 20: Blank email (spaces only, should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"   ","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 21: Empty email (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"","bio":"Valid Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 22: Blank bio (spaces only, should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"blank.bio@valid.com","bio":"   "}' http://localhost:8080/speakers

echo ""
echo "--- Test 23: Empty bio (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"empty.bio@valid.com","bio":""}' http://localhost:8080/speakers

# Step 10: Valid speaker with minimal fields (only required)
echo ""
echo "--- Test 24: Valid speaker with only required fields (should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Smith","email":"jane.smith@valid.com","bio":"Valid Bio"}' http://localhost:8080/speakers

# Step 11: Valid speaker with profilePicture
echo ""
echo "--- Test 25: Valid speaker with profilePicture (should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Bob","lastName":"Wilson","email":"bob.wilson@valid.com","profilePicture":"https://example.com/bob.jpg","bio":"Valid Bio"}' http://localhost:8080/speakers

# Cleanup
rm -f cookies.txt
