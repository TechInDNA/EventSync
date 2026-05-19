#!/bin/bash

echo "Testing GET /rooms"
echo "=================="

# Test 1: Get all rooms (should return 200 with room list)
echo ""
echo "--- Test 1: Get all rooms (default pagination) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/rooms

# Test 2: Get rooms with custom page and size
echo ""
echo "--- Test 2: Get rooms with page=2&size=5 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=2&size=5"

# Test 3: Get rooms with page=1&size=1 (first room only)
echo ""
echo "--- Test 3: Get rooms with page=1&size=1 (first room only) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=1"

# Test 4: Test with invalid page (negative)
echo ""
echo "--- Test 4: Invalid page (negative) - should return 400 Bad request error ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=-1&size=10"

# Test 5: Test with invalid size (zero)
echo ""
echo "--- Test 5: Invalid size (zero) - should return 400 Bad request error ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=0"

# Test 6: Test with invalid size (negative)
echo ""
echo "--- Test 6: Invalid size (negative) - should return 400 Bad request error ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=-5"

# Test 7: Test with very large page (beyond total)
echo ""
echo "--- Test 7: Page beyond total (should return empty data array) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=9999&size=10"

# Test 8: Test with very large size
echo ""
echo "--- Test 8: Very large size (should return all rooms) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=9999"

# Test 9: Test with non-numeric page parameter
echo ""
echo "--- Test 9: Non-numeric page parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=abc&size=10"

# Test 10: Test with non-numeric size parameter
echo ""
echo "--- Test 10: Non-numeric size parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=abc"

# Test 11: Test with additional unknown parameters (should return 400)
echo ""
echo "--- Test 11: Unknown parameters (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=10&unknown=param"

# Test 12: Test with POST method (should fail - 401)
echo ""
echo "--- Test 12: POST method (should return 401) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/rooms

# Test 13: Test with PUT method (should fail - 401)
echo ""
echo "--- Test 13: PUT method on collection (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/rooms

# Test 14: Test with DELETE method (should fail - 401)
echo ""
echo "--- Test 14: DELETE method on collection (should return 401) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/rooms

# Test 15: Test with page=0 (should return 400 or default to 1)
echo ""
echo "--- Test 15: Page 0 (should return 400 Bad request) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=0&size=10"

# Test 16: Test with size=1 (minimum valid size)
echo ""
echo "--- Test 16: Minimum valid size (size=1) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=1"

# Test 17: Test with both parameters as floats (should fail validation)
echo ""
echo "--- Test 17: Float page parameter - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1.5&size=10"

# Test 18: Test with special characters in page parameter
echo ""
echo "--- Test 18: Special characters in page param - should return 400 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/rooms?page=1&size=10&page=2"


echo ""
echo "=================="
echo "All tests completed!"
