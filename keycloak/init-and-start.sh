#!/bin/bash

echo "Starting realm import..."
# Import realm with override
/opt/keycloak/bin/kc.sh import --dir /opt/keycloak/data/import --override true

echo "Starting Keycloak..."
# Start Keycloak in dev mode
exec /opt/keycloak/bin/kc.sh start-dev --hostname-strict=false
