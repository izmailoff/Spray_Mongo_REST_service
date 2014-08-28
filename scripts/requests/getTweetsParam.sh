#!/bin/bash

curl 'localhost:8080/tweets/?pageSize=2&offset=1' -v | gunzip


