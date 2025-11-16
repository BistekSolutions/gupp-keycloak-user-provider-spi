package com.bismarck.keycloak.user.provider;

import com.bismarck.keycloak.user.provider.model.GuppUser;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bismarck.keycloak.gupp.ProviderConstants.USER_PROVIDER_ID;
import static com.bismarck.keycloak.user.provider.config.ConfigConstants.*;

/**
 * ProviderFactory for the {@link com.bismarck.keycloak.gupp.ProviderConstants#USER_PROVIDER_ID}
 *
 * @author n.bismarck
 * @since 0.1.0
 */
public class GuppUserStorageProviderFactory implements UserStorageProviderFactory<GuppUserStorageProvider> {

    private static final List<ProviderConfigProperty> configMetadata;
    private static final Logger logger = Logger.getLogger(GuppUserStorageProviderFactory.class);
    private EntityManagerFactory emf;

    static {
        configMetadata = ProviderConfigurationBuilder.create()
                .property().name(url.getName())
                .type(ProviderConfigProperty.STRING_TYPE)
                .label(url.getLabel())
                .helpText(url.getHelpText()).add()

                .property().name(user.getName())
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("root")
                .label(user.getLabel())
                .helpText(user.getHelpText()).add()

                .property().name(password.getName())
                .type(ProviderConfigProperty.PASSWORD)
                .label(password.getLabel())
                .helpText(password.getHelpText()).add()

                .property().name(client.getName())
                .type(ProviderConfigProperty.STRING_TYPE)
                .label(client.getLabel())
                .helpText(client.getHelpText()).add()
                .build();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        if (config.get(url.getName()).isEmpty()
                || config.get(user.getName()).isEmpty()
                || config.get(password.getName()).isEmpty()
                || config.get(client.getName()).isEmpty()) {
            throw new ComponentValidationException("Configuration not properly set, please verify.");
        }
    }

    @Override
    public GuppUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        if (emf == null || !emf.isOpen()) {
            logger.info("Create Gupp EntityManagerFactory");
            emf = getEntityManagerFactory(model);
        }

        if (emf == null) {
            logger.error("Could not create EntityManagerFactory");
            return null;
        }

        return new GuppUserStorageProvider(session, model, emf.createEntityManager());
    }

    public static EntityManagerFactory getEntityManagerFactory(ComponentModel model) {
        Map<String, Object> props = new HashMap<>();

        props.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        props.put("hibernate.connection.url", model.get(url.getName()));
        props.put("hibernate.connection.username", model.get(user.getName()));
        props.put("hibernate.connection.password", model.get(password.getName()));

        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "true");

        try (var serviceRegistry = new StandardServiceRegistryBuilder().applySettings(props).build()) {

            var metadataSources = new MetadataSources(serviceRegistry);
            metadataSources.addAnnotatedClass(GuppUser.class);

            return metadataSources.buildMetadata().buildSessionFactory();
        }
    }

    @Override
    public String getId() {
        return USER_PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "GUPP (Granular User Permission Provider) User Provider";
    }

    @Override
    public void close() {
        emf.close();
        logger.info("Closing GUPP User Provider Factory...");
    }
}
