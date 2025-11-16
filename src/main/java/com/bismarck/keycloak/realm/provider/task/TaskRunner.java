package com.bismarck.keycloak.realm.provider.task;

import com.bismarck.keycloak.realm.provider.roles.AccessDeniedException;
import com.bismarck.keycloak.gupp.request.GuppRequest;
import com.bismarck.keycloak.realm.provider.auth.AuthException;
import com.bismarck.keycloak.realm.provider.auth.AuthManager;
import com.bismarck.keycloak.realm.provider.user.RestUserProvider;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;

import java.util.concurrent.atomic.AtomicReference;

import static org.keycloak.models.utils.KeycloakModelUtils.runJobInTransaction;

/**
 * Runner class, that exposes {@link #getResponseFromTask(RestTask, GuppRequest)}, to run the requested
 * {@link RestTask} in a temporary session
 * This is necessary, because the {@link com.bismarck.keycloak.realm.provider.RestResource} always needs the present
 * context of the {@link BasicRestTask#getRequester()} and {@link BasicRestTask#requestedUser}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class TaskRunner {
    private final KeycloakSession session;
    private static final Logger log = Logger.getLogger(TaskRunner.class);

    public TaskRunner(KeycloakSession session) {
        this.session = session;
    }

    public Response getResponseFromTask(RestTask task, GuppRequest request) {
        AtomicReference<Response> response = new AtomicReference<>();
        runJobInTransaction(session.getKeycloakSessionFactory(), jobSession -> {
            var realm = jobSession.realms().getRealm(session.getContext().getRealm().getId());
            var userProvider = new RestUserProvider(jobSession, realm);
            var authManager = new AuthManager(userProvider);

            try {
                task.setAuthentication(authManager.resolveAuthentication(jobSession, realm));
            } catch (AuthException e) {
                response.set(Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
                return;
            }

            task.setRealm(realm);
            task.setRequestedUser(userProvider.getUserByInternalId(String.valueOf(request.getUser())));
            task.setSession(jobSession);
            task.setUserProvider(userProvider);

            try {
                response.set(task.run(request));
            } catch (AccessDeniedException e) {
                log.error("Access forbidden", e);
                log.debug("============END OF REQUEST============");
                response.set(Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build());
            }
        });

        log.debug("============END OF REQUEST============");
        return response.get();
    }

    public static Response getSuccessResponse(String message) {
        return Response
                .ok()
                .type(MediaType.APPLICATION_JSON)
                .entity(message)
                .build();
    }
}
