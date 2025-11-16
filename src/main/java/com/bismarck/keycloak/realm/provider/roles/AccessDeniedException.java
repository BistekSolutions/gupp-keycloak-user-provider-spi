package com.bismarck.keycloak.realm.provider.roles;

public class AccessDeniedException extends Exception {
    public AccessDeniedException(String message) {
        super(message);
    }
}
