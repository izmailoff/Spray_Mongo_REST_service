#!/bin/bash

wrk --latency -t4 -c100 -d60 http://127.0.0.1:8080/tweets

