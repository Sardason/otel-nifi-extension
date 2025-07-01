package io.opentelemetry.javaagent.instrumentation.nifi.v1_22_0;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;

@AutoService(InstrumentationModule.class)
public final class NifiProvenanceReporterInstrumentationModule extends InstrumentationModule {
    public NifiProvenanceReporterInstrumentationModule() {
        super("nifi");
    }

    @Override
    public int order() { return 1; }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return hasClassesNamed("org.apache.nifi.controller.repository.StandardProvenanceReporter");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        ArrayList<TypeInstrumentation> result = new ArrayList<>();
        result.add(new NifiProvenanceReporterInstrumentation());
        return result;
    }
}
