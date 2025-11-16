package com.bismarck.keycloak.realm.provider.user;

import com.bismarck.keycloak.user.provider.config.ConfigPropertyRetriever;
import com.bismarck.keycloak.user.provider.model.GuppUser;
import com.bismarck.keycloak.user.provider.GuppUserAdapter;
import jakarta.persistence.EntityManager;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.StorageId;

import static com.bismarck.keycloak.user.provider.GuppUserStorageProviderFactory.getEntityManagerFactory;

/**
 * User provider, that exposes {@link #getUserById(String)} and {@link #getUserByInternalId(String)}
 * like the {@link com.bismarck.keycloak.user.provider.GuppUserStorageProvider}, but with its own session context
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class RestUserProvider {

    private final KeycloakSession session;
    private final RealmModel realm;
    private final EntityManager em;
    private final Logger logger = Logger.getLogger(RestUserProvider.class);

    public RestUserProvider(KeycloakSession session, RealmModel realm) {
        this.session = session;
        this.realm = realm;
        this.em = getEntityManagerFactory(getComponentModel()).createEntityManager();
    }

    public UserModel getUserById(String id) {
        logger.debug("GuppUserProvider:getUserById: " + id);
        String persistenceId = StorageId.externalId(id);
        GuppUser entity = em.find(GuppUser.class, persistenceId);
        if (entity == null) {
            logger.info("could not find user by id: " + id);
            return null;
        }
        return new GuppUserAdapter(session, realm, getComponentModel(), entity);
    }

    public UserModel getUserByInternalId(String id) {
        logger.debug("GuppUserProvider:getUserByInternalId: " + id);
        GuppUser entity = em.find(GuppUser.class, id);
        if (entity == null) {
            logger.info("could not find user by id: " + id);
            return null;
        }
        return new GuppUserAdapter(session, realm, getComponentModel(), entity);
    }

    public void setTenantForUser(String id, String tenant) {
        logger.debug("GuppUserProvider:setTenantForInternalUser: " + id);
        GuppUser entity = em.find(GuppUser.class, StorageId.externalId(id));
        if (entity == null) {
            logger.info("could not find user by id: " + id);
            return;
        }
        var adapter = new GuppUserAdapter(session, realm, getComponentModel(), entity);
        adapter.setTenant(tenant);
    }

    public String getUserTenant(String id) {
        logger.debug("GuppUserProvider:setTenantForInternalUser: " + id);
        GuppUser entity = em.find(GuppUser.class, StorageId.externalId(id));
        if (entity == null) {
            logger.info("could not find user by id: " + id);
            return null;
        }
        var adapter = new GuppUserAdapter(session, realm, getComponentModel(), entity);
        return adapter.getTenant();
    }

    private ComponentModel getComponentModel() {
        return ConfigPropertyRetriever.getComponentModel(realm);
    }
}
