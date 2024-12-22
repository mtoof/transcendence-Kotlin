#!/bin/bash

seq 1000 10000 | xargs -I {} -P 9000 bash -c 'curl -X POST "http://127.0.0.1:8080/user" -H "Content-Type: application/json" -d "{\"username\": \"mmm_{}\", \"password\": \"123abc\", \"email\": \"mmm_{}@gmail.com\", \"avatar\": \"\"}" -w "\nRequest {} - Time taken: %{time_total}s\n"; sleep 2'
