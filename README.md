# Lutover
## Overview
A RESTful API for money transfer between accounts. The API can be invoked by multiple systems and services on behalf of end users.

Assumptions to make when using the API:
* An account has a fixed currency denomination.
* Transfers between two accounts with different currencies will be valued at the exchange rate between those currencies. 
* Transfer requests should be unique, ensured by an idempotence identifier.

## Running the app
The REST service is implemented in Java, utilizing [Spark](http://sparkjava.com/). The service can be started by executing the *jar* file built as the output of the **lutover-app** Maven artifact.

The app will be hosted on port **8090** by default, and can be reached at:
```
localhost:8090
```

### Requirements
The application requires **Java 10+** runtime to run.

For building with Maven, version 3+ is required.
To install the project to the local Maven repository run:
```
mvn install
```

## API reference
### Create transfer
Initiating a new transfer to be processed. After a quote is allocated to the transfer, a transaction will be created and sent for processing.

For each new transfer request, a unique idempotence identifier must be provided is the _x-idempotence-id_ header.

``/api/v1/transfer`` **POST**

####Example request
Headers:

```
x-idempotence-id: 987654321
Content-Type: application/json; version=1
```
```
{
  "sourceAccountId": "RV10000102",
  "targetAccountId": "RV10000101",
  "amount": 2.00
}
```

####Example response
```
{
    "reference": "c365509f-091a-4fe7-a047-87a21ac4ba49",
    "createdAt": 1557260400114,
    "debitAmount": 2,
    "recipientAmount": 0.54,
    "fxRate": 0.272294,
    "status": "CREATED"
}
```

### Get Transfer
Retrieving a transaction resulting from a transfer initiated. The transaction should be referenced by its uinque reference.

``/api/v1/transfer/{reference}`` **GET**

#### Example response
```
{
    "reference": "c365509f-091a-4fe7-a047-87a21ac4ba49",
    "createdAt": 1557259200000,
    "debitAmount": 2,
    "recipientAmount": 0.54,
    "fxRate": 0.272294,
    "status": "PROCESSED"
}
```