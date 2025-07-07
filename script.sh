#!/bin/bash

# A simple while loop that prints a message every second
while true; do
  echo "This loop is running in the background."
  sleep 1
done &

echo "The while loop has been sent to the background."
# You can now execute other commands in the foreground