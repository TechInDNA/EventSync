#!/bin/bash

echo "Testing GET /sessions"
echo "====================="

# Test 1: Get all sessions (should return 200 with session list)
echo ""
echo "--- Test 1: Get all sessions (default pagination) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/sessions

# Test 2: Get sessions with custom page and size
echo ""
echo "--- Test 2: Get sessions with page=2&size=5 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=2&size=5"

# Test 3: Get sessions with page=1&size=1 (first session only)
echo ""
echo "--- Test 3: Get sessions with page=1&size=1 (first session only) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1&size=1"

# Test 4: Test with invalid page (negative)
echo ""
echo "--- Test 4: Invalid page (negative) - should return 400 Bad request error ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=-1&size=10"

# Test 5: Test with invalid size (zero)
echo ""
echo "--- Test 5: Invalid size (zero) - should return 400 Bad request error ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1&size=0"

# Test 6: Test with invalid size (negative)
echo ""
echo "--- Test 6: Invalid size (negative) - should return 400 Bad request error ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1&size=-5"

# Test 7: Test with very large page (beyond total)
echo ""
echo "--- Test 7: Page beyond total (should return empty data array) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=9999&size=10"

# Test 8: Test with very large size
echo ""
echo "--- Test 8: Very large size (should return all sessions) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1&size=9999"

# Test 9: Test with non-numeric page parameter
echo ""
echo "--- Test 9: Non-numeric page parameter - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=abc&size=10"

# Test 10: Test with non-numeric size parameter
echo ""
echo "--- Test 10: Non-numeric size parameter - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1&size=abc"

# Test 11: Test with additional unknown parameters (should be ignored)
echo ""
echo "--- Test 11: Unknown parameters (should be ignored) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1&size=10&unknown=param"

# Test 12: Test with POST method (should fail - 403)
echo ""
echo "--- Test 12: POST method (should return 403) ---"
curlie -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/sessions

# Test 13: Test with PUT method (should fail - 403)
echo ""
echo "--- Test 13: PUT method on collection (should return 403) ---"
curlie -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/sessions

# Test 14: Test with DELETE method (should fail - 403)
echo ""
echo "--- Test 14: DELETE method on collection (should return 403) ---"
curlie -X DELETE -s -w "\nHTTP Status: %{http_code}\n" http://localhost:8080/sessions

# Test 15: Test with page=0 (should return 400)
echo ""
echo "--- Test 15: Page 0 (should return 400 Bad request) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=0&size=10"

# Test 16: Test with size=1 (minimum valid size)
echo ""
echo "--- Test 16: Minimum valid size (size=1) ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1&size=1"

# Test 17: Test with float page parameter (should return 400)
echo ""
echo "--- Test 17: Float page parameter - should return 400 ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=1.5&size=10"

# Test 18: Test with empty page parameter (should use default)
echo ""
echo "--- Test 18: Empty page parameter - should use default ---"
curlie -s -w "\nHTTP Status: %{http_code}\n" "http://localhost:8080/sessions?page=&size=10"

echo ""
echo "====================="
echo "All tests completed!"
