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

# Test 12: Search by first name (John)
echo ""
echo "--- Test 12: Search by first name 'John' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search=John"

# Test 13: Search by last name (Smith)
echo ""
echo "--- Test 13: Search by last name 'Smith' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search=Smith"

# Test 14: Search with partial match (Jo matches John Doe and Bob Johnson)
echo ""
echo "--- Test 14: Search with partial match 'Jo' (should match John Doe and Bob Johnson) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search=Jo"

# Test 15: Case-insensitive search (john lowercase)
echo ""
echo "--- Test 15: Case-insensitive search 'john' (lowercase) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search=john"

# Test 16: Search with no results
echo ""
echo "--- Test 16: Search with no results 'Zzzz' ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search=Zzzz"

# Test 17: Search with pagination
echo ""
echo "--- Test 17: Search 'Williams' with size=1 ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search=Williams&page=1&size=1"

# Test 18: Search with invalid characters (should return 400)
echo ""
echo "--- Test 18: Search with invalid characters (should return 400) ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search=John@Doe"

# Test 19: Search with empty string (should behave like no search)
echo ""
echo "--- Test 19: Search with empty string ---"
curlie -k -s -w "\nHTTP Status: %{http_code}\n" "https://localhost:443/speakers?search="

# Test 20: POST method (should fail - 401)
echo ""
echo "--- Test 20: POST method (should return 401) ---"
curlie -k -X POST -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/speakers

# Test 21: PUT method (should fail - 401)
echo ""
echo "--- Test 21: PUT method on collection (should return 401) ---"
curlie -k -X PUT -H "Content-Type: application/json" -d '{}' -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/speakers

# Test 22: DELETE method (should fail - 401)
echo ""
echo "--- Test 22: DELETE method on collection (should return 401) ---"
curlie -k -X DELETE -s -w "\nHTTP Status: %{http_code}\n" https://localhost:443/speakers


echo ""
echo "====================="
echo "All tests completed!"
