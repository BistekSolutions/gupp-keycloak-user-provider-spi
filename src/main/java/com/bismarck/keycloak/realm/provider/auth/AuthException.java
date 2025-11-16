package com.bismarck.keycloak.realm.provider.auth;

/**
 * Thrown, if the user could not be authenticated via the {@link AuthManager}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}
