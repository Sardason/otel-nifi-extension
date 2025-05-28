package io.opentelemetry.javaagent.instrumentation.nifi.v1_22_0;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import io.opentelemetry.sdk.trace.ReadableSpan;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.provenance.ProvenanceReporter;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ProvenanceReporterSingletons {
    private static final Logger logger =
            Logger.getLogger(ProvenanceReporterSingletons.class.getName());

    public static void handleProvenanceJoin(ProvenanceReporter provenanceReporter, Collection<FlowFile> parentFlowFiles, FlowFile childFlowFile) {
        ProcessSession processSession = ProvenanceProcessSessionTracker.get(provenanceReporter);
        Span span = getSpanByProcessSessionAndFlowFile(processSession, childFlowFile);
        List<Context> parentContexts = getParentContexts(parentFlowFiles);
        parentContexts.forEach(parentContext ->
                SpanLinkTracker.addLink(span, Span.fromContext(parentContext).getSpanContext()));
    }

    private static Span getSpanByProcessSessionAndFlowFile(ProcessSession processSession, FlowFile flowFile) {
        Span span = ProcessSpanTracker.getSpan(processSession, flowFile);
        if (span == null) {
            logger.warning("No active span for flow file found");
        }
        return span;
    }

    private static List<Context> getParentContexts(Collection<FlowFile> inputFlowFiles) {
        return inputFlowFiles.stream()
                .map(flowFile -> GlobalOpenTelemetry.getPropagators()
                        .getTextMapPropagator()
                        .extract(Java8BytecodeBridge.currentContext(), flowFile.getAttributes(),
                                FlowFileAttributesTextMapGetter.INSTANCE)).collect(Collectors.toList());
    }
}
