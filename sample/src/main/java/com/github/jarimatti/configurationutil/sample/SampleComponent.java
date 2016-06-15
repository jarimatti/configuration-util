package com.github.jarimatti.configurationutil.sample;

import com.github.jarimatti.configurationutil.ConfigurationUtil;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Sample component showing how to use the ConfigurationUtil class.
 */
@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
class SampleComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SampleComponent.class);

    @Activate
    public final void activate(BundleContext ctx, Map<String, Object> config) throws IOException {

        LOG.info("SampleComponent activating.");

        for (String key: config.keySet()) {
            LOG.info("User provided: " + key + " = " + config.get(key));
        }

        final Map<String, Object> effectiveConfig = ConfigurationUtil.load(
                config,
                "com.github.jarimatti.configurationutil.sample.SampleComponent.cfg",
                ctx.getBundle());

        for (String key: effectiveConfig.keySet()) {
            LOG.info("Effective configuration: " + key + " = " + effectiveConfig.get(key));
        }

        LOG.info("SampleComponent activated.");
    }

}
