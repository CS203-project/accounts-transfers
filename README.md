# accounts-transfers

### API Documentation
* [POST /accounts](https://github.com/CS203-project/accounts-transfers#post-accounts)
* [POST /accounts/{account_id}/transactions](https://github.com/CS203-project/accounts-transfers#post-accountsaccount_idtransactions)
* [GET /accounts](https://github.com/CS203-project/accounts-transfers#get-accounts)
* [GET /accounts/{account_id}](https://github.com/CS203-project/accounts-transfers#get-accountsaccount_id)
* [GET /accounts/{account_id}/transactions](https://github.com/CS203-project/accounts-transfers#get-accountsaccount_idtransactions)

#### POST /accounts
```
// HEADER Authentication: "Basic <token>"
// ROLE_MANAGER - create account

{
  "customer_id": 1234,
  "balance": 50000.0,
  "available_balance": 10000.0
}
```
#### POST /accounts/{account_id}/transactions
```
// ROLE_USER - make transfers

{
  "from": 12345,
  "to": 12346,
  "amount": 5000.0
}
```
#### GET /accounts
```
// ROLE_USER - view own accounts

{
  {
    "id": 1,
    "customer_id": 1234,
    "balance": 50000.0,
    "available_balance": 10000.0
  },
  {
    "id": 2,
    "customer_id": 1234,
    "balance": 200.0,
    "available_balance": 100.0
  }
}
```
#### GET /accounts/{account_id}
```
{
  "id": 1,
  "customer_id": 1234,
  "balance": 50000.0,
  "available_balance": 10000.0
}
```
#### GET /accounts/{account_id}/transactions
```
{
  {
    "id": 1,
    "from": 12345,
    "to": 12346,
    "amount": 5000.0
  },
  {
    "id": 2,
    "from": 12345,
    "to": 12346,
    "amount": 20.0
  }
}
```
