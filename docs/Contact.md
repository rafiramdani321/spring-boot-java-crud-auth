# Contact API Spec

## Create Contact

Endpoint : POST /api/contacts

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "firstName" : "Test",
  "lastName" : "User",
  "email" : "test@example.com",
  "phone" : "08947367546"
}
```

Response Body (Success, 201) :

```json
{
  "data" : {
    "id" : "8733ehbgehrerg23",
    "firstName" : "Test",
    "lastName" : "User",
    "email" : "test@example.com",
    "phone" : "08947367546"
  },
  "message": "Create Contact Success"
}
```

Response Body (Failed) :

```json
{
  "message": "Validation Failed",
  "errors": {
    "firstName": [
      "Firstname is required", "..."
    ]
  }
}
```

## Update Contact

Endpoint : PUT /api/contacts/{idContact}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "firstName" : "Test",
  "lastName" : "User",
  "email" : "test@example.com",
  "phone" : "08947367546"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "firstName" : "Test",
    "lastName" : "User",
    "email" : "test@example.com",
    "phone" : "08947367546"
  }
}
```

Response Body (Failed) :

```json
{
  "errors" : "Email format invalid, phone format invalid, ...."
}
```

## Get Contact

Endpoint : GET /api/contacts/{idContact}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "firstName" : "Test",
    "lastName" : "User",
    "email" : "test@example.com",
    "phone" : "08947367546"
  }
}
```

Response Body (Failed) : 

```json
{
  "errors" : "Contact not found, ...."
}
```

## Search Contact

Endpoint : GET /api/contacts

Query Params :

- name : String, contact first name or last name, using like query. (optional)
- phone : String, contact phone, using like query. (optional)
- email : String, contact email, using like query. (optional)
- page : Integer, start from 0, default 0
- size : Integer, default 10

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data": [
    {
      "firstName" : "Test 1",
      "lastName" : "User 1",
      "email" : "test@example1.com",
      "phone" : "08947367545"
    },
    {
      "firstName" : "Test 2",
      "lastName" : "User 2",
      "email" : "test@example2.com",
      "phone" : "08947367545657"
    }
  ],
  "currentPage" : 0,
  "totalPage" : 10,
  "size" : 10
}
```

Response Body (Failed) :

```json
{
  "errors" : "Unauthorized"
}
```

## Remove Contact

Endpoint : DELETE /api/contacts/{idContact}

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors" : "Contact not found, ...."
}
```

