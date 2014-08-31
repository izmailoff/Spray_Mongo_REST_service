#!/bin/bash

# A script that helps to create Users in the system.
#
# All arguments will be passed to curl.
#
# Examples:
#
## 1) create a pre-defined user:
# ./createUser.sh
#
## 2) create a pre-defined user and print extra debugging info from curl:
# ./createUser.sh -v
#

URL="localhost:8080/users"

username="test"
fullName="Aleksey"
password="mypass"
email="alex@example.com"

curl \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{ \"username\" : \"$username\",
        \"fullName\" : \"$fullName\",
        \"password\" : \"$password\",
        \"email\" : \"$email\" }" \
  "$URL" \
  "$@"


