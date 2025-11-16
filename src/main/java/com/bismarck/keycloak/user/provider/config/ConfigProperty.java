package com.bismarck.keycloak.user.provider.config;

/**
 * Data-class for the {@link ConfigConstants}
 * Getters get used by {@link com.bismarck.keycloak.user.provider.GuppUserStorageProviderFactory}, to add them to the admin UI and config validation
 * and by all other classes, to retrieve single properties by their name
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class ConfigProperty {
    private final String name;
    private final String label;
    private final String helpText;

    public ConfigProperty(String name, String label, String helpText) {
        this.name = name;
        this.label = label;
        this.helpText = helpText;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public String getHelpText() {
        return helpText;
    }
}
