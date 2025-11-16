package com.bismarck.keycloak.realm.provider.task;

import com.bismarck.keycloak.gupp.request.GuppRequest;
import com.bismarck.keycloak.realm.provider.roles.AccessDeniedException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.models.GroupModel;

import static com.bismarck.keycloak.realm.provider.task.TaskRunner.getSuccessResponse;

public class SyncTenant extends BasicRestTask {

    private static final Logger log = Logger.getLogger(SyncTenant.class);

    /**
     * Adds the user to a group, matching the tenant of the {@link BasicRestTask#getRequester()} if necessary.
     * If no group with this name exists and the user is
     * {@link com.bismarck.keycloak.gupp.KeycloakPermissions#super_admin},
     * creates a new group with the {@link BasicRestTask#getRequester()} tenants name
     *
     * @param request with tenant
     */
    @Override
    public Response run(GuppRequest request) throws AccessDeniedException {
        if (requestedUser == null) {
            throw new AccessDeniedException("Requested User could not be found");
        }

        log.debug("requested User: " + requestedUser.getUsername());

        var tenant = userProvider.getUserTenant(getRequester().getId());

        if (tenant == null || tenant.isEmpty()) {
            throw new AccessDeniedException("Requester has no set tenant");
        }

        log.debug("Requester tenant: " + tenant);

        var requester = getRequester();

        if (!getRoleChecker().isAdmin()) {
            throw new AccessDeniedException("Requester has no admin role. Tenant could not be added");
        }

        var alreadyInGroup = requestedUser.getGroupsStream()
                .anyMatch(group -> group.getName().equals(tenant));

        if (alreadyInGroup) {
            throw new AccessDeniedException("User "+requestedUser.getUsername()+" is already in group "+tenant);
        }

        var tenantMatch = requester.getGroupsStream()
                .filter(group -> group.getName().equals(tenant))
                .findFirst();

        if (tenantMatch.isEmpty()) {
            if (!getRoleChecker().isSuperAdmin()) {
                throw new AccessDeniedException("User tenant does not match requested tenant. Tenant could not be added");
            }

            createAndJoinGroup(tenant);
            return getSuccessResponse("Successfully created tenant: "+tenant+" for user: "+requestedUser.getUsername());
        }

        joinGroup(tenantMatch.get());
        return getSuccessResponse("User "+requestedUser.getUsername()+" successfully joined group: "+tenant);
    }

    private void joinGroup(GroupModel group) {
        requestedUser.joinGroup(group);
        userProvider.setTenantForUser(requestedUser.getId(), group.getName());
    }

    private void createAndJoinGroup(String groupName) {
        requestedUser.joinGroup(realm.createGroup(groupName));
        userProvider.setTenantForUser(requestedUser.getId(), groupName);
    }
}
