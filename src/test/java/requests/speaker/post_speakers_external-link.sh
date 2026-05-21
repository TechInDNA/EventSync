#!/bin/bash

echo "Testing POST /speakers/{id}/external-link"

TEST_SPEAKER_ID="27dfb67f-b1c5-4f71-b0b2-2190674eefa8"
INVALID_UUID="00000000-0000-0000-0000-000000000000"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -X POST -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Valid external link creation (should return 201)
echo ""
echo "--- Test 1: Valid external link creation (should return 201) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.example.com"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 3: Duplicate name for same speaker (should return 409)
echo ""
echo "--- Test 2: Duplicate name for same speaker (should return 409) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio2.example.com"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 4: Duplicate URL for same speaker (should return 409)
echo ""
echo "--- Test 3: Duplicate URL for same speaker (should return 409) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio Copy","url":"https://portfolio.example.com"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 5: Speaker not found (should return 404)
echo ""
echo "--- Test 4: Non-existent speaker ID (should return 404) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Test","url":"https://test.dev"}' "https://localhost:443/speakers/$INVALID_UUID/external-link"

# Step 6: Invalid UUID format (should return 400)
echo ""
echo "--- Test 5: Invalid UUID format (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Test","url":"https://test.dev"}' "https://localhost:443/speakers/not-a-uuid/external-link"

# Step 7: Missing name field (should return 400)
echo ""
echo "--- Test 6: Missing name field (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"url":"https://test.dev"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 8: Missing URL field (should return 400)
echo ""
echo "--- Test 7: Missing URL field (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Test"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 9: Invalid URL format - no http/https (should return 400)
echo ""
echo "--- Test 8: Invalid URL format (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Test","url":"ftp://test.dev"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Step 10: Invalid name characters (should return 400)
echo ""
echo "--- Test 9: Invalid name characters (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Test@Site!","url":"https://test.dev"}' "https://localhost:443/speakers/$TEST_SPEAKER_ID/external-link"

# Cleanup
rm -f cookies.txt
