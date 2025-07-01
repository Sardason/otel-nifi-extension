package io.opentelemetry.javaagent.instrumentation.nifi.v1_22_0;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.provenance.ProvenanceReporter;

import java.util.Collection;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class NifiProvenanceReporterInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.apache.nifi.controller.repository.StandardProvenanceReporter");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return AgentElementMatchers.hasSuperType(
                namedOneOf("org.apache.nifi.controller.repository.StandardProvenanceReporter"));
    }

    @Override
    public void transform(TypeTransformer typeTransformer) {
        typeTransformer.applyAdviceToMethod(
                namedOneOf("join").and(takesArguments(4)).
                        and(takesArguments(Collection.class, FlowFile.class, String.class, long.class)),
                this.getClass().getName() + "$NifiProvenanceJoinAdvice");
    }

    public static class NifiProvenanceJoinAdvice {

        @Advice.OnMethodExit(suppress = Throwable.class)
        public static void onExit(
                @Advice.This ProvenanceReporter provenanceReporter,
                @Advice.Argument(value = 0) Collection<FlowFile> parentFlowFiles,
                @Advice.Argument(value = 1) FlowFile childFlowFile
        ) {
            ProvenanceReporterSingletons.handleProvenanceJoin(provenanceReporter, parentFlowFiles, childFlowFile);
        }
    }
}
