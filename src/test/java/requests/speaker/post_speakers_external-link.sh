#!/bin/bash

WITHOUT_LINKS_ID="45047c18-1984-4d1e-bcbc-a7407c227292"
WITH_LINKS_ID="be8bfef5-d76f-4b54-a7aa-8209b17b5a54"
NONEXISTENT_ID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"
MALFORMED_SHORT="12345"

echo "Testing POST /speakers/{id}/external-link"

# Step 1: Authenticate to get JWT cookie
echo ""
echo "--- Authenticating to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (should return 401)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 3: Valid creation on speaker without existing links (should return 201)
echo ""
echo "--- Test 2: Valid creation on speaker without links (should return 201) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 4: Duplicate URL on same speaker (should return 409)
echo ""
echo "--- Test 3: Duplicate URL (should return 409) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio Copy","url":"https://portfolio.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 5: Valid creation on speaker WITH existing links (should return 201)
echo ""
echo "--- Test 4: Valid creation on speaker with existing links (should return 201) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"GitHub","url":"https://github.com/aliceexternal"}' https://localhost:443/speakers/$WITH_LINKS_ID/external-link

# Step 6: Duplicate URL on different speaker, same URL as seed data (should return 409)
echo ""
echo "--- Test 5: Duplicate URL from seed data (should return 409) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Website","url":"https://bobexternal.dev"}' https://localhost:443/speakers/$WITH_LINKS_ID/external-link

# Step 7: Speaker not found (should return 404)
echo ""
echo "--- Test 6: Non-existent speaker (should return 404) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://nonexistent.example.com"}' https://localhost:443/speakers/$NONEXISTENT_ID/external-link

# Step 8: Invalid UUID format (should return 400)
echo ""
echo "--- Test 7: Malformed UUID (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.example.com"}' https://localhost:443/speakers/$MALFORMED_UUID/external-link

# Step 9: Short malformed UUID (should return 400)
echo ""
echo "--- Test 8: Short malformed UUID (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio","url":"https://portfolio.example.com"}' https://localhost:443/speakers/$MALFORMED_SHORT/external-link

# Step 10: Missing name (should return 400)
echo ""
echo "--- Test 9: Missing name (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"url":"https://missing-name.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 11: Missing url (should return 400)
echo ""
echo "--- Test 10: Missing url (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Missing URL"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 12: Empty name (should return 400)
echo ""
echo "--- Test 11: Empty name (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"","url":"https://empty-name.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 13: Blank name (spaces only, should return 400)
echo ""
echo "--- Test 12: Blank name (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"   ","url":"https://blank-name.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 14: Empty url (should return 400)
echo ""
echo "--- Test 13: Empty url (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Empty URL","url":""}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 15: Name with invalid character '@' (should return 400)
echo ""
echo "--- Test 14: Name with invalid char '@' (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio@Site","url":"https://invalid-char.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 16: Name with invalid character '!' (should return 400)
echo ""
echo "--- Test 15: Name with invalid char '!' (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Portfolio!","url":"https://invalid-char2.example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 17: Invalid URL scheme - ftp:// (should return 400)
echo ""
echo "--- Test 16: Invalid URL scheme ftp:// (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"FTP Link","url":"ftp://example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 18: Invalid URL - no protocol (should return 400)
echo ""
echo "--- Test 17: URL without protocol (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"No Protocol","url":"example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 19: Invalid URL - with spaces (should return 400)
echo ""
echo "--- Test 18: URL with spaces (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Spaces","url":"https://example.com/path with spaces"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 20: Invalid URL - with port (should return 400)
echo ""
echo "--- Test 19: URL with port (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Port","url":"https://example.com:8080"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 21: Invalid URL - with query string (should return 400)
echo ""
echo "--- Test 20: URL with query string (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"Query","url":"https://example.com?q=test"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 22: Invalid URL - with @ symbol (should return 400)
echo ""
echo "--- Test 21: URL with @ symbol (should return 400) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"User","url":"https://user@example.com"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 23: Valid URL with special chars allowed by pattern (should return 201)
echo ""
echo "--- Test 22: Valid URL with allowed chars (should return 201) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"GitHub","url":"https://github.com/username"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Step 24: Valid name with allowed special chars (should return 201)
echo ""
echo "--- Test 23: Valid name with allowed special chars .,'':- (should return 201) ---"
curlie -k -X POST -b cookies.txt -H "Content-Type: application/json" -d '{"name":"OC - Site.dev","url":"https://oconnor.dev"}' https://localhost:443/speakers/$WITHOUT_LINKS_ID/external-link

# Cleanup
rm -f cookies.txt
