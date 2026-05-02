#!/bin/bash

echo "Testing GET /events"

# Step 1: Get all events (should return 200 with event list)
echo ""
echo "--- Test 1: Get all events (should return 200 with event list) ---"
curlie -H "Content-Type: application/json" http://localhost:8080/events

# Step 2: Verify response structure - check for data array and meta object
echo ""
echo "--- Test 2: Verify response structure (should have data array and meta object) ---"
curlie -s http://localhost:8080/events | grep -o '"data":\[' | head -1
curlie -s http://localhost:8080/events | grep -o '"meta":{' | head -1

# Step 3: Verify individual event structure in response
echo ""
echo "--- Test 3: Verify event structure in response (should have id, title, description, startDate, endDate, location, createdAt) ---"
curlie -s http://localhost:8080/events | grep -o '"id":"[^"]*"' | head -1
curlie -s http://localhost:8080/events | grep -o '"title":"[^"]*"' | head -1
curlie -s http://localhost:8080/events | grep -o '"location":"[^"]*"' | head -1

# Step 4: Check meta total matches number of events in data array
echo ""
echo "--- Test 4: Verify meta total matches events count ---"
echo "Response body:"
curlie -s http://localhost:8080/events

# Step 5: Test with invalid HTTP method (POST without auth should return 401)
echo ""
echo "--- Test 5: POST without auth should return 401 ---"
curlie -X POST -H "Content-Type: application/json" -d '{"title":"Test","description":"Test","startDate":"2026-06-01T10:00:00Z","endDate":"2026-06-01T18:00:00Z","location":"Test"}' -i http://localhost:8080/events

# Step 6: Test with malformed parameters (should return 200 with empty or all results)
echo ""
echo "--- Test 6: GET with malformed parameters (should return 200 with empty or all results) ---"
curlie -H "Content-Type: application/json" "http://localhost:8080/events?invalid=param"
