package com.bismarck.keycloak.realm.provider;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

/**
 * ResourceProvider for the {@link RestResource}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class RestResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public RestResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new RestResource(session);
    }

    @Override
    public void close() {
        //NOOP
    }
}
