package com.bismarck.keycloak.user.provider.config;

/**
 * Configuration Properties, that should be available in the Keycloak admin UI
 * when configuration the {@link com.bismarck.keycloak.gupp.ProviderConstants#USER_PROVIDER_ID}
 * They also have to be present in the {@link com.bismarck.keycloak.user.provider.GuppUserStorageProviderFactory#configMetadata}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class ConfigConstants {
    public static ConfigProperty url = new ConfigProperty(
            "mysql",
            "MySQL URI",
            "MySQL URI to connect to DB"
    );
    public static ConfigProperty user = new ConfigProperty(
            "db_username",
            "MySQL DB Username",
            "MySQL username for DB connection"
    );
    public static ConfigProperty password = new ConfigProperty(
            "db_password",
            "MySQL DB Password",
            "MySQL password for DB connection"
    );

    public static ConfigProperty client = new ConfigProperty(
            "auth_client",
            "Permission Client",
            "ID of the Client that should be used for SPI based REST Authentication and Role retrieval"
    );
}
