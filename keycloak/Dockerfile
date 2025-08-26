#Older version by Piili
#FROM quay.io/keycloak/keycloak:23.0.4

#COPY realm/asteriski-realm.json /opt/keycloak/data/import/asteriski-realm.json
#COPY themes/asteriski /opt/keycloak/themes

#ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
#CMD ["start-dev", "--import-realm"]


# Alpine builder stage
FROM alpine:latest as builder
RUN apk add --no-cache dos2unix

COPY init-and-start.sh /init-and-start.sh
RUN dos2unix /init-and-start.sh && chmod +x /init-and-start.sh

# Stage 2: Final Keycloak image
FROM quay.io/keycloak/keycloak:26.3.1

# Set environment variables for dev container and use dev temp database
ENV KC_BOOTSTRAP_ADMIN_USERNAME=admin \
    KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
    #Tasta alaspain voi kommentoida KC_DB alkuiset sanat pois jos haluaa kayttaa kc omaa databasee
    #Tasta alaspain: oman tietokannan speksit jotta keycloak voi kayttaa sita
    #Muista myös poistaa yksi ylempi kenoviiva \
    KC_DB=postgres \
    KC_DB_URL_HOST=postgres \
    KC_DB_URL_PORT=5432 \
    KC_DB_URL_DATABASE=eventsignup \
    KC_DB_USERNAME=postgres \
    KC_DB_PASSWORD=1234 \
    KC_HOSTNAME=localhost

# Copy sanitized script
COPY --from=builder /init-and-start.sh /opt/keycloak/init-and-start.sh

# Copy realm and theme files
COPY realm/asteriski-realm.json /opt/keycloak/data/import/asteriski-realm.json
COPY themes/asteriski /opt/keycloak/themes/asteriski

ENTRYPOINT ["/opt/keycloak/init-and-start.sh"]
