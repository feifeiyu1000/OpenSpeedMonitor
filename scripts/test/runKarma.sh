#!/bin/bash
cd "`dirname $0`/../../"
./node_modules/karma/bin/karma start ./src/test/js/karma.conf.js
