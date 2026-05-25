#!/bin/bash

echo "Testing POST /sessions/{sessionId}/speaker/{speakerId}"
echo "======================================================="

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Session A (no speaker yet): 550e8400-e29b-41d4-a716-446655440030
# Session B (has 2 speakers): 550e8400-e29b-41d4-a716-446655440031
# Session C (exists, no speaker): 550e8400-e29b-41d4-a716-446655440032
# Alice: 550e8400-e29b-41d4-a716-446655440020
# Bob:   550e8400-e29b-41d4-a716-446655440021
# Charlie: 550e8400-e29b-41d4-a716-446655440022

# Step 2: Link Charlie to session with no speaker (should return 201)
echo ""
echo "--- Test 1: Link Charlie to Session A (no speaker yet, should return 201) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440022

# Step 3: Link Bob to Session A (second speaker, should return 201)
echo ""
echo "--- Test 2: Link Bob to Session A (second speaker, should return 201) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T11:00:00Z","endTime":"2026-06-15T12:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440021

# Step 4: Invalid sessionId UUID (should return 400)
echo ""
echo "--- Test 3: Invalid sessionId UUID format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/not-a-uuid/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 5: Invalid speakerId UUID (should return 400)
echo ""
echo "--- Test 4: Invalid speakerId UUID format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/invalid-uuid

# Step 6: Non-existent session (should return 404)
echo ""
echo "--- Test 5: Non-existent session (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/00000000-0000-4000-8000-000000000000/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 7: Non-existent speaker (should return 404)
echo ""
echo "--- Test 6: Non-existent speaker (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/00000000-0000-4000-8000-000000000000

# Step 8: No authentication (should return 401 or 403)
echo ""
echo "--- Test 7: No authentication (should return 401 or 403) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 9: Missing startTime (should return 400)
echo ""
echo "--- Test 8: Missing startTime (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 10: Missing endTime (should return 400)
echo ""
echo "--- Test 9: Missing endTime (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 11: Empty startTime (should return 400)
echo ""
echo "--- Test 10: Empty startTime (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 12: Blank startTime (spaces only, should return 400)
echo ""
echo "--- Test 11: Blank startTime (spaces only, should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"   ","endTime":"2026-06-15T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 13: Empty endTime (should return 400)
echo ""
echo "--- Test 12: Empty endTime (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-06-15T10:00:00Z","endTime":""}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440030/speaker/550e8400-e29b-41d4-a716-446655440020

# Cleanup
rm -f cookies.txt

echo ""
echo "======================================================="
echo "All tests completed!"
