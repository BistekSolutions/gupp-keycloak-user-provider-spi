package com.bismarck.keycloak.realm.provider.roles;

import org.keycloak.models.ClientModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;

import java.util.stream.Stream;

import static com.bismarck.keycloak.gupp.KeycloakPermissions.admin;
import static com.bismarck.keycloak.gupp.KeycloakPermissions.super_admin;

/**
 * Checks for user roles via the {@link UserModel#getClientRoleMappingsStream(ClientModel)}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class RoleChecker {

    private final UserModel user;
    private final ClientModel client;

    public RoleChecker(final UserModel user, final ClientModel client) {
        this.user = user;
        this.client = client;
    }

    private Stream<RoleModel> getRoleStream() {
        return user.getClientRoleMappingsStream(client);
    }

    public boolean isAdmin() {
        return getRoleStream().anyMatch(role -> role.getName().equalsIgnoreCase(admin.toString()));
    }

    public boolean isSuperAdmin() {
        return getRoleStream().anyMatch(role -> role.getName().equalsIgnoreCase(super_admin.toString()));
    }
}
