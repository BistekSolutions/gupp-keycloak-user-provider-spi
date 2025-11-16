package com.bismarck.keycloak.realm.provider.auth;

import com.bismarck.keycloak.realm.provider.user.RestUserProvider;
import jakarta.ws.rs.core.HttpHeaders;
import org.keycloak.TokenVerifier;
import org.keycloak.common.VerificationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import java.util.Arrays;

import static com.bismarck.keycloak.user.provider.config.ConfigConstants.client;
import static com.bismarck.keycloak.user.provider.config.ConfigPropertyRetriever.getPropertyValue;

/**
 * Identifies the user either via cookies or bearer auth
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class AuthManager {

    private static final String BEARER = "Bearer ";
    private final RestUserProvider restUserProvider;

    public AuthManager(RestUserProvider userProvider) {

        this.restUserProvider = userProvider;
    }

    public AuthenticationManager.AuthResult resolveAuthentication(KeycloakSession session, RealmModel realm) throws AuthException {

        var authHeader = getAuthHeader(session);

        if (authHeader == null || authHeader.isEmpty()) {
            throw new AuthException("No authentication header found");
        }

        if (authHeader.startsWith(BEARER)) {
            return resolveBearerTokenAuthentication(session, realm);
        }

        return resolveCookieAuthentication(session, realm);
    }

    private AuthenticationManager.AuthResult resolveCookieAuthentication(KeycloakSession keycloakSession, RealmModel realm) {
        return  new AppAuthManager().authenticateIdentityCookie(keycloakSession, realm);
    }

    private AuthenticationManager.AuthResult resolveBearerTokenAuthentication(KeycloakSession session, RealmModel realm) throws AuthException {
        var authHeader = getAuthHeader(session);

        var tokenString = authHeader.substring(BEARER.length());
        var tokenVerifier = TokenVerifier.create(tokenString, AccessToken.class);

        try {
            var token = tokenVerifier.getToken();

            validateTokenExpiration(token);
            validateTokenAudience(token, realm);

            return new AuthenticationManager.AuthResult(
                    getUserByToken(token),
                    null,
                    token,
                    session.getContext().getClient()
            );
        } catch (VerificationException e) {
            return null;
        }
    }

    private void validateTokenExpiration(AccessToken token) throws AuthException {
        if (token.isExpired()) {
            throw new AuthException("Token is expired");
        }
    }

    private void validateTokenAudience(AccessToken token, RealmModel realm) throws AuthException {
        var clientId = getPropertyValue(client.getName(), realm);

        if (!Arrays.asList(token.getAudience()).contains(clientId)) {
            throw new AuthException("Token does not contain "+clientId+" audience");
        }
    }

    private UserModel getUserByToken(AccessToken token) throws AuthException {
        var user = restUserProvider.getUserById(token.getSubject());

        if (user == null) {
            throw new AuthException("User not found");
        }

        return user;
    }

    private static String getAuthHeader(KeycloakSession session) {
        var headers = session.getContext().getRequestHeaders();
        return headers.getHeaderString(HttpHeaders.AUTHORIZATION);
    }


}
