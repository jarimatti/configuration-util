package com.github.jarimatti.configurationutil;

import org.osgi.framework.Bundle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Default configuration library.
 * <p>
 * The default configuration interface loads a reference configuration using provided classloader and merges it with the OSGi configuration.
 * This enables developers to put a reference configuration inside the bundle and let users override parts of it.
 * <p>
 * Typical use case:
 * <pre>
 * {@code
 * @Activate
 * void activate(BundleContext ctx, Map<String, Object> properties) {
 *     Map<String, Object> config = DefaultConfiguration.load(properties, "reference.cnf", ctx.classLoader());
 *     ...
 * }
 * }
 * </pre>
 */
public final class ConfigurationUtil {

    /**
     * This is an utility class, so the constructor is private.
     */
    private ConfigurationUtil() {
        throw new AssertionError();
    }

    /**
     * Merge a reference configuration with user provided configuration.
     * <p>
     * This function loads the reference configuration from a file inside a bundle.
     *
     * @param userConfig        User provided configuration e.g. from OSGi. This overrides the reference configuration parameters.
     * @param referenceFilename File name where the reference configuration is stored.
     * @param bundle            The bundle where configuration is loaded from.
     * @return Configuration where the reference configuration is overriden with user configuration.
     * @throws FileNotFoundException    if the resource is not found in the ClassLoader.
     * @throws IllegalArgumentException if an illegal Unicode escape sequence is in reference configuration.
     * @throws IOException              if an error occurred during loading of reference configuration
     */
    public static Map<String, Object> load(Map<String, Object> userConfig, String referenceFilename, Bundle bundle) throws IOException {
        Map<String, Object> referenceConfig = loadConfig(referenceFilename, bundle);
        return mergeConfig(userConfig, referenceConfig);
    }

    /**
     * Produce the configuration from the file name.
     * <p>
     * Throws an exception if the configuration can't be loaded or parsed.
     *
     * @param filename Reference configuration file name.
     * @param bundle   The bundle which contains the reference configuration.
     * @return The reference configuration.
     * @throws FileNotFoundException    if the resource is not found in the ClassLoader.
     * @throws IllegalArgumentException if an illegal Unicode escape sequence is in reference configuration.
     * @throws IOException              if an error occurred during loading of reference configuration
     */
    private static Map<String, Object> loadConfig(String filename, Bundle bundle) throws IOException {

        Map<String, Object> config = new HashMap<>();

        Enumeration<URL> configs = bundle.findEntries("/", filename, false);
        if (configs.hasMoreElements()) {
            URL path = configs.nextElement();

            InputStream input = path.openStream();

            Properties referenceProperties = new Properties();
            referenceProperties.load(input);

            for (String key : referenceProperties.stringPropertyNames()) {
                config.put(key, referenceProperties.getProperty(key));
            }
        } else {
            throw new FileNotFoundException("Could not find reference configuration file '" + filename + "' inside bundle.");
        }

        return config;
    }

    /**
     * Produce a map where the reference values are overridden with user values.
     *
     * @param userConfig      User configuration values.
     * @param referenceConfig Reference configuration values.
     * @return A merge of user and reference configuration.
     */
    private static Map<String, Object> mergeConfig(Map<String, Object> userConfig, Map<String, Object> referenceConfig) {
        Map<String, Object> config = new HashMap<>(referenceConfig);
        config.putAll(userConfig);
        return config;
    }

}
