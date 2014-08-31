#!/bin/bash

# A script that helps to create Tweets.
#
# You can either run it without any args to get
# randomly generated string of a random length as tweet text
# or pass any text as an argument.
# Additionally all other arguments except the first one will be
# passed to curl.
#
# Examples:
#
## 1) create a random tweet:
# ./createTweet.sh
#
## 2) create a tweet with specified text:
# ./createTweet.sh "my text here"
#
## 3) create a tweet with specified text and print debugging info from curl:
# ./createTweet.sh "some text" -v
#

URL="localhost:8080/tweets"
tweetText="$1"
tweetLength=$(( ( RANDOM % 100 )  + 2 ))

if [ -z "$tweetText" ]; then
  tweetText=$(tr -cd '[:alnum:]' < /dev/urandom | fold -w"$tweetLength" | head -n1)
else
  shift
fi;

curl \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic dGVzdDpteXBhc3M=" \
  -d "{ \"text\" : \"$tweetText\" }" \
  "$URL" \
  "$@"

