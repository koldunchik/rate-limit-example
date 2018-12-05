#!/bin/bash

source http_operations.sh

url=http://localhost:8080/test

check_http_code 200 50
check_http_code 502 10
printf "TEST 1 PASSED\n"

sleep 60


check_http_code 200 25
sleep 45
check_http_code 200 25

check_http_code 502 10

sleep 20

check_http_code 200 50

check_http_code 502 10

printf "TEST 2 PASSED\n"