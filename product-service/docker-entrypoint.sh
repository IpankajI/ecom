#!/bin/bash

# Enhanced Java/Spring Boot development watcher with:
# 1. Cleaner output
# 2. Compilation cooldown
# 3. Spring Boot DevTools integration
# 4. Process cleanup handling

# Configuration
WATCH_DIR="/app/src/main/"
COMPILE_COOLDOWN=2 # seconds between compilations
MAVEN_OPTS="-o -DskipTests -q"

# Kill background processes on script exit
trap "pkill -P $$" EXIT

# Watcher loop (silent and with cooldown)
(
  while inotifywait -qr -e modify --exclude='.*\.swp|.*~' "$WATCH_DIR"; do
    if ! [[ -f pom.xml ]]; then
      echo "Error: No pom.xml found in current directory"
      exit 1
    fi
    
    echo -n "Detected changes... Compiling... "
    if mvn compile $MAVEN_OPTS > /dev/null; then
      echo "Done!"
    else
      echo "Failed!"
    fi
    
    sleep $COMPILE_COOLDOWN # Prevent rapid successive builds
  done
) >/dev/null 2>&1 &

# Main Spring Boot application with DevTools
echo "Starting Spring Boot application..."
mvn spring-boot:run -Dspring-boot.run.profiles=dev