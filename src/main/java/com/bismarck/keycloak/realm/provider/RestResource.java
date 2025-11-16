package com.bismarck.keycloak.realm.provider;

import com.bismarck.keycloak.gupp.request.BasicGuppRequest;
import com.bismarck.keycloak.realm.provider.task.TaskRunner;
import com.bismarck.keycloak.realm.provider.task.SyncTenant;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import static com.bismarck.keycloak.gupp.request.ActionPathConstants.SYNC_PATH;
import static com.bismarck.keycloak.gupp.request.ProviderPathConstants.TENANT_PATH;

/**
 * Main entry point for gupp related rest requests
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class RestResource {
    private final TaskRunner runner;

    public RestResource(KeycloakSession session) {
        this.runner = new TaskRunner(session);
    }

    @Path(TENANT_PATH+SYNC_PATH)
    @POST
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTenant(BasicGuppRequest request) {
        return runner.getResponseFromTask(new SyncTenant(), request);
    }
}
