package io.opentelemetry.javaagent.instrumentation.nifi.v1_22_0;

import io.opentelemetry.instrumentation.api.util.VirtualField;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.provenance.ProvenanceReporter;

import java.util.logging.Logger;

public class ProvenanceProcessSessionTracker {
    private static final VirtualField<ProvenanceReporter, ProcessSession> processSessionMap =
            VirtualField.find(ProvenanceReporter.class, ProcessSession.class);

    private ProvenanceProcessSessionTracker() {}
    private static final Logger logger = Logger.getLogger(ProvenanceProcessSessionTracker.class.getName());

    public static void set(ProvenanceReporter provenanceReporter, ProcessSession processSession) {
        processSessionMap.set(provenanceReporter, processSession);
    }

    public static ProcessSession get(ProvenanceReporter provenanceReporter) {
        ProcessSession processSession = processSessionMap.get(provenanceReporter);
        if (processSession == null) {
            logger.warning("process session is null");
        }
        return processSession;
    }

}
