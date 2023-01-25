# Billing PreProcessor

## Leverantör

Sundsvalls kommun

## Beskrivning
BillingPreProcessor är en tjänst som ansvarar för att lagra skapade, attesterade, fakturerade samt avslagna fakturarader. 
Från informationen avseende fakturarader och kontering kommer tjänsten vid givna intervall att generera en fil som skickas 
till systemet Raindance för fakturering och bokföring.

## Tekniska detaljer

### Starta tjänsten

|Miljövariabel|Beskrivning|
|---|---|
|**Databasinställningar**||
|`spring.datasource.url`|JDBC-URL för anslutning till databas|
|`spring.datasource.username`|Användarnamn för anslutning till databas|
|`spring.datasource.password`|Lösenord för anslutning till databas|
|`spring.jpa.properties.javax.persistence.schema-generation.database.action`|Action är default none, men bör ändras till önskat värde (tex update eller verify)|
|`spring.flyway.enabled`|Flyway är avslagen default, men kan slås på ifall versionshantering via Flyway önskas|
|**Integration mot Party**|
|`integration.party.url`|URL för endpoint till Party service i WSO2|
|`spring.security.oauth2.client.registration.party.client-id`|Klient-ID som ska användas mot Party service|
|`spring.security.oauth2.client.registration.party.client-secret`|Klient-secret som ska användas mot Party service|
|`spring.security.oauth2.client.provider.party.token-uri`|URI till endpoint för att förnya token mot Party service|

### Paketera och starta tjänsten
Applikationen kan paketeras genom:

```
./mvnw package
```
Kommandot skapar filen `api-service-billing-preprocessor-<version>.jar` i katalogen `target`. Tjänsten kan nu köras genom kommandot `java -jar target/api-service-billing-preprocessor-<version>.jar`. Observera att en lokal databas måste finnas startad för att tjänsten ska fungera.

### Bygga och starta med Docker
Exekvera följande kommando för att bygga en Docker-image:

```
docker build -f src/main/docker/Dockerfile -t api.sundsvall.se/ms-billing-preprocessor:latest .
```

Exekvera följande kommando för att starta samma Docker-image i en container:

```
docker run -i --rm -p8080:8080 api.sundsvall.se/ms-billing-preprocessor

```

#### Kör applikationen lokalt

Exekvera följande kommando för att bygga och starta en container i sandbox mode:  

```
docker-compose -f src/main/docker/docker-compose-sandbox.yaml build && docker-compose -f src/main/docker/docker-compose-sandbox.yaml up
```


## 
Copyright (c) 2022 Sundsvalls kommun