package com.bismarck.keycloak.user.provider;

import com.bismarck.keycloak.user.provider.model.GuppUser;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

/**
 * UserAdapter for the {@link com.bismarck.keycloak.gupp.ProviderConstants#USER_PROVIDER_ID}
 * Sets and gets user related values with the help of the {@link GuppUser} model
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class GuppUserAdapter extends AbstractUserAdapterFederatedStorage {
    protected GuppUser entity;
    protected String keycloakId;

    public GuppUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, GuppUser entity) {
        super(session, realm, model);
        this.entity = entity;
        keycloakId = StorageId.keycloakId(model, entity.getId());
    }

    public String getPassword() {
        return entity.getPassword();
    }

    public void setPassword(String password) {
        entity.setPassword(password);
    }

    public void setTenant(String tenant) {
        entity.setTenant(tenant);
    }

    public String getTenant() {
        return entity.getTenant();
    }

    @Override
    public String getUsername() {
        return entity.getUsername();
    }

    @Override
    public void setUsername(String username) {
        entity.setUsername(username);
    }

    @Override
    public void setEmail(String email) {
        entity.setEmail(email);
    }

    @Override
    public String getEmail() {
        return entity.getEmail();
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public void setFirstName(String firstName) {
        entity.setFirstName(firstName);
    }

    @Override
    public String getFirstName() {
        return entity.getFirstName();
    }

    @Override
    public void setLastName(String lastName) {
        entity.setLastName(lastName);
    }

    @Override
    public String getLastName() {
        return entity.getLastName();
    }
}
