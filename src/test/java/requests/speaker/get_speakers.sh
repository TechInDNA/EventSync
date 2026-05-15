#!/bin/bash

echo "Testing GET /speakers"
echo "====================="

# Test 1: Get all speakers (should return 200 with speaker list)
echo ""
echo "--- Test 1: Get all speakers (default pagination) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/speakers

# Test 2: Get speakers with custom page and size
echo ""
echo "--- Test 2: Get speakers with page=2&size=5 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=2&size=5"

# Test 3: Get speakers with page=1&size=1 (first speaker only)
echo ""
echo "--- Test 3: Get speakers with page=1&size=1 (first speaker only) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=1&size=1"

# Test 4: Test with invalid page (negative)
echo ""
echo "--- Test 4: Invalid page (negative) - should return 400 Bad request error ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=-1&size=10"

# Test 5: Test with invalid size (zero)
echo ""
echo "--- Test 5: Invalid size (zero) - should return 400 Bad request error ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=1&size=0"

# Test 6: Test with invalid size (negative)
echo ""
echo "--- Test 6: Invalid size (negative) - should return 400 Bad request error ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=1&size=-5"

# Test 7: Test with very large page (beyond total)
echo ""
echo "--- Test 7: Page beyond total (should return empty data array) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=9999&size=10"

# Test 8: Test with very large size
echo ""
echo "--- Test 8: Very large size (should return all speakers) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=1&size=9999"

# Test 9: Test with non-numeric page parameter
echo ""
echo "--- Test 9: Non-numeric page parameter - should return 400 or 200 with default ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=abc&size=10"

# Test 10: Test with non-numeric size parameter
echo ""
echo "--- Test 10: Non-numeric size parameter - should return 400 or 200 with default ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=1&size=abc"

# Test 11: Test with additional unknown parameters (should be ignored)
echo ""
echo "--- Test 11: Unknown parameters (should be ignored) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?page=1&size=10&unknown=param"

# Test 12: Test with POST method (should fail - 403)
echo ""
echo "--- Test 12: POST method (should return 403) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/speakers

# Test 13: Test with PUT method (should fail - 403)
echo ""
echo "--- Test 13: PUT method on collection (should return 403) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/speakers

# Test 14: Test with DELETE method (should fail - 403)
echo ""
echo "--- Test 14: DELETE method on collection (should return 403) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/speakers


echo ""
echo "====================="
echo "All tests completed!"
