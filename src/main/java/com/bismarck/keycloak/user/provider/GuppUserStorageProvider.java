package com.bismarck.keycloak.user.provider;

import com.bismarck.keycloak.user.provider.model.GuppUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.models.cache.OnUserCache;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.stream.Stream;

/**
 * Provider for the {@link com.bismarck.keycloak.gupp.ProviderConstants#USER_PROVIDER_ID}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class GuppUserStorageProvider implements UserStorageProvider,
        UserLookupProvider,
        UserRegistrationProvider,
        UserQueryProvider,
        CredentialInputUpdater,
        CredentialInputValidator,
        OnUserCache
{
    private static final Logger logger = Logger.getLogger(GuppUserStorageProvider.class);
    public static final String PASSWORD_CACHE_KEY = GuppUserAdapter.class.getName() + ".password";

    private final ComponentModel model;
    private final KeycloakSession session;
    private final EntityManager em;

    GuppUserStorageProvider(KeycloakSession session, ComponentModel model, EntityManager em) {
        this.session = session;
        this.model = model;
        this.em = em;
    }

    @Override
    public void preRemove(RealmModel realm) {

    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {

    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {

    }

    @Override
    public void close() {
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        logger.debug("getUserById: " + id);
        String persistenceId = StorageId.externalId(id);
        GuppUser entity = em.find(GuppUser.class, persistenceId);
        if (entity == null) {
            logger.info("could not find user by id: " + id);
            return null;
        }
        return new GuppUserAdapter(session, realm, model, entity);
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        logger.debug("getUserByUsername: " + username);
        TypedQuery<GuppUser> query = em.createNamedQuery("getUserByUsername", GuppUser.class);
        query.setParameter("username", username);
        List<GuppUser> result = query.getResultList();
        if (result.isEmpty()) {
            logger.info("could not find username: " + username);
            return null;
        }

        return new GuppUserAdapter(session, realm, model, result.get(0));
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        TypedQuery<GuppUser> query = em.createNamedQuery("getUserByEmail", GuppUser.class);
        query.setParameter("email", email);
        List<GuppUser> result = query.getResultList();
        if (result.isEmpty()) return null;
        return new GuppUserAdapter(session, realm, model, result.get(0));
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        // from documentation: "If your provider has a configuration switch to turn off
        // adding a user, returning null from this method will skip the provider and
        // call the next one."
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        String persistenceId = StorageId.externalId(user.getId());
        GuppUser entity = em.find(GuppUser.class, persistenceId);
        if (entity == null) return false;
        em.remove(entity);
        return true;
    }

    @Override
    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
        String password = ((GuppUserAdapter)delegate).getPassword();
        if (password != null) {
            user.getCachedWith().put(PASSWORD_CACHE_KEY, password);
        }
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) return false;
        UserCredentialModel cred = (UserCredentialModel)input;
        GuppUserAdapter adapter = getUserAdapter(user);
        adapter.setPassword(BCrypt.hashpw(cred.getValue(), BCrypt.gensalt()));

        return true;
    }

    public GuppUserAdapter getUserAdapter(UserModel user) {
        if (user instanceof CachedUserModel) {
            return (GuppUserAdapter)((CachedUserModel) user).getDelegateForUpdate();
        } else {
            return (GuppUserAdapter) user;
        }
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!supportsCredentialType(credentialType)) return;

        getUserAdapter(user).setPassword(null);
    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        if (getUserAdapter(user).getPassword() != null) {
            Set<String> set = new HashSet<>();
            set.add(PasswordCredentialModel.TYPE);
            return set.stream();
        } else {
            return Stream.empty();
        }
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType) && getPassword(user) != null;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel))
            return false;
        UserCredentialModel cred = (UserCredentialModel) input;
        String password = getPassword(user);

        return password != null && BCrypt.checkpw(cred.getValue(), password);
    }

    public String getPassword(UserModel user) {
        String password = null;
        if (user instanceof CachedUserModel) {
            password = (String)((CachedUserModel)user).getCachedWith().get(PASSWORD_CACHE_KEY);
        } else if (user instanceof GuppUserAdapter) {
            password = ((GuppUserAdapter)user).getPassword();
        }
        return password;
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        Object count = em.createNamedQuery("getUserCount")
                .getSingleResult();
        return ((Number)count).intValue();
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        String search = params.get(UserModel.SEARCH);
        TypedQuery<GuppUser> query = em.createNamedQuery("searchForUser", GuppUser.class);
        query.setParameter("search", "%" + search.toLowerCase() + "%");

        logger.info("Searching for users");

        if (firstResult != null) {
            query.setFirstResult(firstResult);
        }

        if (maxResults != null) {
            query.setMaxResults(maxResults);
        }

        if (session == null) {
            logger.warn("No active session found");
        }

        var resulStream = query.getResultStream();

        if (resulStream == null) {
            logger.warn("No result stream found. Will return null");
            return null;
        }

        return resulStream.map(entity -> new GuppUserAdapter(session, realm, model, entity));
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        return Stream.empty();
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        return Stream.empty();
    }
}
