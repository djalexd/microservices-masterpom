#!/bin/bash

# Print out env!
env

# Concatenate all parameters that were received when running this
# container as runtime parameters to java process.
java -jar sensor-config.jar