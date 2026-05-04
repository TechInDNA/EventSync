#!/bin/bash

echo "Testing POST /speakers"

# Step 1: Authenticate to get JWT cookie
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
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Smith","email":"john.doe@valid.com"}' http://localhost:8080/speakers

# Step 4: Bad request tests - missing required fields
echo ""
echo "--- Test 3: Missing firstName (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"lastName":"Doe","email":"missing.first@valid.com"}' http://localhost:8080/speakers

echo ""
echo "--- Test 4: Missing lastName (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","email":"missing.last@valid.com"}' http://localhost:8080/speakers

echo ""
echo "--- Test 5: Missing email (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe"}' http://localhost:8080/speakers

# Step 5: Bad request tests - invalid characters in names and bio (pattern: ^[a-zA-Z0-9 .,':-]+$)
echo ""
echo "--- Test 6: firstName with invalid character '!' (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John!","lastName":"Doe","email":"invalid.char@valid.com"}' http://localhost:8080/speakers

echo ""
echo "--- Test 7: lastName with invalid character '@' (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe@","email":"invalid.char2@valid.com"}' http://localhost:8080/speakers

echo ""
echo "--- Test 8: bio with invalid character '#' (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.char3@valid.com","bio":"Invalid#Bio"}' http://localhost:8080/speakers

echo ""
echo "--- Test 9: firstName with space (valid, should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John Michael","lastName":"Doe","email":"valid.space@valid.com"}' http://localhost:8080/speakers

# Step 6: Bad request tests - invalid email format
echo ""
echo "--- Test 10: Invalid email format - missing @ (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalidemail.com"}' http://localhost:8080/speakers

echo ""
echo "--- Test 11: Invalid email format - missing domain (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid@"}' http://localhost:8080/speakers

echo ""
echo "--- Test 12: Invalid email format - invalid characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"in*valid@email.com"}' http://localhost:8080/speakers

# Step 7: Bad request tests - invalid profilePicture URL
echo ""
echo "--- Test 13: Invalid profilePicture URL - no http/https (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.url@valid.com","profilePicture":"ftp://example.com/pic.jpg"}' http://localhost:8080/speakers

echo ""
echo "--- Test 14: Invalid profilePicture URL - forbidden characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.url2@valid.com","profilePicture":"https://example.com/pic with spaces.jpg"}' http://localhost:8080/speakers

# Step 8: Bad request tests - invalid externalLinks
echo ""
echo "--- Test 15: Invalid externalLink name - forbidden characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link@valid.com","externalLinks":[{"name":"Web#site","url":"https://example.com"}]}' http://localhost:8080/speakers

echo ""
echo "--- Test 16: Invalid externalLink URL - forbidden characters (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"invalid.link2@valid.com","externalLinks":[{"name":"Website","url":"https://example.com/path with spaces"}]}' http://localhost:8080/speakers

# Step 9: Bad request tests - blank/empty fields
echo ""
echo "--- Test 17: Blank firstName (spaces only, should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"   ","lastName":"Doe","email":"blank.field@valid.com"}' http://localhost:8080/speakers

echo ""
echo "--- Test 18: Empty firstName (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"","lastName":"Doe","email":"empty.field@valid.com"}' http://localhost:8080/speakers

echo ""
echo "--- Test 19: Blank email (spaces only, should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":"   "}' http://localhost:8080/speakers

echo ""
echo "--- Test 20: Empty email (should return 400) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"John","lastName":"Doe","email":""}' http://localhost:8080/speakers

# Step 10: Valid speaker with minimal fields (only required)
echo ""
echo "--- Test 21: Valid speaker with only required fields (should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Jane","lastName":"Smith","email":"jane.smith@valid.com"}' http://localhost:8080/speakers

# Step 11: Valid speaker with profilePicture
echo ""
echo "--- Test 22: Valid speaker with profilePicture (should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Bob","lastName":"Wilson","email":"bob.wilson@valid.com","profilePicture":"https://example.com/bob.jpg"}' http://localhost:8080/speakers

# Step 12: Valid speaker with bio containing allowed special characters
echo ""
echo "--- Test 23: Valid bio with allowed special characters (should return 201) ---"
curlie -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"firstName":"Alice","lastName":"Brown","email":"alice.brown@valid.com","bio":"Hello, world! It'\''s a test: valid-text here."}' http://localhost:8080/speakers

# Cleanup
rm -f cookies.txt
