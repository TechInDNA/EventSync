#!/bin/bash

SESSION_ID="a34e0e08-9c8d-4a86-aa87-dd1e3a385bbb"

echo "Testing GET /sessions/{id}/questions"
echo "===================================="

# Test 1: Get questions default params (sort=creationDate, page=1, size=5)
echo ""
echo "--- Test 1: Get questions (default pagination & sort) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions"

# Test 2: Get questions sorted by upvotes (most upvoted first)
echo ""
echo "--- Test 2: Sort by upvotes (most upvoted first) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?sort=upvotes"

# Test 3: Sort by creationDate explicitly
echo ""
echo "--- Test 3: Sort by creationDate explicitly ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?sort=creationDate"

# Test 4: Custom page and size (page=2&size=2 → questions 3-4)
echo ""
echo "--- Test 4: Custom page=2&size=2 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=2&size=2"

# Test 5: Page=1&size=1 (first question only)
echo ""
echo "--- Test 5: Page=1&size=1 (first question only) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=1&size=1"

# Test 6: Invalid page (negative) — should return 400
echo ""
echo "--- Test 6: Invalid page (negative) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=-1&size=10"

# Test 7: Invalid size (zero) — should return 400
echo ""
echo "--- Test 7: Invalid size (zero) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=1&size=0"

# Test 8: Invalid size (negative) — should return 400
echo ""
echo "--- Test 8: Invalid size (negative) - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=1&size=-5"

# Test 9: Page beyond total — should return empty data array
echo ""
echo "--- Test 9: Page beyond total (should return empty data array) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=9999&size=10"

# Test 10: Very large size — should return all 6 questions
echo ""
echo "--- Test 10: Very large size (should return all questions) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=1&size=9999"

# Test 11: Non-numeric page — should return 400
echo ""
echo "--- Test 11: Non-numeric page parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=abc&size=10"

# Test 12: Non-numeric size — should return 400
echo ""
echo "--- Test 12: Non-numeric size parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=1&size=abc"

# Test 13: Unknown parameters (should be ignored)
echo ""
echo "--- Test 13: Unknown parameters (should be ignored) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=1&size=10&unknown=param"

# Test 14: Non-existent session UUID — should return 404
echo ""
echo "--- Test 14: Non-existent session UUID - should return 404 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/26685214-58cb-4e5e-bf7a-1712cca2825d/questions"

# Test 15: Invalid session UUID format — should return 400
echo ""
echo "--- Test 15: Invalid UUID format - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/not-a-uuid/questions"

# Test 16: Invalid sort value 'invalid' - should return 400
echo ""
echo "--- Test 17: Invalid sort value 'invalid' - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?sort=invalid"

# Test 18: POST method — should return 403 or 405
echo ""
echo "--- Test 18: POST method (should return 403 or 405) ---"
curlie -k -s -X POST -H "Content-Type: application/json" -d '{}' -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions"

# Test 19: PUT method — should return 403 or 405
echo ""
echo "--- Test 19: PUT method (should return 403 or 405) ---"
curlie -k -s -X PUT -H "Content-Type: application/json" -d '{}' -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions"

# Test 20: DELETE method — should return 403 or 405
echo ""
echo "--- Test 20: DELETE method (should return 403 or 405) ---"
curlie -k -s -X DELETE -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions"

# Test 21: Verify upvote sort order (ddf75918:3 upvotes first, then 2110b88d:2, 403dea58:1, 20919e99:1)
echo ""
echo "--- Test 21: Verify upvote order (3, then 2, then 1, then 1, then 0, then 0) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?sort=upvotes&size=6"

# Test 22: Verify creationDate sort order (oldest first by default: ddf75918 → 20919e99 → ...)
echo ""
echo "--- Test 22: Verify creationDate order (oldest first) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?sort=creationDate&size=6"

# Test 23: Anonymous question — participant should be null
echo ""
echo "--- Test 23: Anonymous question (participant should be null) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/sessions/${SESSION_ID}/questions?page=1&size=6"

echo ""
echo "===================="
echo "All tests completed!"
