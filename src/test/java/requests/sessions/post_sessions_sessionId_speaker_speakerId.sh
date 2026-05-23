#!/bin/bash

echo "Testing POST /sessions/{sessionId}/speaker/{speakerId}"
echo "======================================================"

SESSION_ID="77c45b84-e13f-41f1-a163-ac2df3a92325"
SPEAKER_ID="45047c18-1984-4d1e-bcbc-a7407c227292"
INVALID_UUID="00000000-0000-0000-0000-000000000000"
MALFORMED_UUID="not-a-valid-uuid"

# Step 1: Authenticate as admin to get JWT cookie
echo ""
echo "--- Authenticating as admin to get JWT cookie ---"
curlie -k -c cookies.txt -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

# Step 2: Unauthenticated request (should return 401 or 403)
echo ""
echo "--- Test 1: Unauthenticated request (should return 401 or 403) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"07:57:15.801Z\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${SPEAKER_ID}"

# Step 3: Valid speaker-to-session link (should return 201)
echo ""
echo "--- Test 2: Valid speaker-to-session link (should return 201) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"07:57:15.801Z\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${SPEAKER_ID}"

# Step 4: Non-existent session (should return 404)
echo ""
echo "--- Test 3: Non-existent session (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"07:57:15.801Z\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${INVALID_UUID}/speaker/${SPEAKER_ID}"

# Step 5: Non-existent speaker (should return 404)
echo ""
echo "--- Test 4: Non-existent speaker (should return 404) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"07:57:15.801Z\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${INVALID_UUID}"

# Step 6: Malformed session UUID (should return 400)
echo ""
echo "--- Test 5: Malformed session UUID (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"07:57:15.801Z\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${MALFORMED_UUID}/speaker/${SPEAKER_ID}"

# Step 7: Malformed speaker UUID (should return 400)
echo ""
echo "--- Test 6: Malformed speaker UUID (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"07:57:15.801Z\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${MALFORMED_UUID}"

# Step 8: Missing startTime (should return 400)
echo ""
echo "--- Test 7: Missing startTime (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${SPEAKER_ID}"

# Step 9: Missing endTime (should return 400)
echo ""
echo "--- Test 8: Missing endTime (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${SPEAKER_ID}"

# Step 10: Empty startTime (should return 400)
echo ""
echo "--- Test 9: Empty startTime (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${SPEAKER_ID}"

# Step 11: Invalid startTime format (should return 400)
echo ""
echo "--- Test 10: Invalid startTime format (should return 400) ---"
curlie -k -b cookies.txt -s -w "\nHTTP Status: %{http_code}\n" -H "Content-Type: application/json" -d "{\"startTime\":\"not-a-time\",\"endTime\":\"07:57:15.801Z\"}" "https://localhost:443/sessions/${SESSION_ID}/speaker/${SPEAKER_ID}"

# Cleanup
rm -f cookies.txt

echo ""
echo "======================"
echo "All tests completed!"
