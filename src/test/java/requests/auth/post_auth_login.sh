#!/bin/bash

echo "Testing POST /auth/login"

echo ""
echo "==========  VALID LOGIN (200)  =========="
echo "--- Test 1: Valid admin login (should return 200 + JWT token) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "==========  UNAUTHORIZED (401)  =========="
echo "--- Test 2: Invalid password (should return 401) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "wrongpassword"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 3: Non-existent email (should return 401 - same generic message to prevent enumeration) ---"
curlie -H "Content-Type: application/json" -d '{"email": "nobody@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 4: Missing/null password (should return 400 - PasswordEncoder.matches returns false for null) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com"}' http://localhost:8080/auth/login

echo ""
echo "==========  BAD REQUEST (400)  =========="
echo "--- Test 5: Invalid email format (no @ symbol) (should return 400 - second regex fails) ---"
curlie -H "Content-Type: application/json" -d '{"email": "not-an-email", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 6: Email with spaces (should return 400 - first regex rejects spaces) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin @test.com", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 7: Email with illegal special chars (should return 400 - first regex rejects <> etc.) ---"
curlie -H "Content-Type: application/json" -d '{"email": "<admin@test.com>", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 8: Email without TLD (should return 400 - second regex needs domain.extension) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@test", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 9: SQL injection attempt in email (should return 400 - first regex rejects quotes) ---"
curlie -H "Content-Type: application/json" -d "{\"email\": \"admin@eventsync.com' OR '1'='1\", \"password\": \"test\"}" http://localhost:8080/auth/login

echo ""
echo "--- Test 10: Missing/null email (should return 400) ---"
curlie -H "Content-Type: application/json" -d '{"password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 11: Empty body with null email + null password (should return 400) ---"
curlie -H "Content-Type: application/json" -d '{}' http://localhost:8080/auth/login

echo ""
echo "==========  RATE LIMITING  =========="
echo "--- Test 12: Trigger rate limiting (should return 429) ---"
echo "    NOTE: AuthService blocks after 5 failures within 12h (in-memory counter)."
echo "    Restart the app between runs to reset the counter."
for i in $(seq 1 5); do
  curlie -s -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "wrong"}' http://localhost:8080/auth/login > /dev/null 2>&1
done
echo "--- 6th attempt (should be blocked) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "wrong"}' http://localhost:8080/auth/login
