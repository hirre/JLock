# JLock - A Locking Service for Distributed Systems
JLock is a non-blocking asynchronous locking service for distributed systems.

![image](https://github.com/user-attachments/assets/e88d1e95-c15e-4be1-a2b2-b5b36a367a42)


- **lockName**: the name of the lock ("default" if omitted)
- **lockHolderId**: unique ID for each client


## Example:

<ins>**Request:**</ins>

**POST api/locks/lock**

```
{
  "lockName": "my-lock",
  "lockHolderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

<ins>**Responses:**</ins>

201 (state change occured by request):
```
{
  "lockName": "my-lock",
  "lockState": "ACQUIRED",
  "createdAt": "2025-04-01T12:04:33.485Z",
  "updatedAt": "2025-04-01T12:04:33.485Z",
  "expiresAt": "2025-04-01T12:14:33.485Z"
}
```

200:
```
{
  "lockName": "my-lock",
  "lockState": "WAIT",                     // Or ACQUIRED if lockHolderId in request matches the lock in the storage
  "createdAt": "2025-04-01T12:04:33.485Z",
  "updatedAt": "2025-04-01T12:04:33.485Z",
  "expiresAt": "2025-04-01T12:14:33.485Z"
}
```

400:

Bad request

---

<ins>**Request:**</ins>

**POST api/locks/unlock**

```
{
  "lockName": "my-lock",
  "lockHolderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

<ins>**Responses:**</ins>

201 (state change occured by request):
```
{
  "lockName": "my-lock",
  "lockState": "FREE",
  "createdAt": "2025-04-01T12:04:33.485Z",
  "updatedAt": "2025-04-01T12:04:33.485Z",
  "expiresAt": "2025-04-01T12:14:33.485Z"
}
```

200:
```
{
  "lockName": "my-lock",
  "lockState": "WAIT",                      // Or FREE
  "createdAt": "2025-04-01T12:04:33.485Z",
  "updatedAt": "2025-04-01T12:04:33.485Z"
  "expiresAt": "2025-04-01T12:14:33.485Z"
}
```

400:

Bad request
