#!/bin/bash

SESSION_ID="3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f"

echo "Testing GET /sessions/{id}/questions"
echo "===================================="

# Test 1: Get questions (default pagination + sort createdAt)
echo ""
echo "--- Test 1: Get questions default params (page=1, size=20, sort=createdAt) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions"

# Test 2: Get questions sorted by upvote (most upvoted first)
echo ""
echo "--- Test 2: Get questions sorted by upvote ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?sort=upvote"

# Test 3: Get questions with custom page and size
echo ""
echo "--- Test 3: Get questions with page=1&size=2 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=1&size=2"

# Test 4: Get questions page 2
echo ""
echo "--- Test 4: Get questions with page=2&size=2 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=2&size=2"

# Test 5: Invalid page (negative)
echo ""
echo "--- Test 5: Invalid page (negative) - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=-1&size=10"

# Test 6: Invalid size (zero)
echo ""
echo "--- Test 6: Invalid size (zero) - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=1&size=0"

# Test 7: Invalid size (negative)
echo ""
echo "--- Test 7: Invalid size (negative) - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=1&size=-5"

# Test 8: Page beyond total (should return empty data array)
echo ""
echo "--- Test 8: Page beyond total (should return empty data array) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=9999&size=10"

# Test 9: Non-numeric page parameter
echo ""
echo "--- Test 9: Non-numeric page parameter - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=abc&size=10"

# Test 10: Non-numeric size parameter
echo ""
echo "--- Test 10: Non-numeric size parameter - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=1&size=abc"

# Test 11: Unknown parameters (should be ignored)
echo ""
echo "--- Test 11: Unknown parameters (should be ignored) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=1&size=10&unknown=param"

# Test 12: Non-existent session ID (should return 404)
echo ""
echo "--- Test 12: Non-existent session UUID - should return 404 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/00000000-0000-0000-0000-000000000000/questions"

# Test 13: Invalid session ID format (should return 400)
echo ""
echo "--- Test 13: Invalid session ID format - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/invalid-uuid/questions"

# Test 14: Session with no questions (should return empty data array)
echo ""
echo "--- Test 14: Session with no questions (Advanced PostgreSQL) - should return empty list ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/8b9c0d1e-2f3a-4b5c-6d7e-8f9a0b1c2d3e/questions"

# Test 15: Sort by createdAt (most recent first)
echo ""
echo "--- Test 15: Sort by createdAt (explicit) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?sort=createdAt"

# Test 16: Invalid sort value (should fallback to default createdAt)
echo ""
echo "--- Test 16: Invalid sort value 'invalid' - should fallback to default ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?sort=invalid"

# Test 17: POST method (should fail - 403 or 405)
echo ""
echo "--- Test 17: POST method (should return 403 or 405) ---"
curlie -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions/${SESSION_ID}/questions"

# Test 18: PUT method (should fail - 403 or 405)
echo ""
echo "--- Test 18: PUT method (should return 403 or 405) ---"
curlie -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions/${SESSION_ID}/questions"

# Test 19: DELETE method (should fail - 403 or 405)
echo ""
echo "--- Test 19: DELETE method (should return 403 or 405) ---"
curlie -X DELETE -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions/${SESSION_ID}/questions"

# Test 20: Session from a different event (Web Development Basics - 2 questions)
echo ""
echo "--- Test 20: Web Development Basics session (should return 2 questions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/4d5e6f7a-8b9c-0d1e-2f3a-4b5c6d7e8f9a/questions"

# Test 21: Verify pagination meta in response (page=2, size=3 of 4 total)
echo ""
echo "--- Test 21: Pagination meta (page=2, size=3, expect 1 item) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=2&size=3"

# Test 22: Very large size (should return all questions)
echo ""
echo "--- Test 22: Very large size (should return all 4 questions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?page=1&size=9999"

# Test 23: Verify upvote counts in upvote sort (4, 2, 3, 1 upvotes)
echo ""
echo "--- Test 23: Upvote sort - verify order (4 upvotes first, then 3, then 2, then 1) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" GET "http://localhost:8080/sessions/${SESSION_ID}/questions?sort=upvote"

echo ""
echo "===================="
echo "All tests completed!"
