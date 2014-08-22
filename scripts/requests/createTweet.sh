#!/bin/bash

curl -X POST -H "Content-Type: application/json" -H "Authorization: Basic dGVzdDpteXBhc3M=" -d '{ "text" : "Hello world tweet" }' localhost:8080/tweets -v

