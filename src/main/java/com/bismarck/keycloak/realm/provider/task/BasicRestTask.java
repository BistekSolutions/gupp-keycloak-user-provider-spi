package com.bismarck.keycloak.realm.provider.task;

import com.bismarck.keycloak.realm.provider.roles.AccessDeniedException;
import com.bismarck.keycloak.gupp.request.GuppRequest;
import com.bismarck.keycloak.realm.provider.RestResourceProviderFactory;
import com.bismarck.keycloak.realm.provider.roles.RoleChecker;
import com.bismarck.keycloak.realm.provider.user.RestUserProvider;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;

import static com.bismarck.keycloak.realm.provider.RestResourceProviderFactory.CLIENT_ID;

/**
 * Base class for tasks
 * Implementing classes only need to override {@link #run(GuppRequest)}
 * In the run() method, auth, session, requestedUser and realm will be available from the class context
 * Used by {@link TaskRunner#getResponseFromTask(RestTask, GuppRequest)}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class BasicRestTask implements RestTask {
    protected AuthenticationManager.AuthResult auth;
    protected KeycloakSession session;
    protected UserModel requestedUser;
    protected RealmModel realm;
    protected RestUserProvider userProvider;
    private RoleChecker roleChecker;

    protected ClientModel getClientModel() {
        if (session.getContext().getClient() != null) {
            return session.getContext().getClient();
        }

        return getStaticClientFromConfig();
    }

    protected RoleChecker getRoleChecker() {
        if (roleChecker == null) {
            roleChecker = new RoleChecker(getRequester(), getClientModel());
        }

        return roleChecker;
    }

    /**
     * @return ClientModel from static name, set in {@link RestResourceProviderFactory#CLIENT_ID}
     */
    protected ClientModel getStaticClientFromConfig() {
        return session.clients().getClientByClientId(realm, CLIENT_ID);
    }

    protected UserModel getRequester() {
        return auth.getUser();
    }

    @Override
    public void setSession(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void setAuthentication(AuthenticationManager.AuthResult authResult) {
        this.auth = authResult;
    }

    @Override
    public void setRequestedUser(UserModel user) {
        this.requestedUser = user;
    }

    @Override
    public void setRealm(RealmModel realm) {
        this.realm = realm;
    }

    @Override
    public void setUserProvider(RestUserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @Override
    public Response run(GuppRequest request) throws AccessDeniedException {
        return null;
    }
}
