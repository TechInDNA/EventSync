#!/bin/bash

echo "Testing POST /auth/login"

echo "--- Test 1: Valid admin login ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 2: Invalid password (should return 401) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com", "password": "wrongpassword"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 3: Non-existent email (should return 401) ---"
curlie -H "Content-Type: application/json" -d '{"email": "nobody@eventsync.com", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 4: Missing email (should return 400) ---"
curlie -H "Content-Type: application/json" -d '{"password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 5: Missing password (should return 400) ---"
curlie -H "Content-Type: application/json" -d '{"email": "admin@eventsync.com"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 6: Invalid email format (should return 400) ---"
curlie -H "Content-Type: application/json" -d '{"email": "not-an-email", "password": "test"}' http://localhost:8080/auth/login

echo ""
echo "--- Test 7: Empty body (should return 400) ---"
curlie -H "Content-Type: application/json" -d '{}' http://localhost:8080/auth/login
