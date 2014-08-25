#!/bin/bash

wrk -s tweet_post.lua --latency -t4 -c100 -d60 http://127.0.0.1:8080/tweets

