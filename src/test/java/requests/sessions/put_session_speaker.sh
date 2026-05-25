#!/bin/bash

echo "Testing PUT /sessions/{sessionId}/speaker/{speakerId}"
echo "========================================================"

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Session B has Alice (10:00-11:00) and Bob (11:00-12:00)
# Session B: 550e8400-e29b-41d4-a716-446655440031
# Alice:     550e8400-e29b-41d4-a716-446655440020
# Bob:       550e8400-e29b-41d4-a716-446655440021
# Charlie:   550e8400-e29b-41d4-a716-446655440022 (no links)
# Session A: 550e8400-e29b-41d4-a716-446655440030 (no speakers)

# Step 2: Update Alice's link in Session B (should return 200)
echo ""
echo "--- Test 1: Update Alice's link in Session B (should return 200) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T09:30:00Z","endTime":"2026-07-01T10:30:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 3: Update Bob's link in Session B (should return 200)
echo ""
echo "--- Test 2: Update Bob's link in Session B (should return 200) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:30:00Z","endTime":"2026-07-01T11:30:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440021

# Step 4: Non-existent session (should return 404)
echo ""
echo "--- Test 3: Non-existent session (should return 404) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/00000000-0000-4000-8000-000000000000/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 5: Non-existent speaker (should return 404)
echo ""
echo "--- Test 4: Non-existent speaker (should return 404) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/00000000-0000-4000-8000-000000000000

# Step 6: Link does not exist (Charlie not linked to Session B, should return 404)
echo ""
echo "--- Test 5: Link does not exist (Charlie not linked, should return 404) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440022

# Step 7: Invalid sessionId UUID (should return 400)
echo ""
echo "--- Test 6: Invalid sessionId UUID format (should return 400) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/not-a-uuid/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 8: Invalid speakerId UUID (should return 400)
echo ""
echo "--- Test 7: Invalid speakerId UUID format (should return 400) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/invalid-uuid

# Step 9: No authentication (should return 401)
echo ""
echo "--- Test 8: No authentication (should return 401) ---"
curlie -k -X PUT -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 10: Missing startTime (should return 400)
echo ""
echo "--- Test 9: Missing startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 11: Missing endTime (should return 400)
echo ""
echo "--- Test 10: Missing endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 12: Empty startTime (should return 400)
echo ""
echo "--- Test 11: Empty startTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 13: Blank startTime (spaces only, should return 400)
echo ""
echo "--- Test 12: Blank startTime (spaces only, should return 400) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"   ","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 14: Empty endTime (should return 400)
echo ""
echo "--- Test 13: Empty endTime (should return 400) ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":""}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 15: Restore Alice's original link
echo ""
echo "--- Test 14: Restore Alice's original link ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T10:00:00Z","endTime":"2026-07-01T11:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440020

# Step 16: Restore Bob's original link
echo ""
echo "--- Test 15: Restore Bob's original link ---"
curlie -k -X PUT -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d '{"startTime":"2026-07-01T11:00:00Z","endTime":"2026-07-01T12:00:00Z"}' https://localhost:443/sessions/550e8400-e29b-41d4-a716-446655440031/speaker/550e8400-e29b-41d4-a716-446655440021

# Cleanup
rm -f cookies.txt

echo ""
echo "========================================================"
echo "All PUT session-speaker tests completed!"
