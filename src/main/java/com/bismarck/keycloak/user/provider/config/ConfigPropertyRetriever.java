package com.bismarck.keycloak.user.provider.config;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.RealmModel;

import static com.bismarck.keycloak.gupp.ProviderConstants.USER_PROVIDER_ID;

/**
 * Utility class, that is responsible for retrieving a property from the {@link ConfigConstants}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class ConfigPropertyRetriever {

    /**
     * Needed, because the {@link com.bismarck.keycloak.realm.provider.user.RestUserProvider}
     * gets the realm from the {@link com.bismarck.keycloak.realm.provider.task.TaskRunner}
     * @param realm that includes the {@link com.bismarck.keycloak.gupp.ProviderConstants#USER_PROVIDER_ID}
     * @return component
     */
    public static ComponentModel getComponentModel(RealmModel realm) {
        return realm.getComponentsStream()
                .filter(component -> component.getProviderId().equals(USER_PROVIDER_ID))
                .findFirst()
                .orElse(null);
    }

    /**
     * See: {@link #getComponentModel(RealmModel)}
     * @param propertyName name of the property that should be fetched
     * @param realm that includes the {@link com.bismarck.keycloak.gupp.ProviderConstants#USER_PROVIDER_ID}
     * @return property value as String
     */
    public static String getPropertyValue(String propertyName, RealmModel realm) {
        return getComponentModel(realm).get(propertyName);
    }
}
