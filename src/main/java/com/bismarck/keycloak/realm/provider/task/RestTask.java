package com.bismarck.keycloak.realm.provider.task;

import com.bismarck.keycloak.realm.provider.roles.AccessDeniedException;
import com.bismarck.keycloak.gupp.request.GuppRequest;
import com.bismarck.keycloak.realm.provider.user.RestUserProvider;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;

/**
 * Interface, used by {@link TaskRunner#getResponseFromTask(RestTask, GuppRequest)}
 * and implemented by the {@link BasicRestTask}, to set necessary context in the getResponseFromTask method
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public interface RestTask {
    void setSession(KeycloakSession session);
    void setAuthentication(AuthenticationManager.AuthResult authResult);
    void setRequestedUser(UserModel user);
    void setRealm(RealmModel realm);
    void setUserProvider(RestUserProvider userProvider);
    Response run(GuppRequest request) throws AccessDeniedException;
}
