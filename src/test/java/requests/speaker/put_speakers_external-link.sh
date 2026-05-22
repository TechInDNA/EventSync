#!/bin/bash

SPEAKER_WITH_LINKS_ID="0fb7a6ca-431a-46d6-a4bb-8e739979dbb5"
SPEAKER_WITHOUT_LINKS_ID="38d851f2-b57c-4425-96b9-24aedbf39d42"
NONEXISTENT_ID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"
MALFORMED_SHORT="12345"

echo "Testing PUT /speakers/{id}/external-link?urlName={name}"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (should return 401)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Website"

# Step 3: Valid update of existing link (should return 200)
echo ""
echo "--- Test 2: Valid update of existing link (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Website"

# Step 4: Update non-existent link name on existing speaker (should return 404)
echo ""
echo "--- Test 3: Non-existent urlName (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"NewLink","url":"https://newlink.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=NonExistent"

# Step 5: Update on speaker without external links (should return 404)
echo ""
echo "--- Test 4: Speaker without links (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"AnyLink","url":"https://anylink.dev"}' "https://localhost:443/speakers/$SPEAKER_WITHOUT_LINKS_ID/external-link?urlName=Anything"

# Step 6: Non-existent speaker (should return 404)
echo ""
echo "--- Test 5: Non-existent speaker (should return 404) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.dev"}' "https://localhost:443/speakers/$NONEXISTENT_ID/external-link?urlName=Website"

# Step 7: Invalid UUID format (should return 400)
echo ""
echo "--- Test 6: Malformed UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.dev"}' "https://localhost:443/speakers/$MALFORMED_UUID/external-link?urlName=Website"

# Step 8: Short malformed UUID (should return 400)
echo ""
echo "--- Test 7: Short malformed UUID (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.dev"}' "https://localhost:443/speakers/$MALFORMED_SHORT/external-link?urlName=Website"

# Step 9: Missing name in body (should return 400)
echo ""
echo "--- Test 8: Missing name (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"url":"https://missing-name.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 10: Missing url in body (should return 400)
echo ""
echo "--- Test 9: Missing url (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Missing URL"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 11: Empty name (should return 400)
echo ""
echo "--- Test 10: Empty name (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"","url":"https://empty-name.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 12: Blank name (spaces only, should return 400)
echo ""
echo "--- Test 11: Blank name (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"   ","url":"https://blank-name.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 13: Empty url (should return 400)
echo ""
echo "--- Test 12: Empty url (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Empty URL","url":""}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 14: Name with invalid character '@' (should return 400)
echo ""
echo "--- Test 13: Name with invalid char '@' (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio@Site","url":"https://invalid-char.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 15: Invalid URL format - ftp:// (should return 400)
echo ""
echo "--- Test 14: Invalid URL scheme ftp:// (should return 400) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"FTP Link","url":"ftp://example.com"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 16: Duplicate URL conflict - use URL already taken by another link (should return 409)
echo ""
echo "--- Test 15: Duplicate URL (should return 409) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://eveputlink.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Step 17: Restore original link data for idempotency (should return 200)
echo ""
echo "--- Test 16: Restore original link (should return 200) ---"
curlie -k -X PUT -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Website","url":"https://charlieputlink.dev"}' "https://localhost:443/speakers/$SPEAKER_WITH_LINKS_ID/external-link?urlName=Portfolio"

# Cleanup
rm -f cookies.txt
