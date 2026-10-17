# JWT key rotation

The application signs tokens with the private key identified by `JWT_KEY_ID`.
It verifies tokens with every public key listed in `JWT_PUBLIC_KEY_LOCATIONS`.
Private keys must never be committed or included in the container image.

## Generate a key pair

```shell
umask 077
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:3072 -out jwt-private.pem
openssl pkey -in jwt-private.pem -pubout -out jwt-public.pem
chmod 600 jwt-private.pem
```

Mount the files read-only and configure the current key:

```properties
JWT_PRIVATE_KEY_LOCATION=file:/run/secrets/jwt-private.pem
JWT_PUBLIC_KEY_LOCATIONS=file:/run/secrets/jwt-public.pem
JWT_PUBLIC_KEY_IDS=restful-key-2026-01
JWT_KEY_ID=restful-key-2026-01
```

## Rotate without immediately invalidating tokens

1. Generate a new key pair with a new key ID.
2. Mount the new private/public key and retain the previous public key.
3. Put the new key first and set `JWT_KEY_ID` to the new ID.
4. Deploy and verify that newly issued JWT headers contain the new `kid`.
5. Keep the previous public key for at least the maximum Refresh Token lifetime.
6. Remove the previous public key and its ID after all old tokens have expired.

Example during the overlap period:

```properties
JWT_PRIVATE_KEY_LOCATION=file:/run/secrets/jwt-private-2026-02.pem
JWT_PUBLIC_KEY_LOCATIONS=file:/run/secrets/jwt-public-2026-02.pem,file:/run/secrets/jwt-public-2026-01.pem
JWT_PUBLIC_KEY_IDS=restful-key-2026-02,restful-key-2026-01
JWT_KEY_ID=restful-key-2026-02
```

The positions in `JWT_PUBLIC_KEY_LOCATIONS` and `JWT_PUBLIC_KEY_IDS` must match.
