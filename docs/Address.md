# Address API Spec

## Create Address

Endpoint : POST /api/contacts/{idContact}/Addresses

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street" : "Street",
  "city" : "Jakarta",
  "Province" : "DKI",
  "country" : "Indonesia",
  "postalCode" : "1233"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "12321321",
    "street" : "Street",
    "city" : "Jakarta",
    "Province" : "DKI",
    "country" : "Indonesia",
    "postalCode" : "1233"
  }
}
```

Response body (Failed) :

```json
{
  "errors" : "Contact is not found"
}
```

## Update Address

Endpoint : PUT /api/contacts/{idContact}/addresses/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "street" : "Street",
  "city" : "Jakarta",
  "Province" : "DKI",
  "country" : "Indonesia",
  "postalCode" : "1233"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "id" : "12321321",
    "street" : "Street",
    "city" : "Jakarta",
    "Province" : "DKI",
    "country" : "Indonesia",
    "postalCode" : "1233"
  }
}
```

Response body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```

## Get Address

Endpoint : GET /api/contacts/{idContact}/addresses/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "id" : "12321321",
    "street" : "Street",
    "city" : "Jakarta",
    "Province" : "DKI",
    "country" : "Indonesia",
    "postalCode" : "1233"
  }
}
```

Response body (Failed) :

```json
{
  "errors" : "Address is not found"
}
```

## Remove Address

Endpoint : DELETE /api/contacts/{idContact}/addresses/{idAddress}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response body (Failed) :

```json
{
  "errors" : "Address not found"
}
```

## List Address

Endpoint : GET /api/contacts/{idContact}/addresses

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : [
    {
      "id" : "12321321",
      "street" : "Street",
      "city" : "Jakarta",
      "Province" : "DKI",
      "country" : "Indonesia",
      "postalCode" : "1233"
    },
    {
      "id" : "12321321",
      "street" : "Street",
      "city" : "Jakarta",
      "Province" : "DKI",
      "country" : "Indonesia",
      "postalCode" : "1233"
    }
  ]
}
```

Response body (Failed) :

```json
{
  "errors" : "Address not found"
}
```