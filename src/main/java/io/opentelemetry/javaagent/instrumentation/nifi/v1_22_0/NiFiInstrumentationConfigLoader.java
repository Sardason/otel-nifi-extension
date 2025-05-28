package io.opentelemetry.javaagent.instrumentation.nifi.v1_22_0;

import com.google.auto.service.AutoService;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoService(AutoConfigurationCustomizerProvider.class)
public class NiFiInstrumentationConfigLoader implements AutoConfigurationCustomizerProvider {

    private static volatile Set<String> useLinksProcessors = Collections.emptySet();
    private static volatile Set<String> externalPropagationProcessors = Collections.emptySet();
    private static volatile Set<String> externalPropagationThreadPrefixes = Collections.emptySet();

    public static Set<String> getUseLinksProcessors() {
        return useLinksProcessors;
    }

    public static Set<String> getExternalPropagationProcessors() {
        return externalPropagationProcessors;
    }

    public static Set<String> getExternalPropagationThreadPrefixes() {
        return externalPropagationThreadPrefixes;
    }

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        autoConfiguration.addPropertiesCustomizer(
            (ConfigProperties config) -> {

                List<String> useLinksList =
                        config.getList("otel.instrumentation.nifi.use-links-processors", Collections.emptyList());
                useLinksProcessors = new HashSet<>(useLinksList);

                List<String> externalProcessorsList =
                        config.getList("otel.instrumentation.nifi.external-propagation-processors", Collections.emptyList());
                externalPropagationProcessors = new HashSet<>(externalProcessorsList);

                List<String> externalThreadsList =
                        config.getList("otel.instrumentation.nifi.external-propagation-thread-prefixes", Collections.emptyList());
                externalPropagationThreadPrefixes = new HashSet<>(externalThreadsList);

                // Return an empty map as you are only reading properties, not adding new ones
                return Collections.emptyMap();
            });
    }
}


