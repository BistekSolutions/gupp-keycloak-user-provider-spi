package com.bismarck.keycloak.realm.provider;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import static com.bismarck.keycloak.gupp.ProviderConstants.REST_PROVIDER_ID;

/**
 * ProviderFactory for the {@link RestResource}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class RestResourceProviderFactory implements RealmResourceProviderFactory {

    //TODO: Als Property aus z.B Docker-Compose heraus accessible machen
    public static final String CLIENT_ID = "genealogy-backend";

    private RestResourceProvider resourceProvider;

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        if (resourceProvider == null) {
            resourceProvider = new RestResourceProvider(keycloakSession);
        }

        return resourceProvider;
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return REST_PROVIDER_ID;
    }
}
