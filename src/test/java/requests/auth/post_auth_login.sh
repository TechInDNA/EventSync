#!/bin/bash

echo "Testing POST /auth/login"

echo ""
echo "==========  VALID LOGIN (200)  =========="
echo "--- Test 1: Valid admin login (should return 200 + JWT token) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' https://localhost:443/auth/login

echo ""
echo "==========  UNAUTHORIZED (401)  =========="
echo "--- Test 2: Invalid password (should return 401) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "wrongpassword"}' https://localhost:443/auth/login

echo ""
echo "--- Test 3: Non-existent email (should return 401 - same generic message to prevent enumeration) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "nobody@eventsync.com", "password": "test"}' https://localhost:443/auth/login

echo ""
echo "--- Test 4: Missing/null password (should return 400 - PasswordEncoder.matches returns false for null) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com"}' https://localhost:443/auth/login

echo ""
echo "==========  BAD REQUEST (400)  =========="
echo "--- Test 5: Invalid email format (no @ symbol) (should return 400 - second regex fails) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "not-an-email", "password": "test"}' https://localhost:443/auth/login

echo ""
echo "--- Test 6: Email with spaces (should return 400 - first regex rejects spaces) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "admin @test.com", "password": "test"}' https://localhost:443/auth/login

echo ""
echo "--- Test 7: Email with illegal special chars (should return 400 - first regex rejects <> etc.) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "<admin@test.com>", "password": "test"}' https://localhost:443/auth/login

echo ""
echo "--- Test 8: Email without TLD (should return 400 - second regex needs domain.extension) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "admin@test", "password": "test"}' https://localhost:443/auth/login

echo ""
echo "--- Test 9: SQL injection attempt in email (should return 400 - first regex rejects quotes) ---"
curlie -k -H "Content-Type: application/json" -d "{\"email\": \"admin@eventsync.com' OR '1'='1\", \"password\": \"test\"}" https://localhost:443/auth/login

echo ""
echo "--- Test 10: Missing/null email (should return 400) ---"
curlie -k -H "Content-Type: application/json" -d '{"password": "test"}' https://localhost:443/auth/login

echo ""
echo "--- Test 11: Empty body with null email + null password (should return 400) ---"
curlie -k -H "Content-Type: application/json" -d '{}' https://localhost:443/auth/login

echo ""
echo "==========  RATE LIMITING  =========="
echo "--- Test 12: Trigger rate limiting (should return 429) ---"
echo "    NOTE: AuthService blocks after 5 failures within 12h (in-memory counter)."
echo "    Restart the app between runs to reset the counter."
for i in $(seq 1 5); do
  curlie -k -s -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "wrong"}' https://localhost:443/auth/login > /dev/null 2>&1
done
echo "--- 6th attempt (should be blocked) ---"
curlie -k -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "wrong"}' https://localhost:443/auth/login
