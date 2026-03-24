# User API Spec

## Register User

Endpoint : POST /api/user/register

Request Body :

```json
{
  "username" : "testuser",
  "password" : "123password",
  "name" : "test user"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "username": "testuser",
    "name": "test user"
  },
  "message": "Register success"
}
```

Response Body (Failed) :

```json
{
  "message": "Business Error",
  "code": "USER_ALREADY_EXIST",
  "errors": {
    "username": [
      "Username already registered"
    ]
  }
}
```

## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
  "username" : "nettpop",
  "password" : "123password"
}
```

Response Body (Success) :

```json
{
  "data": {
    "token": "1234",
    "expiredAt": 46575676867876
  },
  "message": "Login Success"
}
```

Response Body (Failed, 401) :

```json
{
  "message": "Business Error",
  "code": "LOGIN_FAILED",
  "errors": {
    "global": [
      "Username or password wrong"
    ]
  }
}
```

## Get User

Endpoint : GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "username" : "nettpop",
    "name" : "test user"
  },
  "message": "Fetching Get User Success"
}
```

Response Body (Failed, 401) :

```json
{
  "message": "Business Error",
  "code": "UNAUTHORIZED",
  "errors": {
    "global": [
      "Unauthorized"
    ]
  }
}
```

## Update User

Endpoint : PATCH /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "name": "update name",
  "password": "new password"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "username" : "update name",
    "name" : "update password"
  },
  "message": "Update success"
}
```

Response Body (Failed, 401) :

```json
{
  "message": "Business Error",
  "code": "UNAUTHORIZED",
  "errors": {
    "global": [
      "Unauthorized"
    ]
  }
}
```

## Logout User

Endpoint : DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "message": "Logout Success"
}
```

Response Body (Failed, 401) :

```json
{
  "message": "Business Error",
  "code": "LOGOUT_FAILED",
  "errors": {
    "global": [
      "Unauthorized"
    ]
  }
}
```