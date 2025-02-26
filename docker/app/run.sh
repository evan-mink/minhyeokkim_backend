#!/bin/sh
# Waiting for db
until nc -z remittance_db 3306; do
  echo "Waiting for MySQL to be ready..."
  sleep 2
done

echo "MySQL is ready, starting the application..."

# Run application
exec java -jar /app.jar