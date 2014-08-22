#!/bin/bash

curl -X POST -H "Content-Type: application/json" -d '{ "username" : "test", "fullName" : "Aleksey", "password": "mypass", "email" : "alex@example.com" }' localhost:8080/users -v

