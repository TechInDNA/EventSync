#!/bin/bash

echo "Testing DELETE /sessions/{sessionId}/speaker/{speakerId}"
echo "=========================================================="

# Session B (has 2 speakers: Alice + Bob): 550e8400-e29b-41d4-a716-446655440031
# Alice: 550e8400-e29b-41d4-a716-446655440020
# Bob:   550e8400-e29b-41d4-a716-446655440022
# Session A (no speaker): 550e8400-e29b-41d4-a716-446655440030

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Remove Alice from Session B (should return 204)
echo ""
echo "--- Test 1: Remove Alice from Session B (should return 204) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -X DELETE https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 3: Remove Alice again from Session B (already removed, should return 404)
echo ""
echo "--- Test 2: Remove Alice again from Session B (already removed, should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -X DELETE https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 4: Remove speaker from session that has no link (should return 404)
echo ""
echo "--- Test 3: Remove speaker from session with no link (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -X DELETE https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 5: Invalid sessionId UUID (should return 400)
echo ""
echo "--- Test 4: Invalid sessionId UUID format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -X DELETE https://localhost:443/sessions/not-a-uuid/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 6: Invalid speakerId UUID (should return 400)
echo ""
echo "--- Test 5: Invalid speakerId UUID format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -X DELETE https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/invalid-uuid

# Step 7: No authentication (should return 401 or 403)
echo ""
echo "--- Test 6: No authentication (should return 401 or 403) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" -X DELETE https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440022

# Cleanup
rm -f cookies.txt

echo ""
echo "=========================================================="
echo "All tests completed!"
